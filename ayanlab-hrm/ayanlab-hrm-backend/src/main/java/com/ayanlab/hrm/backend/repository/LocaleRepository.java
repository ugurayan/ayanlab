package com.ayanlab.hrm.backend.repository;

import com.ayanlab.hrm.backend.entity.Locale;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocaleRepository extends JpaRepository<Locale, String> {

    List<Locale> findByEnabledTrueOrderBySortOrderAsc();
}