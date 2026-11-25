package com.ayanlab.core.service;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.dto.request.SendTemplateEmailRequest;

public interface EmailDispatchService {

    void sendTemplateEmail(SendTemplateEmailRequest request, CoreContext context);
}

