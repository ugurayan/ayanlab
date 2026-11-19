import { Link, useParams } from "react-router-dom";
import { useUpdateViolation, useViolation } from "../../data/hooks";
import { formatDate, moneyTry } from "../../lib/format";
import { PageHeader } from "../../ui/components/PageHeader";
import { Badge } from "../../ui/components/Badge";
import { Button } from "../../ui/components/Button";
import { Card, CardContent } from "../../ui/components/Card";
import type { Violation } from "../../types/pms";

export function ViolationDetailPage() {
  const { id = "" } = useParams();
  const { data, isLoading } = useViolation(id);
  const update = useUpdateViolation();

  if (isLoading) return <p className="text-sm text-[rgb(var(--text-muted))]">Loading…</p>;
  if (!data) return <p className="text-sm text-rose-600">Not found.</p>;

  async function setStatus(status: Violation["status"]) {
    await update.mutateAsync({ id, status });
  }

  return (
    <div>
      <PageHeader
        title={`Violation — ${data.unitLabel}`}
        subtitle={data.category}
        actions={
          <Link to="/app/associations/violations" className="text-sm text-teal-800 hover:underline">
            Back
          </Link>
        }
      />
      <div className="mb-4">
        <Badge tone={data.status === "OPEN" ? "danger" : data.status === "HEARING" ? "warn" : "ok"}>{data.status}</Badge>
      </div>
      <div className="mb-4 flex flex-wrap gap-2">
        <Button type="button" size="sm" variant="secondary" onClick={() => setStatus("HEARING")} isLoading={update.isPending}>
          Schedule hearing
        </Button>
        <Button type="button" size="sm" variant="ghost" onClick={() => setStatus("CLOSED")} isLoading={update.isPending}>
          Close
        </Button>
      </div>
      <Card>
        <CardContent className="space-y-3">
          <p className="text-sm">{data.description}</p>
          <p className="text-sm">
            Fine: <strong>{moneyTry(data.fineCents)}</strong> · Opened {formatDate(data.openedAt)}
          </p>
          <h3 className="text-sm font-semibold">Timeline</h3>
          <ul className="space-y-2 text-sm text-[rgb(var(--text-muted))]">
            {data.timeline.map((t, i) => (
              <li key={i}>
                <span className="text-xs text-[rgb(var(--text-muted))]/80">{formatDate(t.at)}</span> — {t.note}
              </li>
            ))}
          </ul>
        </CardContent>
      </Card>
    </div>
  );
}