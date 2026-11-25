package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Certificate;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface CertificateService {
    List<Certificate> findAll(CoreContext context);
    Certificate findById(UUID id, CoreContext context);
    Certificate create(Certificate body, CoreContext context);
    Certificate update(Certificate body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
