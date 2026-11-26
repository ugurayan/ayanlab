package com.ayanlab.comm.backend.service.impl;

import com.ayanlab.comm.backend.dto.ContactDto;
import com.ayanlab.comm.backend.entity.Contact;
import com.ayanlab.comm.backend.entity.ContactList;
import com.ayanlab.comm.backend.repository.ContactListRepository;
import com.ayanlab.comm.backend.repository.ContactRepository;
import com.ayanlab.comm.backend.service.ContactService;
import com.ayanlab.comm.backend.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactListRepository contactListRepository;

    @Override
    @Transactional
    public Contact createContact(UUID organizationId, UUID appId, ContactDto dto) {
        log.info("Creating single contact: {} for organization: {}", dto.email(), organizationId);

        Optional<Contact> existing = contactRepository.findByOrganizationIdAndEmail(organizationId, dto.email());
        if (existing.isPresent()) {
            Contact contact = existing.get();
            contact.setFirstName(dto.firstName());
            contact.setLastName(dto.lastName());
            contact.setPreferredLanguage(dto.preferredLanguage());
            contact.setCustomAttributes(dto.customAttributes());
            contact.setStatus(dto.status() != null ? dto.status() : "SUBSCRIBED");
            return contactRepository.save(contact);
        }

        Contact contact = new Contact();
        contact.setOrganizationId(organizationId);
        contact.setAppId(appId);
        contact.setEmail(dto.email());
        contact.setFirstName(dto.firstName());
        contact.setLastName(dto.lastName());
        contact.setPreferredLanguage(dto.preferredLanguage() != null ? dto.preferredLanguage() : "en");
        contact.setCustomAttributes(dto.customAttributes() != null ? dto.customAttributes() : new HashMap<>());
        contact.setStatus("SUBSCRIBED");
        contact.setExternalId(dto.externalId());

        IdGenerator.assignIfAbsent(contact);
        return contactRepository.save(contact);
    }

    @Override
    @Transactional
    public List<Contact> importContactsFromCsv(UUID organizationId, UUID appId, String csvContent) {
        log.info("Importing contacts from CSV for organization: {}", organizationId);
        List<Contact> imported = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new StringReader(csvContent))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isHeader) {
                    isHeader = false; // Skip CSV headers: email,firstName,lastName,preferredLanguage,externalId
                    continue;
                }

                String[] tokens = line.split(",");
                if (tokens.length > 0) {
                    String email = tokens[0].trim();
                    String firstName = tokens.length > 1 ? tokens[1].trim() : "";
                    String lastName = tokens.length > 2 ? tokens[2].trim() : "";
                    String preferredLanguage = tokens.length > 3 ? tokens[3].trim() : "en";
                    String externalId = tokens.length > 4 ? tokens[4].trim() : null;

                    ContactDto dto = new ContactDto(null, email, firstName, lastName, preferredLanguage, new HashMap<>(), "SUBSCRIBED", externalId);
                    imported.add(createContact(organizationId, appId, dto));
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse CSV upload stream.", e);
            throw new RuntimeException("Error parsing CSV: " + e.getMessage(), e);
        }

        return imported;
    }

    @Override
    @Transactional
    public ContactList createList(UUID organizationId, UUID appId, String name, String description, List<UUID> contactIds) {
        log.info("Creating contact list: {} with {} contacts.", name, contactIds.size());

        ContactList contactList = new ContactList();
        contactList.setOrganizationId(organizationId);
        contactList.setAppId(appId);
        contactList.setName(name);
        contactList.setDescription(description);

        Set<Contact> members = new HashSet<>(contactRepository.findAllById(contactIds));
        contactList.setContacts(members);

        IdGenerator.assignIfAbsent(contactList);
        return contactListRepository.save(contactList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContactList> listContactLists(UUID organizationId) {
        return contactListRepository.findByOrganizationId(organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contact> listContacts(UUID organizationId) {
        return contactRepository.findByOrganizationId(organizationId);
    }
}
