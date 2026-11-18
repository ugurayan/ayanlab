import type { User } from "../types/auth";
import type { JobProfile } from "../types/profile";
import type { Task } from "../types/tasks";
import type { Job } from "../types/jobs";
import type { Transaction } from "../types/transactions";
import type { VerificationTask, VerificationRequest } from "../types/verification";

export type Org = { id: string; name: string; country: string; createdAt: string };
export type Location = { id: string; orgId: string; name: string; city: string; createdAt: string };
export type Shift = { id: string; orgId: string; locationId: string; role: string; startAt: string; endAt: string; headcount: number; status: "DRAFT"|"PUBLISHED"|"FILLED"|"COMPLETED" };
export type Application = { id: string; shiftId: string; workerId: string; status: "APPLIED"|"SHORTLISTED"|"HIRED"|"REJECTED" };
export type WorkerIncidentType = "late_start" | "no_show" | "early_leave" | "policy_violation" | "quality_issue" | "other";
export type WorkerIncidentSeverity = "low" | "medium" | "high" | "critical";
export type WorkerIncident = {
  id: string;
  workerId: string;
  type: WorkerIncidentType;
  severity: WorkerIncidentSeverity;
  occurredAt: string;
  summary: string;
};

export type WorkerEarningsSummary = {
  currency: string; // e.g. "CHF"
  lastMonthCents: number;
  last3MonthsCents: number;
  last6MonthsCents: number;
  last12MonthsCents: number;
};

export type Worker = {
  id: string;
  displayName: string;
  rating: number; // avg rating 0..5
  ratingCount: number;
  reliability: number; // 0..1
  acceptanceRate: number; // 0..1
  jobProfiles: string[];
  skills: string[];

  // attendance / punctuality
  onTimeStarts: number;
  lateStarts: number;

  // outcomes
  jobsCompleted: number;
  jobsNotCompleted: number;
  noShows: number;

  // incidents
  criticalIncidents: number;
  reliabilityIncidents: number;

  // pay / performance
  avgHourlyRateCents: number;
  earnings: WorkerEarningsSummary;
  lastIncidents: WorkerIncident[]; // store last incidents for hover panel

  notes?: string;
};
export type Document = { id: string; workerId: string; type: string; status: "PENDING"|"APPROVED"|"REJECTED"; createdAt: string; expiresAt?: string };
export type Invoice = { id: string; orgId: string; amountCents: number; currency: string; status: "DUE"|"PAID"|"VOID"; issuedAt: string };
export type AuditLog = { id: string; actorEmail: string; action: string; target: string; createdAt: string };

const now = () => new Date().toISOString();
const id = (p: string) => `${p}_${Math.random().toString(16).slice(2, 10)}`;
const daysAgo = (n: number) => new Date(Date.now() - n * 24 * 60 * 60 * 1000).toISOString();

