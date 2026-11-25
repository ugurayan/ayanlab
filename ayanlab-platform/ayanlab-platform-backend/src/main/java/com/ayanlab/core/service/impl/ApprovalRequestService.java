package com.ayanlab.core.service.impl;

import com.ayanlab.core.dto.CoreContext;
import com.ayanlab.core.enums.ActionEnum;
import com.ayanlab.core.enums.ApprovalStatusEnum;
import com.ayanlab.core.enums.ApprovalSubjectTypeEnum;
import com.ayanlab.core.dto.request.CreateApprovalRequest;
import com.ayanlab.core.entity.ApprovalRequest;
import com.ayanlab.core.entity.Organization;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.repository.ApprovalRequestRepository;
import com.ayanlab.core.repository.OrganizationRepository;
import com.ayanlab.core.service.IApprovalRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.ayanlab.core.mapping.BaseMapper.map;

@Service
@RequiredArgsConstructor
public class ApprovalRequestService implements IApprovalRequestService {

    private final ApprovalRequestRepository repository;
    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public ApprovalRequest create(ApprovalRequest item, CoreContext context) {
        item = map(item, context != null ? context.getUserId() : null, ActionEnum.CREATE);
        return repository.save(item);
    }

    @Override
    @Transactional
    public ApprovalRequest update(ApprovalRequest item, CoreContext context) {
        if (item.getId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "ApprovalRequest id required");
        }
        ApprovalRequest current = findById(item.getId(), context);
        if (current == null) {
            throw new CoreException("NOT_FOUND", "ApprovalRequest not found");
        }
        item = map(item, context != null ? context.getUserId() : null, ActionEnum.UPDATE);
        return repository.save(item);
    }

    @Override
    @Transactional
    public void delete(ApprovalRequest item, CoreContext context) {
        item.setIsActive(false);
        item = map(item, context != null ? context.getUserId() : null, ActionEnum.DELETE);
        repository.save(item);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, CoreContext context) {
        ApprovalRequest row = findById(id, context);
        if (row != null) {
            delete(row, context);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApprovalRequest findById(UUID id, CoreContext context) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public ApprovalRequest createRequest(CreateApprovalRequest request, CoreContext context) {
        ApprovalRequest row = ApprovalRequest.builder()
                .subjectType(request.subjectType())
                .subjectId(request.subjectId())
                .organizationId(request.organizationId())
                .approvalType(request.approvalType())
                .status(ApprovalStatusEnum.PENDING)
                .requestedByUserId(context != null ? context.getUserId() : null)
                .requestedAt(Instant.now())
                .decisionNote(request.decisionNote())
                .build();
        return create(row, context != null ? context : CoreContext.builder().build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalRequest> list(UUID organizationId, ApprovalSubjectTypeEnum subjectType, UUID subjectId, ApprovalStatusEnum status) {
        if (subjectType != null && subjectId != null && status != null) {
            return repository.findBySubjectTypeAndSubjectIdAndStatusAndIsActiveTrueOrderByUpdatedAtDesc(subjectType, subjectId, status);
        }
        if (subjectType != null && subjectId != null) {
            return repository.findBySubjectTypeAndSubjectIdAndIsActiveTrueOrderByUpdatedAtDesc(subjectType, subjectId);
        }
        if (organizationId != null && status != null) {
            return repository.findByOrganizationIdAndStatusAndIsActiveTrueOrderByUpdatedAtDesc(organizationId, status);
        }
        if (organizationId != null) {
            return repository.findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(organizationId);
        }
        if (status != null) {
            return repository.findByStatusAndIsActiveTrueOrderByUpdatedAtDesc(status);
        }
        return repository.findAll();
    }

    @Override
    @Transactional
    public ApprovalRequest approve(UUID id, String note, CoreContext context) {
        ApprovalRequest row = requireRequest(id);
        row.setStatus(ApprovalStatusEnum.APPROVED);
        row.setDecisionNote(note);
        row.setDecidedByUserId(context != null ? context.getUserId() : null);
        row.setDecidedAt(Instant.now());
        row = update(row, context != null ? context : CoreContext.builder().build());
        updateOrganizationActivation(row.getOrganizationId());
        return row;
    }

    @Override
    @Transactional
    public ApprovalRequest reject(UUID id, String note, CoreContext context) {
        ApprovalRequest row = requireRequest(id);
        row.setStatus(ApprovalStatusEnum.REJECTED);
        row.setDecisionNote(note);
        row.setDecidedByUserId(context != null ? context.getUserId() : null);
        row.setDecidedAt(Instant.now());
        row = update(row, context != null ? context : CoreContext.builder().build());
        deactivateOrganization(row.getOrganizationId());
        return row;
    }

    @Override
    @Transactional
    public ApprovalRequest changesRequested(UUID id, String note, CoreContext context) {
        ApprovalRequest row = requireRequest(id);
        row.setStatus(ApprovalStatusEnum.CHANGES_REQUESTED);
        row.setDecisionNote(note);
        row.setDecidedByUserId(context != null ? context.getUserId() : null);
        row.setDecidedAt(Instant.now());
        row = update(row, context != null ? context : CoreContext.builder().build());
        deactivateOrganization(row.getOrganizationId());
        return row;
    }

    private ApprovalRequest requireRequest(UUID id) {
        ApprovalRequest row = repository.findById(id).orElse(null);
        if (row == null) {
            throw new CoreException("NOT_FOUND", "ApprovalRequest not found");
        }
        return row;
    }

    private void updateOrganizationActivation(UUID organizationId) {
        if (organizationId == null) {
            return;
        }
        List<ApprovalRequest> rows = repository.findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(organizationId);
        if (rows.isEmpty()) {
            return;
        }
        boolean hasRejected = rows.stream().anyMatch(r -> r.getStatus() == ApprovalStatusEnum.REJECTED || r.getStatus() == ApprovalStatusEnum.CHANGES_REQUESTED);
        boolean allApproved = rows.stream().allMatch(r -> r.getStatus() == ApprovalStatusEnum.APPROVED);
        if (hasRejected) {
            deactivateOrganization(organizationId);
            return;
        }
        if (allApproved) {
            setOrganizationActive(organizationId, true);
        }
    }

    private void deactivateOrganization(UUID organizationId) {
        if (organizationId == null) {
            return;
        }
        setOrganizationActive(organizationId, false);
    }

    private void setOrganizationActive(UUID organizationId, boolean active) {
        Organization organization = organizationRepository.findById(organizationId).orElse(null);
        if (organization == null) {
            return;
        }
        organization.setIsActive(active);
        organizationRepository.save(organization);
    }

    @Override
    @Transactional
    public void hardDeleteById(UUID id, CoreContext context) {
        ApprovalRequest found = findById(id, context);
        if (found != null) {
            repository.delete(found);
        }
    }

    @Override
    @Transactional
    public ApprovalRequest setActive(UUID id, boolean active, CoreContext context) {
        ApprovalRequest found = findById(id, context);
        if (found == null) {
            throw new CoreException("NOT_FOUND", "ApprovalRequest not found");
        }
        found.setIsActive(active);
        found = map(found, context.getUserId(), ActionEnum.UPDATE);
        found.setIsActive(active);
        return repository.save(found);
    }
}
