import type { CurrentSession } from "../api/types";

export function isPlatformAdmin(session: CurrentSession | null | undefined): boolean {
  const roles = session?.authorization?.roleCodes ?? [];
  return roles.some((r) => r.toUpperCase() === "ADMIN");
}

/** Mirrors PermissionCodes.fromLegacy: app:module:resource:action → app:resource:action. */
function normalize(code: string): string {
  const parts = code.split(":");
  return parts.length === 4 ? `${parts[0]}:${parts[2]}:${parts[3]}` : code;
}

/** Mirrors PermissionCodes.implies: same app; resource/action equal or "*". */
export function implies(grant: string, required: string): boolean {
  const [gApp, gResource, gAction] = normalize(grant).split(":");
  const [rApp, rResource, rAction] = normalize(required).split(":");
  if (!gApp || !rApp || gApp !== rApp) return false;
  const resourceOk = gResource === "*" || gResource === rResource;
  const actionOk = gAction === "*" || gAction === rAction;
  return resourceOk && actionOk;
}

export function canAccess(
  session: CurrentSession | null | undefined,
  permission?: string,
): boolean {
  if (!permission) return true;
  if (isPlatformAdmin(session)) return true;
  const codes = session?.authorization?.permissionCodes ?? [];
  return codes.some((grant) => implies(grant, permission));
}
