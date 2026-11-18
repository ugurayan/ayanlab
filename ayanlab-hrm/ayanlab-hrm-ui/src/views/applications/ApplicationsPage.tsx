import React from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Table, Td, Th } from "../../ui/components/Table";
import { Badge } from "../../ui/components/Badge";
import { useApplications } from "../../data/hooks";
import { RequirePermission } from "../../router/guards";
import { useTranslations } from "../../state/i18n";
import { TEXTS } from "./i18n";

function tone(s: string) {
  switch (s) {
    case "HIRED": return "green";
    case "SHORTLISTED": return "blue";
    case "REJECTED": return "red";
    default: return "amber";
  }
}

export function ApplicationsPage() {
  return (
    <RequirePermission anyOf={["d6e7f8a9-b0c1-2345-6789-34567890abcd"]}>
      <ApplicationsInner />
    </RequirePermission>
  );
}

function ApplicationsInner() {
  const q = useApplications();
  const copy = useTranslations(TEXTS);

  return (
    <div className="space-y-6">
      <div>
        <div className="text-2xl font-bold">{copy.hero.title}</div>
        <div className="text-slate-500">{copy.hero.subtitle}</div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>{copy.table.title}</CardTitle>
        </CardHeader>
        <CardContent>
          {q.isLoading ? (
            <div className="text-slate-500">{copy.table.loading}</div>
          ) : (
            <Table>
              <thead>
                <tr>
                  <Th>{copy.table.columns.id}</Th>
                  <Th>{copy.table.columns.shift}</Th>
                  <Th>{copy.table.columns.worker}</Th>
                  <Th>{copy.table.columns.status}</Th>
                </tr>
              </thead>
              <tbody>
                {(q.data ?? []).map((a: any) => (
                  <tr key={a.id}>
                    <Td className="font-semibold">{a.id}</Td>
                    <Td>{a.shiftId}</Td>
                    <Td>{a.workerId}</Td>
                    <Td><Badge tone={tone(a.status) as any}>{a.status}</Badge></Td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
