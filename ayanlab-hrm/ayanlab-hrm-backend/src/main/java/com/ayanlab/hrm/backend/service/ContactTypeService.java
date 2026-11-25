package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.ContactType;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface ContactTypeService {
    List<ContactType> findAll(CoreContext context);
    ContactType findById(UUID id, CoreContext context);
    ContactType create(ContactType body, CoreContext context);
    ContactType update(ContactType body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
