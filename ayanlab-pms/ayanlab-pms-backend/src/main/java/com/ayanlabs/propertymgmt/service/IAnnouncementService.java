package com.ayanlabs.propertymgmt.service;

import com.ayanlabs.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlabs.propertymgmt.entity.communication.Announcement;

public interface IAnnouncementService extends IIpmsOrgScopedService<Announcement> {

    List<Announcement> findAll(CoreContext context, UUID associationId);
}
