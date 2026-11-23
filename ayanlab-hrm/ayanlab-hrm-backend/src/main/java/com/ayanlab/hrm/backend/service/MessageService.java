package com.ayanlab.hrm.backend.service;

import com.ayanlab.hrm.backend.entity.Message;
import com.ayanlab.core.data.CoreContext;
import java.util.List;

public interface MessageService {
    List<Message> findAll(CoreContext context);
    Message findById(String id, CoreContext context);
    Message create(Message body, CoreContext context);
    Message update(Message body, CoreContext context);
    void deleteById(String id, CoreContext context);
}
