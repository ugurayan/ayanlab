import type { User } from "../types/auth";
import type {
  ActivityEvent,
  Association,
  BalanceRow,
  Lease,
  MaintenanceRequest,
  Person,
  Property,
  PropertyUnit,
  ArchitecturalRequest,
  Violation
} from "../types/pms";

export type Org = { id: string; name: string };

const daysAgo = (n: number) => new Date(Date.now() - n * 24 * 60 * 60 * 1000).toISOString();
const daysFromNow = (n: number) => new Date(Date.now() + n * 24 * 60 * 60 * 1000).toISOString().slice(0, 10);

export const mockOrg: Org = { id: "org_demo", name: "AyanLab Demo PM" };

export const mockUsers: Record<string, { password: string; user: User }> = {
  "pm@demo.ayanlab": {
    password: "password",
    user: {
      id: "u_pm",
      email: "pm@demo.ayanlab",
      displayName: "Dana Property Manager",
      roles: ["PROPERTY_MANAGER"],
      orgId: mockOrg.id,
      orgName: mockOrg.name
    }
  },
  "accountant@demo.ayanlab": {
    password: "password",
    user: {
      id: "u_acc",
      email: "accountant@demo.ayanlab",
      displayName: "Alex Accountant",
      roles: ["ACCOUNTANT"],
      orgId: mockOrg.id,
      orgName: mockOrg.name
    }
  },
  "leasing@demo.ayanlab": {
    password: "password",
    user: {
      id: "u_lease",
      email: "leasing@demo.ayanlab",
      displayName: "Lina Leasing",
      roles: ["LEASING_AGENT"],
      orgId: mockOrg.id,
      orgName: mockOrg.name
    }
  },
  "owner@demo.ayanlab": {
    password: "password",
    user: {
      id: "u_owner",
      email: "owner@demo.ayanlab",
      displayName: "Omar Homeowner",
      roles: ["HOME_OWNER"],
      orgId: mockOrg.id,
      orgName: mockOrg.name
    }
  },
  "field@demo.ayanlab": {
    password: "password",
    user: {
      id: "u_field",
      email: "field@demo.ayanlab",
      displayName: "Selim Field",
      roles: ["FIELD_STAFF"],
      orgId: mockOrg.id,
      orgName: mockOrg.name
    }
  }
};

