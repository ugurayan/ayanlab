import type { CurrentSession } from "../api/types";

export function isPlatformAdmin(session: CurrentSession | null | undefined): boolean {
  const roles = session?.authorization?.roleCodes ?? [];
  return roles.some((r) => r.toUpperCase() === "ADMIN" || r.toUpperCase().endsWith("_ADMIN"));
}

export function canAccess(
  session: CurrentSession | null | undefined,
  permission?: string,
): boolean {
  if (isPlatformAdmin(session)) return true;
  if (!permission) return true;
  const codes = session?.authorization?.permissionCodes ?? [];
  if (codes.length === 0) return true;
  if (codes.includes(permission)) return true;
  const parts = permission.split(":");
  const resource = parts[2];
  if (resource && codes.some((c) => c.includes(`:${resource}:`) || c.endsWith(`:${resource}`))) {
    return true;
  }
  return codes.some((c) => c.startsWith(permission.replace(/:[^:]+$/, ":")));
}
