package com.ayanlab.core.service;

import com.ayanlab.core.entity.ReferenceLanguage;

import java.util.List;

public interface IReferenceLanguageService {

    List<ReferenceLanguage> listAll();

    ReferenceLanguage findByCode(String code);

    List<ReferenceLanguage> search(String query);
}