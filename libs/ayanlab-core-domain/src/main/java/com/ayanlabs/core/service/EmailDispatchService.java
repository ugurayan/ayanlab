package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.data.requests.SendTemplateEmailRequest;

public interface EmailDispatchService {

    void sendTemplateEmail(SendTemplateEmailRequest request, CoreContext context);
}

