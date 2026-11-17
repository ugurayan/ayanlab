-- Reference DDL for core payment / transaction billing (Hibernate ddl-auto may apply entities directly).

CREATE TABLE IF NOT EXISTS core."Transaction" (
    id                      UUID PRIMARY KEY,
    "appId"                 UUID NOT NULL,
    "idempotencyKey"        VARCHAR(128) NOT NULL,
    "profileId"             UUID,
    "organizationId"        UUID,
    "profileSubscriptionId" UUID,
    "amountCents"           BIGINT NOT NULL,
    currency                VARCHAR(8) DEFAULT 'USD',
    status                  VARCHAR(32) NOT NULL,
    provider                VARCHAR(32) NOT NULL,
    "externalTransactionId" VARCHAR(512),
    "failureReason"         TEXT,
    "createdAt"             TIMESTAMPTZ,
    "updatedAt"             TIMESTAMPTZ,
    "createdBy"             UUID,
    "updatedBy"             UUID,
    "isActive"              BOOLEAN DEFAULT TRUE,
    CONSTRAINT uq_transaction_app_idempotency UNIQUE ("appId", "idempotencyKey")
);

CREATE INDEX IF NOT EXISTS idx_transaction_app ON core."Transaction" ("appId");
CREATE INDEX IF NOT EXISTS idx_transaction_profile ON core."Transaction" ("profileId");
CREATE INDEX IF NOT EXISTS idx_transaction_status ON core."Transaction" ("appId", status);

CREATE TABLE IF NOT EXISTS core."Payment" (
    id                      UUID PRIMARY KEY,
    "appId"                 UUID NOT NULL,
    "transactionId"       UUID NOT NULL REFERENCES core."Transaction"(id),
    "invoiceId"             UUID REFERENCES core."Invoice"(id),
    "profileSubscriptionId" UUID,
    "amountCents"           BIGINT NOT NULL,
    currency                VARCHAR(8) DEFAULT 'USD',
    status                  VARCHAR(32) NOT NULL,
    "paymentMethod"         VARCHAR(32),
    "paidAt"                TIMESTAMPTZ,
    "externalPaymentId"     VARCHAR(512),
    "createdAt"             TIMESTAMPTZ,
    "updatedAt"             TIMESTAMPTZ,
    "createdBy"             UUID,
    "updatedBy"             UUID,
    "isActive"              BOOLEAN DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_payment_app ON core."Payment" ("appId");
CREATE INDEX IF NOT EXISTS idx_payment_invoice ON core."Payment" ("invoiceId");
CREATE INDEX IF NOT EXISTS idx_payment_transaction ON core."Payment" ("transactionId");
