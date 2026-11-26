package com.ayanlab.comm.backend.controller;

import com.ayanlab.comm.backend.dto.ContactDto;
import com.ayanlab.comm.backend.entity.Contact;
import com.ayanlab.comm.backend.entity.ContactList;
import com.ayanlab.comm.backend.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comm/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<Contact> createContact(
            @RequestHeader("X-Organization-Id") UUID organizationId,
            @RequestHeader("X-App-Id") UUID appId,
            @RequestBody ContactDto dto) {
        return ResponseEntity.ok(contactService.createContact(organizationId, appId, dto));
    }

    @PostMapping("/import-csv")
    public ResponseEntity<List<Contact>> importCsv(
            @RequestHeader("X-Organization-Id") UUID organizationId,
            @RequestHeader("X-App-Id") UUID appId,
            @RequestBody String csvContent) {
        return ResponseEntity.ok(contactService.importContactsFromCsv(organizationId, appId, csvContent));
    }

    @PostMapping("/lists")
    public ResponseEntity<ContactList> createList(
            @RequestHeader("X-Organization-Id") UUID organizationId,
            @RequestHeader("X-App-Id") UUID appId,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestBody List<UUID> contactIds) {
        return ResponseEntity.ok(contactService.createList(organizationId, appId, name, description, contactIds));
    }

    @GetMapping("/lists")
    public ResponseEntity<List<ContactList>> listContactLists(
            @RequestHeader("X-Organization-Id") UUID organizationId) {
        return ResponseEntity.ok(contactService.listContactLists(organizationId));
    }

    @GetMapping
    public ResponseEntity<List<Contact>> listContacts(
            @RequestHeader("X-Organization-Id") UUID organizationId) {
        return ResponseEntity.ok(contactService.listContacts(organizationId));
    }
}
