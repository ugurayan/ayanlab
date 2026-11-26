package com.ayanlab.comm.backend.repository;

import com.ayanlab.comm.backend.entity.EmailTemplateTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailTemplateTranslationRepository extends JpaRepository<EmailTemplateTranslation, UUID> {
    Optional<EmailTemplateTranslation> findByTemplateIdAndLanguage(UUID templateId, String language);
    List<EmailTemplateTranslation> findByTemplateId(UUID templateId);
}
