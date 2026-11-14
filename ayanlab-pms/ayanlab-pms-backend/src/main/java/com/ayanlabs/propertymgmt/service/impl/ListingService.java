package com.ayanlabs.propertymgmt.service.impl;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.leasing.Listing;
import com.ayanlabs.propertymgmt.repository.ListingRepository;
import com.ayanlabs.propertymgmt.service.IListingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ListingService extends AbstractIpmsOrgService<Listing, ListingRepository> implements IListingService {

    public ListingService(ListingRepository repository) {
        super(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Listing> findAll(CoreContext context, UUID propertyId) {
        UUID orgId = requireOrgId(context);
        if (propertyId != null) {
            return repository.findByOrganizationIdAndPropertyIdAndIsActiveTrueOrderByUpdatedAtDesc(orgId, propertyId);
        }
        return findAll(context);
    }

    @Override
    protected String entityLabel() {
        return "Listing";
    }
}
