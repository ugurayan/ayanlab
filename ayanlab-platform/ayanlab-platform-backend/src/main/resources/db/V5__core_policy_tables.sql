-- Reference DDL for core policy authorization tables.

ALTER TABLE IF EXISTS core."Permission"
    ADD COLUMN IF NOT EXISTS "appCode" VARCHAR(64),
    ADD COLUMN IF NOT EXISTS module VARCHAR(64),
    ADD COLUMN IF NOT EXISTS resource VARCHAR(64),
    ADD COLUMN IF NOT EXISTS action VARCHAR(32);

ALTER TABLE IF EXISTS core."ProfilePermission"
    ADD COLUMN IF NOT EXISTS effect VARCHAR(16) NOT NULL DEFAULT 'GRANT';

CREATE TABLE IF NOT EXISTS core."PolicyGroup" (
    id              UUID PRIMARY KEY,
    "appId"         UUID NOT NULL,
    code            VARCHAR(128) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    description     TEXT,
    "createdAt"     TIMESTAMPTZ,
    "updatedAt"     TIMESTAMPTZ,
    "createdBy"     UUID,
    "updatedBy"     UUID,
    "isActive"      BOOLEAN DEFAULT TRUE,
    CONSTRAINT uq_policy_group_app_code UNIQUE ("appId", code)
);

CREATE INDEX IF NOT EXISTS idx_policy_group_app ON core."PolicyGroup" ("appId");

CREATE TABLE IF NOT EXISTS core."PolicyGroupPermission" (
    id              UUID PRIMARY KEY,
    "policyGroupId" UUID NOT NULL,
    "permissionId"  UUID NOT NULL,
    "createdAt"     TIMESTAMPTZ,
    "updatedAt"     TIMESTAMPTZ,
    "createdBy"     UUID,
    "updatedBy"     UUID,
    "isActive"      BOOLEAN DEFAULT TRUE,
    CONSTRAINT uq_policy_group_permission UNIQUE ("policyGroupId", "permissionId")
);

CREATE INDEX IF NOT EXISTS idx_policy_group_perm_group ON core."PolicyGroupPermission" ("policyGroupId");

CREATE TABLE IF NOT EXISTS core."RolePolicyGroup" (
    id              UUID PRIMARY KEY,
    "roleId"        UUID NOT NULL,
    "policyGroupId" UUID NOT NULL,
    "createdAt"     TIMESTAMPTZ,
    "updatedAt"     TIMESTAMPTZ,
    "createdBy"     UUID,
    "updatedBy"     UUID,
    "isActive"      BOOLEAN DEFAULT TRUE,
    CONSTRAINT uq_role_policy_group UNIQUE ("roleId", "policyGroupId")
);

CREATE INDEX IF NOT EXISTS idx_role_policy_group_role ON core."RolePolicyGroup" ("roleId");

CREATE TABLE IF NOT EXISTS core."ProfilePolicyGroup" (
    id              UUID PRIMARY KEY,
    "profileId"     UUID NOT NULL,
    "policyGroupId" UUID NOT NULL,
    "grantedAt"     TIMESTAMPTZ,
    "grantedBy"     UUID,
    "expiresAt"     TIMESTAMPTZ,
    "createdAt"     TIMESTAMPTZ,
    "updatedAt"     TIMESTAMPTZ,
    "createdBy"     UUID,
    "updatedBy"     UUID,
    "isActive"      BOOLEAN DEFAULT TRUE,
    CONSTRAINT uq_profile_policy_group UNIQUE ("profileId", "policyGroupId")
);

CREATE INDEX IF NOT EXISTS idx_profile_policy_group_profile ON core."ProfilePolicyGroup" ("profileId");

CREATE TABLE IF NOT EXISTS core."ProfileResourceScope" (
    id              UUID PRIMARY KEY,
    "profileId"     UUID NOT NULL,
    "organizationId" UUID,
    "resourceType"  VARCHAR(32) NOT NULL,
    "resourceId"    UUID NOT NULL,
    "createdAt"     TIMESTAMPTZ,
    "updatedAt"     TIMESTAMPTZ,
    "createdBy"     UUID,
    "updatedBy"     UUID,
    "isActive"      BOOLEAN DEFAULT TRUE,
    CONSTRAINT uq_profile_resource_scope UNIQUE ("profileId", "resourceType", "resourceId")
);

CREATE INDEX IF NOT EXISTS idx_profile_scope_profile ON core."ProfileResourceScope" ("profileId");
CREATE INDEX IF NOT EXISTS idx_profile_scope_org ON core."ProfileResourceScope" ("organizationId");
