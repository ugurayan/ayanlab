import type { AuthSession } from "../types/auth";
import type {
  Bill,
  BudgetLine,
  BankAccount,
  BankLine,
  ChartAccount,
  EftApproval,
  GlTransaction,
  RecurringTransaction
} from "../types/accounting";
import type {
  AtsDebtCode,
  EnforcementCase,
  LatePenaltyExample,
  LatePenaltyRule,
  MeterAllocation,
  MeterReading
} from "../types/tr";
import type {
  ArchitecturalRequest,
  BalanceRow,
  Lease,
  Person,
  PortalOwnerView,
  PortalTenantView,
  Property,
  PropertyDetail,
  RentRollRow,
  TenantDetail,
  Violation
} from "../types/pms";
import { db, mockUsers } from "./mockDb";

const delay = (ms = 280) => new Promise((r) => setTimeout(r, ms));
const token = () => `mock.${Math.random().toString(16).slice(2)}`;

function propertyName(id: string) {
  return db.properties.find((p) => p.id === id)?.name ?? "—";
}

function unitLabel(unitId: string) {
  return db.units.find((u) => u.id === unitId)?.label ?? "—";
}

function tenantName(tenantId: string) {
  return db.tenants.find((t) => t.id === tenantId)?.displayName ?? "—";
}

export async function mockLogin(email: string, password: string): Promise<AuthSession> {
  await delay();
  const rec = mockUsers[email.toLowerCase()];
  if (!rec || rec.password !== password) {
    throw new Error("Invalid email or password");
  }
  return { accessToken: token(), user: { ...rec.user } };
}

export async function mockDashboardSummary() {
  await delay();
  return {
    balances: db.balances,
    maintenance: db.maintenance,
    activity: db.activity,
    insuranceGaps: db.insuranceGaps,
    expiringLeases: db.leases.filter((l) => l.status === "EXPIRING"),
    listings: db.listingStats,
    violations: db.violations,
    associationDelinquentCents: db.associations[0]?.delinquentCents ?? 0,
    bankUnmatched: db.bankUnmatched,
    openTasks: 4,
    eftPendingCount: 2,
    patrolCompleted: db.patrolRoutes[0]?.completedTonight ?? 3,
    patrolTotal: db.patrolRoutes[0]?.checkpoints ?? 5
  };
}

export async function listProperties(): Promise<Property[]> {
  await delay();
  return [...db.properties];
}

export async function getPropertyDetail(id: string): Promise<PropertyDetail | undefined> {
  await delay();
  const property = db.properties.find((p) => p.id === id);
  if (!property) return undefined;
  const units = db.units.filter((u) => u.propertyId === id);
  const ownerNames = db.propertyOwners
    .filter((po) => po.propertyId === id)
    .map((po) => db.owners.find((o) => o.id === po.ownerId)?.displayName ?? "—");
  return {
    ...property,
    units,
    ownerNames,
    notes: db.propertyNotes[id] ?? [],
    files: db.propertyFiles[id] ?? []
  };
}

export type PropertyInput = Pick<Property, "name" | "address" | "mode" | "unitCount">;

export async function createProperty(input: PropertyInput): Promise<Property> {
  await delay(200);
  const created: Property = {
    id: `prop_${Date.now()}`,
    orgId: mockUsers["pm@demo.ayanlab"].user.orgId,
    ...input
  };
  db.properties.push(created);
  db.propertyNotes[created.id] = [];
  db.propertyFiles[created.id] = [];
  return created;
}

export async function getProperty(id: string): Promise<Property | undefined> {
  await delay(120);
  return db.properties.find((p) => p.id === id);
}

export async function listRentRoll(): Promise<RentRollRow[]> {
  await delay();
  return db.units
    .filter((u) => {
      const prop = db.properties.find((p) => p.id === u.propertyId);
      return prop?.mode === "RENTAL";
    })
    .map((u) => {
      const lease = db.leases.find((l) => l.unitId === u.id && (l.status as string) !== "ENDED");
      return {
        unitId: u.id,
        propertyId: u.propertyId,
        propertyName: propertyName(u.propertyId),
        unitLabel: u.label,
        unitStatus: u.status,
        tenantName: lease ? tenantName(lease.tenantId) : undefined,
        leaseId: lease?.id,
        leaseStatus: lease?.status,
        rentCents: lease?.rentCents ?? u.monthlyRentCents,
        leaseEnd: lease?.endDate
      };
    });
}

export async function listTenants(): Promise<Person[]> {
  await delay();
  return [...db.tenants];
}

export async function getTenant(id: string): Promise<TenantDetail | undefined> {
  await delay();
  const person = db.tenants.find((t) => t.id === id);
  if (!person) return undefined;
  const leases = db.leases
    .filter((l) => l.tenantId === id)
    .map((l) => {
      const unit = db.units.find((u) => u.id === l.unitId);
      return {
        ...l,
        unitLabel: unit?.label ?? "—",
        propertyName: unit ? propertyName(unit.propertyId) : "—"
      };
    });
  return { ...person, leases };
}

