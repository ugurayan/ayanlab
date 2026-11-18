package com.ayanlab.pms.backend.service;

import com.ayanlab.core.data.CoreContext;
import java.util.List;
import java.util.UUID;

import java.util.UUID;

import com.ayanlab.pms.backend.entity.communication.Announcement;

public interface IAnnouncementService extends IIpmsOrgScopedService<Announcement> {

    List<Announcement> findAll(CoreContext context, UUID associationId);
}
