/**
 * Mock data for dashboard widgets. Each widget ID has a corresponding data shape.
 */

export type DailyPoint = { date: string; value: number; label?: string };

/** Admin widgets mock data */
const ADMIN_MOCK = {
  orgsRegistered: 42,
  employersByOrg: 128,
  employersByJobRole: 95,
  usersLoggedInToday: 312,
  jobsCreated: { today: 12, lastWeek: 78, lastMonth: 340 },
  jobsAssigned: { today: 8, lastWeek: 56, lastMonth: 280 },
  jobsNotAssigned: 24,
  jobsCreatedChart: generateDaily(30, 5, 25),
  usersRegistrationChart: generateDaily(30, 1, 15),
  transactionDailyChart: generateDaily(30, 100, 800),
  activeShifts: 86,
  openApplications: 124,
  pendingDocuments: 18,
  systemHealth: "Healthy" as const,
  recentActivity: [
    { id: "1", text: "New organization registered: Acme Corp", time: "2 min ago" },
    { id: "2", text: "Job assigned: Warehouse Operative – City Hub", time: "15 min ago" },
    { id: "3", text: "User login: 45 concurrent users", time: "1 hour ago" },
  ],
};

/** Worker widgets mock data */
const WORKER_MOCK = {
  openJobsCount: 28,
  jobsList: [
    { id: "j1", title: "Warehouse Operative", company: "City Logistics Ltd" },
    { id: "j2", title: "Retail Associate", company: "Retail Plus" },
    { id: "j3", title: "Event Staff", company: "Events Co" },
  ],
  earnings: {
    totals: 0,
    period: "Feb 1 – Feb 28",
    breakdown: { shifts: 0, topProBonuses: 0, tipsPromotions: 0 },
    emptyMessage: "You haven't worked any shifts yet!",
  },
  transactionHistoryWeek: [] as { id: string; label: string; amount: string; date: string }[],
  appliedJobs: 5,
  assignedJobs: 2,
  rejectedJobs: 1,
  upcomingShifts: [
    { id: "s1", title: "Warehouse – Morning", location: "City Hub", date: "Mar 2, 09:00" },
    { id: "s2", title: "Retail – Afternoon", location: "Store A", date: "Mar 4, 14:00" },
  ],
  lastMessages: [
    { id: "m1", from: "Support", preview: "Your shift on Mar 2 is confirmed.", time: "1h ago" },
    { id: "m2", from: "City Logistics", preview: "Please confirm your availability.", time: "2h ago" },
  ],
  shiftsCompleted: 12,
  pendingPayments: 2,
  certificatesExpiring: 1,
  profileStrength: 65,
  savedJobs: 4,
};

/** Organization widgets mock data */
const ORG_MOCK = {
  createdJobsList: [
    { id: "o1", title: "Warehouse Operative – Night", status: "Open", created: "Feb 20" },
    { id: "o2", title: "Retail Associate", status: "Filled", created: "Feb 18" },
  ],
  lastShifts: [
    { id: "ls1", title: "Warehouse", date: "Feb 28", workers: 4 },
    { id: "ls2", title: "Retail", date: "Feb 27", workers: 2 },
  ],
  notAssignedJobsList: [
    { id: "na1", title: "Event Staff – Weekend", slots: 3 },
    { id: "na2", title: "Delivery Driver", slots: 1 },
  ],
  transactionList: [
    { id: "t1", description: "Shift payout – Feb 28", amount: "CHF 420.00", date: "Feb 28" },
    { id: "t2", description: "Bonus", amount: "CHF 50.00", date: "Feb 25" },
  ],
  activeWorkers: 32,
  openPositions: 6,
  invoicesDue: 3,
  complianceTasks: 2,
  pendingApplications: 14,
  upcomingShiftsCount: 8,
  totalSpendMonth: "CHF 12,400",
  workersOnShiftToday: 5,
  jobsCreatedMonth: 12,
  fillRate: 87,
  topRoles: ["Warehouse Operative", "Retail Associate", "Event Staff"],
};

function generateDaily(days: number, min: number, max: number): DailyPoint[] {
  const result: DailyPoint[] = [];
  const now = new Date();
  for (let i = days - 1; i >= 0; i--) {
    const d = new Date(now);
    d.setDate(d.getDate() - i);
    result.push({
      date: d.toISOString().slice(0, 10),
      value: Math.round(min + Math.random() * (max - min)),
      label: d.toLocaleDateString("en-GB", { day: "2-digit", month: "short" }),
    });
  }
  return result;
}

export type WidgetDataMap = {
  [key: string]: unknown;
};

