# StaffIQ (ayanlab-hrm) Use Cases

This document maps product use cases to platform responsibilities vs. HRM backend domain APIs.

## Architecture split

| Concern | Owner | Notes |
|--------|--------|--------|
| Users, login, JWT | **Platform** (`/auth/api`) | Single identity per person |
| Profiles, org membership, roles, permissions, policy groups | **Platform** | Scoped by application (`staffiq`), organization, and profile |
| Worker profile payloads (education, skills, documents, …) | **HRM backend** (`/hrm/api`) | Linked by `profileId`, `orgId`, `workerId`, `platformUserId` |
| Permission enforcement | **Platform JWT** + **HRM service** | Controllers use `@RequirePermission`; services call platform `GET /auth/api/authorization/effective` before mutating data |

---

## UC-1 — Application administration (roles, permissions, users, orgs)

**Actor:** Application Admin, Agent Admin, System Admin

**Goal:** Configure StaffIQ at application, organization, and worker levels.

**Platform (not implemented in HRM backend):**

- Define application roles (`ADMIN`, `AGENT_USER`, `ORG_FINANCE`, `WORKER`, …) and map them to policy groups.
- Assign users to profiles under organizations with those roles.
- Department personas (e.g. finance-only UI) are modeled as policy groups such as `STAFFIQ_FINANCE_ONLY` in `authz/policy-groups.json`.
- CRUD for users, roles, permissions, organizations: Platform Admin UI → `/auth/api/*`.

**HRM backend:**

- Publishes `authz/*.json` to platform on startup.
- Exposes org/workforce domain only; no duplicate identity store (except optional `HrmUser` mirror for demos).

**Acceptance:**

- Finance role sees finance modules only (`staffiq:finance:*`, `staffiq:transactions:*`).
- Worker role can manage own profile data policy group `STAFFIQ_PROFILE_DATA_MANAGE`.

---

## UC-2 — Profile-centric worker data

**Actor:** Worker (self), Recruiter, Org Admin

**Goal:** Store everything that appears on the public worker profile and completion widgets.

**Platform fields:** `userId`, `profileId`, `orgId`, effective roles/permissions.

**HRM entities (all implement `ProfileLinkedEntity`, JSON `details` + indexed keys):**

| UI area | Entity | API base |
|---------|--------|----------|
| About / completion | `Profile` | `/hrm/api/profiles` |
| Education | `Education` | `/hrm/api/educations` |
| Work experience | `WorkExperience` | `/hrm/api/work-experiences` |
| Languages | `ProfileLanguage` | `/hrm/api/profile-languages` |
| Contact | `ProfileContact` | `/hrm/api/profile-contacts` |
| Location (home / service area) | `ProfileLocationDetail` | `/hrm/api/profile-location-details` |
| Skills (assignments) | `WorkerSkill` | `/hrm/api/worker-skills` |
| Certificates (holdings) | `WorkerCertificate` | `/hrm/api/worker-certificates` |
| Documents | `Document` | `/hrm/api/documents` |
| Taxonomy: job roles / industries | `JobRole`, `JobRoleCategory`, `RoleType` | `/hrm/api/job-roles`, … |
| Taxonomy: skill & certificate catalog | `Skill`, `Certificate` | `/hrm/api/skills`, `/hrm/api/certificates` |

**List scoping:** When `X-Profile-Id` is present on the request context, list endpoints return rows for that profile only; org admins with `orgId` see org-scoped rows.

**Mutations:** `HrmPlatformAuthorizationService` re-validates the required `staffiq:{resource}:{action}` permission against platform effective authorization before create/update/delete.

---

## UC-3 — Document verification & compliance

**Actors:** Worker, Vendor User, Recruiter

- Workers upload documents (`staffiq:documents:create`).
- Vendors review (`staffiq:verification:*`).
- Entities: `Document`, `DocumentVerification`.

---

## UC-4 — Workforce & operations (existing domain)

Shifts, jobs, applications, tasks, locations, organizations — unchanged CRUD under `/hrm/api/*` with catalog permissions in `authz/permissions.json`.

---

## UC-5 — Finance department isolation

**Actor:** `ORG_FINANCE` role

- Policy group `STAFFIQ_FINANCE_ONLY`: dashboard + finance + transactions + settings read.
- No worker profile write, no shift manage.

---

## UC-6 — Agent / staffing agency

**Actor:** `AGENT_ADMIN`, `AGENT_USER`

- Org + shift + recruiting permissions via existing roles in `authz/roles.json`.
- Profile data read for candidates via workforce manage permissions.

---

## Initial seed data

On startup (`staffiq.db.reload-mock-data-on-startup=true`), `staffiq-mock-data.json` loads demo rows including `profileEducations`, `profileExperiences`, `profileLanguages`, `profileContacts`, `profileLocationDetails`, `workerSkills`, and `workerCertificates` for worker `w_1`.

---

## Related configuration

```yaml
platform:
  api-url: http://localhost:8102
hrm:
  platform-auth:
    enabled: true   # set false only for isolated unit tests without platform
```

Platform must be running for service-layer authorization checks when `hrm.platform-auth.enabled=true`.