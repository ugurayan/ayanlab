# IPMS Full Menu CRUD Wiring (Option 3)

## Goal
Wire every authenticated menu page to real `/ayanlabs/api/property-mgmt/*` CRUD; add missing entities for menu gaps; retire mock-only list/create flows.

## Approach
1. Retarget FE `api.js` → `ayanlabsClient` + property-mgmt paths.
2. Shared `IpmsResourcePage` for list/create/delete against thin CRUD APIs.
3. Generate missing BE entity+repo+service+controller stacks (Hibernate `ddl-auto: update`).
4. Point each menu page at the matching resource API.
5. Smoke-test list/create/delete with an org-scoped JWT.

## Progress (2026-07-11)

### Completed
1. FE `ipmsResourceApi.js` + retargeted `api.js` → `/ayanlabs/api/property-mgmt/*`
2. Shared `IpmsResourcePage` (list / create / delete)
3. Wired ~40 menu pages off mocks onto live APIs
4. Added 15 missing BE entity stacks (inspections, recurring tasks, budgets, COA, SMS, mailings, reports, addons, …)
5. Linked `admin@ipms.local` → `DEMO_ORG` (JWT `oid`)
6. Raised `server.max-http-request-header-size` to 64KB (9KB JWT was causing Tomcat 400)
7. Smoke test: **12/12** list+create+delete OK (properties/vendors/tenants/announcements/tasks/budgets/inspections/messages/SMS/associations/bank/bills)
8. **Follow-up (2026-07-11):** wired remaining mock pages + 9 new BE stacks
   - `public-site-settings`, `resident-center-users/contacts`, `automated-email-settings`, `app-notifications`
   - `org-settings`, `data-imports`, `data-exports`, `subscription-accounts`
   - Header Messages → `messages`; Notifications → `app-notifications`
   - Settings org save → `org-settings`; Authorization → `authorization/context`
9. Full smoke: **51/51** list OK; create+delete OK for all except `property-types` create (admin-role gated — list OK)
10. **Authorization admin (2026-07-12):** IPMS façade over core RBAC
    - `GET/PUT /authorization/me`, `GET /authorization/users`, `PUT /users/{profileId}`, `POST .../roles`
    - `GET/POST /authorization/roles`, `GET /permissions`, `GET /roles/{id}/permissions`
    - FE AuthorizationPage, Profile, My Settings, Settings password/app prefs live
    - Dashboard widgets remain mock (by request)

### Still thin / UI-only
- Dashboard widgets still use `mockDashboardData` — hybrid wiring backlog: [2026-07-12 dashboard widgets](./2026-07-12-ipms-dashboard-widgets-hybrid-wiring.md)
- Notification prefs on My Settings are localStorage (no dedicated prefs entity yet)
- Email/phone OTP verify buttons on Profile point users to real verification/upload flows (no fake auto-verify)
- Domain logic remains stub (persist only)

### Backlog (deferred)
1. **Role-based demo accounts (real API, not FE mock)**
   - Seed users per role (PM admin, tenant, owner, vendor, …) on `DEMO_ORG`
   - Seed sample CRUD rows for demos
   - Login page “Try demo” cards → real vault login (no `USE_MOCK_AUTH` for this path)
   - Keep dashboard widgets mock if desired
   - Optional isolated offline demo build later via `REACT_APP_USE_MOCK_AUTH` — do not mix into daily API path
