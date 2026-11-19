import { useEffect, useState, type ReactNode } from "react";
import {
  useAddGlTransaction,
  useApproveEft,
  useBankAccounts,
  useBankLines,
  useBills,
  useBudget,
  useChartOfAccounts,
  useEftApprovals,
  useGlTransactions,
  useMatchBankLine,
  usePayBill,
  useRecurring
} from "../../data/hooks";
import { formatDate, money, moneyTry } from "../../lib/format";
import { PageHeader } from "../../ui/components/PageHeader";
import { Badge } from "../../ui/components/Badge";
import { Button } from "../../ui/components/Button";
import { Card, CardContent } from "../../ui/components/Card";
import { Input } from "../../ui/components/Input";

export function ChartOfAccountsPage() {
  const { data, isLoading } = useChartOfAccounts();
  return (
    <Shell title="Chart of accounts">
      <table className="w-full text-sm">
        <thead>
          <tr className="text-xs text-[rgb(var(--text-muted))]">
            <th className="py-2 text-left">Code</th>
            <th className="py-2 text-left">Name</th>
            <th className="py-2 text-left">Type</th>
          </tr>
        </thead>
        <tbody>
          {isLoading ? (
            <tr>
              <td colSpan={3}>Loading…</td>
            </tr>
          ) : (
            (data ?? []).map((a) => (
              <tr key={a.id} className="border-t border-[rgb(var(--border))]">
                <td className="py-2" style={{ paddingLeft: a.parentId ? 24 : 0 }}>
                  {a.code}
                </td>
                <td className="py-2">{a.name}</td>
                <td className="py-2">{a.type}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </Shell>
  );
}

export function TransactionsPage({ defaultScope = "ALL" }: { defaultScope?: "PROPERTY" | "COMPANY" | "ALL" }) {
  const [scope, setScope] = useState<"PROPERTY" | "COMPANY" | "ALL">(defaultScope);
  const { data, isLoading } = useGlTransactions(scope);
  const add = useAddGlTransaction();
  const [desc, setDesc] = useState("");
  const [amount, setAmount] = useState(10000);

  return (
    <Shell title="Transactions">
      <div className="mb-3 flex gap-2 text-xs">
        {(["ALL", "PROPERTY", "COMPANY"] as const).map((s) => (
          <button
            key={s}
            type="button"
            onClick={() => setScope(s)}
            className={`rounded-lg px-3 py-1 transition-colors ${
              scope === s
                ? "bg-teal-500/15 font-semibold text-teal-900 dark:text-teal-100"
                : "bg-[rgb(var(--surface-muted))] text-[rgb(var(--text-muted))]"
            }`}
          >
            {s === "ALL" ? "All" : s === "PROPERTY" ? "Property financials" : "Company financials"}
          </button>
        ))}
      </div>
      <table className="mb-4 w-full text-sm">
        <thead>
          <tr className="text-xs text-[rgb(var(--text-muted))]">
            <th className="text-left">Date</th>
            <th className="text-left">Description</th>
            <th className="text-left">Scope</th>
            <th className="text-right">Amount</th>
          </tr>
        </thead>
        <tbody>
          {isLoading ? (
            <tr>
              <td colSpan={4}>Loading…</td>
            </tr>
          ) : (
            (data ?? []).map((t) => (
              <tr key={t.id} className="border-t">
                <td className="py-2">{formatDate(t.date)}</td>
                <td className="py-2">{t.description}</td>
                <td className="py-2">
                  <Badge tone="neutral">{t.scope}</Badge>
                </td>
                <td className="py-2 text-right">{money(t.amountCents)}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
      <GlEntryForm
        desc={desc}
        setDesc={setDesc}
        amount={amount}
        setAmount={setAmount}
        onAdd={async () => {
          await add.mutateAsync({
            date: new Date().toISOString(),
            description: desc || "Manual entry",
            amountCents: amount,
            scope: scope === "COMPANY" ? "COMPANY" : "PROPERTY",
            propertyName: scope === "PROPERTY" ? "Maple Court Apartments" : undefined,
            kind: "CHARGE"
          });
          setDesc("");
        }}
        loading={add.isPending}
      />
    </Shell>
  );
}

export function CompanyFinancialsPage() {
  return <TransactionsPage defaultScope="COMPANY" />;
}

export function BankAccountsPage() {
  const { data: accounts } = useBankAccounts();
  const [accountId, setAccountId] = useState("");
  useEffect(() => {
    if (accounts?.length && !accountId) setAccountId(accounts[0].id);
  }, [accounts, accountId]);
  const { data: lines } = useBankLines(accountId);
  const match = useMatchBankLine();

  return (
    <Shell title="Bank accounts">
      <select
        className="mb-3 rounded-lg border px-2 py-1 text-sm"
        value={accountId}
        onChange={(e) => setAccountId(e.target.value)}
      >
        {(accounts ?? []).map((a) => (
          <option key={a.id} value={a.id}>
            {a.name} ···{a.last4} ({money(a.balanceCents)})
          </option>
        ))}
      </select>
      <table className="w-full text-sm">
        <thead>
          <tr className="text-xs text-[rgb(var(--text-muted))]">
            <th className="text-left">Date</th>
            <th className="text-left">Description</th>
            <th className="text-right">Amount</th>
            <th />
          </tr>
        </thead>
        <tbody>
          {(lines ?? []).map((l) => (
            <tr key={l.id} className="border-t">
              <td className="py-2">{formatDate(l.date)}</td>
              <td className="py-2">{l.description}</td>
              <td className="py-2 text-right">{money(l.amountCents)}</td>
              <td className="py-2 text-right">
                {l.matched ? (
                  <Badge tone="ok">Matched</Badge>
                ) : (
                  <Button type="button" size="sm" variant="secondary" onClick={() => match.mutate(l.id)}>
                    Match
                  </Button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </Shell>
  );
}

export function BillsPage() {
  const { data } = useBills();
  const pay = usePayBill();
  return (
    <Shell title="Bills (AP)">
      <ul className="divide-y text-sm">
        {(data ?? []).map((b) => (
          <li key={b.id} className="flex items-center justify-between py-2">
            <div>
              <div className="font-medium">{b.vendor}</div>
              <div className="text-xs text-[rgb(var(--text-muted))]">Due {b.dueDate}</div>
            </div>
            <div className="flex items-center gap-2">
              <span>{money(b.amountCents)}</span>
              <Badge tone={b.status === "PAID" ? "ok" : "warn"}>{b.status}</Badge>
              {b.status === "DUE" && (
                <Button type="button" size="sm" onClick={() => pay.mutate(b.id)}>
                  Pay
                </Button>
              )}
            </div>
          </li>
        ))}
      </ul>
    </Shell>
  );
}

export function RecurringPage() {
  const { data } = useRecurring();
  return (
    <Shell title="Recurring transactions">
      <ul className="text-sm">
        {(data ?? []).map((r) => (
          <li key={r.id} className="flex justify-between border-t py-2">
            <span>
              {r.label} <Badge tone="neutral">{r.cadence}</Badge>
            </span>
            <span>{money(r.amountCents)}</span>
          </li>
        ))}
      </ul>
    </Shell>
  );
}

export function EftApprovalsPage() {
  const { data } = useEftApprovals();
  const approve = useApproveEft();
  return (
    <Shell title="EFT approvals">
      <ul className="divide-y text-sm">
        {(data ?? []).map((e) => (
          <li key={e.id} className="flex justify-between py-2">
            <span>{e.payee}</span>
            <div className="flex items-center gap-2">
              <span>{money(e.amountCents)}</span>
              <Badge tone={e.status === "APPROVED" ? "ok" : "warn"}>{e.status}</Badge>
              {e.status === "PENDING" && (
                <Button type="button" size="sm" onClick={() => approve.mutate(e.id)}>
                  Approve
                </Button>
              )}
            </div>
          </li>
        ))}
      </ul>
    </Shell>
  );
}

export function BudgetPage() {
  const { data } = useBudget();
  return (
    <Shell title="Budget vs actual" subtitle="İşletme projesi (Apsiyon)">
      <table className="w-full text-sm">
        <thead>
          <tr className="text-xs text-[rgb(var(--text-muted))]">
            <th className="text-left">Category</th>
            <th className="text-right">Planned</th>
            <th className="text-right">Actual</th>
            <th className="text-right">Var %</th>
          </tr>
        </thead>
        <tbody>
          {(data ?? []).map((b) => {
            const varPct = b.plannedCents ? Math.round(((b.actualCents - b.plannedCents) / b.plannedCents) * 100) : 0;
            return (
              <tr key={b.category} className="border-t">
                <td className="py-2">{b.category}</td>
                <td className="py-2 text-right">{moneyTry(b.plannedCents)}</td>
                <td className="py-2 text-right">{moneyTry(b.actualCents)}</td>
                <td className="py-2 text-right">
                  <Badge tone={varPct > 5 ? "danger" : "ok"}>{varPct}%</Badge>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </Shell>
  );
}

function Shell({ title, subtitle, children }: { title: string; subtitle?: string; children: ReactNode }) {
  return (
    <div>
      <PageHeader title={title} subtitle={subtitle} />
      <Card>
        <CardContent>{children}</CardContent>
      </Card>
    </div>
  );
}

function GlEntryForm({
  desc,
  setDesc,
  amount,
  setAmount,
  onAdd,
  loading
}: {
  desc: string;
  setDesc: (v: string) => void;
  amount: number;
  setAmount: (v: number) => void;
  onAdd: () => void;
  loading?: boolean;
}) {
  return (
    <div className="rounded-lg border border-dashed border-[rgb(var(--border))] p-3 text-sm">
      <p className="mb-2 text-xs font-semibold text-[rgb(var(--text-muted))]">New charge / payment (mock)</p>
      <Input placeholder="Description" value={desc} onChange={(e) => setDesc(e.target.value)} className="mb-2" />
      <Input type="number" value={amount} onChange={(e) => setAmount(Number(e.target.value))} className="mb-2" />
      <Button type="button" size="sm" onClick={onAdd} isLoading={loading}>
        Post
      </Button>
    </div>
  );
}