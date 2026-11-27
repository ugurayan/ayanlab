package com.ayanlab.core.service.impl;

import com.ayanlab.core.entity.ReferenceLanguage;
import com.ayanlab.core.repository.ReferenceLanguageRepository;
import com.ayanlab.core.service.IReferenceLanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReferenceLanguageService implements IReferenceLanguageService {

    private final ReferenceLanguageRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ReferenceLanguage> listAll() {
        return repository.findByIsActiveTrueOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public ReferenceLanguage findByCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return repository.findByCode(code.trim().toLowerCase()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReferenceLanguage> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String q = query.trim();
        if (q.length() <= 3) {
            var byCode = repository.findTop50ByCodeStartingWithIgnoreCaseAndIsActiveTrueOrderByCodeAsc(q.toLowerCase());
            if (!byCode.isEmpty()) {
                return byCode;
            }
        }
        return repository.findTop50ByNameContainingIgnoreCaseAndIsActiveTrueOrderByNameAsc(q);
    }
}