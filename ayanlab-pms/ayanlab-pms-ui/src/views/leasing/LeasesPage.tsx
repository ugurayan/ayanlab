import { Link } from "react-router-dom";
import { useLeases } from "../../data/hooks";
import { formatDate, money } from "../../lib/format";
import { PageHeader } from "../../ui/components/PageHeader";
import { Badge } from "../../ui/components/Badge";
import { Button } from "../../ui/components/Button";
import { Card, CardContent } from "../../ui/components/Card";

export function LeasesPage() {
  const { data, isLoading } = useLeases();

  return (
    <div>
      <PageHeader
        title="Leases"
        subtitle="Active, expiring, and draft leases."
        actions={
          <Link to="/app/leasing/leases/new">
            <Button type="button" size="sm">
              New lease
            </Button>
          </Link>
        }
      />
      <Card>
        <CardContent className="overflow-x-auto p-0">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b bg-[rgb(var(--surface-muted))] text-xs text-[rgb(var(--text-muted))]">
                <th className="px-4 py-2 text-left">Property / unit</th>
                <th className="px-4 py-2 text-left">Tenant</th>
                <th className="px-4 py-2 text-left">Status</th>
                <th className="px-4 py-2 text-left">Term</th>
                <th className="px-4 py-2 text-right">Rent</th>
                <th className="px-4 py-2" />
              </tr>
            </thead>
            <tbody>
              {isLoading ? (
                <tr>
                  <td colSpan={6} className="p-4 text-[rgb(var(--text-muted))]">
                    Loading…
                  </td>
                </tr>
              ) : (
                (data ?? []).map((l) => (
                  <tr key={l.id} className="border-b border-[rgb(var(--border))]">
                    <td className="px-4 py-3">
                      {l.propertyName} · {l.unitLabel}
                    </td>
                    <td className="px-4 py-3">{l.tenantName}</td>
                    <td className="px-4 py-3">
                      <Badge tone={l.status === "EXPIRING" ? "warn" : "ok"}>{l.status}</Badge>
                    </td>
                    <td className="px-4 py-3 text-xs text-[rgb(var(--text-muted))]">
                      {formatDate(l.startDate)} – {formatDate(l.endDate)}
                    </td>
                    <td className="px-4 py-3 text-right">{money(l.rentCents)}</td>
                    <td className="px-4 py-3 text-right">
                      <Link className="text-teal-800 hover:underline" to={`/app/leasing/leases/${l.id}`}>
                        Edit
                      </Link>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </CardContent>
      </Card>
    </div>
  );
}