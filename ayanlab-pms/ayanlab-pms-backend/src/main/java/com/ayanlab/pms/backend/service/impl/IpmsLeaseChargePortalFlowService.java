package com.ayanlab.pms.backend.service.impl;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.exception.CoreException;
import com.ayanlab.core.util.IdGenerator;
import com.ayanlab.pms.backend.data.enums.IpmsTransactionStatusEnum;
import com.ayanlab.pms.backend.data.enums.IpmsTransactionTypeEnum;
import com.ayanlab.pms.backend.data.enums.LeaseStatusEnum;
import com.ayanlab.pms.backend.data.enums.OutstandingBalanceStatusEnum;
import com.ayanlab.pms.backend.data.responses.LeaseChargePortalResult;
import com.ayanlab.pms.backend.entity.accounting.IpmsTransaction;
import com.ayanlab.pms.backend.entity.leasing.Lease;
import com.ayanlab.pms.backend.entity.rentals.OutstandingBalance;
import com.ayanlab.pms.backend.repository.IpmsTransactionRepository;
import com.ayanlab.pms.backend.repository.LeaseRepository;
import com.ayanlab.pms.backend.repository.OutstandingBalanceRepository;
import com.ayanlab.pms.backend.service.IIpmsLeaseChargePortalFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IpmsLeaseChargePortalFlowService implements IIpmsLeaseChargePortalFlowService {

    private final LeaseRepository leaseRepository;
    private final IpmsTransactionRepository transactionRepository;
    private final OutstandingBalanceRepository outstandingBalanceRepository;

    @Override
    @Transactional
    public LeaseChargePortalResult execute(CoreContext context, UUID leaseId, Long chargeAmountCents) {
        UUID orgId = requireOrgId(context);
        Lease lease = leaseRepository.findByIdAndOrganizationId(leaseId, orgId)
                .orElseThrow(() -> new CoreException("NOT_FOUND", "Lease not found"));

        if (lease.getStatus() == LeaseStatusEnum.DRAFT) {
            lease.setStatus(LeaseStatusEnum.ACTIVE);
            lease.setUpdatedAt(Instant.now());
            leaseRepository.save(lease);
        }

        long amount = chargeAmountCents != null && chargeAmountCents > 0
                ? chargeAmountCents
                : (lease.getMonthlyRentCents() == null ? 0L : lease.getMonthlyRentCents());
        if (amount <= 0) {
            throw new CoreException("INVALID_ARGUMENT", "chargeAmountCents required when lease has no rent");
        }

        IpmsTransaction charge = IpmsTransaction.builder()
                .organizationId(orgId)
                .propertyId(lease.getPropertyId())
                .type(IpmsTransactionTypeEnum.INCOME)
                .amountCents(amount)
                .description("Rent charge — flow B2.4")
                .transactionDate(LocalDate.now())
                .status(IpmsTransactionStatusEnum.COMPLETED)
                .build();
        charge.setIsActive(true);
        charge.setCreatedAt(Instant.now());
        charge.setUpdatedAt(Instant.now());
        IdGenerator.assignIfAbsent(charge);
        transactionRepository.save(charge);

        OutstandingBalance balance = OutstandingBalance.builder()
                .organizationId(orgId)
                .leaseId(leaseId)
                .tenantProfileId(lease.getTenantProfileId())
                .amountDueCents(amount)
                .amountPaidCents(0L)
                .dueDate(LocalDate.now())
                .status(OutstandingBalanceStatusEnum.OPEN)
                .description("Portal balance — lease " + leaseId)
                .build();
        balance.setIsActive(true);
        balance.setCreatedAt(Instant.now());
        balance.setUpdatedAt(Instant.now());
        IdGenerator.assignIfAbsent(balance);
        outstandingBalanceRepository.save(balance);

        return new LeaseChargePortalResult(
                leaseId,
                charge.getId(),
                balance.getId(),
                amount,
                amount,
                "PORTAL_VISIBLE"
        );
    }

    private UUID requireOrgId(CoreContext context) {
        if (context.getOrgId() == null) {
            throw new CoreException("INVALID_ARGUMENT", "orgId required");
        }
        return context.getOrgId();
    }
}