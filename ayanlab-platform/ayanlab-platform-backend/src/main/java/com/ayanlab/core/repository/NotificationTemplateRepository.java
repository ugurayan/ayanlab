package com.ayanlab.core.repository;

import com.ayanlab.core.entity.NotificationTemplate;
import com.ayanlab.core.enums.NotificationTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {
    boolean existsByAppIdAndCodeAndTypeAndLanguage(UUID appId, String code, NotificationTypeEnum type, String language);
    Page<NotificationTemplate> findByAppIdAndType(UUID appId, NotificationTypeEnum type, Pageable pageable);
    Page<NotificationTemplate> findByAppIdAndCodeContainingIgnoreCase(UUID appId, String code, Pageable pageable);
    Page<NotificationTemplate> findByAppId(UUID appId, Pageable pageable);
}

