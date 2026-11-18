package com.ayanlab.core.repository;

import com.ayanlab.core.data.enums.ApprovalStatusEnum;
import com.ayanlab.core.data.enums.ApprovalSubjectTypeEnum;
import com.ayanlab.core.entity.ApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, UUID> {

    List<ApprovalRequest> findBySubjectTypeAndSubjectIdAndIsActiveTrueOrderByUpdatedAtDesc(
            ApprovalSubjectTypeEnum subjectType, UUID subjectId);

    List<ApprovalRequest> findByOrganizationIdAndIsActiveTrueOrderByUpdatedAtDesc(UUID organizationId);

    List<ApprovalRequest> findByStatusAndIsActiveTrueOrderByUpdatedAtDesc(ApprovalStatusEnum status);

    List<ApprovalRequest> findBySubjectTypeAndSubjectIdAndStatusAndIsActiveTrueOrderByUpdatedAtDesc(
            ApprovalSubjectTypeEnum subjectType, UUID subjectId, ApprovalStatusEnum status);

    List<ApprovalRequest> findByOrganizationIdAndStatusAndIsActiveTrueOrderByUpdatedAtDesc(
            UUID organizationId, ApprovalStatusEnum status);
}
