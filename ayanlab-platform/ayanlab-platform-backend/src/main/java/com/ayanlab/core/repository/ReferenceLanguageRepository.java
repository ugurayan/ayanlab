package com.ayanlab.core.repository;

import com.ayanlab.core.entity.ReferenceLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReferenceLanguageRepository extends JpaRepository<ReferenceLanguage, UUID> {

    Optional<ReferenceLanguage> findByCode(String code);

    List<ReferenceLanguage> findByIsActiveTrueOrderByNameAsc();

    List<ReferenceLanguage> findTop50ByNameContainingIgnoreCaseAndIsActiveTrueOrderByNameAsc(String namePart);

    List<ReferenceLanguage> findTop50ByCodeStartingWithIgnoreCaseAndIsActiveTrueOrderByCodeAsc(String codePrefix);
}