package com.ayanlab.pms.backend.controller;

import com.ayanlab.core.annotation.ClientHeaders;
import com.ayanlab.core.data.ClientHeadersPayload;
import com.ayanlab.core.data.responses.ApiResponse;
import com.ayanlab.pms.backend.entity.settings.OrgWhatsappChannelSetting;
import com.ayanlab.pms.backend.service.IOrgWhatsappChannelSettingService;
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
@RequestMapping("/pms/api/org-whatsapp-settings")
@Tag(name = "IPMS · OrgWhatsappChannelSettingController", description = "Org WhatsApp channel (B1)")
@RequiredArgsConstructor
public class OrgWhatsappChannelSettingController extends IpmsCrudController<OrgWhatsappChannelSetting> {

    private static final String BASE_PATH = "/pms/api/org-whatsapp-settings";
    private final IOrgWhatsappChannelSettingService orgWhatsappChannelSettingService;

    @Override
    protected IOrgWhatsappChannelSettingService service() {
        return orgWhatsappChannelSettingService;
    }

    @Override
    protected String basePath() {
        return BASE_PATH;
    }

    @Override
    protected String operationPrefix() {
        return "org-whatsapp-settings";
    }

    @Override
    protected String entityLabel() {
        return "OrgWhatsappChannelSetting";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<List<OrgWhatsappChannelSetting>>> list(@ClientHeaders ClientHeadersPayload headers) {
        return executeList(headers, Map.of(), service()::findAll);
    }
}
