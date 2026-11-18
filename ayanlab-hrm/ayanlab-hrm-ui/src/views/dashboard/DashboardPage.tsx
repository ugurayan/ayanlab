import React from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Badge } from "../../ui/components/Badge";
import { useApplications, useDocuments, useInvoices, useShifts, useWorkers } from "../../data/hooks";
import { useTranslations } from "../../state/i18n";
import { useAuth } from "../../state/auth";
import { TEXTS } from "./i18n";
import { LoadingSpinner } from "../../ui/components/LoadingSpinner";
import { MAIN_ROLE_BY_ID, type MainRoleId } from "../../data/constants/mainRoles";
import { getWidgetsForRole } from "../../data/constants/dashboardWidgets";
import { DashboardWidgetCard } from "./DashboardWidgetCard";

export function DashboardPage() {
  const shifts = useShifts();
  const apps = useApplications();
  const workers = useWorkers();
  const docs = useDocuments();
  const invoices = useInvoices();

  const dueTasks = (invoices.data ?? []).filter((i: any) => i.status === "DUE").length;
  const overdueTasks = (invoices.data ?? []).filter((i: any) => i.status === "OVERDUE").length;
  const openIssues = (docs.data ?? []).filter((d: any) => d.status === "PENDING").length;
  const featureProposals = (apps.data ?? []).length;

  const copy = useTranslations(TEXTS);
  
  // Safely get user - context should be available but handle edge cases
  let user = null;
  try {
    const auth = useAuth();
    user = auth.user;
  } catch (error) {
    // Context not available - return loading state
    return (
      <div className="relative min-h-[400px]">
        <LoadingSpinner type="ring" size={50} text="Loading dashboard..." />
      </div>
    );
  }

  // Determine high-level main role based on UUID roles if present
  const mainRoleId = (user?.roles.find((r) => r in MAIN_ROLE_BY_ID) ?? null) as MainRoleId | null;

  const isWorkerLegacy = user?.roles.includes("WORKER");
  const isSystemAdminLegacy = user?.roles.includes("PLATFORM_ADMIN");
  const isCompanyAdminLegacy = !isSystemAdminLegacy && !isWorkerLegacy;

  const chartDays = CHART_DATA.map((d) => ({
    ...d,
    label: copy.chart.dayLabels[d.key]
  }));

  // Worker dashboard (main role or legacy)
  if (mainRoleId === "9c2b8be8-bdec-4484-96b6-a2145b654b4d" || isWorkerLegacy) {
    return (
      <div className="space-y-6">
        <div className="flex flex-col gap-1">
          <div className="text-2xl font-bold">Worker dashboard</div>
          <div className="text-slate-500 text-sm">
            See your payments, upcoming shifts and applied jobs (mock data).
          </div>
        </div>

        <div className="grid gap-4 md:grid-cols-3">
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Total shifts</div>
              <div className="text-3xl font-bold">{(shifts.data ?? []).length || 12}</div>
              <div className="text-xs text-slate-500">Completed and upcoming shifts</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Applied jobs</div>
              <div className="text-3xl font-bold">{(apps.data ?? []).length || 5}</div>
              <div className="text-xs text-slate-500">Applications in review</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Payments (mock)</div>
              <div className="text-3xl font-bold">CHF 1'240</div>
              <div className="text-xs text-slate-500">Expected payout this month</div>
            </CardContent>
          </Card>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Upcoming shifts</CardTitle>
          </CardHeader>
          <CardContent className="space-y-2 text-sm">
            {(shifts.data ?? []).slice(0, 5).map((s: any) => (
              <div key={s.id} className="flex items-center justify-between border-b last:border-b-0 border-slate-100 py-2">
                <div>
                  <div className="font-semibold">{s.title ?? "Shift"}</div>
                  <div className="text-xs text-slate-500">{s.location ?? "Location"}</div>
                </div>
                <div className="text-xs text-slate-500">{s.dateLabel ?? "Today 09:00"}</div>
              </div>
            ))}
          </CardContent>
        </Card>

        {/* All worker widgets (mocked) */}
        <div className="space-y-4">
          <h2 className="text-lg font-semibold text-slate-800">All widgets</h2>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {getWidgetsForRole("9c2b8be8-bdec-4484-96b6-a2145b654b4d").map((w) => (
              <DashboardWidgetCard key={w.id} widget={w} />
            ))}
          </div>
        </div>
      </div>
    );
  }

  const isSystemAdminMain = mainRoleId === "4a3207bf-cc00-46db-8db7-42e8d9568b8b";
  const isBusinessAdminMain = mainRoleId === "4b029d24-e658-4a65-8d76-ddbc2627a05c";
  const isBusinessMemberMain = mainRoleId === "055978b5-7e39-494d-9725-a0dca32dda6c";
  const isEmployerMain = mainRoleId === "b0633699-641c-47c5-89c6-6a88f7d83754";
  const isStaffMain = mainRoleId === "a0fdb0b8-a079-4315-ae21-31e68cd80778";
  const isGuestMain = mainRoleId === "e0cd0911-c905-436e-b729-6e9be593f4dd";

  const isSystemAdmin = isSystemAdminMain || isSystemAdminLegacy;
  const isCompanyAdmin = isBusinessAdminMain || isCompanyAdminLegacy;

  // System admin dashboard (platform-wide)
  if (isSystemAdmin) {
    return (
      <div className="space-y-6">
        <div className="flex flex-col gap-1">
          <div className="text-2xl font-bold">System admin dashboard</div>
          <div className="text-slate-500 text-sm">
            High-level overview of organizations, workers and activity (mock data).
          </div>
        </div>

        <div className="grid gap-4 md:grid-cols-4">
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Organizations</div>
              <div className="text-3xl font-bold">18</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Workers</div>
              <div className="text-3xl font-bold">{(workers.data ?? []).length || 320}</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Active shifts</div>
              <div className="text-3xl font-bold">{(shifts.data ?? []).length || 42}</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Open applications</div>
              <div className="text-3xl font-bold">{(apps.data ?? []).length || 85}</div>
            </CardContent>
          </Card>
        </div>

        {/* System-level overview reusing admin widgets */}
        <AdminOverview
          copy={copy}
          chartDays={chartDays}
          workersCount={(workers.data ?? []).length}
          shiftsCount={(shifts.data ?? []).length}
          appsCount={(apps.data ?? []).length}
        />

        {/* All admin widgets (mocked) */}
        <div className="space-y-4">
          <h2 className="text-lg font-semibold text-slate-800">All widgets</h2>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {getWidgetsForRole("4a3207bf-cc00-46db-8db7-42e8d9568b8b").map((w) => (
              <DashboardWidgetCard key={w.id} widget={w} />
            ))}
          </div>
        </div>
      </div>
    );
  }

  // Staff dashboard: internal staff supporting operations
  if (isStaffMain) {
    const totalShifts = (shifts.data ?? []).length || 42;
    const totalWorkers = (workers.data ?? []).length || 120;
    const totalApplications = (apps.data ?? []).length || 80;

    return (
      <div className="space-y-6">
        <div className="flex flex-col gap-1">
          <div className="text-2xl font-bold">Staff dashboard</div>
          <div className="text-slate-500 text-sm">
            Operational overview for staffing team: shifts, workers and applications (mock data).
          </div>
        </div>

        <div className="grid gap-4 md:grid-cols-3">
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Open shifts</div>
              <div className="text-3xl font-bold">{totalShifts}</div>
              <div className="text-xs text-slate-500">Published and draft shifts across all clients</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Active workers</div>
              <div className="text-3xl font-bold">{totalWorkers}</div>
              <div className="text-xs text-slate-500">Workers with at least one approved profile</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Applications in review</div>
              <div className="text-3xl font-bold">{totalApplications}</div>
              <div className="text-xs text-slate-500">Applications to triage and shortlist</div>
            </CardContent>
          </Card>
        </div>

        <AdminOverview
          copy={copy}
          chartDays={chartDays}
          workersCount={totalWorkers}
          shiftsCount={totalShifts}
          appsCount={totalApplications}
        />

        {/* All admin/staff widgets (mocked) */}
        <div className="space-y-4">
          <h2 className="text-lg font-semibold text-slate-800">All widgets</h2>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {getWidgetsForRole("a0fdb0b8-a079-4315-ae21-31e68cd80778").map((w) => (
              <DashboardWidgetCard key={w.id} widget={w} />
            ))}
          </div>
        </div>
      </div>
    );
  }

  // Business admin dashboard: organization owner / admin
  if (isBusinessAdminMain) {
    const dueTasks = (invoices.data ?? []).filter((i: any) => i.status === "DUE").length;
    const openIssues = (docs.data ?? []).filter((d: any) => d.status === "PENDING").length;
    const featureProposals = (apps.data ?? []).length;

    return (
      <div className="space-y-6">
        <div className="flex flex-col gap-1">
          <div className="text-2xl font-bold">Business admin dashboard</div>
          <div className="text-slate-500 text-sm">
            Overview of your organization, locations and financial health (mock data).
          </div>
        </div>

        {/* Top KPI cards for business admins */}
        <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          <Card>
            <CardContent className="p-5 flex flex-col gap-4">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-500">Open invoices</span>
              </div>
              <div>
                <div className="text-4xl font-bold">{dueTasks || 12}</div>
                <div className="text-sm text-slate-500">Invoices due this month</div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-5 flex flex-col gap-4">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-500">Compliance tasks</span>
              </div>
              <div>
                <div className="text-4xl font-bold">{openIssues || 8}</div>
                <div className="text-sm text-slate-500">Documents waiting for approval</div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-5 flex flex-col gap-4">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-500">Active workers</span>
              </div>
              <div>
                <div className="text-4xl font-bold">{(workers.data ?? []).length || 32}</div>
                <div className="text-sm text-slate-500">Workers assigned to your org</div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardContent className="p-5 flex flex-col gap-4">
              <div className="flex items-center justify-between">
                <span className="text-xs font-semibold text-slate-500">Open positions</span>
              </div>
              <div>
                <div className="text-4xl font-bold">{featureProposals || 6}</div>
                <div className="text-sm text-slate-500">Jobs currently hiring</div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Reuse existing chart and cards for admin */}
        <AdminOverview
          copy={copy}
          chartDays={chartDays}
          workersCount={(workers.data ?? []).length}
          shiftsCount={(shifts.data ?? []).length}
          appsCount={(apps.data ?? []).length}
        />

        {/* All organization widgets (mocked) */}
        <div className="space-y-4">
          <h2 className="text-lg font-semibold text-slate-800">All widgets</h2>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {getWidgetsForRole("4b029d24-e658-4a65-8d76-ddbc2627a05c").map((w) => (
              <DashboardWidgetCard key={w.id} widget={w} />
            ))}
          </div>
        </div>
      </div>
    );
  }

  // Business member dashboard: managers within an organization
  if (isBusinessMemberMain) {
    return (
      <div className="space-y-6">
        <div className="flex flex-col gap-1">
          <div className="text-2xl font-bold">Business member dashboard</div>
          <div className="text-slate-500 text-sm">
            Track shifts, team performance and pending tasks for your locations (mock data).
          </div>
        </div>

        <div className="grid gap-4 md:grid-cols-3">
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Upcoming shifts</div>
              <div className="text-3xl font-bold">{(shifts.data ?? []).length || 10}</div>
              <div className="text-xs text-slate-500">Next 7 days</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Workers on site</div>
              <div className="text-3xl font-bold">{(workers.data ?? []).length || 18}</div>
              <div className="text-xs text-slate-500">Across your locations</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Documents to review</div>
              <div className="text-3xl font-bold">
                {(docs.data ?? []).filter((d: any) => d.status === "PENDING").length || 3}
              </div>
              <div className="text-xs text-slate-500">Compliance follow-ups</div>
            </CardContent>
          </Card>
        </div>

        <AdminOverview
          copy={copy}
          chartDays={chartDays}
          workersCount={(workers.data ?? []).length}
          shiftsCount={(shifts.data ?? []).length}
          appsCount={(apps.data ?? []).length}
        />

        {/* All organization widgets (mocked) */}
        <div className="space-y-4">
          <h2 className="text-lg font-semibold text-slate-800">All widgets</h2>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {getWidgetsForRole("055978b5-7e39-494d-9725-a0dca32dda6c").map((w) => (
              <DashboardWidgetCard key={w.id} widget={w} />
            ))}
          </div>
        </div>
      </div>
    );
  }

  // Employer dashboard: a single hiring manager / employer
  if (isEmployerMain) {
    return (
      <div className="space-y-6">
        <div className="flex flex-col gap-1">
          <div className="text-2xl font-bold">Employer dashboard</div>
          <div className="text-slate-500 text-sm">
            Snapshot of your open jobs, candidates and upcoming shifts (mock data).
          </div>
        </div>

        <div className="grid gap-4 md:grid-cols-3">
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Open jobs</div>
              <div className="text-3xl font-bold">{(apps.data ?? []).length || 4}</div>
              <div className="text-xs text-slate-500">Jobs currently accepting applications</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Candidates</div>
              <div className="text-3xl font-bold">{(workers.data ?? []).length || 15}</div>
              <div className="text-xs text-slate-500">Shortlisted candidates</div>
            </CardContent>
          </Card>
          <Card>
            <CardContent className="p-5 space-y-2">
              <div className="text-xs font-semibold text-slate-500">Upcoming interviews</div>
              <div className="text-3xl font-bold">3</div>
              <div className="text-xs text-slate-500">Planned for this week</div>
            </CardContent>
          </Card>
        </div>

        <AdminOverview
          copy={copy}
          chartDays={chartDays}
          workersCount={(workers.data ?? []).length}
          shiftsCount={(shifts.data ?? []).length}
          appsCount={(apps.data ?? []).length}
        />

        {/* All organization widgets (mocked) */}
        <div className="space-y-4">
          <h2 className="text-lg font-semibold text-slate-800">All widgets</h2>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {getWidgetsForRole("b0633699-641c-47c5-89c6-6a88f7d83754").map((w) => (
              <DashboardWidgetCard key={w.id} widget={w} />
            ))}
          </div>
        </div>
      </div>
    );
  }

  // Guest dashboard: very lightweight overview (mainly for completeness)
  if (isGuestMain) {
    return (
      <div className="space-y-6">
        <div className="flex flex-col gap-1">
          <div className="text-2xl font-bold">Guest dashboard</div>
          <div className="text-slate-500 text-sm">
            Limited read-only view. Contact your administrator to request more access.
          </div>
        </div>

        <Card>
          <CardContent className="p-6 space-y-3">
            <div className="text-sm font-semibold text-slate-700">What you can see</div>
            <ul className="list-disc list-inside text-xs text-slate-500 space-y-1">
              <li>High-level activity overview</li>
              <li>Non-sensitive counts (workers, shifts, applications)</li>
              <li>No access to financial or personal data</li>
            </ul>
          </CardContent>
        </Card>

        <AdminOverview
          copy={copy}
          chartDays={chartDays}
          workersCount={(workers.data ?? []).length}
          shiftsCount={(shifts.data ?? []).length}
          appsCount={(apps.data ?? []).length}
        />
      </div>
    );
  }

  // Company dashboard (default, includes business admins by legacy logic)
  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-1">
        <div className="text-2xl font-bold">{copy.hero.title}</div>
        <div className="text-slate-500 text-sm">
          {copy.hero.subtitle}
        </div>
      </div>

      {/* Top KPI cards */}
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <Card>
          <CardContent className="p-5 flex flex-col gap-4">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500">{copy.kpis.today}</span>
              <button className="inline-flex items-center rounded-full border border-slate-200 px-2 py-1 text-xs font-semibold text-slate-600">
                {copy.kpis.today}
              </button>
            </div>
            <div>
              <div className="text-4xl font-bold">{dueTasks || 25}</div>
              <div className="text-sm text-slate-500">{copy.kpis.dueTasks}</div>
            </div>
            <div className="text-xs text-slate-500">
              {copy.kpis.completed}{" "}
              <span className="font-semibold text-slate-700">
                {Math.max(0, (shifts.data ?? []).length - dueTasks) || 7}
              </span>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-5 flex flex-col gap-4">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500">{copy.kpis.overdue}</span>
              <span className="h-6 w-6 rounded-full border border-slate-200 flex items-center justify-center text-slate-400 text-xs">
                …
              </span>
            </div>
            <div>
              <div className="text-4xl font-bold">{overdueTasks || 4}</div>
              <div className="text-sm text-slate-500">{copy.kpis.tasks}</div>
            </div>
            <div className="text-xs text-slate-500">
              {copy.kpis.yesterdayOverdue}{" "}
              <span className="font-semibold text-slate-700">2</span>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-5 flex flex-col gap-4">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500">{copy.kpis.issues}</span>
              <span className="h-6 w-6 rounded-full border border-slate-200 flex items-center justify-center text-slate-400 text-xs">
                …
              </span>
            </div>
            <div>
              <div className="text-4xl font-bold">{openIssues || 32}</div>
              <div className="text-sm text-slate-500">{copy.kpis.open}</div>
            </div>
            <div className="text-xs text-slate-500">
              {copy.kpis.closedToday}{" "}
              <span className="font-semibold text-slate-700">0</span>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-5 flex flex-col gap-4">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500">{copy.kpis.features}</span>
              <span className="h-6 w-6 rounded-full border border-slate-200 flex items-center justify-center text-slate-400 text-xs">
                …
              </span>
            </div>
            <div>
              <div className="text-4xl font-bold">{featureProposals || 42}</div>
              <div className="text-sm text-slate-500">{copy.kpis.proposals}</div>
            </div>
            <div className="text-xs text-slate-500">
              {copy.kpis.implemented}{" "}
              <span className="font-semibold text-slate-700">8</span>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Company overview & chart (reusing existing widgets) */}
      <AdminOverview
        copy={copy}
        chartDays={chartDays}
        workersCount={(workers.data ?? []).length}
        shiftsCount={(shifts.data ?? []).length}
        appsCount={(apps.data ?? []).length}
      />
    </div>
  );
}