/** Returns mock data for the given widget ID. */
export function getWidgetData(widgetId: string): unknown {
  switch (widgetId) {
    case "admin-orgs-registered":
      return { value: ADMIN_MOCK.orgsRegistered, label: "Registered organizations" };
    case "admin-employers-by-org":
      return { value: ADMIN_MOCK.employersByOrg, label: "Employers (by org)" };
    case "admin-employers-by-role":
      return { value: ADMIN_MOCK.employersByJobRole, label: "Employers (by job role)" };
    case "admin-users-logged-today":
      return { value: ADMIN_MOCK.usersLoggedInToday, label: "Users logged in today" };
    case "admin-jobs-created-today":
      return {
        today: ADMIN_MOCK.jobsCreated.today,
        lastWeek: ADMIN_MOCK.jobsCreated.lastWeek,
        lastMonth: ADMIN_MOCK.jobsCreated.lastMonth,
        label: "Jobs created",
      };
    case "admin-jobs-assigned-today":
      return {
        today: ADMIN_MOCK.jobsAssigned.today,
        lastWeek: ADMIN_MOCK.jobsAssigned.lastWeek,
        lastMonth: ADMIN_MOCK.jobsAssigned.lastMonth,
        label: "Jobs assigned",
      };
    case "admin-jobs-not-assigned":
      return { value: ADMIN_MOCK.jobsNotAssigned, label: "Jobs not assigned" };
    case "admin-jobs-created-chart":
      return { series: ADMIN_MOCK.jobsCreatedChart, title: "Jobs created (last 30 days)" };
    case "admin-users-registration-chart":
      return { series: ADMIN_MOCK.usersRegistrationChart, title: "User registration (last 30 days)" };
    case "admin-transaction-daily-chart":
      return { series: ADMIN_MOCK.transactionDailyChart, title: "Total transactions (last 30 days)" };
    case "admin-active-shifts":
      return { value: ADMIN_MOCK.activeShifts, label: "Active shifts" };
    case "admin-open-applications":
      return { value: ADMIN_MOCK.openApplications, label: "Open applications" };
    case "admin-pending-documents":
      return { value: ADMIN_MOCK.pendingDocuments, label: "Pending documents" };
    case "admin-system-health":
      return { status: ADMIN_MOCK.systemHealth, label: "System health" };
    case "admin-recent-activity":
      return { items: ADMIN_MOCK.recentActivity };

    case "worker-open-jobs-count":
      return { value: WORKER_MOCK.openJobsCount, label: "Open jobs (all profiles)" };
    case "worker-jobs-list":
      return { items: WORKER_MOCK.jobsList };
    case "worker-earnings":
      return WORKER_MOCK.earnings;
    case "worker-transaction-history-week":
      return { items: WORKER_MOCK.transactionHistoryWeek, emptyMessage: "You don't have any earnings yet." };
    case "worker-applied-jobs":
      return { value: WORKER_MOCK.appliedJobs, label: "Applied jobs" };
    case "worker-assigned-jobs":
      return { value: WORKER_MOCK.assignedJobs, label: "Assigned jobs" };
    case "worker-rejected-jobs":
      return { value: WORKER_MOCK.rejectedJobs, label: "Rejected jobs" };
    case "worker-upcoming-shifts":
      return { items: WORKER_MOCK.upcomingShifts };
    case "worker-last-messages":
      return { items: WORKER_MOCK.lastMessages };
    case "worker-shifts-completed":
      return { value: WORKER_MOCK.shiftsCompleted, label: "Shifts completed" };
    case "worker-pending-payments":
      return { value: WORKER_MOCK.pendingPayments, label: "Pending payments" };
    case "worker-certificates-expiring":
      return { value: WORKER_MOCK.certificatesExpiring, label: "Certificates expiring" };
    case "worker-profile-strength":
      return { value: WORKER_MOCK.profileStrength, label: "Profile strength (%)" };
    case "worker-saved-jobs":
      return { value: WORKER_MOCK.savedJobs, label: "Saved jobs" };

    case "org-created-jobs-list":
      return { items: ORG_MOCK.createdJobsList };
    case "org-last-shifts":
      return { items: ORG_MOCK.lastShifts };
    case "org-not-assigned-jobs":
      return { items: ORG_MOCK.notAssignedJobsList };
    case "org-transaction-list":
      return { items: ORG_MOCK.transactionList };
    case "org-active-workers":
      return { value: ORG_MOCK.activeWorkers, label: "Active workers" };
    case "org-open-positions":
      return { value: ORG_MOCK.openPositions, label: "Open positions" };
    case "org-invoices-due":
      return { value: ORG_MOCK.invoicesDue, label: "Invoices due" };
    case "org-compliance-tasks":
      return { value: ORG_MOCK.complianceTasks, label: "Compliance tasks" };
    case "org-pending-applications":
      return { value: ORG_MOCK.pendingApplications, label: "Pending applications" };
    case "org-upcoming-shifts":
      return { value: ORG_MOCK.upcomingShiftsCount, label: "Upcoming shifts" };
    case "org-total-spend-month":
      return { value: ORG_MOCK.totalSpendMonth, label: "Total spend (month)" };
    case "org-workers-on-shift":
      return { value: ORG_MOCK.workersOnShiftToday, label: "Workers on shift today" };
    case "org-jobs-created-month":
      return { value: ORG_MOCK.jobsCreatedMonth, label: "Jobs created (month)" };
    case "org-fill-rate":
      return { value: ORG_MOCK.fillRate, label: "Fill rate (%)" };
    case "org-top-roles":
      return { items: ORG_MOCK.topRoles, label: "Top roles" };

    default:
      return { value: 0, label: widgetId };
  }
}