export async function listRentalOwners(): Promise<(Person & { properties: string[] })[]> {
  await delay();
  return db.owners.map((o) => ({
    ...o,
    properties: db.propertyOwners
      .filter((po) => po.ownerId === o.id)
      .map((po) => propertyName(po.propertyId))
  }));
}

export async function listBalances(): Promise<BalanceRow[]> {
  await delay();
  return [...db.balances];
}

export async function listLeases(): Promise<(Lease & { tenantName: string; unitLabel: string; propertyName: string })[]> {
  await delay();
  return db.leases.map((l) => {
    const unit = db.units.find((u) => u.id === l.unitId);
    return {
      ...l,
      tenantName: tenantName(l.tenantId),
      unitLabel: unit?.label ?? "—",
      propertyName: unit ? propertyName(unit.propertyId) : "—"
    };
  });
}

export async function getLease(id: string) {
  await delay(120);
  const l = db.leases.find((x) => x.id === id);
  if (!l) return undefined;
  const unit = db.units.find((u) => u.id === l.unitId);
  return {
    ...l,
    tenantName: tenantName(l.tenantId),
    unitLabel: unit?.label ?? "—",
    propertyName: unit ? propertyName(unit.propertyId) : "—"
  };
}

export type LeaseInput = {
  unitId: string;
  tenantId: string;
  startDate: string;
  endDate: string;
  rentCents: number;
  status: Lease["status"];
};

export async function saveLease(id: string | "new", input: LeaseInput): Promise<Lease> {
  await delay(200);
  if (id === "new") {
    const created: Lease = { id: `lease_${Date.now()}`, ...input };
    db.leases.push(created);
    const unit = db.units.find((u) => u.id === input.unitId);
    if (unit) unit.status = "OCCUPIED";
    return created;
  }
  const idx = db.leases.findIndex((l) => l.id === id);
  if (idx < 0) throw new Error("Lease not found");
  db.leases[idx] = { ...db.leases[idx], ...input };
  return db.leases[idx];
}

export async function listViolations(): Promise<Violation[]> {
  await delay();
  return [...db.violations];
}

export async function getViolation(id: string): Promise<Violation | undefined> {
  await delay(120);
  return db.violations.find((v) => v.id === id);
}

export async function updateViolationStatus(id: string, status: Violation["status"]): Promise<Violation> {
  await delay(180);
  const v = db.violations.find((x) => x.id === id);
  if (!v) throw new Error("Not found");
  v.status = status;
  v.timeline.push({ at: new Date().toISOString(), note: `Status → ${status}` });
  return v;
}

export async function listArchitectural(): Promise<ArchitecturalRequest[]> {
  await delay();
  return [...db.architecturalRequests];
}

export async function getArchitectural(id: string): Promise<ArchitecturalRequest | undefined> {
  await delay(120);
  return db.architecturalRequests.find((a) => a.id === id);
}

export async function updateArchitecturalStatus(
  id: string,
  status: ArchitecturalRequest["status"],
  committeeNote?: string
): Promise<ArchitecturalRequest> {
  await delay(180);
  const row = db.architecturalRequests.find((a) => a.id === id);
  if (!row) throw new Error("Not found");
  row.status = status;
  if (committeeNote !== undefined) row.committeeNote = committeeNote;
  return row;
}

export async function getPortalTenantPreview(): Promise<PortalTenantView> {
  await delay();
  const tenant = db.tenants[0];
  const leases = db.leases
    .filter((l) => l.tenantId === tenant.id)
    .map((l) => {
      const unit = db.units.find((u) => u.id === l.unitId);
      return {
        propertyName: unit ? propertyName(unit.propertyId) : "—",
        unitLabel: unit?.label ?? "—",
        endDate: l.endDate
      };
    });
  return {
    displayName: tenant.displayName,
    balanceCents: db.balances.find((b) => b.partyName === tenant.displayName)?.amountCents ?? 0,
    leases,
    maintenance: db.maintenance.map((m) => ({ id: m.id, summary: m.summary, status: m.status }))
  };
}

export async function getPortalOwnerPreview(): Promise<PortalOwnerView> {
  await delay();
  const owner = db.owners[1];
  return {
    displayName: owner.displayName,
    balanceCents: db.balances.find((b) => b.partyName === owner.displayName)?.amountCents ?? 0,
    statements: [
      { period: "Oct 2026", amountCents: 420000 },
      { period: "Sep 2026", amountCents: 0 }
    ],
    violations: db.violations.map((v) => ({
      id: v.id,
      unitLabel: v.unitLabel,
      status: v.status,
      fineCents: v.fineCents,
      category: v.category
    })),
    architecturalOpen: db.architecturalRequests.filter((a) => (a.status as string) === "SUBMITTED" || a.status === "COMMITTEE").length
  };
}

