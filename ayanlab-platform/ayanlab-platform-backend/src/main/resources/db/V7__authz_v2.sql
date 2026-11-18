-- Reference DDL for authorization v2 (applied by Hibernate ddl-auto + AuthorizationModelMigration).

ALTER TABLE IF EXISTS core."Role"
    ADD COLUMN IF NOT EXISTS "appId" UUID,
    ADD COLUMN IF NOT EXISTS "organizationId" UUID;

CREATE INDEX IF NOT EXISTS idx_role_app_code ON core."Role" ("appId", code);

-- Created by AuthorizationModelMigration after legacy rows are merged / split.
CREATE UNIQUE INDEX IF NOT EXISTS uq_permission_code ON core."Permission" (code);

CREATE UNIQUE INDEX IF NOT EXISTS uq_role_app_org_code ON core."Role"
    ("appId", COALESCE("organizationId", '00000000-0000-0000-0000-000000000000'::uuid), code)
    WHERE "appId" IS NOT NULL;