type AdminOverviewProps = {
  copy: typeof TEXTS.en;
  chartDays: any[];
  workersCount: number;
  shiftsCount: number;
  appsCount: number;
};

function AdminOverview({ copy, chartDays, workersCount, shiftsCount, appsCount }: AdminOverviewProps) {
  return (
    <Card>
        <CardHeader className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <CardTitle>{copy.github.title}</CardTitle>
            <p className="text-xs text-slate-500 mt-1">
              {copy.github.subtitle}
            </p>
          </div>
          <div className="inline-flex items-center rounded-full bg-slate-100 p-1 text-xs font-semibold">
            <button className="rounded-full bg-white px-3 py-1 shadow-sm text-slate-900">
              {copy.github.thisWeek}
            </button>
            <button className="rounded-full px-3 py-1 text-slate-500">
              {copy.github.lastWeek}
            </button>
          </div>
        </CardHeader>
        <CardContent>
          <div className="grid gap-6 lg:grid-cols-[2fr,1.2fr]">
            {/* Left: chart */}
            <div className="space-y-4">
              <div className="text-sm font-semibold text-slate-700">{copy.chart.title}</div>
              <div className="border border-slate-100 rounded-2xl p-4 bg-slate-50/60 cursor-pointer">
                <div className="flex items-center justify-between text-xs text-slate-500 mb-3">
                  <span>{copy.chart.issues}</span>
                  <span>{copy.chart.last7Days}</span>
                </div>

                <div className="relative h-40">
                  {/* Y-axis labels */}
                  <div className="absolute inset-y-0 left-0 flex flex-col justify-between text-[10px] text-slate-400">
                    <span>50</span>
                    <span>40</span>
                    <span>30</span>
                    <span>20</span>
                    <span>10</span>
                    <span>0</span>
                  </div>

                  {/* Bars + line */}
                  <div className="ml-8 h-full flex items-end gap-4">
                    {chartDays.map((d) => (
                      <div key={d.label} className="flex-1 flex flex-col items-center gap-1">
                        {/* line point */}
                        <div className="relative w-full h-20">
                          <div className="absolute left-1/2 -translate-x-1/2 bottom-0 w-[1px] h-full bg-slate-100" />
                          <div
                            className="absolute -top-2 px-1 rounded bg-slate-900 text-[10px] text-white"
                            style={{ left: "50%", transform: "translateX(-50%)" }}
                          >
                            {d.closed}
                          </div>
                          <div
                            className="absolute w-2 h-2 rounded-full bg-slate-900"
                            style={{ left: "50%", transform: "translate(-50%, 0)" }}
                          />
                        </div>
                        {/* bar */}
                        <div
                          className="w-6 rounded-md bg-blue-500/80"
                          style={{ height: `${(d.value / 50) * 100}%` }}
                        />
                        <div className="text-[10px] text-slate-500 mt-1">{d.label}</div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>

            {/* Right: overview cards */}
            <div className="space-y-4">
              <div className="text-sm font-semibold text-slate-700">{copy.overview.title}</div>
              <div className="grid gap-3 grid-cols-2">
                <Card>
                  <CardContent className="p-4">
                    <div className="text-xs text-slate-500">{copy.overview.newIssues}</div>
                    <div className="text-3xl font-bold text-blue-600">214</div>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="p-4">
                    <div className="text-xs text-slate-500">{copy.overview.closed}</div>
                    <div className="text-3xl font-bold text-blue-600">75</div>
                  </CardContent>
                </Card>
              </div>

              <div className="grid gap-3 grid-cols-2 md:grid-cols-3">
                <Card>
                  <CardContent className="p-3 flex flex-col gap-1">
                    <div className="text-xs text-slate-500">{copy.overview.fixed}</div>
                    <div className="text-lg font-semibold">3</div>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="p-3 flex flex-col gap-1">
                    <div className="text-xs text-slate-500">{copy.overview.wontFix}</div>
                    <div className="text-lg font-semibold">4</div>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="p-3 flex flex-col gap-1">
                    <div className="text-xs text-slate-500">{copy.overview.reopened}</div>
                    <div className="text-lg font-semibold">8</div>
                  </CardContent>
                </Card>
                <Card>
                  <CardContent className="p-3 flex flex-col gap-1">
                    <div className="text-xs text-slate-500">{copy.overview.needsTriage}</div>
                    <div className="text-lg font-semibold">6</div>
                  </CardContent>
                </Card>
              </div>

              <div className="flex flex-wrap gap-2 pt-1">
                <Badge tone="green">
                  {copy.badges.workers}: {workersCount}
                </Badge>
                <Badge tone="blue">
                  {copy.badges.shifts}: {shiftsCount}
                </Badge>
                <Badge tone="gray">
                  {copy.badges.applications}: {appsCount}
                </Badge>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
  );
}

const CHART_DATA = [
  { key: "mon", value: 28, closed: 42 },
  { key: "tue", value: 18, closed: 28 },
  { key: "wed", value: 22, closed: 43 },
  { key: "thu", value: 16, closed: 34 },
  { key: "fri", value: 19, closed: 20 },
  { key: "sat", value: 24, closed: 25 },
  { key: "sun", value: 35, closed: 22 }
] as const;

