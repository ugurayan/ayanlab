import { Link } from "react-router-dom";
import { usePortalOwner } from "../../data/hooks";
import { moneyTry } from "../../lib/format";
import { PortalShell } from "../../ui/layout/PortalShell";
import { Badge } from "../../ui/components/Badge";
import { Button } from "../../ui/components/Button";

export function OwnerPortalPage() {
  const { data, isLoading } = usePortalOwner();

  if (isLoading || !data) {
    return (
      <PortalShell title="Owner">
        <p className="text-sm text-[rgb(var(--text-muted))]">Loading…</p>
      </PortalShell>
    );
  }

  return (
    <PortalShell title="Owner">
      <div className="space-y-6">
        <header>
          <h1 className="text-xl font-bold">{data.displayName}</h1>
          <p className="text-sm text-[rgb(var(--text-muted))]">Homeowner portal — mock (Apsiyon-style dues)</p>
        </header>
        <section className="rounded-xl bg-amber-50 p-4">
          <p className="text-xs text-[rgb(var(--text-muted))]">Outstanding assessment</p>
          <p className="text-2xl font-bold">{moneyTry(data.balanceCents)}</p>
          <Button type="button" size="sm" className="mt-2" variant="secondary">
            Pay aidat (mock)
          </Button>
        </section>
        <section>
          <h2 className="mb-2 text-sm font-semibold">Statements</h2>
          <ul className="text-sm">
            {data.statements.map((s) => (
              <li key={s.period} className="flex justify-between border-b border-[rgb(var(--border))] py-2">
                <span>{s.period}</span>
                <span>{moneyTry(s.amountCents)}</span>
              </li>
            ))}
          </ul>
        </section>
        <section>
          <h2 className="mb-2 text-sm font-semibold">Violations</h2>
          <ul className="space-y-2 text-sm">
            {data.violations.map((v) => (
              <li key={v.id}>
                <Link to={`/app/associations/violations/${v.id}`} className="flex justify-between hover:text-teal-800">
                  <span>
                    {v.unitLabel} · {v.category}
                  </span>
                  <Badge tone={v.status === "OPEN" ? "danger" : "warn"}>{v.status}</Badge>
                </Link>
              </li>
            ))}
          </ul>
        </section>
        <section className="text-sm">
          <p>
            Open architectural requests: <strong>{data.architecturalOpen}</strong>
          </p>
          <Link to="/app/associations/architectural" className="text-teal-800 hover:underline">
            View / submit requests
          </Link>
        </section>
      </div>
    </PortalShell>
  );
}