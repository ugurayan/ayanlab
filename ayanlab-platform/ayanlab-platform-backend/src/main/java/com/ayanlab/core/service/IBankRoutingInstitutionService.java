package com.ayanlab.core.service;

import com.ayanlab.core.entity.BankRoutingInstitution;

import java.util.List;

public interface IBankRoutingInstitutionService {

    BankRoutingInstitution lookupByRoutingNumber(String routingNumber);

    List<BankRoutingInstitution> search(String query);
}