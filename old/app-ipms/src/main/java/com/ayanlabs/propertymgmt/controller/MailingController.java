package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.propertymgmt.entity.communication.Mailing;
import com.ayanlabs.propertymgmt.service.IMailingService;
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
@RequestMapping("/ayanlabs/api/property-mgmt/mailings")
@Tag(name = "IPMS · MailingController", description = "Mailing CRUD")
@RequiredArgsConstructor
public class MailingController extends IpmsCrudController<Mailing> {

    private static final String BASE_PATH = "/ayanlabs/api/property-mgmt/mailings";
    private final IMailingService mailingService;

    @Override
    protected IMailingService service() {
        return mailingService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "mailings";
    }

    @Override
    protected String entityLabel() {
        return "Mailing";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Mailing>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
