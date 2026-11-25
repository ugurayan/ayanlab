# HRM organization, location, and shift CRUD

**Date:** 2026-11-23
**Status:** Approved for implementation

## Goal

HRM stores a staffing profile for an existing platform organization, plus legal entities, brands, locations, Instawork-style shift templates, and dated shift instances. Each record has create, read, update, and soft delete in the HRM API and in the HRM UI.

Platform `core.Organization` stays the identity. This work does not create or delete platform organizations.

## Decisions

- HRM tables live in the `staffiq` schema. Hibernate `ddl-auto: update` adds new columns. It does not change an existing `varchar` id into `uuid`. Mock seed data is rewritten to real UUIDs.
- Every HRM row in this feature extends a local `com.ayanlab.hrm.backend.entity.BaseEntity`. It is not the platform class. Platform `BaseEntity` has no `isDeleted`, and its `updatedBy` column is not updatable.
- Every identifier that is a UUID is `java.util.UUID` in Java and `uuid` in PostgreSQL, in platform, HRM, PMS, and every other app. It is never stored as `String`, and code does not call `UUID.toString()` to persist or query it. Path variables for those ids are `UUID`. JSON has no UUID type, so the wire format remains a UUID string and TypeScript keeps `string` only at that boundary.
- These are not UUIDs and stay `String`: correlation id, GCP project id, Kafka group id, Apple and Google product ids, payment-provider ids, WhatsApp business and phone-number ids, tax ids, Google `placeId`, and menu or locale codes.
- Platform `BaseEntity` and PMS foreign keys already use `UUID`. HRM is the gap: entity ids and `profileId`, `orgId`, `workerId`, `platformUserId`, and `platformRecordId` are `String`, filled with `UUID.randomUUID().toString()` or `context.getOrgId().toString()`. Those fields become `UUID` on every HRM entity, not only the new organization, location, and shift records. Existing slug values such as `org_acme` cannot sit in a `uuid` column. Seed data for those rows is rewritten to real UUIDs. Hibernate `ddl-auto: update` does not cast `varchar` to `uuid`, so those columns are recreated rather than altered in place.
- Ids are assigned with `UUID.randomUUID()` before save. No id is generated as a string.
- Audit fields are set in the service from `CoreContext`, not by Spring Data auditing. `createdAt` and `createdBy` are written once. `updatedAt` and `updatedBy` are written on every save.
- `isActive` defaults to `true`. `isDeleted` defaults to `false`. Soft delete sets `isDeleted=true` and `isActive=false`. Lists return rows with `isDeleted=false` only. There is no hard delete.
- The caller's `CoreContext.orgId`, when present, limits every list and write to that platform organization.
- Controllers follow the existing HRM `BaseController` traced-call pattern and the standard `X-Client-*` and `X-Correlation-Id` headers.
- UI lists follow the skills page: table, row actions, and a form on the same page. Copy is in the English, German, and Turkish catalogs.

## Data model

### BaseEntity

| Column | Type | Rule |
|---|---|---|
| id | UUID | Assigned in the service |
| createdAt | timestamptz | Set once |
| createdBy | UUID | Set once from the context user |
| updatedAt | timestamptz | Set on every save |
| updatedBy | UUID | Set on every save from the context user |
| isActive | boolean | Default true |
| isDeleted | boolean | Default false |

### Organization profile

Replaces the current thin `Organization` entity (`id`, `name`, `country`, `createdAt`).

| Column | Rule |
|---|---|
| platformOrganizationId | `UUID`. Required, unique among rows with `isDeleted=false` |
| name | Required |
| email | Required |
| phone | Required |
| timezone | Required IANA id, for example `Europe/Zurich` |
| currency | Required ISO 4217 code, for example `CHF` |

One live profile per platform organization. A second create for the same platform id returns `409`.

### Legal entity

Belongs to one organization profile.

| Column | Rule |
|---|---|
| platformOrganizationId | `UUID`. Required |
| legalName | Required |
| entityType | Required, free text such as `AG` or `GmbH` |
| vatNumber | Required |
| email | Required |
| phone | Required |

