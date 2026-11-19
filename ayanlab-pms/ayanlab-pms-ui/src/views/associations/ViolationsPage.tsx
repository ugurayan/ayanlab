import { Link } from "react-router-dom";
import { useViolations } from "../../data/hooks";
import { moneyTry } from "../../lib/format";
import { PageHeader } from "../../ui/components/PageHeader";
import { Badge } from "../../ui/components/Badge";
import { Card, CardContent } from "../../ui/components/Card";

export function ViolationsPage() {
  const { data, isLoading } = useViolations();

  return (
    <div>
      <PageHeader title="Violations" subtitle="Association CC&R enforcement (mock)." />
      <Card>
        <CardContent className="divide-y divide-[rgb(var(--border))] p-0">
          {isLoading && <p className="p-4 text-sm text-[rgb(var(--text-muted))]">Loading…</p>}
          {(data ?? []).map((v) => (
            <Link
              key={v.id}
              to={`/app/associations/violations/${v.id}`}
              className="flex items-start justify-between gap-4 px-4 py-3 hover:bg-[rgb(var(--surface-muted))]"
            >
              <div>
                <div className="font-medium">
                  Unit {v.unitLabel} · {v.category}
                </div>
                <p className="text-xs text-[rgb(var(--text-muted))] line-clamp-1">{v.description}</p>
              </div>
              <div className="text-right text-xs">
                <Badge tone={v.status === "OPEN" ? "danger" : v.status === "HEARING" ? "warn" : "ok"}>{v.status}</Badge>
                <div className="mt-1 font-semibold">{moneyTry(v.fineCents)}</div>
              </div>
            </Link>
          ))}
        </CardContent>
      </Card>
    </div>
  );
}