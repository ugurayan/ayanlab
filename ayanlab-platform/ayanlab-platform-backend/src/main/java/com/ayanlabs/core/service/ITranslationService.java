package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.Translation;

import java.util.List;
import java.util.UUID;

public interface ITranslationService extends IBaseService<Translation> {

    List<Translation> findByAppAndLanguage(UUID appId, String language, CoreContext context);
}