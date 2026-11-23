package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.ContactType;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface ContactTypeService {
    List<ContactType> findAll(CoreContext context);
    ContactType findById(String id, CoreContext context);
    ContactType create(ContactType body, CoreContext context);
    ContactType update(ContactType body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
