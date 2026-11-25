package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.entity.Translation;

import java.util.List;
import java.util.UUID;

public interface ITranslationService extends IBaseService<Translation> {

    List<Translation> findByAppAndLanguage(UUID appId, String language, CoreContext context);
}