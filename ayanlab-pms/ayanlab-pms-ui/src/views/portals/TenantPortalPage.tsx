import { Link } from "react-router-dom";
import { usePortalTenant } from "../../data/hooks";
import { formatDate, money } from "../../lib/format";
import { PortalShell } from "../../ui/layout/PortalShell";
import { Badge } from "../../ui/components/Badge";
import { Button } from "../../ui/components/Button";

export function TenantPortalPage() {
  const { data, isLoading } = usePortalTenant();

  if (isLoading || !data) {
    return (
      <PortalShell title="Tenant">
        <p className="text-sm text-[rgb(var(--text-muted))]">Loading…</p>
      </PortalShell>
    );
  }

  return (
    <PortalShell title="Tenant">
      <div className="space-y-6">
        <header>
          <h1 className="text-xl font-bold">Welcome, {data.displayName}</h1>
          <p className="text-sm text-[rgb(var(--text-muted))]">Resident Center — mock preview</p>
        </header>
        <section className="rounded-xl bg-[rgb(var(--surface-muted))] p-4">
          <p className="text-xs text-[rgb(var(--text-muted))]">Balance due</p>
          <p className="text-2xl font-bold text-rose-700">{money(data.balanceCents)}</p>
          <Button type="button" className="mt-2" size="sm" variant="secondary">
            Pay now (mock)
          </Button>
        </section>
        <section>
          <h2 className="mb-2 text-sm font-semibold">My lease</h2>
          <ul className="space-y-2 text-sm">
            {data.leases.map((l, i) => (
              <li key={i} className="rounded-lg border border-[rgb(var(--border))] p-2">
                {l.propertyName} — Unit {l.unitLabel}
                <div className="text-xs text-[rgb(var(--text-muted))]">Ends {formatDate(l.endDate)}</div>
              </li>
            ))}
          </ul>
        </section>
        <section>
          <h2 className="mb-2 text-sm font-semibold">Maintenance</h2>
          <ul className="space-y-2 text-sm">
            {data.maintenance.map((m) => (
              <li key={m.id} className="flex justify-between gap-2">
                <span>{m.summary}</span>
                <Badge tone={m.status === "OVERDUE" ? "danger" : "neutral"}>{m.status}</Badge>
              </li>
            ))}
          </ul>
          <Button type="button" size="sm" className="mt-2" variant="ghost">
            New request (mock)
          </Button>
        </section>
        <Link to="/app/associations/architectural" className="text-xs text-teal-800 hover:underline">
          HOA owners: submit architectural request →
        </Link>
      </div>
    </PortalShell>
  );
}