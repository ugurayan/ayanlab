import type { Permission, Role } from "../types/auth";

const all: Permission[] = [
  "org:read","org:write",
  "location:read","location:write",
  "shift:read","shift:write",
  "application:read","application:write",
  "worker:read","worker:write",
  "document:read","document:review",
  "finance:read","finance:write",
  "audit:read"
];

export const rolePermissions: Record<Role, Permission[]> = {
  PLATFORM_ADMIN: all,
  ORG_ADMIN: [
    "org:read","org:write",
    "location:read","location:write",
    "shift:read","shift:write",
    "application:read","application:write",
    "worker:read",
    "document:read","document:review",
    "finance:read",
    "audit:read"
  ],
  ORG_FINANCE: ["finance:read","finance:write","org:read","audit:read"],
  LOCATION_MANAGER: ["location:read","shift:read","shift:write","application:read","application:write","worker:read","document:read"],
  RECRUITER: ["shift:read","shift:write","application:read","application:write","worker:read","document:read"],
  WORKER: ["shift:read","application:write","document:read"],
  AGENT_ADMIN: ["org:read","org:write","shift:read","shift:write","application:read","application:write","worker:read","document:read","audit:read"],
  AGENT_USER: ["org:read","shift:read","shift:write","application:read","application:write","worker:read","document:read"],
  VENDOR_USER: ["document:read","document:review"]
};

export function computePermissions(roles: Role[]): Permission[] {
  const set = new Set<Permission>();
  for (const r of roles) {
    for (const p of rolePermissions[r] ?? []) set.add(p);
  }
  return [...set];
}
