package com.ayanlabs.core.startup;

import com.ayanlabs.core.condition.AuthSeedSurface;
import com.ayanlabs.core.data.enums.AppSubscriptionModeEnum;
import com.ayanlabs.core.entity.App;
import com.ayanlabs.core.repository.AppRepository;
import com.ayanlabs.core.util.IdGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Optional;

/**
 * Loads or updates product metadata from {@code db/apps-catalog.json} (idempotent).
 */
@AuthSeedSurface
@Component
@Order(8)
public class AppsCatalogLoader implements CommandLineRunner {

    private final AppRepository appRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${platform.db.load-apps-catalog:true}")
    private boolean loadAppsCatalog;

    public AppsCatalogLoader(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    @Override
    public void run(String... args) {
        if (!loadAppsCatalog) {
            return;
        }
        ClassPathResource res = new ClassPathResource("db/apps-catalog.json");
        if (!res.exists()) {
            System.out.println("[AppsCatalogLoader] db/apps-catalog.json not found — skip");
            return;
        }
        int created = 0;
        int updated = 0;
        try (InputStream is = res.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            JsonNode apps = root.get("applications");
            if (apps == null || !apps.isArray()) {
                return;
            }
            for (JsonNode a : apps) {
                String code = a.get("code").asText();
                Optional<App> existing = appRepository.findByCode(code);
                if (existing.isPresent()) {
                    App app = existing.get();
                    boolean dirty = merge(app, a);
                    if (dirty) {
                        appRepository.save(app);
                        updated++;
                    }
                } else {
                    App app = App.builder()
                        .code(code)
                        .name(a.get("name").asText())
                        .description(text(a, "description"))
                        .apiBasePath(text(a, "apiBasePath"))
                        .subscriptionMode(parseMode(a))
                        .multiTenantEnabled(a.has("multiTenantEnabled") ? a.get("multiTenantEnabled").asBoolean() : true)
                        .moduleAuthEnabled(true)
                        .build();
                    if (a.has("id")) {
                        try {
                            app.setId(java.util.UUID.fromString(a.get("id").asText()));
                        } catch (Exception ignored) {}
                    }
                    IdGenerator.assignIfAbsent(app);
                    appRepository.save(app);
                    created++;
                }
            }
            System.out.printf("[AppsCatalogLoader] apps created=%d updated=%d%n", created, updated);
        } catch (Exception e) {
            System.err.println("[AppsCatalogLoader] failed: " + e.getMessage());
        }
    }

    private static boolean merge(App app, JsonNode a) {
        boolean dirty = false;
        if (a.has("name")) {
            String name = a.get("name").asText();
            if (!name.equals(app.getName())) {
                app.setName(name);
                dirty = true;
            }
        }
        if (a.has("description")) {
            String desc = text(a, "description");
            if (desc != null && !desc.equals(app.getDescription())) {
                app.setDescription(desc);
                dirty = true;
            }
        }
        if (a.has("apiBasePath")) {
            String path = text(a, "apiBasePath");
            if (path != null && !path.equals(app.getApiBasePath())) {
                app.setApiBasePath(path);
                dirty = true;
            }
        }
        AppSubscriptionModeEnum mode = parseMode(a);
        if (mode != null && mode != app.getSubscriptionMode()) {
            app.setSubscriptionMode(mode);
            dirty = true;
        }
        if (a.has("multiTenantEnabled")) {
            boolean mt = a.get("multiTenantEnabled").asBoolean();
            if (!Boolean.valueOf(mt).equals(app.getMultiTenantEnabled())) {
                app.setMultiTenantEnabled(mt);
                dirty = true;
            }
        }
        return dirty;
    }

    private static AppSubscriptionModeEnum parseMode(JsonNode a) {
        if (!a.has("subscriptionMode")) {
            return null;
        }
        try {
            return AppSubscriptionModeEnum.valueOf(a.get("subscriptionMode").asText());
        } catch (Exception e) {
            return null;
        }
    }

    private static String text(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }
}