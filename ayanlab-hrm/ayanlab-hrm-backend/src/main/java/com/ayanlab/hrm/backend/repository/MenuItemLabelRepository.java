package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.MenuItemLabel;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemLabelRepository extends JpaRepository<MenuItemLabel, String> {

    List<MenuItemLabel> findByLocaleCode(String localeCode);

    List<MenuItemLabel> findByMenuItemCodeInAndLocaleCode(Collection<String> menuItemCodes, String localeCode);
}