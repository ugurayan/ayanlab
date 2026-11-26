package com.ayanlab.comm.backend.service;

import com.ayanlab.comm.backend.dto.ContactDto;
import com.ayanlab.comm.backend.entity.Contact;
import com.ayanlab.comm.backend.entity.ContactList;

import java.util.List;
import java.util.UUID;

public interface ContactService {
    Contact createContact(UUID organizationId, UUID appId, ContactDto dto);
    List<Contact> importContactsFromCsv(UUID organizationId, UUID appId, String csvContent);
    ContactList createList(UUID organizationId, UUID appId, String name, String description, List<UUID> contactIds);
    List<ContactList> listContactLists(UUID organizationId);
    List<Contact> listContacts(UUID organizationId);
}
