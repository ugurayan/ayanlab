package com.ayanlabs.core.service;

import com.ayanlabs.core.entity.InvoiceLineItem;

import java.util.List;
import java.util.UUID;

public interface IInvoiceLineItemService extends IBaseService<InvoiceLineItem> {

    List<InvoiceLineItem> listByInvoice(UUID invoiceId);
}
