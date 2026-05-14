package com.ayanlabs.propertymgmt.controller;

import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.annotation.RequireAppRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/ayanlabs/api/property-mgmt")
@RequiredArgsConstructor
public class PropertyController {

    @GetMapping("/properties")
    @RequireAppRole({"PROPERTY_MANAGER", "AGENT", "HOMEOWNER"})
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getProperties() {
        // TODO: implement with PropertyService
        return ResponseEntity.ok(ApiResponse.ok(List.of(), "Properties endpoint ready"));
    }

    @PostMapping("/properties")
    @RequireAppRole({"PROPERTY_MANAGER", "HOMEOWNER"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> createProperty(@RequestBody Map<String, Object> request) {
        // TODO: implement
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(request, "Property created"));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.ok("Property Management module is running"));
    }
}
