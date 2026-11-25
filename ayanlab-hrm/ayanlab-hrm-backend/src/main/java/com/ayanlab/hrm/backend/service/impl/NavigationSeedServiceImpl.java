package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.config.HrmDataProperties;
import com.ayanlab.hrm.backend.entity.Locale;
import com.ayanlab.hrm.backend.entity.MenuItem;
import com.ayanlab.hrm.backend.entity.MenuItemLabel;
import com.ayanlab.hrm.backend.enums.NavigationSection;
import com.ayanlab.hrm.backend.repository.LocaleRepository;
import com.ayanlab.hrm.backend.repository.MenuItemLabelRepository;
import com.ayanlab.hrm.backend.repository.MenuItemRepository;
import com.ayanlab.hrm.backend.service.NavigationSeedService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NavigationSeedServiceImpl implements NavigationSeedService {

    private final HrmDataProperties dataProperties;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;
    private final LocaleRepository localeRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuItemLabelRepository labelRepository;

    @Override
    @Transactional
    public void reloadFromClasspathResource() throws Exception {
        Resource resource = resourceLoader.getResource(dataProperties.getNavigationSeedResource());
        if (!resource.exists()) {
            throw new IllegalStateException("Missing navigation seed: " + dataProperties.getNavigationSeedResource());
        }
        labelRepository.deleteAllInBatch();
        menuItemRepository.deleteAllInBatch();
        localeRepository.deleteAllInBatch();

        try (InputStream in = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(in);
            loadLocales(root.path("locales"));
            loadMenuItems(root.path("menuItems"));
            loadLabels(root.path("menuItemLabels"));
        }
    }

    private void loadLocales(JsonNode nodes) {
        List<Locale> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            Locale row = new Locale();
            row.setCode(n.path("code").asText());
            row.setDisplayName(n.path("displayName").asText());
            row.setBcp47Tag(n.path("bcp47Tag").asText(row.getCode()));
            row.setSortOrder(n.path("sortOrder").asInt());
            row.setEnabled(!n.has("enabled") || n.path("enabled").asBoolean(true));
            rows.add(row);
        }
        localeRepository.saveAll(rows);
    }

    private void loadMenuItems(JsonNode nodes) {
        List<MenuItem> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            MenuItem row = new MenuItem();
            row.setCode(n.path("code").asText());
            row.setRoutePath(n.path("routePath").asText());
            row.setSection(NavigationSection.valueOf(n.path("section").asText("PRIMARY")));
            row.setIconKey(n.path("iconKey").asText());
            row.setSortOrder(n.path("sortOrder").asInt());
            row.setRequiredPermission(n.path("requiredPermission").asText());
            rows.add(row);
        }
        menuItemRepository.saveAll(rows);
    }

    private void loadLabels(JsonNode nodes) {
        List<MenuItemLabel> rows = new ArrayList<>();
        for (JsonNode n : nodes) {
            String menuItemCode = n.path("menuItemCode").asText();
            String localeCode = n.path("localeCode").asText();
            MenuItemLabel row = new MenuItemLabel();
            row.setMenuItemCode(menuItemCode);
            row.setLocaleCode(localeCode);
            row.setId(MenuItemLabel.composeId(menuItemCode, localeCode));
            row.setLabel(n.path("label").asText());
            rows.add(row);
        }
        labelRepository.saveAll(rows);
    }
}