export const db = {
  properties: [
    {
      id: "prop_1",
      orgId: mockOrg.id,
      name: "Maple Court Apartments",
      mode: "RENTAL",
      address: "1200 Maple St, Austin, TX",
      unitCount: 24
    },
    {
      id: "prop_2",
      orgId: mockOrg.id,
      name: "Riverside Duplex",
      mode: "RENTAL",
      address: "88 River Rd, Austin, TX",
      unitCount: 2
    },
    {
      id: "prop_3",
      orgId: mockOrg.id,
      name: "Sunset Villas HOA",
      mode: "ASSOCIATION",
      address: "Çamlıca Sitesi, İstanbul",
      unitCount: 48
    }
  ] satisfies Property[],

  units: [
    { id: "u_101", propertyId: "prop_1", label: "101", status: "OCCUPIED", monthlyRentCents: 145000 },
    { id: "u_102", propertyId: "prop_1", label: "102", status: "VACANT", monthlyRentCents: 150000 },
    { id: "u_201", propertyId: "prop_1", label: "201", status: "NOTICE", monthlyRentCents: 155000 },
    { id: "u_a", propertyId: "prop_2", label: "Unit A", status: "OCCUPIED", monthlyRentCents: 210000 },
    { id: "u_b", propertyId: "prop_2", label: "Unit B", status: "VACANT", monthlyRentCents: 205000 }
  ] satisfies PropertyUnit[],

  tenants: [
    { id: "t_1", displayName: "Jordan Lee", email: "jordan@example.com", phone: "+1 512-555-0101" },
    { id: "t_2", displayName: "Sam Rivera", email: "sam@example.com", phone: "+1 512-555-0102" }
  ] satisfies Person[],

  owners: [
    { id: "o_1", displayName: "Investor Group LLC", email: "investor@example.com" },
    { id: "o_2", displayName: "Ayşe Yılmaz", email: "ayse@example.com", phone: "+90 532 000 00 01" }
  ] satisfies Person[],

  leases: [
    {
      id: "lease_1",
      unitId: "u_101",
      tenantId: "t_1",
      status: "ACTIVE",
      startDate: "2024-06-01",
      endDate: "2025-05-31",
      rentCents: 145000
    },
    {
      id: "lease_2",
      unitId: "u_201",
      tenantId: "t_2",
      status: "EXPIRING",
      startDate: "2023-08-01",
      endDate: new Date(Date.now() + 20 * 86400000).toISOString().slice(0, 10),
      rentCents: 155000
    }
  ] as Lease[],

  balances: [
    {
      id: "bal_1",
      propertyName: "Maple Court",
      partyName: "Sam Rivera",
      partyType: "TENANT",
      amountCents: 155000,
      daysPastDue: 12
    },
    {
      id: "bal_2",
      propertyName: "Sunset Villas HOA",
      partyName: "Ayşe Yılmaz",
      partyType: "OWNER",
      amountCents: 420000,
      daysPastDue: 45
    }
  ] satisfies BalanceRow[],

  maintenance: [
    {
      id: "mr_1",
      propertyName: "Maple Court",
      summary: "HVAC not cooling — Unit 201",
      status: "OVERDUE",
      createdAt: daysAgo(5)
    },
    {
      id: "mr_2",
      propertyName: "Riverside Duplex",
      summary: "Leaky faucet — Unit A",
      status: "OPEN",
      createdAt: daysAgo(1)
    }
  ] satisfies MaintenanceRequest[],

  activity: [
    { id: "ev_1", summary: "Lease renewal sent — Jordan Lee", createdAt: daysAgo(0) },
    { id: "ev_2", summary: "Rent charge posted — Maple Court", createdAt: daysAgo(1) },
    { id: "ev_3", summary: "Violation opened — Sunset Villas A-12", createdAt: daysAgo(2) }
  ] satisfies ActivityEvent[],

  associations: [
    { id: "assoc_1", name: "Sunset Villas HOA", city: "İstanbul", unitCount: 48, delinquentCents: 890000 }
  ] satisfies Association[],

  violations: [
    {
      id: "vio_1",
      associationId: "assoc_1",
      unitLabel: "A-12",
      status: "OPEN",
      fineCents: 150000,
      category: "Parking",
      description: "Unauthorized vehicle in visitor slot for more than 48 hours",
      openedAt: daysAgo(4),
      timeline: [
        { at: daysAgo(4), note: "Notice mailed to owner" },
        { at: daysAgo(2), note: "Hearing scheduled — board 11/25" }
      ]
    },
    {
      id: "vio_2",
      associationId: "assoc_1",
      unitLabel: "B-04",
      status: "HEARING",
      fineCents: 75000,
      category: "Noise",
      description: "Repeated quiet-hours complaints",
      openedAt: daysAgo(10),
      timeline: [{ at: daysAgo(10), note: "First warning issued" }]
    }
  ] as Violation[],

  architecturalRequests: [
    {
      id: "arch_1",
      associationId: "assoc_1",
      unitLabel: "C-08",
      title: "Balcony enclosure — aluminum frame",
      description: "Replace glass panels; color RAL 7016 per CC&R exhibit B.",
      status: "COMMITTEE",
      submittedAt: daysAgo(6),
      committeeNote: "Awaiting elevation photo from owner"
    },
    {
      id: "arch_2",
      associationId: "assoc_1",
      unitLabel: "A-12",
      title: "Front door stain — cedar",
      description: "Match existing building stain palette.",
      status: "APPROVED",
      submittedAt: daysAgo(20),
      committeeNote: "Approved with 12-month maintenance reminder"
    }
  ] as ArchitecturalRequest[],

  propertyOwners: [
    { propertyId: "prop_1", ownerId: "o_1" },
    { propertyId: "prop_2", ownerId: "o_1" }
  ] as { propertyId: string; ownerId: string }[],

  insuranceGaps: [
    { tenantName: "Sam Rivera", propertyName: "Maple Court", status: "MISSING" as const },
    { tenantName: "Jordan Lee", propertyName: "Maple Court", status: "ACTIVE" as const }
  ],

  listingStats: { vacant: 3, applicationsPending: 2 },

  bankUnmatched: [
    { id: "bf_1", description: "ACH DEP — UNKNOWN", amountCents: 145000, date: daysAgo(0) }
  ],

  propertyNotes: {
    prop_1: [
      { id: "n1", createdAt: daysAgo(3), body: "Annual fire inspection due Q1.", author: "Dana PM" },
      { id: "n2", createdAt: daysAgo(14), body: "Pool key fobs restocked.", author: "Maintenance" }
    ],
    prop_2: [{ id: "n3", createdAt: daysAgo(7), body: "Owner approved new AC for Unit B.", author: "Dana PM" }]
  } as Record<string, import("../types/pms").PropertyNote[]>,

  propertyFiles: {
    prop_1: [
      { id: "f1", name: "Insurance-2026.pdf", sizeKb: 420 },
      { id: "f2", name: "Lease-template.docx", sizeKb: 88 }
    ],
    prop_2: [{ id: "f3", name: "Deed-Riverside.pdf", sizeKb: 1200 }]
  } as Record<string, import("../types/pms").PropertyFile[]>,

  chartOfAccounts: [
    { id: "coa_1000", code: "1000", name: "Operating Cash", type: "ASSET" as const },
    { id: "coa_1100", code: "1100", name: "Trust Cash", type: "ASSET" as const, parentId: "coa_1000" },
    { id: "coa_2000", code: "2000", name: "Accounts Payable", type: "LIABILITY" as const },
    { id: "coa_4000", code: "4000", name: "Rental Income", type: "INCOME" as const },
    { id: "coa_5000", code: "5000", name: "Repairs & Maintenance", type: "EXPENSE" as const }
  ],

  glTransactions: [
    {
      id: "gl_1",
      date: daysAgo(2),
      description: "Rent charge — Unit 101",
      amountCents: 145000,
      scope: "PROPERTY" as const,
      propertyName: "Maple Court Apartments",
      kind: "CHARGE" as const
    },
    {
      id: "gl_2",
      date: daysAgo(1),
      description: "Mgmt fee — company",
      amountCents: 25000,
      scope: "COMPANY" as const,
      kind: "PAYMENT" as const
    }
  ],

  bankAccounts: [
    { id: "bank_1", name: "Trust — Maple Court", last4: "4821", balanceCents: 2450000 },
    { id: "bank_2", name: "Operating", last4: "9910", balanceCents: 180000 }
  ],

  bankLines: [
    {
      id: "bl_1",
      bankAccountId: "bank_1",
      date: daysAgo(0),
      description: "ACH DEP — UNKNOWN",
      amountCents: 145000,
      matched: false
    },
    {
      id: "bl_2",
      bankAccountId: "bank_1",
      date: daysAgo(1),
      description: "Rent — Jordan Lee",
      amountCents: 145000,
      matched: true
    }
  ],

  bills: [
    { id: "bill_1", vendor: "CoolAir HVAC", dueDate: "2026-11-25", amountCents: 89000, status: "DUE" as const },
    { id: "bill_2", vendor: "City Water", dueDate: "2026-11-10", amountCents: 42000, status: "PAID" as const }
  ],

  recurringTransactions: [
    { id: "rtx_1", label: "Monthly rent post", amountCents: 145000, cadence: "MONTHLY" as const },
    { id: "rtx_2", label: "HOA management fee", amountCents: 120000, cadence: "QUARTERLY" as const }
  ],

  eftApprovals: [
    { id: "eft_1", payee: "Investor Group LLC", amountCents: 980000, status: "PENDING" as const },
    { id: "eft_2", payee: "CoolAir HVAC", amountCents: 89000, status: "APPROVED" as const }
  ],

  budgetLines: [
    { category: "Utilities", plannedCents: 450000, actualCents: 412000 },
    { category: "Landscaping", plannedCents: 120000, actualCents: 135000 },
    { category: "Security", plannedCents: 200000, actualCents: 198000 }
  ],

  latePenaltyRule: { ratePercentMonthly: 5, graceDays: 5, compound: false },

  meterReadings: [
    { id: "m1", unitLabel: "A-12", meterType: "WATER" as const, previous: 1204, current: 1248 },
    { id: "m2", unitLabel: "B-04", meterType: "WATER" as const, previous: 982, current: 1001 }
  ],

  enforcementCases: [
    {
      id: "enf_1",
      ownerName: "Ayşe Yılmaz",
      unitLabel: "A-12",
      amountCents: 420000,
      status: "LAWYER" as const,
      openedAt: daysAgo(30),
      timeline: [
        { at: daysAgo(30), note: "Borç ihtarı gönderildi" },
        { at: daysAgo(14), note: "İcra dosyası açıldı — İstanbul 12. İcra" }
      ]
    }
  ],

  atsCodes: [
    { id: "ats_1", partyName: "Ayşe Yılmaz", code10: "4829103756", unitLabel: "A-12" },
    { id: "ats_2", partyName: "Jordan Lee", code10: "1092837465", unitLabel: "101" }
  ],

  propertyListings: [
    { id: "lst_1", propertyName: "Maple Court", unitLabel: "102", rentCents: 150000, status: "ACTIVE" as const },
    { id: "lst_2", propertyName: "Riverside Duplex", unitLabel: "Unit B", rentCents: 205000, status: "ACTIVE" as const }
  ],
  prospects: [
    { id: "pr_1", name: "Casey Morgan", stage: "TOURING" as const, listingId: "lst_1" },
    { id: "pr_2", name: "Pat Kim", stage: "NEW" as const, listingId: "lst_2" }
  ],
  rentalApplications: [
    { id: "app_1", applicant: "Taylor Brooks", listingId: "lst_1", screening: "PENDING" as const, esign: "WAITING" as const },
    { id: "app_2", applicant: "Morgan Lee", listingId: "lst_2", screening: "CLEAR" as const, esign: "SIGNED" as const }
  ],
  leaseRenewals: [
    { id: "rn_1", tenantName: "Sam Rivera", unitLabel: "201", endDate: new Date(Date.now() + 20 * 86400000).toISOString().slice(0, 10), status: "OFFER_SENT" as const }
  ],
  draftLeases: [{ id: "dl_1", unitLabel: "102", status: "DRAFT" as const, esign: "PENDING" as const }],

  commercialLeaseTerms: [
    {
      id: "clt_1",
      tenantName: "Roastery Co.",
      unitLabel: "Retail-1",
      leaseStructure: "NNN" as const,
      baseRentCents: 420000,
      camCents: 85000,
      percentageRentBps: 500
    },
    {
      id: "clt_2",
      tenantName: "Metro Dental",
      unitLabel: "Suite 400",
      leaseStructure: "MODIFIED_GROSS" as const,
      baseRentCents: 615000,
      camCents: 0,
      percentageRentBps: 0
    }
  ],

  cooperativeShareRecords: [
    { id: "csr_1", unitLabel: "12-C", shareholderName: "Elena Moss", shareCount: 350, certificateNo: "BK-2210" },
    { id: "csr_2", unitLabel: "7-A", shareholderName: "Jonah Patel", shareCount: 280, certificateNo: "BK-1988" }
  ],
  coopBoardPackets: [
    {
      id: "cbp_1",
      title: "March board packet — 90 Park",
      status: "DRAFT" as const,
      dueDate: daysFromNow(12),
      documentCount: 4
    },
    {
      id: "cbp_2",
      title: "Q1 financial review",
      status: "PUBLISHED" as const,
      dueDate: daysFromNow(-3),
      documentCount: 9
    }
  ],

  associationUnits: [
    { unitLabel: "A-12", ownerName: "Ayşe Yılmaz", occupant: "Kiracı: Mehmet K.", occupantType: "TENANT" as const },
    { unitLabel: "B-04", ownerName: "Can Demir", occupant: "Malik", occupantType: "OWNER" as const }
  ],
  assessmentPlans: [
    { id: "as_1", name: "2026 Monthly aidat", totalCents: 4800000, categories: ["Genel", "Güvenlik", "Havuz"] }
  ],

  workOrders: [
    { id: "wo_1", summary: "Replace HVAC filter — 201", status: "IN_PROGRESS" as const, vendor: "CoolAir HVAC" },
    { id: "wo_2", summary: "Paint touch-up — common hall", status: "OPEN" as const, vendor: null as string | null }
  ],
  vendors: [
    { id: "v_1", name: "CoolAir HVAC", coiExpires: "2026-12-01" },
    { id: "v_2", name: "GreenScape LLC", coiExpires: "2026-08-15" }
  ],
  inspections: [{ id: "insp_1", propertyName: "Maple Court", template: "Move-out", happyCo: true }],

  tasks: [
    { id: "tk_1", title: "Follow up delinquent balance", assignee: "Dana PM", due: daysAgo(-2) },
    { id: "tk_2", title: "Board packet PDF", assignee: "Alex Accountant", due: daysAgo(-5) }
  ],
  recurringTasks: [{ id: "rtk_1", title: "Post monthly rent", cadence: "MONTHLY" as const }],

  announcements: [{ id: "an_1", title: "Pool maintenance Saturday", audience: "ALL" as const, sentAt: daysAgo(1) }],
  emailTemplates: [{ id: "et_1", name: "Rent reminder", trigger: "DUE_DATE" as const }],
  smsLogs: [{ id: "sms_1", to: "Jordan Lee", body: "Rent due tomorrow", sentAt: daysAgo(0) }],
  mailings: [{ id: "ml_1", name: "November newsletter", status: "SCHEDULED" as const }],
  inboxMessages: [{ id: "msg_1", from: "Jordan Lee", subject: "Lease question", unread: true }],

  filesTree: [
    { id: "dir_1", name: "Leases", type: "folder" as const },
    { id: "file_1", name: "Maple-COA.pdf", type: "file" as const, parent: "dir_1" }
  ],
  reportCatalog: [
    { id: "rep_1", name: "Rent roll", category: "Rentals" },
    { id: "rep_2", name: "AR aging (HOA)", category: "Associations" }
  ],
  marketplaceApps: [
    { id: "mp_1", name: "HappyCo Inspect", category: "Maintenance" },
    { id: "mp_2", name: "TransUnion Screening", category: "Leasing" }
  ],
  addons: [{ id: "ad_1", name: "Analytics Hub", priceCents: 9900 }],

  parcels: [
    { id: "pkg_1", carrier: "Yurtiçi", unitLabel: "A-12", status: "READY" as const },
    { id: "pkg_2", carrier: "MNG", unitLabel: "B-04", status: "DELIVERED" as const }
  ],
  patrolRoutes: [{ id: "pt_1", name: "Night security", checkpoints: 5, completedTonight: 3 }],
  accessEvents: [
    { id: "ac_1", gate: "Main", detail: "Plate 34 ABC 123", at: daysAgo(0) },
    { id: "ac_2", gate: "Pool", detail: "Card #8821", at: daysAgo(0) }
  ],
  timesheets: [
    { id: "ts_1", staff: "Ali Security", hours: 160, period: "Nov 2026" }
  ],
  whatsappTemplates: [{ id: "wa_1", name: "Aidat hatırlatma", body: "Sayın {{name}}, aidatınız..." }],

  publicSite: {
    announcements: 2,
    polls: 1,
    gallery: 12,
    forumThreads: 4
  },

  orgUsers: [
    { id: "ou_1", name: "Dana PM", role: "PROPERTY_MANAGER" },
    { id: "ou_2", name: "Alex Accountant", role: "ACCOUNTANT" }
  ]
};