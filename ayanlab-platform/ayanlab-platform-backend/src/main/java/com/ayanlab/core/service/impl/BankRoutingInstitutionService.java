package com.ayanlab.core.service.impl;

import com.ayanlab.core.entity.BankRoutingInstitution;
import com.ayanlab.core.repository.BankRoutingInstitutionRepository;
import com.ayanlab.core.service.IBankRoutingInstitutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankRoutingInstitutionService implements IBankRoutingInstitutionService {

    private final BankRoutingInstitutionRepository repository;

    @Override
    @Transactional(readOnly = true)
    public BankRoutingInstitution lookupByRoutingNumber(String routingNumber) {
        if (routingNumber == null) {
            return null;
        }
        String normalized = routingNumber.replaceAll("\\D", "");
        if (normalized.length() != 9) {
            return null;
        }
        return repository.findByRoutingNumber(normalized).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankRoutingInstitution> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        String q = query.trim();
        String digits = q.replaceAll("\\D", "");
        if (digits.length() >= 2) {
            return repository.findTop25ByRoutingNumberStartingWithAndIsActiveTrueOrderByRoutingNumberAsc(digits);
        }
        return repository.findTop25ByInstitutionNameContainingIgnoreCaseAndIsActiveTrueOrderByInstitutionNameAsc(q);
    }
}