package com.ayanlab.core.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.entity.Translation;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.TranslationRepository;
import com.ayanlab.core.service.ITranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.mapping.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class TranslationService implements ITranslationService {

    private final TranslationRepository translationRepository;

    @Override
    @Transactional
    public Translation create(Translation item, CoreContext context) {
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return translationRepository.save(item);
    }

    @Override
    @Transactional
    public Translation update(Translation item, CoreContext context) {
        if (item.getId() == null) throw new CoreException("INVALID_ARGUMENT", "Translation id required");
        if (translationRepository.findById(item.getId()).isEmpty()) {
            throw new CoreException("NOT_FOUND", "Translation not found");
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return translationRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(Translation item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        translationRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        Translation t = findById(id, context);
        if (t != null) delete(t, context);
    }

    @Override
    public Translation findById(UUID id, CoreContext context) {
        return translationRepository.findById(id).orElse(null);
    }

    @Override
    public List<Translation> findByAppAndLanguage(UUID appId, String language, CoreContext context) {
        UUID resolvedApp = appId != null ? appId : context.getAppId();
        if (resolvedApp == null) throw new CoreException("VALIDATION_ERROR", "appId required");
        String lang = language != null ? language : context.getLanguage();
        if (lang == null || lang.isBlank()) lang = "en";
        return translationRepository.findByAppIdAndLanguageAndIsActiveTrue(resolvedApp, lang);
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        Translation found = findById(id, context);
        if (found != null) {
            translationRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public Translation setActive(UUID id, boolean active, CoreContext context) {
        Translation found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "Translation not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return translationRepository.save(found);
    }
}