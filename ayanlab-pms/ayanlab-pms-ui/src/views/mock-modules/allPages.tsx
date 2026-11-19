import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import {
  fetchAccessEvents,
  fetchAnnouncements,
  fetchApplications,
  fetchCommercialLeaseTerms,
  fetchCoopBoardPackets,
  fetchCooperativeShareRecords,
  fetchAssessmentPlans,
  fetchAssociationUnits,
  fetchDraftLeases,
  fetchEmailTemplates,
  fetchFilesTree,
  fetchInboxMessages,
  fetchInspections,
  fetchListings,
  fetchMailings,
  fetchMarketplace,
  fetchOrgUsers,
  fetchParcels,
  fetchPatrolRoutes,
  fetchProspects,
  fetchPublicSite,
  fetchRenewals,
  fetchReportCatalog,
  fetchRecurringTasks,
  fetchSmsLogs,
  fetchTasks,
  fetchTimesheets,
  fetchVendors,
  fetchWhatsappTemplates,
  fetchWorkOrders
} from "../../mock/mockExtras";
import { db } from "../../mock/mockDb";
import { money, moneyTry, formatDate } from "../../lib/format";
import { PageHeader } from "../../ui/components/PageHeader";
import { Badge } from "../../ui/components/Badge";
import { Button } from "../../ui/components/Button";
import { Card, CardContent } from "../../ui/components/Card";
import type { FC, ReactNode } from "react";

function Shell({ title, subtitle, children }: { title: string; subtitle?: string; children: ReactNode }) {
  return (
    <div>
      <PageHeader title={title} subtitle={subtitle} />
      <Card>
        <CardContent className="text-sm">{children}</CardContent>
      </Card>
    </div>
  );
}

function useMock<T>(key: string, fn: () => Promise<T>) {
  return useQuery({ queryKey: ["mock", key], queryFn: fn });
}

export function ListingsPage() {
  const { data } = useMock("listings", fetchListings);
  return (
    <Shell title="Listings">
      <ul className="divide-y divide-[rgb(var(--border))]">
        {(data ?? []).map((l) => (
          <li key={l.id} className="flex justify-between py-2">
            <span>
              {l.propertyName} · {l.unitLabel}
            </span>
            <span>{money(l.rentCents)}</span>
          </li>
        ))}
      </ul>
    </Shell>
  );
}

export function ProspectsPage() {
  const { data } = useMock("prospects", fetchProspects);
  const stages = ["NEW", "TOURING", "APPLIED", "LOST"] as const;
  return (
    <Shell title="Prospects" subtitle="Kanban (mock)">
      <div className="grid gap-2 md:grid-cols-4">
        {stages.map((s) => (
          <div key={s} className="rounded-lg bg-[rgb(var(--surface-muted))] p-2">
            <div className="mb-2 text-xs font-bold text-[rgb(var(--text-muted))]">{s}</div>
            {(data ?? [])
              .filter((p) => p.stage === s)
              .map((p) => (
                <div key={p.id} className="mb-1 rounded border border-[rgb(var(--border))] bg-[rgb(var(--surface))] p-2">
                  {p.name}
                </div>
              ))}
          </div>
        ))}
      </div>
    </Shell>
  );
}

export function ApplicationsPage() {
  const { data } = useMock("apps", fetchApplications);
  return (
    <Shell title="Applications">
      {(data ?? []).map((a) => (
        <div key={a.id} className="mb-3 rounded-lg border p-3">
          <div className="font-medium">{a.applicant}</div>
          <div className="mt-1 flex gap-2 text-xs">
            <Badge tone={a.screening === "CLEAR" ? "ok" : "warn"}>Screening: {a.screening}</Badge>
            <Badge tone={a.esign === "SIGNED" ? "ok" : "danger"}>eSign: {a.esign}</Badge>
          </div>
        </div>
      ))}
    </Shell>
  );
}

export function RenewalsPage() {
  const renewals = useMock("renewals", fetchRenewals);
  const drafts = useMock("drafts", fetchDraftLeases);
  return (
    <Shell title="Renewals & draft leases">
      <p className="mb-2 rounded-lg bg-amber-50 p-2 text-xs">eSign: 1 lease awaiting signature (mock)</p>
      <h3 className="font-semibold">Renewals</h3>
      <ul className="mb-4 divide-y divide-[rgb(var(--border))]">
        {(renewals.data ?? []).map((r) => (
          <li key={r.id} className="py-2">
            {r.tenantName} · {r.unitLabel} · ends {r.endDate}{" "}
            <Badge tone="warn">{r.status}</Badge>
          </li>
        ))}
      </ul>
      <h3 className="font-semibold">Draft leases</h3>
      <ul className="divide-y divide-[rgb(var(--border))]">
        {(drafts.data ?? []).map((d) => (
          <li key={d.id} className="py-2">
            Unit {d.unitLabel} — eSign <Badge tone="danger">{d.esign}</Badge>
          </li>
        ))}
      </ul>
    </Shell>
  );
}

