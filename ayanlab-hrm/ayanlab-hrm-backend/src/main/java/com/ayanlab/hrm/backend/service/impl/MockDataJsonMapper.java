package com.ayanlab.hrm.backend.service.impl;

import com.ayanlab.hrm.backend.entity.Document;
import com.ayanlab.hrm.backend.entity.DocumentVerification;
import com.ayanlab.hrm.backend.entity.FinanceRecord;
import com.ayanlab.hrm.backend.entity.Job;
import com.ayanlab.hrm.backend.entity.SystemAudit;
import com.ayanlab.hrm.backend.entity.Task;
import com.ayanlab.hrm.backend.entity.TransactionRecord;
import com.ayanlab.hrm.backend.entity.UserAudit;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.experimental.UtilityClass;

@UtilityClass
final class MockDataJsonMapper {

    static Job jobFrom(JsonNode n) {
        Job row = new Job();
        row.setId(text(n, "id"));
        row.setOrgId(textOrNull(n, "orgId"));
        row.setDetails(n);
        return row;
    }

    static Task taskFrom(JsonNode n) {
        Task row = new Task();
        row.setId(text(n, "id"));
        row.setOrgId(textOrNull(n, "orgId"));
        row.setWorkerId(textOrNull(n, "assignedWorkerId"));
        row.setCreatedAt(textOrNull(n, "createdAt"));
        row.setDetails(n);
        return row;
    }

    static Document documentFrom(JsonNode n) {
        Document row = new Document();
        row.setId(text(n, "id"));
        row.setWorkerId(textOrNull(n, "workerId"));
        row.setOrgId(textOrNull(n, "orgId"));
        row.setCreatedAt(textOrNull(n, "createdAt"));
        row.setDetails(n);
        return row;
    }

    static FinanceRecord financeFromInvoice(JsonNode n) {
        FinanceRecord row = new FinanceRecord();
        String id = text(n, "id");
        row.setId(id);
        row.setOrgId(textOrNull(n, "orgId"));
        row.setPlatformRecordId("platform:finance:" + id);
        row.setAmountCents(longOrNull(n, "amountCents"));
        row.setCurrency(textOrNull(n, "currency"));
        row.setDetails(n);
        return row;
    }

    static TransactionRecord transactionFrom(JsonNode n) {
        TransactionRecord row = new TransactionRecord();
        row.setId(text(n, "id"));
        row.setOrgId(textOrNull(n, "orgId"));
        row.setWorkerId(textOrNull(n, "workerId"));
        row.setCreatedAt(textOrNull(n, "createdAt"));
        row.setPlatformRecordId(
                textOrNull(n, "externalReferenceId") != null
                        ? n.path("externalReferenceId").asText()
                        : text(n, "id"));
        row.setAmountCents(longOrNull(n, "amountCents"));
        row.setCurrency(textOrNull(n, "currency"));
        row.setDetails(n);
        return row;
    }

    static UserAudit userAuditFrom(JsonNode n) {
        UserAudit row = new UserAudit();
        row.setId(text(n, "id"));
        row.setCreatedAt(textOrNull(n, "createdAt"));
        row.setDetails(n);
        return row;
    }

    static SystemAudit systemAuditFrom(JsonNode n) {
        SystemAudit row = new SystemAudit();
        row.setId(text(n, "id"));
        row.setCreatedAt(textOrNull(n, "createdAt"));
        row.setDetails(n);
        return row;
    }

    static DocumentVerification verificationFrom(JsonNode n) {
        DocumentVerification row = new DocumentVerification();
        row.setId(text(n, "id"));
        row.setWorkerId(textOrNull(n, "workerId"));
        row.setOrgId(textOrNull(n, "orgId"));
        row.setCreatedAt(textOrNull(n, "submittedAt"));
        row.setDetails(n);
        return row;
    }

    private static String text(JsonNode n, String field) {
        return n.path(field).asText();
    }

    private static String textOrNull(JsonNode n, String field) {
        JsonNode v = n.path(field);
        return v.isMissingNode() || v.isNull() || v.asText().isBlank() ? null : v.asText();
    }

    private static Long longOrNull(JsonNode n, String field) {
        JsonNode v = n.path(field);
        return v.isMissingNode() || v.isNull() || !v.isNumber() ? null : v.asLong();
    }
}