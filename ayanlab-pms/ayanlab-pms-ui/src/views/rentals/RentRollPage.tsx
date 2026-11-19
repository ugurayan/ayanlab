import { Link } from "react-router-dom";
import { useRentRoll } from "../../data/hooks";
import { formatDate, money } from "../../lib/format";
import { PageHeader } from "../../ui/components/PageHeader";
import { Badge } from "../../ui/components/Badge";
import { Card, CardContent } from "../../ui/components/Card";

export function RentRollPage() {
  const { data, isLoading } = useRentRoll();

  return (
    <div>
      <PageHeader title="Rent roll" subtitle="Units, occupancy, and lease summary (mock)." />
      <Card>
        <CardContent className="overflow-x-auto p-0">
          {isLoading ? (
            <p className="p-4 text-sm text-[rgb(var(--text-muted))]">Loading…</p>
          ) : (
            <table className="w-full text-left text-sm">
              <thead>
                <tr className="border-b border-[rgb(var(--border))] bg-[rgb(var(--surface-muted))] text-xs text-[rgb(var(--text-muted))]">
                  <th className="px-4 py-2">Property</th>
                  <th className="px-4 py-2">Unit</th>
                  <th className="px-4 py-2">Status</th>
                  <th className="px-4 py-2">Tenant</th>
                  <th className="px-4 py-2">Rent</th>
                  <th className="px-4 py-2">Lease end</th>
                  <th className="px-4 py-2" />
                </tr>
              </thead>
              <tbody>
                {(data ?? []).map((row) => (
                  <tr key={row.unitId} className="border-b border-[rgb(var(--border))]">
                    <td className="px-4 py-3">{row.propertyName}</td>
                    <td className="px-4 py-3 font-medium">{row.unitLabel}</td>
                    <td className="px-4 py-3">
                      <Badge tone={row.unitStatus === "VACANT" ? "warn" : row.unitStatus === "NOTICE" ? "danger" : "ok"}>
                        {row.unitStatus}
                      </Badge>
                    </td>
                    <td className="px-4 py-3">{row.tenantName ?? "—"}</td>
                    <td className="px-4 py-3">{row.rentCents ? money(row.rentCents) : "—"}</td>
                    <td className="px-4 py-3">{row.leaseEnd ? formatDate(row.leaseEnd) : "—"}</td>
                    <td className="px-4 py-3 text-right">
                      {row.leaseId && (
                        <Link className="text-teal-800 hover:underline" to={`/app/leasing/leases/${row.leaseId}`}>
                          Lease
                        </Link>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </CardContent>
      </Card>
    </div>
  );
}