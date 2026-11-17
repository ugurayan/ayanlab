# Platform admin CRUD + dashboard UI

**Date:** 2026-11-15  
**Status:** Approved for implementation

## Backend contract

All standard entity APIs (`@AuthPlatformApi` / `@CoreBusinessApi`, excluding POST-only email dispatch):

| Action | HTTP |
|--------|------|
| Create | `POST /` |
| List | `GET /` (`page`, `size`, `q`, `includeInactive`, optional `profileId` / `appId`) |
| Read | `GET /{id}` |
| Update | `PUT /{id}` |
| Soft delete | `DELETE /{id}` → `isActive=false` |
| Hard delete | `DELETE /{id}?hard=true` |
| Restore | `PUT /{id}/activation?active=true` |

`BaseEntity.isActive` is the only lifecycle flag. No `isDeleted` column.

`IBaseService`: `deleteById` remains soft; add `hardDeleteById` and `setActive`.

`GET /auth/api/me`: current user, profiles, `EffectiveAuthorization`.

Profile-scoped lists honor `X-Profile-Id` and optional `profileId` query.

## UI

Metadata-driven CRUD (one table/drawer/confirm) for all registered resources.

Shell: Linear/Vercel — collapsible sidebar, sticky header (search, avatar dropdown), My Profile route `/profile`, nav filtered by permissions (platform `ADMIN` / wildcard sees all).

Stack: React + Tailwind utilities (Radix-like focus/dialog patterns without a full shadcn install unless needed).
