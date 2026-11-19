import type { BalanceRow, Lease, Person, Violation } from "../../types/pms";
import { USE_MOCKS } from "../../env";
import {
  listBalances as mockListBalances,
  listLeases as mockListLeases,
  listTenants as mockListTenants,
  listViolations as mockListViolations,
  getViolation as mockGetViolation,
  updateViolationStatus as mockUpdateViolationStatus
} from "../../mock/mockApi";
import { unwrapData } from "../apiEnvelope";
import { endpoints } from "../endpoints";
import { http } from "../pmsClient";

type ApiOutstandingBalance = {
  id: string;
  amountDueCents: number;
  amountPaidCents?: number;
  dueDate?: string;
  description?: string;
  status?: string;
};

type ApiTenant = {
  id: string;
  firstName: string;
  lastName: string;
  email?: string;
  phone?: string;
};

type ApiLease = {
  id: string;
  propertyId: string;
  unitId: string;
  tenantProfileId: string;
  status?: string;
  startDate?: string;
  endDate?: string;
  monthlyRentCents?: number;
};

type ApiViolation = {
  id: string;
  associationId: string;
  title: string;
  description?: string;
  status?: string;
  fineAmountCents?: number;
  violationDate?: string;
  createdAt?: string;
};

function mapViolationStatus(status?: string): Violation["status"] {
  if (status === "UNDER_REVIEW") return "HEARING";
  if (status === "RESOLVED" || status === "DISMISSED") return "CLOSED";
  return "OPEN";
}

function mapViolation(v: ApiViolation): Violation {
  return {
    id: v.id,
    associationId: v.associationId,
    unitLabel: "—",
    status: mapViolationStatus(v.status),
    fineCents: v.fineAmountCents ?? 0,
    category: v.title,
    description: v.description ?? v.title,
    openedAt: v.violationDate ?? v.createdAt ?? new Date().toISOString(),
    timeline: []
  };
}

export async function listBalances(): Promise<BalanceRow[]> {
  if (USE_MOCKS) return mockListBalances();
  const res = await http.get(endpoints.pms.outstandingBalances);
  const rows = unwrapData<ApiOutstandingBalance[]>(res);
  const today = new Date();
  return rows.map((b) => {
    const due = Math.max(0, (b.amountDueCents ?? 0) - (b.amountPaidCents ?? 0));
    let daysPastDue = 0;
    if (b.dueDate) {
      const dueDate = new Date(b.dueDate);
      daysPastDue = Math.max(0, Math.floor((today.getTime() - dueDate.getTime()) / 86400000));
    }
    return {
      id: b.id,
      propertyName: "—",
      partyName: b.description?.trim() || "Resident",
      partyType: "TENANT",
      amountCents: due,
      daysPastDue
    };
  });
}

export async function listTenants(): Promise<Person[]> {
  if (USE_MOCKS) return mockListTenants();
  const res = await http.get(endpoints.pms.tenants);
  return unwrapData<ApiTenant[]>(res).map((t) => ({
    id: t.id,
    displayName: `${t.firstName} ${t.lastName}`.trim(),
    email: t.email ?? "",
    phone: t.phone
  }));
}

export async function listLeases(): Promise<
  (Lease & { tenantName: string; unitLabel: string; propertyName: string })[]
> {
  if (USE_MOCKS) return mockListLeases();
  const res = await http.get(endpoints.pms.leases);
  const rows = unwrapData<ApiLease[]>(res);
  return rows.map((l) => {
    let status: Lease["status"] = "ACTIVE";
    if (l.status === "DRAFT") status = "DRAFT";
    else if (l.status === "EXPIRED" || l.status === "TERMINATED") status = "ENDED";
    else if (l.endDate) {
      const end = new Date(l.endDate);
      const soon = new Date();
      soon.setDate(soon.getDate() + 60);
      if (end <= soon) status = "EXPIRING";
    }
    return {
      id: l.id,
      unitId: l.unitId,
      tenantId: l.tenantProfileId,
      status,
      startDate: l.startDate ?? "",
      endDate: l.endDate ?? "",
      rentCents: l.monthlyRentCents ?? 0,
      tenantName: "—",
      unitLabel: "—",
      propertyName: "—"
    };
  });
}

export async function listViolations(): Promise<Violation[]> {
  if (USE_MOCKS) return mockListViolations();
  const res = await http.get(endpoints.pms.violations);
  return unwrapData<ApiViolation[]>(res).map(mapViolation);
}

export async function getViolation(id: string): Promise<Violation | undefined> {
  if (USE_MOCKS) return mockGetViolation(id);
  const res = await http.get(`${endpoints.pms.violations}/${id}`);
  return mapViolation(unwrapData<ApiViolation>(res));
}

function uiViolationToApi(status: Violation["status"]): string {
  if (status === "HEARING") return "UNDER_REVIEW";
  if (status === "CLOSED") return "RESOLVED";
  return "OPEN";
}

export async function updateViolationStatus(id: string, status: Violation["status"]): Promise<Violation> {
  if (USE_MOCKS) return mockUpdateViolationStatus(id, status);
  await http.put(`${endpoints.pms.violations}/${id}`, { status: uiViolationToApi(status) });
  const updated = await getViolation(id);
  if (!updated) throw new Error("Violation not found after update");
  return updated;
}