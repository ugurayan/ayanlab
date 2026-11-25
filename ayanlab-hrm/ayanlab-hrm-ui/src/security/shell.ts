import { useEffect, useState } from "react";
import { http } from "../api/client";
import { unwrapHrmData } from "../api/hrmApi";
import { useAuth } from "../state/auth";
import type { User } from "../types/auth";

export type HrmShell = "employer" | "worker" | "admin";

const STORAGE_KEY = "hrm.activeShell";

const EMPLOYER_ROLES = new Set([
  "ORG_ADMIN",
  "ORG_FINANCE",
  "LOCATION_MANAGER",
  "RECRUITER",
  "BUSINESS_ADMIN",
  "BUSINESS_MEMBER",
  "EMPLOYER",
  "AGENT_ADMIN",
  "AGENT_USER",
  "4b029d24-e658-4a65-8d76-ddbc2627a05c",
  "055978b5-7e39-494d-9725-a0dca32dda6c",
  "b0633699-641c-47c5-89c6-6a88f7d83754"
]);

const WORKER_ROLES = new Set(["WORKER", "RIDER", "9c2b8be8-bdec-4484-96b6-a2145b654b4d"]);

const ADMIN_ROLES = new Set([
  "PLATFORM_ADMIN",
  "ADMIN",
  "SYSTEM_ADMIN",
  "STAFF",
  "4a3207bf-cc00-46db-8db7-42e8d9568b8b",
  "a0fdb0b8-a079-4315-ae21-31e68cd80778"
]);

const SHARED_ROUTES = ["/app/dashboard", "/app/inbox", "/app/support", "/app/settings", "/app/profile"];

const EMPLOYER_ROUTES = [
  ...SHARED_ROUTES,
  "/app/jobs",
  "/app/tasks",
  "/app/orgs",
  "/app/legal-entities",
  "/app/brands",
  "/app/locations",
  "/app/shifts",
  "/app/applications",
  "/app/workers",
  "/app/documents",
  "/app/finance",
  "/app/transactions",
  "/app/audit/user",
  "/app/audit/system"
];

const WORKER_ROUTES = [
  ...SHARED_ROUTES,
  "/app/jobs",
  "/app/shifts",
  "/app/applications",
  "/app/documents"
];

const ADMIN_ROUTES = [
  ...SHARED_ROUTES,
  "/app/verification",
  "/app/job-roles",
  "/app/industries",
  "/app/skills",
  "/app/certificates",
  "/app/document-types",
  "/app/audit/user",
  "/app/audit/system"
];

const SHELL_ROUTES: Record<HrmShell, Set<string>> = {
  employer: new Set(EMPLOYER_ROUTES),
  worker: new Set(WORKER_ROUTES),
  admin: new Set(ADMIN_ROUTES)
};

export const SHELL_LABEL: Record<HrmShell, string> = {
  employer: "Company Admin",
  worker: "Worker Profile",
  admin: "System Admin"
};

function isShell(value: string): value is HrmShell {
  return value === "employer" || value === "worker" || value === "admin";
}

function hasPerm(perms: string[], code: string): boolean {
  if (perms.includes(code) || perms.includes("staffiq:*:*")) return true;
  const [app, resource, action] = code.split(":");
  return perms.some((grant) => {
    const parts = grant.split(":");
    if (parts.length !== 3 || parts[0] !== app) return false;
    const resourceOk = parts[1] === "*" || parts[1] === resource;
    const actionOk = parts[2] === "*" || parts[2] === action;
    return resourceOk && actionOk;
  });
}

/** Same rule as {@code HrmShells} on the HRM backend. */
export function shellsFor(user: User | null): HrmShell[] {
  const roles = user?.roles ?? [];
  const perms = user?.permissions ?? [];
  const roleHit = (set: Set<string>) => roles.some((role) => set.has(role));
  const wildcard = hasPerm(perms, "staffiq:*:*");
  const allShells = wildcard || roles.includes("PLATFORM_ADMIN") || roles.includes("ADMIN");
  const orgWork = hasPerm(perms, "staffiq:organizations:read") || hasPerm(perms, "staffiq:shifts:create");
  const shells: HrmShell[] = [];
  if (allShells || roleHit(EMPLOYER_ROLES) || orgWork) shells.push("employer");
  if (allShells || roleHit(WORKER_ROLES) || (hasPerm(perms, "staffiq:applications:create") && !orgWork)) {
    shells.push("worker");
  }
  if (allShells || roleHit(ADMIN_ROLES) || hasPerm(perms, "staffiq:job_roles:create")) shells.push("admin");
  return shells;
}

export function routeVisibleInShell(path: string, shell: HrmShell): boolean {
  for (const route of SHELL_ROUTES[shell]) {
    if (path === route || path.startsWith(`${route}/`)) return true;
  }
  return false;
}

export function useActiveShell() {
  const { user } = useAuth();
  const local = shellsFor(user);
  const [remote, setRemote] = useState<HrmShell[] | null>(null);
  const [stored, setStored] = useState<string | null>(() => {
    try {
      return window.localStorage.getItem(STORAGE_KEY);
    } catch {
      return null;
    }
  });

  useEffect(() => {
    let cancelled = false;
    http
      .get("/navigation/shells")
      .then((res) => {
        const data = unwrapHrmData<{ shells?: string[] }>(res.data);
        const shells = (data?.shells ?? []).filter(isShell);
        if (!cancelled && shells.length) setRemote(shells);
      })
      .catch(() => {
        if (!cancelled) setRemote(null);
      });
    return () => {
      cancelled = true;
    };
  }, [user?.id]);

  useEffect(() => {
    const sync = () => {
      try {
        setStored(window.localStorage.getItem(STORAGE_KEY));
      } catch {
        setStored(null);
      }
    };
    window.addEventListener("hrm-shell", sync);
    window.addEventListener("storage", sync);
    return () => {
      window.removeEventListener("hrm-shell", sync);
      window.removeEventListener("storage", sync);
    };
  }, []);

  const available = remote && remote.length ? remote : local;
  const shell: HrmShell = available.includes(stored as HrmShell)
    ? (stored as HrmShell)
    : (available[0] ?? "employer");

  const setShell = (next: HrmShell) => {
    try {
      window.localStorage.setItem(STORAGE_KEY, next);
    } catch {
      // ignore
    }
    setStored(next);
    window.dispatchEvent(new Event("hrm-shell"));
  };

  return { shell, available, setShell, constrained: available.length > 0 };
}
