# IPMS Dashboard Widgets — Hybrid Wiring Todo

**Status:** waiting (todo backlog)  
**Date:** 2026-07-12  
**Approach:** Hybrid — Phase A uses existing list CRUD + FE aggregation; Phase B adds thin summary / “me” APIs where needed; blocked widgets stay mock until models exist.

**Related:** [2026-07-11 IPMS full CRUD wiring](./2026-07-11-ipms-full-crud-wiring-design.md)  
**FE sources:** `ipms-web/src/data/dashboardConfig.js`, `mockDashboardData.js`, `DashboardWidgetRenderer.jsx`  
**API prefix:** `/ayanlabs/api/property-mgmt/*`

---

## Strategy (agreed)

| Step | What | When |
|------|------|------|
| A | Wire basic widgets to **existing** list APIs; FE aggregates | First |
| B | List widgets that need **new** APIs; implement step-by-step | After A |
| C | Keep **blocked** widgets on mock; discuss model work later | Parallel backlog |
| D | Seed DEMO_ORG sample data so live widgets are non-empty | With A/B |

---

## Master matrix (all 23 widgets)

| # | Widget ID | Title | Role(s) | Track | Status | Notes |
|---|-----------|-------|---------|-------|--------|-------|
| 1 | `portfolio_occupancy` | Portfolio Occupancy | PM Admin, PM | **A — existing API** | ✅ FE | Count `property-units` by status |
| 2 | `outstanding_balances` | Outstanding Balances | PM Admin, PM | **A — existing API** | ✅ FE | List open balances; enrich lease/unit label |
| 3 | `incoming_tasks` | Incoming Requests | PM Admin, PM | **A — existing API** | ✅ FE | `incoming-requests` recent/NEW |
| 4 | `overdue_tasks` | Overdue Tasks | PM Admin, PM | **A — existing API** | ✅ FE | `tasks` where dueDate &lt; today |
| 5 | `expiring_leases` | Lease Renewals | PM Admin, PM | **A — existing API** | ✅ FE | `leases` + `lease-renewals` buckets |
| 6 | `tenant_announcements` | Announcements | Tenant | **A — existing API** | ✅ FE | `announcements` PUBLISHED |
| 7 | `maintenance_pipeline` | Maintenance Pipeline | PM Admin, PM | **A — existing API** | ✅ FE | Group by status; taxonomy ≠ mock |
| 8 | `revenue_summary` | Revenue Summary | PM Admin | **B — new API** | ✅ | Month income + expected rent aggregate |
| 9 | `my_lease` | My Lease | Tenant | **B — new API** | ✅ | Profile-scoped lease + labels |
| 10 | `rent_payment` | Rent & Payments | Tenant | **B — new API** | ✅ | Balance + last payment for me |
| 11 | `tenant_maintenance` | My Maintenance | Tenant | **B — new API** | ✅ | Filter by `reportedByProfileId` |
| 12 | `platform_health` | Platform Health | System Admin | **B — new API** | ✅ | Org count + health; no real uptime |
| 13 | `platform_orgs` | Organizations | System Admin/User | **B — new API** | ✅ | Counts; company type may be thin |
| 14 | `platform_users` | User Directory | System Admin/User | **B — new API** | ✅ | Total / active today / new week |
| 15 | `vendor_work_orders` | Open Work Orders | Maint Org | **B — new API** | ✅ | Filter WO by vendor↔profile |
| 16 | `renters_insurance` | Renters Insurance | PM Admin, PM | **C — model+API** | ✅ | Insurance entity |
| 17 | `tenant_insurance` | Insurance Status | Tenant | **C — model+API** | ✅ | Insurance entity |
| 18 | `system_activity` | Recent Activity | System Admin | **C — model+API** | ✅ | Activity event log |
| 19 | `vendor_performance` | SLA Performance | Maint Org | **C — model+API** | ✅ | WO SLA timestamps |
| 20 | `vendor_staff` | Field Staff | Maint Org | **C — model+API** | ✅ | FieldStaffPresence |
| 21 | `staff_work_orders` | My Work Orders | Maint Staff | **C — model+API** | ✅ | WO assignee |
| 22 | `staff_schedule` | Today's Schedule | Maint Staff | **C — model+API** | ✅ | WO scheduledAt |
| 23 | `staff_completed` | Completed This Week | Maint Staff | **C — model+API** | ✅ | WO hoursLogged |

Legend: ⬜ todo · 🔄 in progress · ✅ done · ⏸ blocked / deferred

---

## Phase A — Existing APIs (FE aggregate)

