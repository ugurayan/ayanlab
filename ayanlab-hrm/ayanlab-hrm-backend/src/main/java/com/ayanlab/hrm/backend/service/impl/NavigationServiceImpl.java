package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Locale;
import com.ayanlab.hrm.backend.entity.MenuItem;
import com.ayanlab.hrm.backend.entity.MenuItemLabel;
import com.ayanlab.hrm.backend.repository.LocaleRepository;
import com.ayanlab.hrm.backend.repository.MenuItemLabelRepository;
import com.ayanlab.hrm.backend.repository.MenuItemRepository;
import com.ayanlab.hrm.backend.service.NavigationService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NavigationServiceImpl implements NavigationService {

    private static final String FALLBACK_LOCALE = "en";

    private final LocaleRepository localeRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuItemLabelRepository labelRepository;

    @Override
    public List<Locale> listEnabledLocales() {
        return localeRepository.findByEnabledTrueOrderBySortOrderAsc();
    }

    @Override
    public List<Map<String, Object>> listMenuItemsForLocale(String localeCode) {
        String resolved = resolveLocale(localeCode);
        List<MenuItem> items = menuItemRepository.findAllByOrderBySortOrderAsc();
        List<String> codes = items.stream().map(MenuItem::getCode).toList();
        Map<String, String> labels =
                labelRepository.findByMenuItemCodeInAndLocaleCode(codes, resolved).stream()
                        .collect(Collectors.toMap(MenuItemLabel::getMenuItemCode, MenuItemLabel::getLabel));
        if (!FALLBACK_LOCALE.equals(resolved)) {
            Map<String, String> english =
                    labelRepository.findByMenuItemCodeInAndLocaleCode(codes, FALLBACK_LOCALE).stream()
                            .collect(
                                    Collectors.toMap(
                                            MenuItemLabel::getMenuItemCode, MenuItemLabel::getLabel));
            for (MenuItem item : items) {
                labels.putIfAbsent(item.getCode(), english.get(item.getCode()));
            }
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (MenuItem item : items) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("code", item.getCode());
            row.put("routePath", item.getRoutePath());
            row.put("section", item.getSection().name());
            row.put("iconKey", item.getIconKey());
            row.put("sortOrder", item.getSortOrder());
            row.put("requiredPermission", item.getRequiredPermission());
            row.put("label", labels.getOrDefault(item.getCode(), item.getCode()));
            row.put("locale", resolved);
            out.add(row);
        }
        return out;
    }

    private String resolveLocale(String localeCode) {
        if (localeCode == null || localeCode.isBlank()) {
            return FALLBACK_LOCALE;
        }
        String normalized = localeCode.trim().toLowerCase();
        return localeRepository.findById(normalized).filter(Locale::isEnabled).map(Locale::getCode).orElse(FALLBACK_LOCALE);
    }
}