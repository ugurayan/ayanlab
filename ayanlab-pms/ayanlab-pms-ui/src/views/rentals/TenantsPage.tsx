import { Link } from "react-router-dom";
import { useTenants } from "../../data/hooks";
import { PageHeader } from "../../ui/components/PageHeader";
import { Card, CardContent } from "../../ui/components/Card";

export function TenantsPage() {
  const { data, isLoading } = useTenants();

  return (
    <div>
      <PageHeader title="Tenants" subtitle="Resident profiles (mock)." />
      <Card>
        <CardContent className="divide-y divide-[rgb(var(--border))] p-0">
          {isLoading && <p className="p-4 text-sm text-[rgb(var(--text-muted))]">Loading…</p>}
          {(data ?? []).map((t) => (
            <Link
              key={t.id}
              to={`/app/rentals/tenants/${t.id}`}
              className="flex items-center justify-between px-4 py-3 hover:bg-[rgb(var(--surface-muted))]"
            >
              <div>
                <div className="font-medium">{t.displayName}</div>
                <div className="text-xs text-[rgb(var(--text-muted))]">{t.email}</div>
              </div>
              <span className="text-xs text-teal-800">View</span>
            </Link>
          ))}
        </CardContent>
      </Card>
    </div>
  );
}