import { http, authHttp } from "../client";
import { endpoints } from "../endpoints";

const generateCorrelationId = () => `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;

export async function apiListAudit() {
  const res = await http.get(endpoints.audit.list);
  return res.data;
}

export async function apiListUserAudit() {
  const res = await http.get(endpoints.audit.user);
  return res.data;
}

// Shape returned by the system audit endpoint (auth service)
type SystemAuditApiResponse = {
  correlationId: string;
  success: boolean;
  message?: string;
  data?: {
    content: SystemAuditItem[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
  };
  timestamp: number;
};

export type SystemAuditItem = {
  id: string;
  version: number;
  createdAt: string;
  updatedAt: string | null;
  createdBy: string | null;
  updatedBy: string | null;
  createdIp: string | null;
  updatedIp: string | null;
  deleted: boolean;
  correlationId: string;
  className: string;
  methodName: string;
  remoteAddr: string;
  remoteHost: string;
  url: string;
  responseStatus: string;
  headers: string | null;
  request: string | null;
  response: string | null;
};

export type SystemAuditRow = SystemAuditItem;

export type SystemAuditPage = {
  items: SystemAuditRow[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
};

export async function apiListSystemAudit(page: number, size: number): Promise<SystemAuditPage> {
  const res = await authHttp.get<SystemAuditApiResponse>(endpoints.audit.system, {
    // Match backend contract: page/size as in the curl example
    params: { page, size },
    headers: {
      accept: "application/json",
      "X-Correlation-ID": generateCorrelationId(),
    },
  });

  const payload = res.data;
  const items: SystemAuditItem[] = payload?.data?.content ?? [];

  const meta = payload.data;

  return {
    items,
    totalElements: meta?.totalElements ?? items.length,
    totalPages: meta?.totalPages ?? 1,
    page: meta?.number ?? page,
    size: meta?.size ?? size,
  };
}
