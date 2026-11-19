import { useBalances } from "../../data/hooks";
import { money } from "../../lib/format";
import { PageHeader } from "../../ui/components/PageHeader";
import { Badge } from "../../ui/components/Badge";
import { Card, CardContent } from "../../ui/components/Card";

export function BalancesPage() {
  const { data, isLoading } = useBalances();

  return (
    <div>
      <PageHeader title="Outstanding balances" subtitle="Tenants and association owners with open charges." />
      <Card>
        <CardContent className="overflow-x-auto p-0">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b bg-[rgb(var(--surface-muted))] text-xs text-[rgb(var(--text-muted))]">
                <th className="px-4 py-2 text-left">Property</th>
                <th className="px-4 py-2 text-left">Party</th>
                <th className="px-4 py-2 text-left">Type</th>
                <th className="px-4 py-2 text-right">Amount</th>
                <th className="px-4 py-2 text-right">Past due</th>
              </tr>
            </thead>
            <tbody>
              {isLoading ? (
                <tr>
                  <td colSpan={5} className="p-4 text-[rgb(var(--text-muted))]">
                    Loading…
                  </td>
                </tr>
              ) : (
                (data ?? []).map((b) => (
                  <tr key={b.id} className="border-b border-[rgb(var(--border))]">
                    <td className="px-4 py-3">{b.propertyName}</td>
                    <td className="px-4 py-3 font-medium">{b.partyName}</td>
                    <td className="px-4 py-3">{b.partyType}</td>
                    <td className="px-4 py-3 text-right font-semibold">{money(b.amountCents)}</td>
                    <td className="px-4 py-3 text-right">
                      <Badge tone={b.daysPastDue > 30 ? "danger" : "warn"}>{b.daysPastDue}d</Badge>
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