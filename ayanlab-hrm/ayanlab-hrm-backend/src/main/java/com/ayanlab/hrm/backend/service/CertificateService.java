package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Certificate;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface CertificateService {
    List<Certificate> findAll(CoreContext context);
    Certificate findById(String id, CoreContext context);
    Certificate create(Certificate body, CoreContext context);
    Certificate update(Certificate body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
