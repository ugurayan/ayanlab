import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { usePropertyDetail } from "../../data/hooks";
import { formatDate, money } from "../../lib/format";
import { PageHeader } from "../../ui/components/PageHeader";
import { Tabs } from "../../ui/components/Tabs";
import { Badge } from "../../ui/components/Badge";
import { Card, CardContent } from "../../ui/components/Card";

export function PropertyDetailPage() {
  const { id = "" } = useParams();
  const { data, isLoading } = usePropertyDetail(id);
  const [tab, setTab] = useState("units");

  if (isLoading) return <p className="text-sm text-[rgb(var(--text-muted))]">Loading…</p>;
  if (!data) return <p className="text-sm text-rose-600">Property not found.</p>;

  return (
    <div>
      <PageHeader
        title={data.name}
        subtitle={data.address}
        actions={
          <Link to="/app/rentals/properties" className="text-sm text-teal-800 hover:underline">
            Back
          </Link>
        }
      />
      <Badge tone={data.mode === "ASSOCIATION" ? "ok" : "neutral"}>{data.mode}</Badge>
      <div className="mt-4">
        <Tabs
          active={tab}
          onChange={setTab}
          tabs={[
            { id: "units", label: "Units" },
            { id: "owners", label: "Owners" },
            { id: "files", label: "Files" },
            { id: "notes", label: "Notes" }
          ]}
        />
        <Card className="rounded-t-none">
          <CardContent className="pt-4">
            {tab === "units" && (
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-xs text-[rgb(var(--text-muted))]">
                    <th className="py-2 text-left">Unit</th>
                    <th className="py-2 text-left">Status</th>
                    <th className="py-2 text-right">Market rent</th>
                  </tr>
                </thead>
                <tbody>
                  {data.units.map((u) => (
                    <tr key={u.id} className="border-t border-[rgb(var(--border))]">
                      <td className="py-2">{u.label}</td>
                      <td className="py-2">
                        <Badge tone={u.status === "VACANT" ? "warn" : "ok"}>{u.status}</Badge>
                      </td>
                      <td className="py-2 text-right">{u.monthlyRentCents ? money(u.monthlyRentCents) : "—"}</td>
                    </tr>
                  ))}
                  {!data.units.length && <tr><td colSpan={3} className="py-4 text-[rgb(var(--text-muted))]">No units (association mode).</td></tr>}
                </tbody>
              </table>
            )}
            {tab === "owners" && (
              <ul className="list-disc pl-5 text-sm">
                {data.ownerNames.length ? data.ownerNames.map((n) => <li key={n}>{n}</li>) : <li className="list-none text-[rgb(var(--text-muted))]">No owners linked.</li>}
              </ul>
            )}
            {tab === "files" && (
              <ul className="divide-y text-sm">
                {data.files.map((f) => (
                  <li key={f.id} className="flex justify-between py-2">
                    <span>{f.name}</span>
                    <span className="text-[rgb(var(--text-muted))]">{f.sizeKb} KB</span>
                  </li>
                ))}
                {!data.files.length && <p className="text-[rgb(var(--text-muted))]">No files.</p>}
              </ul>
            )}
            {tab === "notes" && (
              <ul className="space-y-3 text-sm">
                {data.notes.map((n) => (
                  <li key={n.id} className="rounded-lg border border-[rgb(var(--border))] p-3">
                    <p>{n.body}</p>
                    <p className="mt-1 text-xs text-[rgb(var(--text-muted))]">
                      {n.author} · {formatDate(n.createdAt)}
                    </p>
                  </li>
                ))}
                {!data.notes.length && <p className="text-[rgb(var(--text-muted))]">No notes yet.</p>}
              </ul>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}