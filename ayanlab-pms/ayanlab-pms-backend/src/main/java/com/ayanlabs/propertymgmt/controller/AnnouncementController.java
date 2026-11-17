package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.propertymgmt.entity.communication.Announcement;
import com.ayanlabs.propertymgmt.service.IAnnouncementService;
import com.ayanlabs.core.annotation.ClientHeaders;
import com.ayanlabs.core.data.ClientHeadersPayload;
import com.ayanlabs.core.data.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pms/api/announcements")
@Tag(name = "IPMS · AnnouncementController", description = "Announcement management (Buildium-style CRUD)")
@RequiredArgsConstructor
public class AnnouncementController extends IpmsCrudController<Announcement> {

    private static final String BASE_PATH = "/pms/api/announcements";
    private final IAnnouncementService announcementService;

    @Override
    protected IAnnouncementService service() {
        return announcementService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "announcements";
    }

    @Override
    protected String entityLabel() {
        return "Announcement";
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<Announcement>>> list(
            @RequestParam(value = "associationId", required = false) UUID associationId,
            @ClientHeaders ClientHeadersPayload headers) {
        Map<String, Object> traceParams = new LinkedHashMap<>();
        traceParams.put("associationId", associationId);
        return executeList(headers, traceParams, ctx -> ((IAnnouncementService) service()).findAll(ctx, associationId));
    }

}