An organization may have zero legal entities.

### Brand

Belongs to one organization profile. Optional. An organization may have zero brands.

| Column | Rule |
|---|---|
| platformOrganizationId | `UUID`. Required |
| name | Required |
| area | Required free text, such as `Zurich` or `DACH` |

A brand does not require a legal entity.

### Location

A location can point at any supported record. There is no `orgId` column.

| Column | Rule |
|---|---|
| referenceId | `UUID`. Required |
| referenceEntity | Required. One of `ORGANIZATION`, `BRAND`, `LEGAL_ENTITY`, `PROFILE`, `USER`, `BANK` |
| placeId | Optional. Google Maps place id |
| name | Required |
| formattedAddress | Required |
| street | Optional. First segment of the formatted address when the search response has no street |
| city | Optional |
| state | Optional |
| zipCode | Optional |
| country | Optional |
| latitude | Optional |
| longitude | Optional |
| notes | Optional. Parking and on-site directions |

`latitude` and `longitude` are the meeting pin. Geofence polygons are out of scope.

`ORGANIZATION`, `BRAND`, and `LEGAL_ENTITY` must refer to a live HRM row in the caller's organization. `PROFILE`, `USER`, and `BANK` are accepted when `referenceId` is a UUID. This delivery does not call another service to prove those rows exist.

The organization locations list returns both of these:

- `referenceEntity=ORGANIZATION` and `referenceId` equal to the platform organization id
- `referenceEntity=BRAND` and `referenceId` equal to any live brand of that organization

### Shift template

One template is one schedule at one location. A second schedule is a second template. The template is the Instawork posting, stored so it can be reused. It belongs to the organization and points at one location.

| Column | Rule |
|---|---|
| platformOrganizationId | `UUID`. Required |
| locationId | `UUID`. Required live location |
| name | Required internal name, such as `AM Dishwasher` |
| staffingType | `SHORT_TERM` or `LONG_TERM` |
| roleId | `UUID`. Required |
| description | Required job duties |
| additionalInstructions | Optional |
| contractTypeId | `UUID`. Optional |
| recurring | Required boolean |
| daysOfWeek | Required when recurring. Values `sun` through `sat` |
| startTime | Required local time `HH:mm` |
| endTime | Required local time `HH:mm` |
| unpaidBreakMinutes | Required, zero or greater |
| wageType | `HOURLY` or `DAILY` |
| wageAmountMinor | Required integer, greater than zero. Minor currency units |
| currency | Required ISO 4217 |
| tipPercent | Optional decimal, zero or greater |
| tipAmountMinor | Optional integer, zero or greater |
| employeesNeeded | Required integer, at least 1 |
| dressCodeId | `UUID`. Optional |
| attireRestrictionLevel | Optional. `basic`, `strict`, `custom`, or `no-preference` |
| groomingLevel | Optional. `elevated`, `professional`, `standard`, `custom`, or `no-preference` |
| staffSelectionType | Required. `open-to-all`, `open-to-new`, `smart-assign`, `request-approval`, or `top-pros-only` |

Child rows, each with the base columns plus a template id:

- Skill requirement: `skillId` (`UUID`), optional `expertiseLevel`, optional `experienceDuration`
- Language requirement: `language`, `level`, `certificationNeeded`
- Certificate requirement: `certificateId` (`UUID`) or `customName`
- Document requirement: `documentId` (`UUID`) or `customName`
- On-site contact: `contactId` (`UUID`)

### Shift instance

A dated shift a worker would see. Creating one from a template copies the worker-facing fields. Later template edits do not change existing instances.

| Column | Rule |
|---|---|
| platformOrganizationId | `UUID`. Required |
| templateId | `UUID`. Optional. Null for a one-off instance |
| locationId | `UUID`. Required |
| roleId | `UUID`. Required |
| name | Required |
| description | Required |
| additionalInstructions | Optional |
| startsAt | Required timestamptz |
| endsAt | Required timestamptz |
| unpaidBreakMinutes | Required |
| wageType | `HOURLY` or `DAILY` |
| wageAmountMinor | Required |
| currency | Required |
| tipPercent | Optional |
| tipAmountMinor | Optional |
| employeesNeeded | Required |
| dressCodeId | `UUID`. Optional |
| staffSelectionType | Required |
| status | `OPEN`, `FILLED`, `IN_PROGRESS`, `COMPLETED`, or `CANCELLED` |

