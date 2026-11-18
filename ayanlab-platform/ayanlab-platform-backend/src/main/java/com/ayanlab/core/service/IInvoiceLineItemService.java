package com.ayanlab.core.service;

import com.ayanlab.core.entity.InvoiceLineItem;

import java.util.List;
import java.util.UUID;

public interface IInvoiceLineItemService extends IBaseService<InvoiceLineItem> {

    List<InvoiceLineItem> listByInvoice(UUID invoiceId);
}
