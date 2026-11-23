package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Document;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface DocumentService {
    List<Document> findAll(CoreContext context);
    Document findById(String id, CoreContext context);
    Document create(Document body, CoreContext context);
    Document update(Document body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
