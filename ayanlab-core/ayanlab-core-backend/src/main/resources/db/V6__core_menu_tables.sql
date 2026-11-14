-- App-scoped navigation menus and role ↔ menu grants.

CREATE TABLE IF NOT EXISTS core."Menu" (
    id              UUID PRIMARY KEY,
    "appId"         UUID NOT NULL,
    code            VARCHAR(128) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    path            VARCHAR(512),
    "moduleCode"    VARCHAR(64),
    "parentCode"    VARCHAR(128),
    "sortOrder"     INT DEFAULT 0,
    "createdAt"     TIMESTAMPTZ,
    "updatedAt"     TIMESTAMPTZ,
    "createdBy"     UUID,
    "updatedBy"     UUID,
    "isActive"      BOOLEAN DEFAULT TRUE,
    CONSTRAINT uq_menu_app_code UNIQUE ("appId", code)
);

CREATE INDEX IF NOT EXISTS idx_menu_app ON core."Menu" ("appId");
CREATE INDEX IF NOT EXISTS idx_menu_module ON core."Menu" ("moduleCode");

CREATE TABLE IF NOT EXISTS core."RoleMenu" (
    id              UUID PRIMARY KEY,
    "roleId"        UUID NOT NULL,
    "menuId"        UUID NOT NULL,
    "createdAt"     TIMESTAMPTZ,
    "updatedAt"     TIMESTAMPTZ,
    "createdBy"     UUID,
    "updatedBy"     UUID,
    "isActive"      BOOLEAN DEFAULT TRUE,
    CONSTRAINT uq_role_menu UNIQUE ("roleId", "menuId")
);

CREATE INDEX IF NOT EXISTS idx_role_menu_role ON core."RoleMenu" ("roleId");
CREATE INDEX IF NOT EXISTS idx_role_menu_menu ON core."RoleMenu" ("menuId");
