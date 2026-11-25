package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.DocumentVerification;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface DocumentVerificationService {
    List<DocumentVerification> findAll(CoreContext context);
    DocumentVerification findById(UUID id, CoreContext context);
    DocumentVerification create(DocumentVerification body, CoreContext context);
    DocumentVerification update(DocumentVerification body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