export type ResidentCenterBranding = {
  logoUrl: string;
  primaryColor: string;
  showPayments: boolean;
  showMaintenance: boolean;
  showDocuments: boolean;
};

let residentBranding: ResidentCenterBranding = {
  logoUrl: "",
  primaryColor: "#0f766e",
  showPayments: true,
  showMaintenance: true,
  showDocuments: true
};

export async function getResidentCenterBranding() {
  await delay(80);
  return { ...residentBranding };
}

export async function saveResidentCenterBranding(patch: Partial<ResidentCenterBranding>) {
  await delay(150);
  residentBranding = { ...residentBranding, ...patch };
  return residentBranding;
}

export async function listChartOfAccounts(): Promise<ChartAccount[]> {
  await delay();
  return [...db.chartOfAccounts];
}

export async function listGlTransactions(scope?: "PROPERTY" | "COMPANY" | "ALL"): Promise<GlTransaction[]> {
  await delay();
  const rows = db.glTransactions as GlTransaction[];
  if (!scope || scope === "ALL") return [...rows];
  return rows.filter((t) => t.scope === scope);
}

export async function addGlTransaction(input: Omit<GlTransaction, "id">) {
  await delay(150);
  const row: GlTransaction = { id: `gl_${Date.now()}`, ...input };
  (db.glTransactions as GlTransaction[]).push(row);
  return row;
}

export async function listBankAccounts(): Promise<BankAccount[]> {
  await delay();
  return [...db.bankAccounts];
}

export async function listBankLines(accountId?: string): Promise<BankLine[]> {
  await delay();
  const lines = db.bankLines as BankLine[];
  return accountId ? lines.filter((l) => l.bankAccountId === accountId) : [...lines];
}

export async function matchBankLine(id: string) {
  await delay(120);
  const line = (db.bankLines as BankLine[]).find((l) => l.id === id);
  if (line) line.matched = true;
  return line;
}

export async function listBills(): Promise<Bill[]> {
  await delay();
  return [...(db.bills as Bill[])];
}

export async function payBill(id: string) {
  await delay(150);
  const bill = (db.bills as Bill[]).find((b) => b.id === id);
  if (bill) bill.status = "PAID";
  return bill;
}

export async function listRecurring(): Promise<RecurringTransaction[]> {
  await delay();
  return [...(db.recurringTransactions as RecurringTransaction[])];
}

export async function listEftApprovals(): Promise<EftApproval[]> {
  await delay();
  return [...(db.eftApprovals as EftApproval[])];
}

export async function approveEft(id: string) {
  await delay(120);
  const row = (db.eftApprovals as EftApproval[]).find((e) => e.id === id);
  if (row) row.status = "APPROVED";
  return row;
}

export async function listBudget(): Promise<BudgetLine[]> {
  await delay();
  return [...db.budgetLines];
}

export async function getLatePenaltyRule(): Promise<LatePenaltyRule> {
  await delay();
  return { ...db.latePenaltyRule };
}

export async function saveLatePenaltyRule(rule: LatePenaltyRule) {
  await delay(150);
  db.latePenaltyRule = { ...rule };
  return db.latePenaltyRule;
}

export function computeLatePenaltyPreview(rule: LatePenaltyRule, principalCents: number, daysLate: number): LatePenaltyExample[] {
  const rows: LatePenaltyExample[] = [];
  for (const d of [10, 30, 60, 90]) {
    if (d <= daysLate) {
      const months = Math.max(0, (d - rule.graceDays) / 30);
      const penaltyCents = Math.round(principalCents * (rule.ratePercentMonthly / 100) * months);
      rows.push({ principalCents, daysLate: d, penaltyCents });
    }
  }
  if (!rows.length) rows.push({ principalCents, daysLate, penaltyCents: 0 });
  return rows;
}

export async function listMeterReadings(): Promise<MeterReading[]> {
  await delay();
  return [...db.meterReadings];
}

export async function previewMeterAllocation(readings: MeterReading[]): Promise<MeterAllocation[]> {
  await delay(100);
  const total = readings.reduce((s, r) => s + (r.current - r.previous), 0);
  const poolCents = 125000;
  return readings.map((r) => {
    const consumption = r.current - r.previous;
    return {
      unitLabel: r.unitLabel,
      consumption,
      shareCents: total ? Math.round((consumption / total) * poolCents) : 0
    };
  });
}

export async function listEnforcementCases(): Promise<EnforcementCase[]> {
  await delay();
  return [...db.enforcementCases];
}

export async function listAtsCodes(): Promise<AtsDebtCode[]> {
  await delay();
  return [...db.atsCodes];
}

export async function regenerateAtsCode(id: string) {
  await delay(150);
  const row = db.atsCodes.find((a) => a.id === id);
  if (!row) throw new Error("Not found");
  row.code10 = String(Math.floor(1e9 + Math.random() * 9e9));
  return row;
}