export const mockUsers: Record<string, { password: string; user: User }> = {
  "next.is.future@icloud.com": {
    password: "Password123@",
    user: {
      id: "u_platform_admin",
      email: "next.is.future@icloud.com",
      displayName: "Platform Admin",
      // Technical role + main role: System Admin
      roles: ["PLATFORM_ADMIN", "4a3207bf-cc00-46db-8db7-42e8d9568b8b"],
      permissions: [],
      scope: {}
    }
  },
  "admin@acme.test": {
    password: "password",
    user: {
      id: "u_org_admin",
      email: "admin@acme.test",
      displayName: "Acme Org Admin",
      // Technical role + main role: Business Admin
      roles: ["ORG_ADMIN", "4b029d24-e658-4a65-8d76-ddbc2627a05c"],
      permissions: [],
      scope: { orgId: "org_acme", locationIds: ["loc_acme_1","loc_acme_2"] }
    }
  },
  "manager@acme.test": {
    password: "password",
    user: {
      id: "u_manager",
      email: "manager@acme.test",
      displayName: "Site Manager",
      // Technical role + main role: Business Member
      roles: ["LOCATION_MANAGER", "055978b5-7e39-494d-9725-a0dca32dda6c"],
      permissions: [],
      scope: { orgId: "org_acme", locationIds: ["loc_acme_1"] }
    }
  },
  "finance@acme.test": {
    password: "password",
    user: {
      id: "u_finance",
      email: "finance@acme.test",
      displayName: "Finance",
      // Technical role + main role: Business Admin
      roles: ["ORG_FINANCE", "4b029d24-e658-4a65-8d76-ddbc2627a05c"],
      permissions: [],
      scope: { orgId: "org_acme" }
    }
  },
  "agent@staffing.test": {
    password: "password",
    user: {
      id: "u_agent",
      email: "agent@staffing.test",
      displayName: "Staffing Agent",
      // Technical role + main role: Staff
      roles: ["AGENT_USER", "a0fdb0b8-a079-4315-ae21-31e68cd80778"],
      permissions: [],
      scope: { orgId: "org_acme" }
    }
  },
  "notApproved@platform.test": {
    password: "password",
    user: {
      id: "u_not_approved",
      email: "notApproved@platform.test",
      displayName: "Not Approved User",
      // Technical role + main role: Worker
      roles: ["WORKER", "9c2b8be8-bdec-4484-96b6-a2145b654b4d"],
      permissions: ["worker:read", "shift:read"],
      scope: {},
      profileApproved: false
    }
  }
};

