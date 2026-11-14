package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.propertymgmt.entity.files.StoredFile;
import com.ayanlabs.propertymgmt.service.IStoredFileService;
import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.enums.ApprovalSubjectTypeEnum;
import com.ayanlabs.core.data.enums.ApprovalTypeEnum;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.requests.CreateApprovalRequest;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.service.IApprovalRequestService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ayanlabs/api/property-mgmt/stored-files")
@Tag(name = "IPMS · StoredFileController", description = "StoredFile management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class StoredFileController extends IpmsCrudController<StoredFile> {

    private static final String BASE_PATH = "/ayanlabs/api/property-mgmt/stored-files";
    private final IStoredFileService storedFileService;
    private final IApprovalRequestService approvalRequestService;

    @Override
    protected IStoredFileService service() {
        return storedFileService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "stored-files";
    }

    @Override
    protected String entityLabel() {
        return "StoredFile";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<StoredFile>>> list(
            @RequestParam(value = "entityType", required = false) String entityType,
            @RequestParam(value = "entityId", required = false) UUID entityId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("entityType", entityType);
        traceParams.put("entityId", entityId);
        return executeList(headers, traceParams, ctx -> ((IStoredFileService) service()).findAll(ctx, entityType, entityId));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<StoredFile>> upload(
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") UUID entityId,
            @RequestParam("file") MultipartFile file,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("entityType", entityType);
        traceParams.put("entityId", entityId);
        TracedCall call = tracedCall(log, "stored-files-upload", "POST", BASE_PATH + "/upload", headers, null, traceParams);
        try {
            StoredFile uploaded = storedFileService.upload(call.context(), entityType, entityId, file);
            approvalRequestService.createRequest(new CreateApprovalRequest(
                    ApprovalSubjectTypeEnum.STORED_FILE,
                    uploaded.getId(),
                    uploaded.getOrganizationId(),
                    ApprovalTypeEnum.DOCUMENT,
                    "Document uploaded and queued for approval"
            ), call.context());
            call.trace().success(Map.of("storedFileId", uploaded.getId(), "success", true));
            return createdBody(uploaded, "File uploaded", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

}
