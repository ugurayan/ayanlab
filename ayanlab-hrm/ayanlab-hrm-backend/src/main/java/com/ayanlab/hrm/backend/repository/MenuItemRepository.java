package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.MenuItem;
import com.ayanlab.hrm.backend.enums.NavigationSection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, String> {

    List<MenuItem> findAllByOrderBySortOrderAsc();

    List<MenuItem> findBySectionOrderBySortOrderAsc(NavigationSection section);
}