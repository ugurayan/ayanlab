package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Locale;
import com.ayanlab.hrm.backend.entity.MenuItem;
import java.util.List;
import java.util.Map;

public interface NavigationService {

    List<Locale> listEnabledLocales();

    List<Map<String, Object>> listMenuItemsForLocale(String localeCode);
}