import { http } from "../client";
import { endpoints } from "../endpoints";
import { unwrapHrmData } from "../hrmApi";
import { apiListSystemAudits, apiListUserAudits } from "./hrmResources";

export async function apiListAudit() {
  return apiListUserAudits();
}

export async function apiListUserAudit() {
  return apiListUserAudits();
}

export type SystemAuditRow = {
  id: string;
  actorEmail?: string;
  action?: string;
  target?: string;
  createdAt?: string;
};

export type SystemAuditPage = {
  items: SystemAuditRow[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
};

/** HRM-backed system audit (mock list; optional page/size ignored). */
export async function apiListSystemAudit(page: number, size: number): Promise<SystemAuditPage> {
  const items = await apiListSystemAudits();
  const start = page * size;
  const slice = items.slice(start, start + size);
  const total = items.length;
  return {
    items: slice,
    totalElements: total,
    totalPages: Math.max(1, Math.ceil(total / size)),
    page,
    size,
  };
}