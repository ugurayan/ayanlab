package com.ayanlab.core.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.enums.InvoiceStatusEnum;
import com.ayanlab.core.entity.Invoice;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.InvoiceRepository;
import com.ayanlab.core.service.IInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.mapping.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class InvoiceService implements IInvoiceService {

    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public Invoice create(Invoice item, CoreContext context) {
        if (item.getAppId() == null) {
            item.setAppId(context.getAppId());
        }
        if (item.getProfileId() == null && context.getProfileId() != null) {
            item.setProfileId(context.getProfileId());
        }
        if (item.getProfileId() == null) {
            throw new CoreException("VALIDATION_ERROR", "profileId required (body or X-Profile-Id)");
        }
        assertAppScope(item.getAppId(), context);
        assertUniqueInvoiceNumber(item.getAppId(), item.getInvoiceNumber(), null);
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return invoiceRepository.save(item);
    }

    @Override
    @Transactional
    public Invoice update(Invoice item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "Invoice id required");
        }
        Invoice current = invoiceRepository.findById(item.getId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Invoice not found"));
        assertAppScope(current.getAppId(), context);
        if (item.getAppId() == null) {
            item.setAppId(current.getAppId());
        }
        if (item.getInvoiceNumber() != null && !item.getInvoiceNumber().equals(current.getInvoiceNumber())) {
            assertUniqueInvoiceNumber(item.getAppId(), item.getInvoiceNumber(), item.getId());
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return invoiceRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(Invoice item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        invoiceRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        Invoice invoice = findById(id, context);
        if (invoice != null) {
            delete(invoice, context);
        }
    }

    @Override
    public Invoice findById(UUID id, CoreContext context) {
        Invoice invoice = invoiceRepository.findById(id).orElse(null);
        if (invoice == null) {
            return null;
        }
        assertAppScope(invoice.getAppId(), context);
        return invoice;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> listByApp(UUID appId, InvoiceStatusEnum status) {
        if (status == null) {
            return invoiceRepository.findByAppIdOrderByIssueDateDesc(appId);
        }
        return invoiceRepository.findByAppIdAndStatusOrderByDueDateAsc(appId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> listByProfile(UUID profileId) {
        return invoiceRepository.findByProfileIdOrderByIssueDateDesc(profileId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> listByOrganization(UUID organizationId) {
        return invoiceRepository.findByOrganizationIdOrderByIssueDateDesc(organizationId);
    }

    private void assertAppScope(UUID invoiceAppId, CoreContext context) {
        if (context.getAppId() != null && invoiceAppId != null && !context.getAppId().equals(invoiceAppId)) {
            throw new CoreException("FORBIDDEN", "Invoice does not belong to the current app");
        }
    }

    private void assertUniqueInvoiceNumber(UUID appId, String invoiceNumber, UUID excludeId) {
        invoiceRepository.findByAppIdAndInvoiceNumber(appId, invoiceNumber).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new CoreException("CONFLICT", "Invoice number already exists for this app");
            }
        });
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        Invoice found = findById(id, context);
        if (found != null) {
            invoiceRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public Invoice setActive(UUID id, boolean active, CoreContext context) {
        Invoice found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "Invoice not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return invoiceRepository.save(found);
    }
}
