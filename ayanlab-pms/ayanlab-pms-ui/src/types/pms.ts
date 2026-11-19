export type PropertyMode = "RENTAL" | "ASSOCIATION";

export type Property = {
  id: string;
  orgId: string;
  name: string;
  mode: PropertyMode;
  address: string;
  unitCount: number;
};

export type PropertyNote = {
  id: string;
  createdAt: string;
  body: string;
  author: string;
};

export type PropertyFile = {
  id: string;
  name: string;
  sizeKb: number;
};

export type PropertyDetail = Property & {
  units: PropertyUnit[];
  ownerNames: string[];
  notes: PropertyNote[];
  files: PropertyFile[];
};

export type PropertyUnit = {
  id: string;
  propertyId: string;
  label: string;
  status: "VACANT" | "OCCUPIED" | "NOTICE";
  monthlyRentCents?: number;
};

export type Person = {
  id: string;
  displayName: string;
  email: string;
  phone?: string;
};

export type Lease = {
  id: string;
  unitId: string;
  tenantId: string;
  status: "DRAFT" | "ACTIVE" | "EXPIRING" | "ENDED";
  startDate: string;
  endDate: string;
  rentCents: number;
};

export type BalanceRow = {
  id: string;
  propertyName: string;
  partyName: string;
  partyType: "TENANT" | "OWNER";
  amountCents: number;
  daysPastDue: number;
};

export type MaintenanceRequest = {
  id: string;
  propertyName: string;
  summary: string;
  status: "OPEN" | "OVERDUE" | "DONE";
  createdAt: string;
};

export type ActivityEvent = {
  id: string;
  summary: string;
  createdAt: string;
};

export type Association = {
  id: string;
  name: string;
  city: string;
  unitCount: number;
  delinquentCents: number;
};

export type Violation = {
  id: string;
  associationId: string;
  unitLabel: string;
  status: "OPEN" | "HEARING" | "CLOSED";
  fineCents: number;
  category: string;
  description: string;
  openedAt: string;
  timeline: { at: string; note: string }[];
};

export type ArchitecturalRequest = {
  id: string;
  associationId: string;
  unitLabel: string;
  title: string;
  description: string;
  status: "SUBMITTED" | "COMMITTEE" | "APPROVED" | "DENIED";
  submittedAt: string;
  committeeNote?: string;
};

export type RentRollRow = {
  unitId: string;
  propertyId: string;
  propertyName: string;
  unitLabel: string;
  unitStatus: PropertyUnit["status"];
  tenantName?: string;
  leaseId?: string;
  leaseStatus?: Lease["status"];
  rentCents?: number;
  leaseEnd?: string;
};

export type TenantDetail = Person & {
  leases: (Lease & { unitLabel: string; propertyName: string })[];
};

export type PortalTenantView = {
  displayName: string;
  balanceCents: number;
  leases: { propertyName: string; unitLabel: string; endDate: string }[];
  maintenance: { id: string; summary: string; status: string }[];
};

export type PortalOwnerView = {
  displayName: string;
  balanceCents: number;
  statements: { period: string; amountCents: number }[];
  violations: Pick<Violation, "id" | "unitLabel" | "status" | "fineCents" | "category">[];
  architecturalOpen: number;
};