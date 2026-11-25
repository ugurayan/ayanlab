package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.dto.ClientHeadersPayload;
import com.ayanlab.core.dto.response.ApiResponse;
import com.ayanlab.pms.backend.entity.associations.CoopBoardPacket;
import com.ayanlab.pms.backend.service.ICoopBoardPacketService;
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
@RequestMapping("/pms/api/coop-board-packets")
@Tag(name = "IPMS · CoopBoardPacketController", description = "Co-op board packet (P5.9)")
@RequiredArgsConstructor
public class CoopBoardPacketController extends IpmsCrudController<CoopBoardPacket> {

    private static final String BASE_PATH = "/pms/api/coop-board-packets";
    private final ICoopBoardPacketService coopBoardPacketService;

    @Override
    protected ICoopBoardPacketService service() {
        return coopBoardPacketService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "coop-board-packets";
    }

    @Override
    protected String entityLabel() {
        return "CoopBoardPacket";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<CoopBoardPacket>>> list(@ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
