package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Document;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface DocumentService {
    List<Document> findAll(CoreContext context);
    Document findById(UUID id, CoreContext context);
    Document create(Document body, CoreContext context);
    Document update(Document body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