export function AssociationsListPage() {
  return (
    <Shell title="Associations">
      <ul className="divide-y divide-[rgb(var(--border))]">
        {db.associations.map((a) => (
          <li key={a.id} className="py-2">
            <div className="font-medium">{a.name}</div>
            <div className="text-xs text-[rgb(var(--text-muted))]">
              {a.city} · {a.unitCount} units · delinquent {moneyTry(a.delinquentCents)}
            </div>
            <AssocLinks />
          </li>
        ))}
      </ul>
    </Shell>
  );
}

export function AssociationUnitsPage() {
  const { data } = useMock("assocUnits", fetchAssociationUnits);
  return (
    <Shell title="Owner / occupant matrix" subtitle="Malik kiracı (Apsiyon)">
      <table className="w-full">
        <thead>
          <tr className="text-xs text-[rgb(var(--text-muted))]">
            <th className="text-left">Unit</th>
            <th className="text-left">Owner</th>
            <th className="text-left">Occupant</th>
          </tr>
        </thead>
        <tbody>
          {(data ?? []).map((u) => (
            <tr key={u.unitLabel} className="border-t">
              <td className="py-2">{u.unitLabel}</td>
              <td className="py-2">{u.ownerName}</td>
              <td className="py-2">{u.occupant}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </Shell>
  );
}

export function AssessmentsPage() {
  const { data } = useMock("assessments", fetchAssessmentPlans);
  return (
    <Shell title="Assessments / aidat">
      {(data ?? []).map((p) => (
        <div key={p.id} className="mb-3 rounded-lg border p-3">
          <div className="font-medium">{p.name}</div>
          <AssessmentMeta total={p.totalCents} cats={p.categories} />
        </div>
      ))}
    </Shell>
  );
}

function AssocLinks() {
  return (
    <span className="text-xs">
      <Link className="text-teal-800" to="/app/associations/occupants">
        Occupants
      </Link>
      {" · "}
      <Link className="text-teal-800" to="/app/associations/violations">
        Violations
      </Link>
    </span>
  );
}

function AssessmentMeta({ total, cats }: { total: number; cats: string[] }) {
  return (
    <p className="text-xs text-[rgb(var(--text-muted))]">
      {moneyTry(total)} — {cats.join(", ")}
    </p>
  );
}

export function MaintenanceRequestsPage() {
  return (
    <Shell title="Maintenance requests">
      <ul className="divide-y divide-[rgb(var(--border))]">
        {db.maintenance.map((m) => (
          <li key={m.id} className="flex justify-between py-2">
            <span>{m.summary}</span>
            <Badge tone={m.status === "OVERDUE" ? "danger" : "neutral"}>{m.status}</Badge>
          </li>
        ))}
      </ul>
    </Shell>
  );
}

export function WorkOrdersPage() {
  const { data } = useMock("wo", fetchWorkOrders);
  return (
    <Shell title="Work orders">
      {(data ?? []).map((w) => (
        <div key={w.id} className="mb-2 flex justify-between border-b py-2">
          <span>{w.summary}</span>
          <Badge>{w.status}</Badge>
        </div>
      ))}
    </Shell>
  );
}

export function VendorsPage() {
  const { data } = useMock("vendors", fetchVendors);
  const now = Date.now();
  return (
    <Shell title="Vendors">
      {(data ?? []).map((v) => {
        const exp = new Date(v.coiExpires).getTime();
        const warn = exp - now < 90 * 86400000;
        return (
          <div key={v.id} className="flex justify-between border-b py-2">
            <span>{v.name}</span>
            <Badge tone={warn ? "warn" : "ok"}>COI {v.coiExpires}</Badge>
          </div>
        );
      })}
    </Shell>
  );
}

export function InspectionsPage() {
  const { data } = useMock("insp", fetchInspections);
  return (
    <Shell title="Inspections">
      {(data ?? []).map((i) => (
        <InspectionRow key={i.id} row={i} />
      ))}
    </Shell>
  );
}

function InspectionRow({ row }: { row: { propertyName: string; template: string; happyCo: boolean } }) {
  return (
    <p className="py-2">
      {row.propertyName} — {row.template}{" "}
      {row.happyCo && <Badge tone="ok">HappyCo</Badge>}
    </p>
  );
}

export function TasksListPage() {
  const { data } = useMock("tasks", fetchTasks);
  return (
    <Shell title="Tasks">
      {(data ?? []).map((t) => (
        <p key={t.id} className="border-b py-2">
          {t.title} · {t.assignee} · due {formatDate(t.due)}
        </p>
      ))}
    </Shell>
  );
}

export function RecurringTasksPage() {
  const { data } = useMock("rtasks", fetchRecurringTasks);
  return (
    <Shell title="Recurring tasks">
      {(data ?? []).map((t) => (
        <p key={t.id}>{t.title} — {t.cadence}</p>
      ))}
    </Shell>
  );
}

export function AnnouncementsPage() {
  const { data } = useMock("ann", fetchAnnouncements);
  return (
    <Shell title="Announcements">
      {(data ?? []).map((a) => (
        <p key={a.id} className="py-2">
          <strong>{a.title}</strong> · {a.audience}
        </p>
      ))}
    </Shell>
  );
}

export function EmailTemplatesPage() {
  const { data } = useMock("et", fetchEmailTemplates);
  return (
    <Shell title="Email templates">
      {(data ?? []).map((t) => (
        <p key={t.id}>
          {t.name} — trigger {t.trigger}
        </p>
      ))}
    </Shell>
  );
}

export function SmsLogPage() {
  const { data } = useMock("sms", fetchSmsLogs);
  return (
    <Shell title="SMS log">
      {(data ?? []).map((s) => (
        <p key={s.id} className="border-b py-2 text-xs">
          {s.to}: {s.body}
        </p>
      ))}
    </Shell>
  );
}

export function MailingPage() {
  const { data } = useMock("mail", fetchMailings);
  return (
    <Shell title="Mailing campaigns" subtitle="Wizard step 1 mock">
      <Button type="button" size="sm" className="mb-3">
        New campaign
      </Button>
      {(data ?? []).map((m) => (
        <p key={m.id}>
          {m.name} — {m.status}
        </p>
      ))}
    </Shell>
  );
}

export function MessagesInboxPage() {
  const { data } = useMock("inbox", fetchInboxMessages);
  return (
    <Shell title="Messages">
      {(data ?? []).map((m) => (
        <p key={m.id} className={m.unread ? "font-semibold" : ""}>
          {m.from}: {m.subject}
        </p>
      ))}
    </Shell>
  );
}

export function FilesPage() {
  const { data } = useMock("files", fetchFilesTree);
  return (
    <Shell title="Files">
      <Button type="button" size="sm" variant="secondary" className="mb-3">
        Upload (mock)
      </Button>
      <ul>
        {(data ?? []).map((f) => (
          <li key={f.id} className="py-1">
            {f.type === "folder" ? "📁" : "📄"} {f.name}
          </li>
        ))}
      </ul>
    </Shell>
  );
}

export function ReportsPage() {
  const { data } = useMock("reports", fetchReportCatalog);
  return (
    <Shell title="Reports">
      <ReportFiltersMock />
      <ul className="mt-3">
        {(data ?? []).map((r) => (
          <li key={r.id} className="border-t py-2">
            {r.name} <span className="text-[rgb(var(--text-muted))]">({r.category})</span>
          </li>
        ))}
      </ul>
    </Shell>
  );
}

function ReportFiltersMock() {
  return (
    <div className="rounded border border-dashed p-2">
      <p className="text-xs text-[rgb(var(--text-muted))]">Date range + property filters (mock)</p>
    </div>
  );
}

export function AnalyticsHubPage() {
  return (
    <Shell title="Analytics Hub">
      <div className="grid h-32 grid-cols-3 gap-2">
        {[65, 82, 44].map((n, i) => (
          <div key={i} className="flex items-end rounded bg-[rgb(var(--surface-muted))] p-2">
            <div className="w-full rounded bg-teal-600" style={{ height: `${n}%` }} />
          </div>
        ))}
      </div>
    </Shell>
  );
}

export function ShortcutsPage() {
  return (
    <Shell title="Shortcuts">
      <ShortcutBtn title="Collect management fee" />
      <ShortcutBtn title="Owner draw" />
      <ShortcutBtn title="Passthrough payment" />
    </Shell>
  );
}

function ShortcutBtn({ title }: { title: string }) {
  return (
    <Button type="button" variant="secondary" className="mb-2 w-full justify-start">
      {title}
    </Button>
  );
}

export function MarketplaceAddonsPage() {
  const { data } = useMock("addons", () => fetchMarketplace().then((m) => m.addons));
  return (
    <Shell title="Add-on services">
      <div className="grid gap-2 sm:grid-cols-2">
        {(data ?? []).map((a) => (
          <div key={a.id} className="rounded-lg border p-3">
            <div className="font-medium">{a.name}</div>
            <div className="text-xs text-[rgb(var(--text-muted))]">{money(a.priceCents)}/mo</div>
          </div>
        ))}
      </div>
    </Shell>
  );
}

export function MarketplaceAppsPage() {
  const { data } = useMock("mkt", () => fetchMarketplace().then((m) => m.apps));
  return (
    <Shell title="Marketplace">
      <MarketplaceGrid items={data ?? []} field="category" />
    </Shell>
  );
}

function MarketplaceGrid({ items, field }: { items: { id: string; name: string; [k: string]: unknown }[]; field: string }) {
  return (
    <div className="grid gap-2 sm:grid-cols-2">
      {items.map((a) => (
        <div key={a.id} className="rounded-lg border p-3">
          <div className="font-medium">{a.name}</div>
          <div className="text-xs text-[rgb(var(--text-muted))]">{String(a[field])}</div>
        </div>
      ))}
    </div>
  );
}

export function PublicSitePage() {
  const { data } = useMock("site", fetchPublicSite);
  if (!data) return <Shell title="Public site CMS">Loading…</Shell>;
  return (
    <Shell title="Public site CMS" subtitle="Apsiyon">
      <ul>
        <li>Duyurular: {data.announcements}</li>
        <li>Anketler: {data.polls}</li>
        <li>Galeri: {data.gallery} foto</li>
        <li>Forum: {data.forumThreads} konu</li>
      </ul>
    </Shell>
  );
}

export function ParcelsPage() {
  const { data } = useMock("parcel", fetchParcels);
  return (
    <Shell title="Kargo takibi">
      {(data ?? []).map((p) => (
        <p key={p.id} className="py-2">
          {p.carrier} · {p.unitLabel} <Badge>{p.status}</Badge>
        </p>
      ))}
    </Shell>
  );
}

export function PatrolPage() {
  const { data } = useMock("patrol", fetchPatrolRoutes);
  return (
    <Shell title="Tur kontrol">
      {(data ?? []).map((r) => (
        <p key={r.id}>
          {r.name}: {r.completedTonight}/{r.checkpoints} checkpoints
        </p>
      ))}
    </Shell>
  );
}

export function AccessControlPage() {
  const { data } = useMock("access", fetchAccessEvents);
  return (
    <Shell title="Geçiş kontrol">
      {(data ?? []).map((e) => (
        <p key={e.id} className="text-xs">
          {e.gate} — {e.detail} · {formatDate(e.at)}
        </p>
      ))}
    </Shell>
  );
}

export function TimesheetsPage() {
  const { data } = useMock("ts", fetchTimesheets);
  return (
    <Shell title="Personel puantaj">
      {(data ?? []).map((t) => (
        <p key={t.id}>
          {t.staff}: {t.hours}h ({t.period})
        </p>
      ))}
    </Shell>
  );
}

export function WhatsappPage() {
  const { data } = useMock("wa", fetchWhatsappTemplates);
  return (
    <Shell title="WhatsApp şablonları">
      {(data ?? []).map((t) => (
        <div key={t.id} className="mb-2 rounded border p-2 text-xs">
          <strong>{t.name}</strong>
          <p className="text-[rgb(var(--text-muted))]">{t.body}</p>
          <Button type="button" size="sm" variant="secondary" className="mt-1">
            Gönder (mock)
          </Button>
        </div>
      ))}
    </Shell>
  );
}

export function OrgSettingsPage() {
  const { data } = useMock("org", fetchOrgUsers);
  return (
    <Shell title="Org settings" subtitle="Users & roles (read-only mock)">
      {(data ?? []).map((u) => (
        <p key={u.id}>
          {u.name} — <Badge tone="neutral">{u.role}</Badge>
        </p>
      ))}
    </Shell>
  );
}

export function SubscriptionsPage() {
  return (
    <Shell title="Subscriptions">
      <p>Plan: Professional (mock)</p>
      <p className="text-[rgb(var(--text-muted))]">Billing portal placeholder — B0</p>
    </Shell>
  );
}

export function HelpPage() {
  return (
    <Shell title="Help & Support">
      <a className="text-teal-800" href="https://example.com" onClick={(e) => e.preventDefault()}>
        Academy (mock link)
      </a>
      <p className="mt-2">Support ticket form — placeholder</p>
    </Shell>
  );
}

export function CommercialLeaseTermsPage() {
  const { data } = useMock("commercial-lease", fetchCommercialLeaseTerms);
  return (
    <Shell title="Commercial lease terms" subtitle="NNN / gross / modified gross (P5.8)">
      <ul className="divide-y divide-[rgb(var(--border))]">
        {(data ?? []).map((row) => (
          <li key={row.id} className="flex flex-wrap items-center justify-between gap-2 py-3">
            <span>
              <span className="font-medium">{row.tenantName}</span>
              <span className="text-[rgb(var(--text-muted))]"> · {row.unitLabel}</span>
            </span>
            <span className="flex items-center gap-2 text-sm">
              <Badge tone="neutral">{row.leaseStructure}</Badge>
              <span>Base {money(row.baseRentCents)}</span>
              {row.camCents > 0 && <span className="text-[rgb(var(--text-muted))]">CAM {money(row.camCents)}</span>}
            </span>
          </li>
        ))}
      </ul>
    </Shell>
  );
}

export function CooperativeSharesPage() {
  const { data } = useMock("coop-shares", fetchCooperativeShareRecords);
  return (
    <Shell title="Cooperative share registry" subtitle="Share certificates & unit linkage (P5.9)">
      <table className="w-full text-left text-sm">
        <thead className="text-xs text-[rgb(var(--text-muted))]">
          <tr>
            <th className="pb-2">Unit</th>
            <th>Shareholder</th>
            <th>Shares</th>
            <th>Certificate</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-[rgb(var(--border))]">
          {(data ?? []).map((r) => (
            <tr key={r.id}>
              <td className="py-2 font-medium">{r.unitLabel}</td>
              <td>{r.shareholderName}</td>
              <td>{r.shareCount}</td>
              <td className="font-mono text-xs">{r.certificateNo}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </Shell>
  );
}

export function CoopBoardPacketsPage() {
  const { data } = useMock("coop-packets", fetchCoopBoardPackets);
  return (
    <Shell title="Co-op board packets" subtitle="NYC-style board review packages">
      {(data ?? []).map((p) => (
        <div key={p.id} className="mb-3 flex items-start justify-between gap-3 rounded-lg border p-3">
          <div>
            <p className="font-medium">{p.title}</p>
            <p className="text-xs text-[rgb(var(--text-muted))]">
              Due {formatDate(p.dueDate)} · {p.documentCount} documents
            </p>
          </div>
          <Badge tone={p.status === "PUBLISHED" ? "ok" : "warn"}>{p.status}</Badge>
        </div>
      ))}
    </Shell>
  );
}

/** Map nav path → page (no dynamic segments) */
export const MOCK_ROUTE_PAGES: Record<string, FC> = {
  "leasing/listings": ListingsPage,
  "leasing/prospects": ProspectsPage,
  "leasing/applications": ApplicationsPage,
  "leasing/renewals": RenewalsPage,
  "leasing/commercial": CommercialLeaseTermsPage,
  associations: AssociationsListPage,
  "associations/occupants": AssociationUnitsPage,
  "associations/assessments": AssessmentsPage,
  "associations/cooperative-shares": CooperativeSharesPage,
  "associations/board-packets": CoopBoardPacketsPage,
  "maintenance/requests": MaintenanceRequestsPage,
  "maintenance/work-orders": WorkOrdersPage,
  "maintenance/vendors": VendorsPage,
  "maintenance/inspections": InspectionsPage,
  tasks: TasksListPage,
  "tasks/recurring": RecurringTasksPage,
  "communication/announcements": AnnouncementsPage,
  "communication/templates": EmailTemplatesPage,
  "communication/sms": SmsLogPage,
  "communication/mailing": MailingPage,
  "communication/messages": MessagesInboxPage,
  files: FilesPage,
  reports: ReportsPage,
  "reports/analytics": AnalyticsHubPage,
  shortcuts: ShortcutsPage,
  "marketplace/addons": MarketplaceAddonsPage,
  "marketplace/apps": MarketplaceAppsPage,
  "portals/public-site": PublicSitePage,
  "tr/parcels": ParcelsPage,
  "tr/patrol": PatrolPage,
  "tr/access": AccessControlPage,
  "tr/timesheets": TimesheetsPage,
  "tr/whatsapp": WhatsappPage,
  settings: OrgSettingsPage,
  "settings/subscriptions": SubscriptionsPage
};