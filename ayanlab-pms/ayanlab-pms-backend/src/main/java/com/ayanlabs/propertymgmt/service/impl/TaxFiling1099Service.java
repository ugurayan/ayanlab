package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.propertymgmt.entity.accounting.TaxFiling1099;
import com.ayanlabs.propertymgmt.repository.TaxFiling1099Repository;
import com.ayanlabs.propertymgmt.service.ITaxFiling1099Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaxFiling1099Service extends AbstractIpmsOrgService<TaxFiling1099, TaxFiling1099Repository> implements ITaxFiling1099Service {

    public TaxFiling1099Service(TaxFiling1099Repository repository) {
        super(repository);
    }

    @Override
    protected String entityLabel() {
        return "TaxFiling1099";
    }
}
