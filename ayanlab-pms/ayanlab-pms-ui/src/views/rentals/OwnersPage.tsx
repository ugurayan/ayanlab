import { useRentalOwners } from "../../data/hooks";
import { PageHeader } from "../../ui/components/PageHeader";
import { Card, CardContent } from "../../ui/components/Card";

export function OwnersPage() {
  const { data, isLoading } = useRentalOwners();

  return (
    <div>
      <PageHeader title="Rental owners" subtitle="Ownership accounts linked to properties." />
      <Card>
        <CardContent className="space-y-3">
          {isLoading && <p className="text-sm text-[rgb(var(--text-muted))]">Loading…</p>}
          {(data ?? []).map((o) => (
            <div key={o.id} className="rounded-xl border border-[rgb(var(--border))] p-3">
              <div className="font-medium">{o.displayName}</div>
              <div className="text-xs text-[rgb(var(--text-muted))]">{o.email}</div>
              <div className="mt-2 text-xs">
                Properties: {(o.properties.length ? o.properties : ["—"]).join(", ")}
              </div>
            </div>
          ))}
        </CardContent>
      </Card>
    </div>
  );
}