Wire `DashboardWidgetRenderer` to live list clients; keep mock fallback when empty/error until seed lands.

| Widget ID | Existing API | FE client | Aggregation | API tweak needed? | Todo |
|-----------|--------------|-----------|-------------|-------------------|------|
| `portfolio_occupancy` | `GET …/property-units` | `propertyUnitsAPI` | Count OCCUPIED / VACANT (+other); rate | Optional later: server counts | ✅ |
| `outstanding_balances` | `GET …/outstanding-balances` | `outstandingBalancesAPI` | OPEN/PARTIAL; top N by remaining; map leaseId→label via `leases` / units | Optional: include label in list DTO | ✅ |
| `incoming_tasks` | `GET …/incoming-requests` | `incomingRequestsAPI` | Sort by receivedAt; filter NEW; take N | Priority / property not on entity — omit or N/A | ✅ |
| `overdue_tasks` | `GET …/tasks` | `tasksAPI` | dueDate &lt; today; status not DONE/CANCELLED; days overdue | Property address missing — show title only | ✅ |
| `expiring_leases` | `GET …/leases`, `GET …/lease-renewals` | `leasesAPI`, `leaseRenewalsAPI` | ACTIVE endDate buckets 30/60/90; OFFERED renewals count | None for MVP | ✅ |
| `tenant_announcements` | `GET …/announcements` | `announcementsAPI` | PUBLISHED; not expired; recent | Audience targeting N/A — org-wide OK | ✅ |
| `maintenance_pipeline` | `GET …/maintenance-requests` | `maintenanceAPI` | Group OPEN / IN_PROGRESS / COMPLETED / CANCELLED | Labels aligned to enum | ✅ |

### Phase A checklist

| # | Task | Status |
|---|------|--------|
| A1 | FE: shared dashboard data loader / hooks for org-scoped list APIs | ✅ |
| A2 | Wire 7 Phase A widgets; mock fallback on empty/error | ✅ |
| A3 | Align pipeline UI labels with `MaintenanceStatusEnum` | ✅ |
| A4 | Smoke with `admin@ipms.local` + DEMO_ORG (expect empty/sample until seed) | ⬜ |

**Phase A FE landed (2026-07-12):**
- `ipms-web/src/services/dashboardPhaseAAggregators.js`
- `ipms-web/src/hooks/usePhaseADashboardData.js`
- `ipms-web/src/context/DashboardLiveDataContext.jsx`
- `Dashboard.jsx` + `DashboardWidgetRenderer.jsx` wired
- Mock fallback shows **Sample** badge; header shows live count when applicable


---

## Phase B — New APIs required

Implement only after Phase A; prefer thin endpoints under `/ayanlabs/api/property-mgmt/dashboard/…` or resource `…/me` filters.

| Widget ID | Proposed endpoint (draft) | Why existing list is not enough | Depends on | Todo |
|-----------|---------------------------|---------------------------------|------------|------|
| `revenue_summary` | `GET …/dashboard/pm/revenue-summary` | Need month sum income + expected from ACTIVE leases | `IpmsTransaction`, `Lease` | ✅ |
| `my_lease` | `GET …/dashboard/tenant/my-lease` | Must scope by `tenantProfileId` + resolve unit/property labels | `Lease`, unit, property | ✅ |
| `rent_payment` | `GET …/dashboard/tenant/rent-payment` | Balance + last payment for current profile; autopay N/A | `OutstandingBalance` | ✅ |
| `tenant_maintenance` | `GET …/dashboard/tenant/maintenance` | List API has no reportedBy filter | `MaintenanceRequest` | ✅ |
| `platform_health` | `GET …/dashboard/platform/health` | Module health + org count; **no** real uptime/incidents | Core app-org links | ✅ |
| `platform_orgs` | `GET …/dashboard/platform/organizations` | Typed counts (PM vs maintenance) | App orgs + onboarding companyType | ✅ |
| `platform_users` | `GET …/dashboard/platform/users` | Aggregate total / activeToday / newThisWeek | Core `User` | ✅ |
| `vendor_work_orders` | `GET …/dashboard/vendor/work-orders` | Filter by vendor linked to current profile | `WorkOrder`, `Vendor.profileId` | ✅ |

### Phase B checklist

| # | Task | Status |
|---|------|--------|
| B0 | Agree DTO shapes + path convention (`/dashboard/{role}/…`) | ✅ |
| B1 | Implement PM `revenue-summary` + FE wire | ✅ |
| B2 | Implement tenant `my-lease` + `rent-payment` + FE wire | ✅ |
| B3 | Implement tenant maintenance `me` filter + FE wire | ✅ |
| B4 | Implement platform summary endpoints + FE wire | ✅ |
| B5 | Implement vendor work-orders mine filter + FE wire | ✅ |

