package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Message;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    List<Message> findAll(CoreContext context);
    Message findById(UUID id, CoreContext context);
    Message create(Message body, CoreContext context);
    Message update(Message body, CoreContext context);
    void deleteById(UUID id, CoreContext context);
}
