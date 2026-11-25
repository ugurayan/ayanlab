package com.ayanlab.core.service;

import com.ayanlab.core.enums.InvoiceStatusEnum;
import com.ayanlab.core.entity.Invoice;

import java.util.List;
import java.util.UUID;

public interface IInvoiceService extends IBaseService<Invoice> {

    List<Invoice> listByApp(UUID appId, InvoiceStatusEnum status);

    List<Invoice> listByProfile(UUID profileId);

    List<Invoice> listByOrganization(UUID organizationId);
}
