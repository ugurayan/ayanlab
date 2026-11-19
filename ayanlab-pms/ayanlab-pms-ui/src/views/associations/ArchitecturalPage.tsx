import { Link } from "react-router-dom";
import { useArchitecturalList } from "../../data/hooks";
import { formatDate } from "../../lib/format";
import { PageHeader } from "../../ui/components/PageHeader";
import { Badge } from "../../ui/components/Badge";
import { Card, CardContent } from "../../ui/components/Card";

export function ArchitecturalPage() {
  const { data, isLoading } = useArchitecturalList();

  return (
    <div>
      <PageHeader title="Architectural requests" subtitle="Committee review workflow (mock)." />
      <Card>
        <CardContent className="divide-y divide-[rgb(var(--border))] p-0">
          {isLoading && <p className="p-4 text-sm text-[rgb(var(--text-muted))]">Loading…</p>}
          {(data ?? []).map((a) => (
            <Link
              key={a.id}
              to={`/app/associations/architectural/${a.id}`}
              className="block px-4 py-3 hover:bg-[rgb(var(--surface-muted))]"
            >
              <div className="flex items-start justify-between gap-3">
                <div>
                  <div className="font-medium">{a.title}</div>
                  <div className="text-xs text-[rgb(var(--text-muted))]">
                    Unit {a.unitLabel} · {formatDate(a.submittedAt)}
                  </div>
                </div>
                <Badge
                  tone={
                    a.status === "APPROVED" ? "ok" : a.status === "DENIED" ? "danger" : a.status === "COMMITTEE" ? "warn" : "neutral"
                  }
                >
                  {a.status}
                </Badge>
              </div>
            </Link>
          ))}
        </CardContent>
      </Card>
    </div>
  );
}