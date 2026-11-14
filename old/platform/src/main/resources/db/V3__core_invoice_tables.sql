-- Reference DDL for core billing invoices (Hibernate ddl-auto may apply entities directly).

CREATE TABLE IF NOT EXISTS core."Invoice" (
    id                      UUID PRIMARY KEY,
    "appId"                 UUID NOT NULL,
    "organizationId"        UUID,
    "profileId"             UUID,
    "profileSubscriptionId" UUID,
    "invoiceNumber"         VARCHAR(64) NOT NULL,
    "customerName"          VARCHAR(255) NOT NULL,
    "customerEmail"         VARCHAR(320),
    "issueDate"             DATE NOT NULL,
    "dueDate"               DATE NOT NULL,
    "subtotalCents"         BIGINT,
    "taxAmountCents"        BIGINT,
    "totalAmountCents"      BIGINT NOT NULL,
    currency                VARCHAR(8) DEFAULT 'USD',
    status                  VARCHAR(32) NOT NULL,
    notes                   TEXT,
    "paidAt"                TIMESTAMPTZ,
    "externalReference"     VARCHAR(512),
    "createdAt"             TIMESTAMPTZ,
    "updatedAt"             TIMESTAMPTZ,
    "createdBy"             UUID,
    "updatedBy"             UUID,
    "isActive"              BOOLEAN DEFAULT TRUE,
    CONSTRAINT uq_invoice_app_number UNIQUE ("appId", "invoiceNumber")
);

CREATE INDEX IF NOT EXISTS idx_invoice_app ON core."Invoice" ("appId");
CREATE INDEX IF NOT EXISTS idx_invoice_profile ON core."Invoice" ("profileId");
CREATE INDEX IF NOT EXISTS idx_invoice_org ON core."Invoice" ("organizationId");

CREATE TABLE IF NOT EXISTS core."InvoiceLineItem" (
    id                  UUID PRIMARY KEY,
    "invoiceId"         UUID NOT NULL REFERENCES core."Invoice"(id),
    description         TEXT NOT NULL,
    quantity            NUMERIC(12, 4) NOT NULL DEFAULT 1,
    "unitAmountCents"   BIGINT NOT NULL,
    "amountCents"       BIGINT NOT NULL,
    "sortOrder"         INTEGER DEFAULT 0,
    "createdAt"         TIMESTAMPTZ,
    "updatedAt"         TIMESTAMPTZ,
    "createdBy"         UUID,
    "updatedBy"         UUID,
    "isActive"          BOOLEAN DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_invoice_line_invoice ON core."InvoiceLineItem" ("invoiceId");
