package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.requests.SendTemplateEmailRequest;

public interface EmailDispatchService {

    void sendTemplateEmail(SendTemplateEmailRequest request, CoreContext context);
}

