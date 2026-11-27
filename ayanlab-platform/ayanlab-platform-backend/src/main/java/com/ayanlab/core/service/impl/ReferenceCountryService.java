package com.ayanlab.core.service.impl;

import com.ayanlab.core.entity.ReferenceCountry;
import com.ayanlab.core.repository.ReferenceCountryRepository;
import com.ayanlab.core.service.IReferenceCountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReferenceCountryService implements IReferenceCountryService {

    private final ReferenceCountryRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ReferenceCountry> listAll() {
        return repository.findByIsActiveTrueOrderByNameAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public ReferenceCountry findByAlpha2(String alpha2) {
        if (alpha2 == null || alpha2.isBlank()) {
            return null;
        }
        return repository.findByAlpha2Code(alpha2.trim().toUpperCase()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReferenceCountry> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String q = query.trim();
        if (q.length() == 2) {
            ReferenceCountry one = findByAlpha2(q);
            if (one != null) {
                return List.of(one);
            }
        }
        return repository.findTop50ByNameContainingIgnoreCaseAndIsActiveTrueOrderByNameAsc(q);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReferenceCountry> listByRegion(String region) {
        if (region == null || region.isBlank()) {
            return List.of();
        }
        return repository.findTop50ByRegionIgnoreCaseAndIsActiveTrueOrderByNameAsc(region.trim());
    }
}