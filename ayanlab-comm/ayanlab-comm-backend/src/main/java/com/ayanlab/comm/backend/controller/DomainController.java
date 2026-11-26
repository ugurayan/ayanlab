package com.ayanlab.comm.backend.controller;

import com.ayanlab.comm.backend.dto.DomainRegisterRequest;
import com.ayanlab.comm.backend.entity.OrganizationDomain;
import com.ayanlab.comm.backend.service.DomainVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comm/domains")
@RequiredArgsConstructor
public class DomainController {

    private final DomainVerificationService domainVerificationService;

    @PostMapping
    public ResponseEntity<OrganizationDomain> registerDomain(
            @RequestHeader("X-Organization-Id") UUID organizationId,
            @RequestHeader("X-App-Id") UUID appId,
            @RequestBody DomainRegisterRequest request) {
        return ResponseEntity.ok(domainVerificationService.registerDomain(organizationId, appId, request));
    }

    @PostMapping("/{id}/verify")
    public ResponseEntity<Void> verifyDomain(@PathVariable("id") UUID domainId) {
        domainVerificationService.verifyDomain(domainId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<OrganizationDomain>> listDomains(
            @RequestHeader("X-Organization-Id") UUID organizationId) {
        return ResponseEntity.ok(domainVerificationService.listDomains(organizationId));
    }
}
