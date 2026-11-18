package com.ayanlab.core.repository;

import com.ayanlab.core.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TranslationRepository extends JpaRepository<Translation, UUID> {
    Optional<Translation> findByAppIdAndLanguageAndMessageKey(UUID appId, String language, String messageKey);

    List<Translation> findByAppIdAndLanguageAndIsActiveTrue(UUID appId, String language);
}