package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Message;
import com.ayanlab.hrm.backend.repository.MessageRepository;
import com.ayanlab.hrm.backend.service.MessageService;
import com.ayanlab.hrm.backend.service.platform.HrmPlatformAuthorizationService;
import com.ayanlab.hrm.backend.util.HrmProfileContextSupport;
import com.ayanlab.core.dto.CoreContext;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private static final String PERM_READ = "staffiq:inbox:read";
    private static final String PERM_CREATE = "staffiq:inbox:create";
    private static final String PERM_UPDATE = "staffiq:inbox:update";
    private static final String PERM_DELETE = "staffiq:inbox:delete";

    private final MessageRepository repository;
    private final HrmPlatformAuthorizationService platformAuthorization;


    @Override
    public List<Message> findAll(CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        return repository.findAll();
    }

    @Override
    public Message findById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_READ);
        Message row = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        HrmProfileContextSupport.assertReadable(row, context);
        return row;
    }

    @Override
    @Transactional
    public Message create(Message body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_CREATE);
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public Message update(Message body, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_UPDATE);
        Message existing = repository
                .findById(body.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        HrmProfileContextSupport.stampFromContext(body, context);
        return repository.save(body);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        platformAuthorization.requirePermission(context, PERM_DELETE);
        Message existing = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        HrmProfileContextSupport.assertReadable(existing, context);
        repository.deleteById(id);
    }
}
