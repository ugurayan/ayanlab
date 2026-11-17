package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.propertymgmt.entity.accounting.EftApproval;
import com.ayanlabs.propertymgmt.service.IEftApprovalService;
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
@RequestMapping("/pms/api/eft-approvals")
@Tag(name = "IPMS · EftApprovalController", description = "EftApproval CRUD")
@RequiredArgsConstructor
public class EftApprovalController extends IpmsCrudController<EftApproval> {

    private static final String BASE_PATH = "/pms/api/eft-approvals";
    private final IEftApprovalService eftApprovalService;

    @Override
    protected IEftApprovalService service() {
        return eftApprovalService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "eft-approvals";
    }

    @Override
    protected String entityLabel() {
        return "EftApproval";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<EftApproval>>> list(
            @ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
