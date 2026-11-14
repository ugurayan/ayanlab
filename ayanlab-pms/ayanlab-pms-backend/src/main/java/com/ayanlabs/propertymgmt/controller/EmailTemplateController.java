package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.propertymgmt.entity.communication.EmailTemplate;
import com.ayanlabs.propertymgmt.service.IEmailTemplateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ayanlabs/api/property-mgmt/email-templates")
@Tag(name = "IPMS · EmailTemplateController", description = "EmailTemplate CRUD")
@RequiredArgsConstructor
public class EmailTemplateController extends IpmsCrudController<EmailTemplate> {

    private static final String BASE_PATH = "/ayanlabs/api/property-mgmt/email-templates";
    private final IEmailTemplateService emailTemplateService;

    @Override
    protected IEmailTemplateService service() {
        return emailTemplateService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "email-templates";
    }

    @Override
    protected String entityLabel() {
        return "EmailTemplate";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<EmailTemplate>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
