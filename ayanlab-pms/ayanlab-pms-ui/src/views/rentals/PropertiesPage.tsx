import { Link } from "react-router-dom";
import { useProperties } from "../../data/hooks";
import { PageHeader } from "../../ui/components/PageHeader";
import { Card, CardContent } from "../../ui/components/Card";
import { Badge } from "../../ui/components/Badge";
import { Button } from "../../ui/components/Button";

export function PropertiesPage() {
  const { data, isLoading } = useProperties();

  return (
    <div>
      <PageHeader
        title="Properties"
        subtitle="Rentals and associations."
        actions={
          <Link to="/app/rentals/properties/new">
            <Button type="button" size="sm">
              New property
            </Button>
          </Link>
        }
      />
    <Card>
      <CardContent>
        {isLoading && <p className="text-sm text-[rgb(var(--text-muted))]">Loading…</p>}
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="border-b border-[rgb(var(--border))] text-xs text-[rgb(var(--text-muted))]">
                <th className="py-2 pr-4">Name</th>
                <th className="py-2 pr-4">Mode</th>
                <th className="py-2 pr-4">Units</th>
                <th className="py-2">Address</th>
              </tr>
            </thead>
            <tbody>
              {(data ?? []).map((p) => (
                <tr key={p.id} className="border-b border-[rgb(var(--border))]">
                  <td className="py-3 pr-4 font-medium">
                    <Link className="text-teal-900 hover:underline" to={`/app/rentals/properties/${p.id}`}>
                      {p.name}
                    </Link>
                  </td>
                  <td className="py-3 pr-4">
                    <Badge tone={p.mode === "ASSOCIATION" ? "ok" : "neutral"}>{p.mode}</Badge>
                  </td>
                  <td className="py-3 pr-4">{p.unitCount}</td>
                  <td className="py-3 text-[rgb(var(--text-muted))]">{p.address}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
    </div>
  );
}