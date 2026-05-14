package com.ayanlabs.accounting.controller;

import com.ayanlabs.core.annotation.RequireAppRole;
import com.ayanlabs.core.data.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/ayanlabs/api/accounting")
public class AccountingController {

    @GetMapping("/accounts")
    @RequireAppRole({"ACCOUNTANT", "ADMIN", "CLIENT"})
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAccounts() {
        return ResponseEntity.ok(ApiResponse.ok(List.of(), "Accounting endpoint ready"));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.ok("Accounting module is running"));
    }
}
