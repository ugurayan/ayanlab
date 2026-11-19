import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useArchitectural, useUpdateArchitectural } from "../../data/hooks";
import { formatDate } from "../../lib/format";
import { PageHeader } from "../../ui/components/PageHeader";
import { Badge } from "../../ui/components/Badge";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import { Label } from "../../ui/components/Label";
import { Card, CardContent } from "../../ui/components/Card";

export function ArchitecturalDetailPage() {
  const { id = "" } = useParams();
  const { data, isLoading } = useArchitectural(id);
  const update = useUpdateArchitectural();
  const [note, setNote] = useState("");

  if (isLoading) return <p className="text-sm text-[rgb(var(--text-muted))]">Loading…</p>;
  if (!data) return <p className="text-sm text-rose-600">Not found.</p>;

  return (
    <div>
      <PageHeader
        title={data.title}
        subtitle={`Unit ${data.unitLabel}`}
        actions={
          <Link to="/app/associations/architectural" className="text-sm text-teal-800 hover:underline">
            Back
          </Link>
        }
      />
      <Badge tone={data.status === "APPROVED" ? "ok" : data.status === "DENIED" ? "danger" : "warn"}>{data.status}</Badge>
      <Card className="mt-4">
        <CardContent className="space-y-3 text-sm">
          <p>{data.description}</p>
          <p className="text-xs text-[rgb(var(--text-muted))]">Submitted {formatDate(data.submittedAt)}</p>
          {data.committeeNote && (
            <p className="rounded-lg bg-[rgb(var(--surface-muted))] p-2 text-xs">
              Committee: {data.committeeNote}
            </p>
          )}
          <div className="space-y-2 border-t border-[rgb(var(--border))] pt-3">
            <Label htmlFor="committee-note">Committee note</Label>
            <Input
              id="committee-note"
              value={note}
              onChange={(e) => setNote(e.target.value)}
              placeholder="Optional note on decision"
            />
            <div className="flex flex-wrap gap-2">
              <Button
                type="button"
                size="sm"
                onClick={() => update.mutate({ id, status: "APPROVED", committeeNote: note || data.committeeNote })}
                isLoading={update.isPending}
              >
                Approve
              </Button>
              <Button
                type="button"
                size="sm"
                variant="danger"
                onClick={() => update.mutate({ id, status: "DENIED", committeeNote: note || "Denied" })}
                isLoading={update.isPending}
              >
                Deny
              </Button>
              <Button
                type="button"
                size="sm"
                variant="secondary"
                onClick={() => update.mutate({ id, status: "COMMITTEE", committeeNote: note })}
                isLoading={update.isPending}
              >
                Request info
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}