package com.ayanlab.core.service.impl;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.data.enums.ActionEnum;
import com.ayanlab.core.entity.Invoice;
import com.ayanlab.core.entity.InvoiceLineItem;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.InvoiceLineItemRepository;
import com.ayanlab.core.repository.InvoiceRepository;
import com.ayanlab.core.service.IInvoiceLineItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.data.mapper.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class InvoiceLineItemService implements IInvoiceLineItemService {

    private final InvoiceLineItemRepository lineItemRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public InvoiceLineItem create(InvoiceLineItem item, CoreContext context) {
        assertInvoiceAccessible(item.getInvoiceId(), context);
        item = map(item, context.getUserId(), ActionEnum.CREATE);
        return lineItemRepository.save(item);
    }

    @Override
    @Transactional
    public InvoiceLineItem update(InvoiceLineItem item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "InvoiceLineItem id required");
        }
        InvoiceLineItem current = lineItemRepository.findById(item.getId())
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Invoice line item not found"));
        assertInvoiceAccessible(current.getInvoiceId(), context);
        if (item.getInvoiceId() == null) {
            item.setInvoiceId(current.getInvoiceId());
        }
        item = map(item, context.getUserId(), ActionEnum.UPDATE);
        return lineItemRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(InvoiceLineItem item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context.getUserId(), ActionEnum.DELETE);
        lineItemRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        InvoiceLineItem item = findById(id, context);
        if (item != null) {
            delete(item, context);
        }
    }

    @Override
    public InvoiceLineItem findById(UUID id, CoreContext context) {
        InvoiceLineItem item = lineItemRepository.findById(id).orElse(null);
        if (item == null) {
            return null;
        }
        assertInvoiceAccessible(item.getInvoiceId(), context);
        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceLineItem> listByInvoice(UUID invoiceId) {
        return lineItemRepository.findByInvoiceIdAndIsActiveTrueOrderBySortOrderAsc(invoiceId);
    }

    private void assertInvoiceAccessible(UUID invoiceId, CoreContext context) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new CoreException("NOT_FOUND", "Invoice not found"));
        if (context.getAppId() != null && !context.getAppId().equals(invoice.getAppId())) {
            throw new CoreException("FORBIDDEN", "Invoice does not belong to the current app");
        }
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        InvoiceLineItem found = findById(id, context);
        if (found != null) {
            lineItemRepository.delete(found);
        }
    }

    @Override
    @Transactional
    public InvoiceLineItem setActive(UUID id, boolean active, CoreContext context) {
        InvoiceLineItem found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "InvoiceLineItem not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return lineItemRepository.save(found);
    }
}
