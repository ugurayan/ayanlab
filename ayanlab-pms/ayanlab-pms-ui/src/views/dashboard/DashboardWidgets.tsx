import type { ReactNode } from "react";
import { Link } from "react-router-dom";
import type { DashboardWidgetId } from "../../config/dashboardWidgets";
import type { DashboardSummary } from "../../api/services/dashboard";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Badge } from "../../ui/components/Badge";
import { money } from "../../lib/format";

export function DashboardWidgetCard({
  title,
  href,
  viewLabel,
  children
}: {
  title: string;
  href: string;
  viewLabel: string;
  children: ReactNode;
}) {
  return (
    <Card className="flex flex-col">
      <CardHeader className="flex flex-row items-center justify-between pb-2">
        <CardTitle className="text-base">{title}</CardTitle>
        <Link
          to={href}
          className="text-xs font-semibold text-teal-800 hover:underline focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-teal-600/40 rounded dark:text-teal-300"
        >
          {viewLabel}
        </Link>
      </CardHeader>
      <CardContent className="flex-1">{children}</CardContent>
    </Card>
  );
}

export function renderDashboardWidget(
  id: DashboardWidgetId,
  data: DashboardSummary,
  t: (key: string) => string
): ReactNode {
  switch (id) {
    case "balances":
      return (
        <ul className="space-y-2 text-sm">
          {data.balances.map((b) => (
            <li key={b.id} className="flex justify-between gap-2">
              <span>
                {b.partyName}{" "}
                <Badge tone={b.daysPastDue > 30 ? "danger" : "warn"}>{b.daysPastDue}d</Badge>
              </span>
              <span className="font-semibold">{money(b.amountCents)}</span>
            </li>
          ))}
        </ul>
      );
    case "tasks":
      return <p className="text-3xl font-bold">{data.openTasks}</p>;
    case "maintenance":
      return (
        <ul className="space-y-2 text-sm">
          {data.maintenance.map((m) => (
            <li key={m.id}>
              {m.summary}{" "}
              <Badge tone={m.status === "OVERDUE" ? "danger" : "neutral"}>{m.status}</Badge>
            </li>
          ))}
        </ul>
      );
    case "insurance":
      return (
        <ul className="space-y-1 text-sm">
          {data.insuranceGaps.map((r, i) => (
            <li key={i}>
              {r.tenantName} — <Badge tone={r.status === "MISSING" ? "danger" : "ok"}>{r.status}</Badge>
            </li>
          ))}
        </ul>
      );
    case "expiringLeases":
      return <p className="text-3xl font-bold">{data.expiringLeases.length}</p>;
    case "listings":
      return (
        <p className="text-sm">
          {t("dashboard.vacant")} <strong>{data.listings.vacant}</strong> · {t("dashboard.pendingApps")}{" "}
          <strong>{data.listings.applicationsPending}</strong>
        </p>
      );
    case "activity":
      return (
        <ul className="space-y-1 text-sm text-[rgb(var(--text-muted))]">
          {data.activity.map((a) => (
            <li key={a.id}>{a.summary}</li>
          ))}
        </ul>
      );
    case "violations":
      return (
        <>
          <p className="text-sm">
            {data.violations.length} {t("dashboard.openViolations")}
          </p>
          <p className="text-sm font-semibold">
            {t("dashboard.delinquent")} {money(data.associationDelinquentCents)}
          </p>
        </>
      );
    case "bank":
      return data.bankUnmatched.map((row) => (
        <p key={row.id} className="text-sm">
          {row.description} — {money(row.amountCents)}
        </p>
      ));
    case "billsEft":
      return (
        <p className="text-3xl font-bold">
          {data.eftPendingCount ?? 2}
          <span className="ml-2 text-sm font-normal text-[rgb(var(--text-muted))]">{t("dashboard.eftPending")}</span>
        </p>
      );
    case "patrol":
      return (
        <p className="text-3xl font-bold">
          {data.patrolCompleted ?? 3}/{data.patrolTotal ?? 5}
          <span className="ml-2 text-sm font-normal text-[rgb(var(--text-muted))]">{t("dashboard.patrolTonight")}</span>
        </p>
      );
    default:
      return null;
  }
}