export const db = {
  orgs: [
    { id: "org_acme", name: "Acme Retail GmbH", country: "CH", createdAt: now() },
    { id: "org_zen", name: "Zen Logistics AG", country: "DE", createdAt: now() }
  ] as Org[],
  locations: [
    { id: "loc_acme_1", orgId: "org_acme", name: "Acme — Downtown", city: "Zurich", createdAt: now() },
    { id: "loc_acme_2", orgId: "org_acme", name: "Acme — Airport", city: "Zurich", createdAt: now() },
    { id: "loc_zen_1", orgId: "org_zen", name: "Zen — Warehouse A", city: "Berlin", createdAt: now() }
  ] as Location[],
  shifts: [
    { id: "sh_1", orgId: "org_acme", locationId: "loc_acme_1", role: "Retail Associate", startAt: now(), endAt: now(), headcount: 2, status: "PUBLISHED" },
    { id: "sh_2", orgId: "org_acme", locationId: "loc_acme_2", role: "Cashier", startAt: now(), endAt: now(), headcount: 1, status: "DRAFT" }
  ] as Shift[],
  applications: [
    { id: "app_1", shiftId: "sh_1", workerId: "w_1", status: "APPLIED" }
  ] as Application[],
  workers: [
    {
      id: "w_1",
      displayName: "Aylin K.",
      rating: 4.6,
      ratingCount: 124,
      reliability: 0.93,
      acceptanceRate: 0.86,
      jobProfiles: ["Retail Associate", "Warehouse Operative"],
      skills: ["POS", "Stocking", "Customer Service"],
      onTimeStarts: 87,
      lateStarts: 6,
      jobsCompleted: 92,
      jobsNotCompleted: 2,
      noShows: 1,
      criticalIncidents: 0,
      reliabilityIncidents: 2,
      avgHourlyRateCents: 3200,
      earnings: {
        currency: "CHF",
        lastMonthCents: 412000,
        last3MonthsCents: 1185000,
        last6MonthsCents: 2315000,
        last12MonthsCents: 4740000,
      },
      lastIncidents: [
        {
          id: id("inc"),
          workerId: "w_1",
          type: "late_start",
          severity: "low",
          occurredAt: daysAgo(9),
          summary: "Arrived 8 minutes late due to public transport delay.",
        },
        {
          id: id("inc"),
          workerId: "w_1",
          type: "quality_issue",
          severity: "medium",
          occurredAt: daysAgo(23),
          summary: "Stock labels were misplaced; corrected after coaching.",
        },
      ],
      notes: "Strong retail performance. Coach on punctuality during peak season.",
    },
    {
      id: "w_2",
      displayName: "Emre S.",
      rating: 4.2,
      ratingCount: 58,
      reliability: 0.88,
      acceptanceRate: 0.73,
      jobProfiles: ["Cashier", "Picker/Packer"],
      skills: ["Picking", "Packing", "RF Scanner"],
      onTimeStarts: 41,
      lateStarts: 9,
      jobsCompleted: 45,
      jobsNotCompleted: 3,
      noShows: 2,
      criticalIncidents: 1,
      reliabilityIncidents: 4,
      avgHourlyRateCents: 2900,
      earnings: {
        currency: "CHF",
        lastMonthCents: 205000,
        last3MonthsCents: 612000,
        last6MonthsCents: 1320000,
        last12MonthsCents: 2410000,
      },
      lastIncidents: [
        {
          id: id("inc"),
          workerId: "w_2",
          type: "no_show",
          severity: "critical",
          occurredAt: daysAgo(14),
          summary: "No-show for an assigned shift; unreachable by phone.",
        },
        {
          id: id("inc"),
          workerId: "w_2",
          type: "late_start",
          severity: "medium",
          occurredAt: daysAgo(31),
          summary: "Arrived 18 minutes late; no prior notice.",
        },
        {
          id: id("inc"),
          workerId: "w_2",
          type: "policy_violation",
          severity: "high",
          occurredAt: daysAgo(58),
          summary: "Ignored safety protocol (PPE). Received written warning.",
        },
      ],
      notes: "Monitor reliability closely; requires approval for premium shifts.",
    }
  ] as Worker[],
  jobProfiles: [
    {
      id: "jp_1",
      workerId: "w_1",
      label: "Retail Associate",
      approvalStatus: "approved"
    },
    {
      id: "jp_2",
      workerId: "w_1",
      label: "Warehouse Operative",
      approvalStatus: "pending_admin"
    },
    {
      id: "jp_3",
      workerId: "w_2",
      label: "Cashier",
      approvalStatus: "approved"
    }
  ] as JobProfile[],
  documents: [
    { id: "doc_1", workerId: "w_1", type: "Right to Work", status: "APPROVED", createdAt: now() },
    {
      id: "doc_2",
      workerId: "w_2",
      type: "Food Safety",
      status: "PENDING",
      createdAt: now(),
      expiresAt: new Date(Date.now()+1000*60*60*24*120).toISOString()
    }
  ] as Document[],
  invoices: [
    { id: "inv_1", orgId: "org_acme", amountCents: 124500, currency: "CHF", status: "DUE", issuedAt: now() }
  ] as Invoice[],
  audit: [
    { id: "aud_1", actorEmail: "next.is.future@icloud.comt", action: "ORG_CREATED", target: "org_acme", createdAt: now() }
  ] as AuditLog[],
  /** User audit: login, profile changes, permission changes, etc. */
  auditUser: [
    { id: "aud_u_1", actorEmail: "admin@acme.test", action: "LOGIN", target: "auth", createdAt: now() },
    { id: "aud_u_2", actorEmail: "manager@acme.test", action: "PROFILE_UPDATED", target: "profile", createdAt: now() },
    { id: "aud_u_3", actorEmail: "next.is.future@icloud.com", action: "ROLE_ASSIGNED", target: "user_u_org_admin", createdAt: now() }
  ] as AuditLog[],
  /** System audit: org/shift/worker/system-level events. */
  auditSystem: [
    { id: "aud_s_1", actorEmail: "system", action: "ORG_CREATED", target: "org_acme", createdAt: now() },
    { id: "aud_s_2", actorEmail: "system", action: "SHIFT_CREATED", target: "sh_1", createdAt: now() },
    { id: "aud_s_3", actorEmail: "system", action: "WORKER_UPDATED", target: "w_1", createdAt: now() }
  ] as AuditLog[],
  tasks: [
    {
      id: "task_1",
      sectionId: "sec_1",
      title: "Create the landing/marketing page and host it on the beta channel",
      notes: "Coordinate with marketing for copy and assets.",
      dueDate: new Date().toISOString(),
      priority: "high",
      status: "open",
      createdAt: now(),
      assignedWorkerId: "w_1"
    },
    {
      id: "task_2",
      sectionId: "sec_1",
      title: "Move dependency system to Yarn for easier package management",
      dueDate: new Date().toISOString(),
      priority: "medium",
      status: "open",
      createdAt: now(),
      assignedWorkerId: "w_2"
    },
    {
      id: "task_3",
      sectionId: "sec_2",
      title: "Re-think the current API restrictions to loosen them a bit",
      dueDate: new Date().toISOString(),
      priority: "low",
      status: "open",
      createdAt: now()
    }
  ] as Task[],
  jobs: [
    {
      id: "job_1",
      title: "Temporary Payroll Specialist (m/w/d) | 80-100% | Neuendorf SO",
      company: "H&M Hennes & Mauritz AG",
      location: "Neuendorf SO",
      distanceLabel: "57.9km",
      address: "Industriestrasse 16, Neuendorf, 4623",
      referenceCode: "NADDJP00003660",
      logoText: "H&M",
      type: "temporary",
      schedule: "part-time",
      workloadLabel: "80 - 100%",
      salaryLabel: "CHF 30 per hour",
      contractLabel: "Temporary contract",
      startDateLabel: "Start on 4 Jan",
      experienceLabel: "0 years",
      description:
        "As a payroll specialist, you support the HR team with payroll processing, data checks and employee queries. You work closely with store management and finance to ensure accurate and timely payouts.",
      venueMapLabel: "Neuendorf logistics hub"
    },
    {
      id: "job_2",
      title: "Mitarbeiter:in Cleaning | 30% | Zurich ZH | NADDJP00003675",
      company: "SBB CFF FFS",
      location: "Zurich ZH",
      distanceLabel: "2.3km",
      address: "Langstrasse 175, Zurich, 8005",
      referenceCode: "NADDJP00003675",
      logoText: "SBB",
      type: "permanent",
      schedule: "part-time",
      workloadLabel: "30%",
      salaryLabel: "CHF 27 per hour",
      contractLabel: "Permanent contract",
      startDateLabel: "Start on 1 Feb",
      experienceLabel: "0-1 years",
      description:
        "You are responsible for cleaning and basic maintenance in busy station areas, ensuring a welcoming environment for passengers.",
      venueMapLabel: "Zurich main station"
    },
    {
      id: "job_3",
      title: "Verkäufer*in Fleisch-/Fischtheke | Schlieren ZH | 80-100%",
      company: "Genossenschaft Migros Zurich - Group",
      location: "Schlieren ZH",
      distanceLabel: "9.4km",
      address: "Rietbachstrasse 14, Schlieren, 8952",
      referenceCode: "NADDJP00003681",
      logoText: "MIGROS",
      type: "permanent",
      schedule: "part-time",
      workloadLabel: "80 - 100%",
      salaryLabel: "CHF 32 per hour",
      contractLabel: "Permanent contract",
      startDateLabel: "Start on 15 Feb",
      experienceLabel: "1-3 years",
      description:
        "You advise customers at the fresh counter, prepare meat and fish products and ensure attractive presentation and hygiene standards.",
      venueMapLabel: "Migros Schlieren retail center"
    }
  ] as Job[],
  transactions: (() => {
    const txs: Transaction[] = [];
    const orgs = ["org_acme", "org_zen"];
    const workers = ["w_1", "w_2"];
    const jobs = ["job_1", "job_2", "job_3"];
    const jobTitles = ["Retail Associate", "Cashier", "Warehouse Operative", "Cleaning Staff", "Kitchen Assistant"];
    const shifts = ["sh_1", "sh_2"];
    const paymentTypes: Array<"bank_transfer" | "credit_card" | "debit_card" | "paypal" | "other"> = ["bank_transfer", "credit_card", "debit_card"];
    const statuses: Array<"pending" | "completed" | "failed" | "refunded"> = ["completed", "pending", "failed", "refunded"];
    const currencies = ["CHF", "EUR", "USD"];
    const banks = ["UBS", "Credit Suisse", "ZKB", "PostFinance", "Raiffeisen"];
    const cardBrands = ["Visa", "Mastercard", "American Express"];
    const admins = ["next.is.future@icloud.com", "admin@acme.test", "finance@acme.test"];
    const adminNames = ["Platform Admin", "Acme Admin", "Finance Team"];

    // Generate 100+ transactions
    for (let i = 0; i < 120; i++) {
      const isCredit = Math.random() > 0.3; // 70% credit (payouts to workers)
      const orgId = orgs[Math.floor(Math.random() * orgs.length)];
      const workerId = isCredit ? workers[Math.floor(Math.random() * workers.length)] : undefined;
      const jobId = jobs[Math.floor(Math.random() * jobs.length)];
      const jobTitle = jobTitles[Math.floor(Math.random() * jobTitles.length)];
      const shiftId = shifts[Math.floor(Math.random() * shifts.length)];
      const status = statuses[Math.floor(Math.random() * statuses.length)];
      const currency = currencies[Math.floor(Math.random() * currencies.length)];
      const paymentType = paymentTypes[Math.floor(Math.random() * paymentTypes.length)];
      const amountCents = Math.floor(Math.random() * 500000) + 5000; // 50-5000 CHF/EUR/USD
      const feesCents = Math.floor(amountCents * 0.02); // 2% fee
      const daysAgo = Math.floor(Math.random() * 90); // Last 90 days
      const createdAt = new Date(Date.now() - daysAgo * 24 * 60 * 60 * 1000).toISOString();
      const processedAt = status === "completed" ? new Date(Date.now() - (daysAgo - 1) * 24 * 60 * 60 * 1000).toISOString() : undefined;
      const updatedAt = Math.random() > 0.7 ? new Date(Date.now() - (daysAgo - 2) * 24 * 60 * 60 * 1000).toISOString() : undefined;
      const adminIdx = Math.floor(Math.random() * admins.length);

      const tx: Transaction = {
        id: `tx_${i + 1}`,
        workerId,
        orgId,
        amountCents,
        currency,
        direction: isCredit ? "credit" : "debit",
        status,
        description: isCredit
          ? `Payout for ${jobTitle} shift - ${shiftId}`
          : `Payment for ${jobTitle} job posting and worker payouts`,
        jobId,
        jobTitle,
        shiftId,
        paymentType,
        feesCents,
        createdAt,
        processedAt,
        createdBy: admins[adminIdx],
        createdByName: adminNames[adminIdx],
        updatedBy: updatedAt ? admins[Math.floor(Math.random() * admins.length)] : undefined,
        updatedByName: updatedAt ? adminNames[Math.floor(Math.random() * adminNames.length)] : undefined,
        updatedAt,
        externalReferenceId: `EXT-${Math.random().toString(36).substring(2, 10).toUpperCase()}`,
        relatedInvoiceId: Math.random() > 0.5 ? `inv_${Math.floor(Math.random() * 5) + 1}` : undefined
      };

      // Add payment method details
      if (paymentType === "bank_transfer") {
        tx.bankName = banks[Math.floor(Math.random() * banks.length)];
        tx.bankAccountLast4 = String(Math.floor(Math.random() * 9000) + 1000);
      } else if (paymentType === "credit_card" || paymentType === "debit_card") {
        tx.creditCardLast4 = String(Math.floor(Math.random() * 9000) + 1000);
        tx.creditCardBrand = cardBrands[Math.floor(Math.random() * cardBrands.length)];
      }

      txs.push(tx);
    }

    return txs.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  })() as Transaction[],
  verificationTasks: (() => {
    const tasks: VerificationTask[] = [];
    const workers = [
      { id: "w_1", name: "Aylin K.", email: "aylin@example.com" },
      { id: "w_2", name: "Emre S.", email: "emre@example.com" },
      { id: "w_3", name: "Maria L.", email: "maria@example.com" },
      { id: "w_4", name: "John D.", email: "john@example.com" },
      { id: "w_5", name: "Sarah M.", email: "sarah@example.com" }
    ];
    const types: VerificationTask["type"][] = [
      "name_surname", "address", "driving_licence", "ssn", "work_permit", "itin",
      "employer_id", "national_id", "birth_certificate", "utility_bill", "bank_statement",
      "lease_contract", "green_card", "tax_document", "training_certificate",
      "education_diploma", "safety_certificate", "criminal_record", "reference_letter", "photo"
    ];
    const categories: VerificationTask["category"][] = ["identity", "address", "workEligibility", "certificate", "photo", "background"];
    const statuses: VerificationTask["status"][] = ["pending", "reviewing", "accepted", "rejected", "needs_attention", "needs_interview"];
    const priorities: VerificationTask["priority"][] = ["low", "medium", "high"];
    const documentUrls = [
      "https://via.placeholder.com/400x600/4F46E5/FFFFFF?text=Document+Front",
      "https://via.placeholder.com/400x600/059669/FFFFFF?text=Document+Back",
      "https://via.placeholder.com/600x400/DC2626/FFFFFF?text=Photo+ID"
    ];

    for (let i = 0; i < 25; i++) {
      const worker = workers[Math.floor(Math.random() * workers.length)];
      const type = types[Math.floor(Math.random() * types.length)];
      const status = statuses[Math.floor(Math.random() * statuses.length)];
      const priority = priorities[Math.floor(Math.random() * priorities.length)];
      const category = categories[Math.floor(Math.random() * categories.length)];
      const daysAgo = Math.floor(Math.random() * 30);
      const submittedAt = new Date(Date.now() - daysAgo * 24 * 60 * 60 * 1000).toISOString();
      const reviewedAt = status !== "pending" && status !== "reviewing" ? new Date(Date.now() - (daysAgo - 1) * 24 * 60 * 60 * 1000).toISOString() : undefined;
      const assignedTo = status === "reviewing" ? "u_platform_admin" : undefined;

      tasks.push({
        id: `vt_${i + 1}`,
        workerId: worker.id,
        workerName: worker.name,
        workerEmail: worker.email,
        type,
        category,
        status,
        documentUrls: documentUrls.slice(0, Math.floor(Math.random() * 3) + 1),
        submittedAt,
        assignedTo,
        reviewedAt,
        reviewedBy: reviewedAt ? "u_platform_admin" : undefined,
        reviewNotes: reviewedAt && Math.random() > 0.5 ? "Document verified successfully" : undefined,
        priority
      });
    }

    return tasks.sort((a, b) => {
      // Unverified first, then by priority and date
      if (a.status === "pending" && b.status !== "pending") return -1;
      if (a.status !== "pending" && b.status === "pending") return 1;
      if (a.priority === "high" && b.priority !== "high") return -1;
      if (a.priority !== "high" && b.priority === "high") return 1;
      return new Date(b.submittedAt).getTime() - new Date(a.submittedAt).getTime();
    });
  })() as VerificationTask[],
  verificationRequests: [] as VerificationRequest[]
};

export function createShift(input: Omit<Shift, "id" | "status"> & { status?: Shift["status"] }): Shift {
  const sh: Shift = { id: id("sh"), status: input.status ?? "DRAFT", ...input };
  db.shifts.unshift(sh);
  db.auditSystem.unshift({ id: id("aud"), actorEmail: "system", action: "SHIFT_CREATED", target: sh.id, createdAt: now() });
  db.audit.unshift({ id: id("aud"), actorEmail: "system", action: "SHIFT_CREATED", target: sh.id, createdAt: now() });
  return sh;
}

export function updateWorker(workerId: string, patch: Partial<Worker>): Worker {
  const idx = db.workers.findIndex((w) => w.id === workerId);
  if (idx < 0) throw new Error("Worker not found");
  const next: Worker = { ...db.workers[idx], ...patch };
  db.workers[idx] = next;
  db.auditSystem.unshift({ id: id("aud"), actorEmail: "system", action: "WORKER_UPDATED", target: workerId, createdAt: now() });
  db.audit.unshift({ id: id("aud"), actorEmail: "system", action: "WORKER_UPDATED", target: workerId, createdAt: now() });
  return next;
}
