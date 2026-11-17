package com.ayanlabs.core.controller;

import com.ayanlabs.core.condition.CoreBusinessApi;
import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.enums.DocumentTypeEnum;
import com.ayanlabs.core.data.enums.EncryptionTypeEnum;
import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.entity.CloudFile;
import com.ayanlabs.core.service.ICloudFileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@CoreBusinessApi
@RestController
@RequestMapping("/core/api/files")
@Tag(name = "Core · CloudFileController", description = "GCP-backed file upload and signed-url APIs")
@RequiredArgsConstructor
public class CloudFileController extends BaseController {

    private static final String BASE_PATH = "/core/api/files";

    private final ICloudFileService cloudFileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CloudFile>> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder,
            @RequestParam(value = "entityId", required = false) UUID entityId,
            @RequestParam(value = "entityName", required = false) String entityName,
            @RequestParam(value = "documentType", required = false) DocumentTypeEnum documentType,
            @RequestParam(value = "approvalNeeded", required = false) Boolean approvalNeeded,
            @RequestParam(value = "approvalDescription", required = false) String approvalDescription,
            @RequestParam(value = "approvalReferenceId", required = false) String approvalReferenceId,
            @RequestParam(value = "isEncrypted", required = false) Boolean isEncrypted,
            @RequestParam(value = "encryptionType", required = false) EncryptionTypeEnum encryptionType,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("folder", folder);
        params.put("entityId", entityId);
        params.put("entityName", entityName);
        params.put("documentType", documentType);
        params.put("approvalNeeded", approvalNeeded);
        TracedCall call = tracedCall(log, "upload-cloud-file", "POST", BASE_PATH + "/upload", headers, null, params);
        try {
            CloudFile data = cloudFileService.upload(file, folder, entityId, entityName, documentType, approvalNeeded, approvalDescription,
                    approvalReferenceId, isEncrypted, encryptionType, call.context());
            call.trace().success(Map.of("fileId", data.getId(), "success", true));
            return createdBody(data, "File uploaded", call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<CloudFile>>> list(@ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "list-cloud-files", "GET", BASE_PATH, headers, null, null);
        try {
            List<CloudFile> data = cloudFileService.listByApp(call.context());
            call.trace().success(Map.of("count", data.size(), "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}/signed-url/read", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> signedReadUrl(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signed-read-url", "GET", BASE_PATH + "/{id}/signed-url/read", headers, null, Map.of("id", id));
        try {
            String url = cloudFileService.createReadSignedUrl(id, call.context());
            call.trace().success(Map.of("id", id, "success", true));
            return okBody(Map.of("url", url), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @GetMapping(value = "/{id}/signed-url/write", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> signedWriteUrl(
            @PathVariable UUID id,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "signed-write-url", "GET", BASE_PATH + "/{id}/signed-url/write", headers, null, Map.of("id", id));
        try {
            String url = cloudFileService.createWriteSignedUrl(id, call.context());
            call.trace().success(Map.of("id", id, "success", true));
            return okBody(Map.of("url", url), call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }

    @PostMapping(value = "/{id}/approve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<CloudFile>> approve(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, String> body,
            @ClientHeaders ClientHeadersPayload headers) {
        TracedCall call = tracedCall(log, "approve-cloud-file", "POST", BASE_PATH + "/{id}/approve", headers, body, Map.of("id", id));
        try {
            String description = body != null ? body.get("approvalDescription") : null;
            CloudFile data = cloudFileService.approve(id, description, call.context());
            call.trace().success(Map.of("id", id, "success", true));
            return ok(data, call.context());
        } catch (Exception e) {
            call.trace().error(e);
            throw e;
        }
    }
}

