package com.ayanlab.hrm.backend.mapping;

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
public final class MockDataJsonMapper {

    public static Job jobFrom(JsonNode n) {
        Job row = new Job();
        row.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
        row.setOrgId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "orgId")));
        row.setDetails(n);
        return row;
    }

    public static Task taskFrom(JsonNode n) {
        Task row = new Task();
        row.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
        row.setOrgId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "orgId")));
        row.setWorkerId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "assignedWorkerId")));
        row.setCreatedAt(textOrNull(n, "createdAt"));
        row.setDetails(n);
        return row;
    }

    public static Document documentFrom(JsonNode n) {
        Document row = new Document();
        row.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
        row.setWorkerId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "workerId")));
        row.setOrgId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "orgId")));
        row.setCreatedAt(textOrNull(n, "createdAt"));
        row.setDetails(n);
        return row;
    }

    public static FinanceRecord financeFromInvoice(JsonNode n) {
        FinanceRecord row = new FinanceRecord();
        java.util.UUID id = com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id"));
        row.setId(id);
        row.setOrgId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "orgId")));
        row.setPlatformRecordId(com.ayanlab.hrm.backend.util.Ids.parse("platform:finance:" + text(n, "id")));
        row.setAmountCents(longOrNull(n, "amountCents"));
        row.setCurrency(textOrNull(n, "currency"));
        row.setDetails(n);
        return row;
    }

    public static TransactionRecord transactionFrom(JsonNode n) {
        TransactionRecord row = new TransactionRecord();
        row.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
        row.setOrgId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "orgId")));
        row.setWorkerId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "workerId")));
        row.setCreatedAt(textOrNull(n, "createdAt"));
        row.setPlatformRecordId(com.ayanlab.hrm.backend.util.Ids.parse(
                textOrNull(n, "externalReferenceId") != null
                        ? n.path("externalReferenceId").asText()
                        : text(n, "id")));
        row.setAmountCents(longOrNull(n, "amountCents"));
        row.setCurrency(textOrNull(n, "currency"));
        row.setDetails(n);
        return row;
    }

    public static UserAudit userAuditFrom(JsonNode n) {
        UserAudit row = new UserAudit();
        row.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
        row.setCreatedAt(textOrNull(n, "createdAt"));
        row.setDetails(n);
        return row;
    }

    public static SystemAudit systemAuditFrom(JsonNode n) {
        SystemAudit row = new SystemAudit();
        row.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
        row.setCreatedAt(textOrNull(n, "createdAt"));
        row.setDetails(n);
        return row;
    }

    public static DocumentVerification verificationFrom(JsonNode n) {
        DocumentVerification row = new DocumentVerification();
        row.setId(com.ayanlab.hrm.backend.util.Ids.parse(text(n, "id")));
        row.setWorkerId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "workerId")));
        row.setOrgId(com.ayanlab.hrm.backend.util.Ids.parse(textOrNull(n, "orgId")));
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