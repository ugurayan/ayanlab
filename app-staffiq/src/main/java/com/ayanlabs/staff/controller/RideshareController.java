package com.ayanlabs.rideshare.controller;

import com.ayanlabs.core.data.responses.ApiResponse;
import com.ayanlabs.core.annotation.RequireAppRole;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/ayanlabs/api/rideshare")
public class RideshareController {

    @PostMapping("/rides/request")
    @RequireAppRole({"RIDER"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> requestRide(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(ApiResponse.ok(request, "Ride requested"));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.ok("Rideshare module is running"));
    }
}
