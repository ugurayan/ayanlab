package com.ayanlabs.core.repository;

import com.ayanlabs.core.entity.InvoiceLineItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvoiceLineItemRepository extends JpaRepository<InvoiceLineItem, UUID> {

    List<InvoiceLineItem> findByInvoiceIdAndIsActiveTrueOrderBySortOrderAsc(UUID invoiceId);

    List<InvoiceLineItem> findByInvoiceIdOrderBySortOrderAsc(UUID invoiceId);
}