**Phase B landed (2026-07-12):**
- BE: `DashboardController` → `/ayanlabs/api/property-mgmt/dashboard/*`
- BE: `IpmsDashboardService` + response DTOs
- FE: `dashboardApi.js` + hook/renderer wiring (Sample fallback)

| Endpoint | Widget |
|----------|--------|
| `GET …/dashboard/pm/revenue-summary` | `revenue_summary` |
| `GET …/dashboard/tenant/my-lease` | `my_lease` |
| `GET …/dashboard/tenant/rent-payment` | `rent_payment` |
| `GET …/dashboard/tenant/maintenance` | `tenant_maintenance` |
| `GET …/dashboard/platform/health` | `platform_health` |
| `GET …/dashboard/platform/organizations` | `platform_orgs` |
| `GET …/dashboard/platform/users` | `platform_users` |
| `GET …/dashboard/vendor/work-orders` | `vendor_work_orders` |

---

## Phase C — Model + APIs (implemented)

| Widget ID | Blocker (resolved) | Model / API | Status |
|-----------|--------------------|-------------|--------|
| `renters_insurance` | Insurance entity | `RentersInsurancePolicy` + `GET …/dashboard/pm/renters-insurance` + CRUD `…/renters-insurance` | ✅ |
| `tenant_insurance` | Same | `GET …/dashboard/tenant/insurance` | ✅ |
| `system_activity` | Audit/event store | `IpmsActivityEvent` + `GET …/dashboard/platform/activity` + CRUD `…/activity-events` | ✅ |
| `vendor_performance` | SLA fields | `WorkOrder.respondedAt` / `slaDueAt` + `GET …/dashboard/vendor/performance` | ✅ |
| `vendor_staff` | Roster / presence | `FieldStaffPresence` + `GET …/dashboard/vendor/staff` + CRUD `…/field-staff-presence` | ✅ |
| `staff_work_orders` | Assignee | `WorkOrder.assignedToProfileId` + `GET …/dashboard/staff/work-orders` | ✅ |
| `staff_schedule` | Date-only schedule | `WorkOrder.scheduledAt` + `GET …/dashboard/staff/schedule` | ✅ |
| `staff_completed` | Hours + assignee | `WorkOrder.hoursLogged` + `GET …/dashboard/staff/completed` | ✅ |

### Phase C discussion notes (decisions applied)

| Topic | Decision |
|-------|----------|
| Insurance | First-class `RentersInsurancePolicy` entity |
| Audit | App-scoped `IpmsActivityEvent` (platform feed; optional orgId) |
| SLA | Fields on `WorkOrder` (`respondedAt`, `slaDueAt`); on-time = completedAt ≤ slaDueAt |
| Field staff | Assign WO to `assignedToProfileId`; presence via `FieldStaffPresence` |

---

## Supporting todos (data)

| # | Task | Why | Status |
|---|------|-----|--------|
| S1 | Seed DEMO_ORG: units, leases, balances, incoming requests, tasks, maintenance, announcements | Live Phase A widgets otherwise empty | ⬜ |
| S2 | Seed role-based demo users (PM / tenant / vendor / staff) + Try demo → real login | Role dashboards need real JWT context | ⬜ |

---

## Suggested implementation order

| Order | Item | Track |
|------:|------|-------|
| 1 | A1–A4 Phase A FE wiring | A |
| 2 | S1 sample seed (enough for A) | Support |
| 3 | B0 path/DTO convention | B |
| 4 | B1 revenue | B |
| 5 | B2–B3 tenant widgets | B |
| 6 | B4 platform | B |
| 7 | B5 vendor WOs | B |
| 8 | S2 demo accounts | Support |
| 9 | Phase C design discussions (insurance → assignee → SLA → audit) | C |

---

## Out of scope (this doc)

- Replacing domain CRUD with full AppFolio-parity business rules
- Real platform uptime / incident management
- Notification preference entity (separate from widgets)

---

## Change log

| Date | Change |
|------|--------|
| 2026-07-12 | Initial hybrid matrix + todo backlog created |
| 2026-07-12 | Phase A FE: 7 widgets wired to list APIs + mock/Sample fallback |
| 2026-07-12 | Phase B: 8 dashboard summary APIs + FE wire |
| 2026-07-12 | Phase C: insurance / activity / field-staff entities + WO SLA/assignee + FE wire |
