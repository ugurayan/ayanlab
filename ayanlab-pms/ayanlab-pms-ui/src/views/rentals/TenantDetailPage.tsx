import { Link, useParams } from "react-router-dom";
import { useTenant } from "../../data/hooks";
import { formatDate, money } from "../../lib/format";
import { PageHeader } from "../../ui/components/PageHeader";
import { Badge } from "../../ui/components/Badge";
import { Card, CardContent } from "../../ui/components/Card";

export function TenantDetailPage() {
  const { id = "" } = useParams();
  const { data, isLoading } = useTenant(id);

  if (isLoading) return <p className="text-sm text-[rgb(var(--text-muted))]">Loading…</p>;
  if (!data) return <p className="text-sm text-rose-600">Tenant not found.</p>;

  return (
    <div>
      <PageHeader
        title={data.displayName}
        subtitle={data.email}
        actions={
          <Link to="/app/rentals/tenants" className="text-sm text-teal-800 hover:underline">
            Back to list
          </Link>
        }
      />
      <div className="grid gap-4 md:grid-cols-2">
        <Card>
          <CardContent>
            <h2 className="mb-2 text-sm font-semibold">Contact</h2>
            <p className="text-sm">Phone: {data.phone ?? "—"}</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent>
            <h2 className="mb-2 text-sm font-semibold">Lease history</h2>
            <ul className="space-y-2 text-sm">
              {data.leases.map((l) => (
                <li key={l.id} className="rounded-lg border border-[rgb(var(--border))] p-2">
                  <div className="font-medium">
                    {l.propertyName} · {l.unitLabel}{" "}
                    <Badge tone={l.status === "EXPIRING" ? "warn" : "ok"}>{l.status}</Badge>
                  </div>
                  <div className="text-xs text-[rgb(var(--text-muted))]">
                    {formatDate(l.startDate)} – {formatDate(l.endDate)} · {money(l.rentCents)}
                  </div>
                  <Link to={`/app/leasing/leases/${l.id}`} className="text-xs text-teal-800 hover:underline">
                    Open lease
                  </Link>
                </li>
              ))}
            </ul>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}