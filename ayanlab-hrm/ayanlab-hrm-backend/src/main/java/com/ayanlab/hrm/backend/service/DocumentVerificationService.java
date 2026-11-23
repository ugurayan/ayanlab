package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.DocumentVerification;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface DocumentVerificationService {
    List<DocumentVerification> findAll(CoreContext context);
    DocumentVerification findById(String id, CoreContext context);
    DocumentVerification create(DocumentVerification body, CoreContext context);
    DocumentVerification update(DocumentVerification body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
