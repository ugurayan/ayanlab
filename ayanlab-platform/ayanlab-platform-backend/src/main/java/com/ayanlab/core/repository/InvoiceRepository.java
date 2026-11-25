package com.ayanlab.core.repository;

import com.ayanlab.core.enums.InvoiceStatusEnum;
import com.ayanlab.core.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findByAppIdAndInvoiceNumber(UUID appId, String invoiceNumber);

    List<Invoice> findByAppIdOrderByIssueDateDesc(UUID appId);

    List<Invoice> findByProfileIdOrderByIssueDateDesc(UUID profileId);

    List<Invoice> findByOrganizationIdOrderByIssueDateDesc(UUID organizationId);

    List<Invoice> findByAppIdAndStatusOrderByDueDateAsc(UUID appId, InvoiceStatusEnum status);
}
