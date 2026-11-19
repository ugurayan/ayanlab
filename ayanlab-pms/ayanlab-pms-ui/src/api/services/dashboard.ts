import { USE_MOCKS } from "../../env";
import { mockDashboardSummary } from "../../mock/mockApi";
import type { ActivityEvent, BalanceRow, Lease, MaintenanceRequest, Violation } from "../../types/pms";
import { unwrapData } from "../apiEnvelope";
import { http } from "../pmsClient";

type WidgetsApi = {
  balances: {
    id: string;
    propertyName: string;
    partyName: string;
    partyType: string;
    amountCents: number;
    daysPastDue: number;
  }[];
  maintenance: {
    id: string;
    propertyName: string;
    summary: string;
    status: string;
    createdAt?: string;
  }[];
  activity: { id: string; summary: string; createdAt?: string }[];
  insuranceGaps: { tenantName: string; status: string }[];
  expiringLeases: { id: string; status: string; endDate: string }[];
  listings: { vacant: number; applicationsPending: number };
  violations: {
    id: string;
    associationId: string;
    unitLabel: string;
    status: string;
    fineCents: number;
    category: string;
    description: string;
    openedAt?: string;
  }[];
  associationDelinquentCents: number;
  bankUnmatched: { id: string; description: string; amountCents: number }[];
  openTasks: number;
};

export type DashboardSummary = {
  balances: BalanceRow[];
  maintenance: MaintenanceRequest[];
  activity: ActivityEvent[];
  insuranceGaps: { tenantName: string; propertyName: string; status: "MISSING" | "ACTIVE" }[];
  expiringLeases: Lease[];
  listings: { vacant: number; applicationsPending: number };
  violations: Violation[];
  associationDelinquentCents: number;
  bankUnmatched: { id: string; description: string; amountCents: number; date: string }[];
  openTasks: number;
  eftPendingCount: number;
  patrolCompleted: number;
  patrolTotal: number;
};

function mapViolation(v: WidgetsApi["violations"][number]): Violation {
  return {
    id: v.id,
    associationId: v.associationId,
    unitLabel: v.unitLabel,
    status: v.status as Violation["status"],
    fineCents: v.fineCents,
    category: v.category,
    description: v.description,
    openedAt: v.openedAt ?? new Date().toISOString(),
    timeline: []
  };
}

export async function fetchDashboardSummary(): Promise<DashboardSummary> {
  if (USE_MOCKS) {
    return mockDashboardSummary();
  }
  const res = await http.get("/dashboard/pm/widgets");
  const d = unwrapData<WidgetsApi>(res);
  return {
    balances: d.balances.map(
      (b): BalanceRow => ({
        id: String(b.id),
        propertyName: b.propertyName,
        partyName: b.partyName,
        partyType: b.partyType as BalanceRow["partyType"],
        amountCents: b.amountCents,
        daysPastDue: b.daysPastDue
      })
    ),
    maintenance: d.maintenance.map(
      (m): MaintenanceRequest => ({
        id: String(m.id),
        propertyName: m.propertyName,
        summary: m.summary,
        status: (m.status === "OVERDUE" ? "OVERDUE" : m.status === "DONE" ? "DONE" : "OPEN") as MaintenanceRequest["status"],
        createdAt: m.createdAt ?? new Date().toISOString()
      })
    ),
    activity: d.activity.map(
      (a): ActivityEvent => ({
        id: String(a.id),
        summary: a.summary,
        createdAt: a.createdAt ?? new Date().toISOString()
      })
    ),
    insuranceGaps: d.insuranceGaps.map((g) => ({
      tenantName: g.tenantName,
      propertyName: "—",
      status: (g.status === "EXPIRED" ? "MISSING" : g.status === "ACTIVE" ? "ACTIVE" : "MISSING") as
        | "MISSING"
        | "ACTIVE"
    })),
    expiringLeases: d.expiringLeases.map(
      (l): Lease => ({
        id: String(l.id),
        unitId: "",
        tenantId: "",
        status: "EXPIRING",
        startDate: "",
        endDate: l.endDate,
        rentCents: 0
      })
    ),
    listings: d.listings,
    violations: d.violations.map(mapViolation),
    associationDelinquentCents: d.associationDelinquentCents,
    bankUnmatched: d.bankUnmatched.map((row) => ({
      id: String(row.id),
      description: row.description,
      amountCents: row.amountCents,
      date: new Date().toISOString()
    })),
    openTasks: d.openTasks,
    eftPendingCount: 2,
    patrolCompleted: 3,
    patrolTotal: 5
  };
}