Requirement and contact rows are copied onto the instance as its own child rows, with an instance id instead of a template id.

## Instawork alignment

The business posting flow at [How do I post a shift?](https://help.instawork.com/en/articles/4819315-how-do-i-post-a-shift) maps as follows.

| Instawork step | HRM record |
|---|---|
| Short-term or long-term staffing | `staffingType` on the template |
| Existing or new worksite, address, name, parking notes | Location, filled from the existing Google Maps search response |
| Meeting pin | Location `latitude` and `longitude` |
| On-site contact | Template contact child rows |
| Shift date, start, end, break, internal name | Template schedule, then one instance per date |
| Recurring schedule | Template `recurring` plus `daysOfWeek`, expanded by the generate endpoint |
| Position | `roleId` |
| Attire, grooming, restrictions | `dressCodeId`, `attireRestrictionLevel`, `groomingLevel` |
| Abilities and job duties | Skill child rows and `description` |
| Who receives the shift | `staffSelectionType` |
| Hourly bill rate, tip | `wageAmountMinor`, `wageType`, `tipPercent`, `tipAmountMinor` |

These Instawork behaviors are out of scope:

- Geofence clock-in and clock-out
- Market minimum bill-rate floor and markup
- Worker booking, roster notification, and fill
- Several schedules inside a single template
- Editing a live instance only until 24 hours before start

## Relationships and delete rules

- Organization profile to brand is one-to-many.
- Organization profile to legal entity is one-to-many.
- A location belongs to whatever `referenceId` and `referenceEntity` say. An organization and each of its brands may each have many locations.
- A shift template and a shift instance belong to one organization and one location.

Soft delete returns `409` when a live child still points at the row:

- Profile: any live legal entity, brand, direct location, template, or instance
- Legal entity: any live location with `referenceEntity=LEGAL_ENTITY`
- Brand: any live location with `referenceEntity=BRAND`
- Location: any live template or instance
- Template: allowed. Live instances keep their copied fields and `templateId` is set to null
- Instance: allowed

## API

Base path prefix: `/hrm/api`. Standard actions on each collection: `GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}`.

| Collection | Path |
|---|---|
| Organization profiles | `/hrm/api/organizations` |
| Legal entities | `/hrm/api/legal-entities` |
| Brands | `/hrm/api/brands` |
| Locations | `/hrm/api/locations` |
| Shift templates | `/hrm/api/shift-templates` |
| Shift instances | `/hrm/api/shifts` |

`POST /hrm/api/organizations` creates the HRM profile for a `platformOrganizationId` the caller may manage. It does not call the platform to create an organization.

`GET /hrm/api/locations` accepts optional `referenceEntity` and `referenceId`. Without those filters, and with an organization in context, the response is the organization locations list defined above.

`POST /hrm/api/shift-templates/{id}/instances` body is either `{ "dates": ["2026-12-01"] }` or `{ "from": "2026-12-01", "to": "2026-12-31" }`. The range uses the template's `daysOfWeek` and local `startTime` and `endTime`, interpreted in the location timezone when set, otherwise the organization timezone. One instance is created per date. A date that already has a live instance for the same template, location, and `startsAt` is skipped. The response is the instances created on this call.

`POST /hrm/api/shifts` creates a one-off instance when `templateId` is absent. `locationId`, schedule, wage, and `employeesNeeded` are required.

### Validation

Return `400` with a field message when:

- A required field is missing or blank
- `timezone` is not an IANA id
- `currency` is not a 3-letter ISO code
- `email` has no `@`
- `referenceEntity` is outside the allowed set
- `referenceId` is not a UUID, or the HRM target row is missing for `ORGANIZATION`, `BRAND`, or `LEGAL_ENTITY`
- `endsAt` is not after `startsAt`
- Local `endTime` equal to `startTime`, or the resulting duration is greater than 12 hours. An `endTime` earlier than `startTime` means the shift ends the next day. Night shifts are valid when the duration is greater than zero and at most 12 hours
- `unpaidBreakMinutes` is negative or greater than or equal to the duration
- A new instance with status `OPEN` starts less than 30 minutes from now
- `wageAmountMinor` is not greater than zero, or `employeesNeeded` is less than 1
- Recurring is true and `daysOfWeek` is empty

Return `404` when the id is unknown or the row is soft-deleted. Return `409` for a duplicate live profile and for a blocked delete. Permission failures stay on the existing `403` path.

### Permissions

Add these resources to `ayanlab-hrm-backend/src/main/resources/authz/permissions.json`, each with actions `read`, `create`, `update`, `delete`:

- `legal_entities`
- `brands`
- `shift_templates`

`organizations`, `locations`, and `shifts` already exist. Grant the new codes to the same policy groups that already have the matching `organizations` or `shifts` actions. `HrmAuthorizationCatalogTest` must still pass. The UI permission map in `ayanlab-hrm-ui/src/security/permissions.ts` gets the same codes.

## UI

Navigation codes `legal-entities` and `brands` are added beside `orgs`, `locations`, and `shifts` in `staffiq-navigation-seed.json` and in `ayanlab-hrm-ui/src/router/index.tsx`.

| Screen | Behavior |
|---|---|
| Organizations | List profiles. When the request context already has an organization id, create uses that id and the field is read-only. Otherwise create asks for the platform organization id. The other fields are name, email, phone, timezone, and currency. Edit does not change the platform id. |
| Legal entities | List and edit for the current organization. |
| Brands | List and edit for the current organization. Empty state explains that an organization can have no brands. |
| Locations | Choose `referenceEntity`, then a record of that type. Address fields are filled by the existing `apiGoogleMapsSearch` response: `placeId`, `name`, `formattedAddress`, `city`, `state`, `zipCode`, `country`, `latitude`, `longitude`. |
| Shifts | Two tabs, Templates and Instances. The template form is one page with sections, not a copy of the eight-step job wizard. Instances can be generated from a template or created as a one-off. Status is a badge. |

Create, edit, and delete controls render only when the user has the matching permission. Pages show loading, empty, and error states. `400` and `409` messages stay on the form. Delete confirmation says the record will be deactivated. A `409` from a blocked delete leaves the record in place.

## Testing

Backend tests use JUnit and AssertJ, the same stack as `HrmAuthorizationCatalogTest`. Service tests cover:

- Creating a profile, then a `409` on a second live profile for the same platform id
- Soft delete of a profile that still has a brand returns `409`
- Soft delete of a brand that still has a location returns `409`
- A location with `referenceEntity=BRAND` and an unknown brand id returns `400`
- A location with `referenceEntity=USER` and a UUID reference id is saved
- Generating instances from a recurring template creates one row per matching date and skips a duplicate `startsAt`
- A shift longer than 12 hours, a break that covers the whole shift, and an `OPEN` start inside 30 minutes each return `400`
- Deleting a template nulls `templateId` on its instances and keeps the copied wage

The UI has no test runner. Verify the five screens in the browser: create, edit, blocked delete, and empty state. `npm run build` in `ayanlab-hrm-ui` must pass.

## Implementation split

Four plans, in order. Each leaves the API and its screen usable.

1. Convert every existing HRM identifier that is a UUID from `String` to `java.util.UUID`, including path variables and repository methods. Recreate the `varchar` id columns as `uuid` and rewrite seed values that are not UUID syntax. Platform and PMS already store these as `UUID`; external provider ids stay `String`.
2. `BaseEntity`, organization profile, legal entities, brands, permissions, and those three screens.
3. Locations, including Google Maps fill and reference checks, and the locations screen.
4. Shift templates, child rows, instance generation, one-off instances, and the shifts screen.
