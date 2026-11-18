package com.ayanlab.core.startup;

import com.ayanlab.core.authz.catalog.AppAuthorizationCatalog;
import com.ayanlab.core.condition.AuthSeedSurface;
import com.ayanlab.core.entity.*;
import com.ayanlab.core.repository.*;
import com.ayanlab.core.service.impl.AuthorizationCatalogImportService;
import com.ayanlab.core.util.IdGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

/**
 * Imports the platform-owned app catalogs ({@code db/authz/*.json}, schema v2) and {@code db/menu-catalog.json}.
 * App backends that own their catalog (e.g. HRM / staffiq) publish it via the internal catalog endpoint instead.
 */
@Slf4j
@AuthSeedSurface
@Component
@Order(15)
public class AuthorizationCatalogLoader implements CommandLineRunner {

    private final AuthorizationCatalogImportService importService;
    private final AppRepository appRepository;
    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final ObjectMapper objectMapper;

    @Value("${platform.db.load-authorization-catalog:${platform.db.load-policy-data:true}}")
    private boolean loadAuthorizationCatalog;

    public AuthorizationCatalogLoader(AuthorizationCatalogImportService importService,
                                      AppRepository appRepository,
                                      RoleRepository roleRepository,
                                      MenuRepository menuRepository,
                                      RoleMenuRepository roleMenuRepository,
                                      ObjectMapper objectMapper) {
        this.importService = importService;
        this.appRepository = appRepository;
        this.roleRepository = roleRepository;
        this.menuRepository = menuRepository;
        this.roleMenuRepository = roleMenuRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!loadAuthorizationCatalog) {
            log.info("[AuthorizationCatalogLoader] Disabled by platform.db.load-authorization-catalog=false");
            return;
        }
        Resource[] catalogs = new PathMatchingResourcePatternResolver().getResources("classpath:db/authz/*.json");
        Arrays.sort(catalogs, Comparator.comparing(r -> Objects.requireNonNullElse(r.getFilename(), "")));
        for (Resource resource : catalogs) {
            try (InputStream is = resource.getInputStream()) {
                AppAuthorizationCatalog catalog = objectMapper.readValue(is, AppAuthorizationCatalog.class);
                if (appRepository.findByCode(catalog.appCode()).isEmpty()) {
                    log.warn("[AuthorizationCatalogLoader] Skipping {}: app '{}' not found", resource.getFilename(), catalog.appCode());
                    continue;
                }
                importService.importCatalog(catalog);
            }
        }
        int[] menuStats = loadMenuCatalog();
        log.info("[AuthorizationCatalogLoader] catalogs={} menusCreated={} roleMenuLinksCreated={}",
            catalogs.length, menuStats[0], menuStats[1]);
    }

    /** @return [menusCreated, roleMenuLinksCreated] */
    private int[] loadMenuCatalog() throws Exception {
        ClassPathResource res = new ClassPathResource("db/menu-catalog.json");
        if (!res.exists()) {
            return new int[]{0, 0};
        }
        int menusCreated = 0;
        int linksCreated = 0;
        try (InputStream is = res.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            Map<String, Map<String, Menu>> menusByApp = new LinkedHashMap<>();

            JsonNode menus = root.get("menus");
            if (menus != null && menus.isArray()) {
                for (JsonNode node : menus) {
                    Optional<App> appOpt = appRepository.findByCode(node.get("appCode").asText());
                    if (appOpt.isEmpty()) {
                        continue;
                    }
                    App app = appOpt.get();
                    String code = node.get("code").asText();
                    Optional<Menu> existing = menuRepository.findByAppIdAndCode(app.getId(), code);
                    Menu menu = existing.orElseGet(() -> {
                        Menu created = Menu.builder()
                            .appId(app.getId())
                            .code(code)
                            .name(node.get("name").asText())
                            .path(textOrNull(node, "path"))
                            .moduleCode(textOrNull(node, "moduleCode"))
                            .parentCode(textOrNull(node, "parentCode"))
                            .sortOrder(node.has("sortOrder") ? node.get("sortOrder").asInt() : 0)
                            .build();
                        created.setIsActive(true);
                        IdGenerator.assignIfAbsent(created);
                        return menuRepository.save(created);
                    });
                    if (existing.isEmpty()) {
                        menusCreated++;
                    }
                    menusByApp.computeIfAbsent(app.getCode(), k -> new LinkedHashMap<>()).put(code, menu);
                }
            }

            JsonNode roleMenus = root.get("roleMenus");
            if (roleMenus != null && roleMenus.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> appFields = roleMenus.fields();
                while (appFields.hasNext()) {
                    Map.Entry<String, JsonNode> appEntry = appFields.next();
                    Optional<App> appOpt = appRepository.findByCode(appEntry.getKey());
                    Map<String, Menu> appMenus = menusByApp.getOrDefault(appEntry.getKey(), Map.of());
                    if (appOpt.isEmpty() || appMenus.isEmpty()) {
                        continue;
                    }
                    Iterator<Map.Entry<String, JsonNode>> roleFields = appEntry.getValue().fields();
                    while (roleFields.hasNext()) {
                        Map.Entry<String, JsonNode> roleEntry = roleFields.next();
                        Optional<Role> role = roleRepository.findFirstByAppIdAndCodeAndOrganizationIdIsNull(appOpt.get().getId(), roleEntry.getKey());
                        if (role.isEmpty()) {
                            continue;
                        }
                        List<String> menuCodes = new ArrayList<>();
                        roleEntry.getValue().forEach(n -> menuCodes.add(n.asText()));
                        Collection<Menu> targets = menuCodes.size() == 1 && "*".equals(menuCodes.get(0))
                            ? appMenus.values()
                            : menuCodes.stream().map(appMenus::get).filter(Objects::nonNull).toList();
                        for (Menu menu : targets) {
                            if (!roleMenuRepository.existsByRoleIdAndMenuId(role.get().getId(), menu.getId())) {
                                RoleMenu link = RoleMenu.builder().roleId(role.get().getId()).menuId(menu.getId()).build();
                                link.setIsActive(true);
                                IdGenerator.assignIfAbsent(link);
                                roleMenuRepository.save(link);
                                linksCreated++;
                            }
                        }
                    }
                }
            }
        }
        return new int[]{menusCreated, linksCreated};
    }

    private static String textOrNull(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }
}
