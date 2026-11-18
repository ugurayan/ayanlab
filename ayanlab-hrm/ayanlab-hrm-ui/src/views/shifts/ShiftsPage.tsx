import React, { useMemo, useState, useCallback } from "react";
import { Link } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Table, Td, Th } from "../../ui/components/Table";
import { Badge } from "../../ui/components/Badge";
import { Button } from "../../ui/components/Button";
import { useShifts } from "../../data/hooks";
import { RequirePermission } from "../../router/guards";
import { formatDateTime } from "../../lib/format";
import { useTranslations } from "../../state/i18n";
import { TEXTS } from "./i18n";

function statusTone(s: string) {
  switch (s) {
    case "PUBLISHED": return "blue";
    case "FILLED": return "green";
    case "COMPLETED": return "gray";
    default: return "amber";
  }
}

export function ShiftsPage() {
  return (
    <RequirePermission anyOf={["c5d6e7f8-a9b0-1234-5678-234567890abc"]}>
      <ShiftsInner />
    </RequirePermission>
  );
}

function ShiftsInner() {
  const q = useShifts();
  const copy = useTranslations(TEXTS);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  const handlePageSizeChange = useCallback((e: React.ChangeEvent<HTMLSelectElement>) => {
    setPageSize(Number(e.target.value));
    setPage(0);
  }, []);

  const rows = (q.data ?? []) as any[];
  const totalRows = rows.length;
  const totalPages = Math.max(1, Math.ceil(totalRows / pageSize));
  const currentPage = Math.min(page, Math.max(0, totalPages - 1));
  const paginatedRows = useMemo(() => {
    const start = currentPage * pageSize;
    return rows.slice(start, start + pageSize);
  }, [rows, currentPage, pageSize]);

  return (
    <div className="space-y-6">
      <div className="flex items-end justify-between gap-4">
        <div>
          <div className="text-2xl font-bold">{copy.hero.title}</div>
          <div className="text-slate-500">{copy.hero.subtitle}</div>
        </div>

        <Link to="/app/shifts/new">
          <Button>{copy.hero.create}</Button>
        </Link>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>{copy.table.title}</CardTitle>
        </CardHeader>
        <CardContent>
          {q.isLoading ? (
            <div className="text-slate-500">{copy.table.loading}</div>
          ) : totalRows === 0 ? (
            <div className="text-slate-500 text-sm">No shifts found.</div>
          ) : (
            <>
              <div className="flex items-center justify-between text-sm text-slate-600 mb-3">
                <span>
                  {totalRows} {totalRows === 1 ? "shift" : "shifts"}
                  {totalRows > 0 && (
                    <> (showing {currentPage * pageSize + 1}–{Math.min((currentPage + 1) * pageSize, totalRows)})</>
                  )}
                </span>
                <label className="flex items-center gap-2">
                  <span>Rows per page</span>
                  <select
                    value={pageSize}
                    onChange={handlePageSizeChange}
                    className="rounded-lg border border-slate-200 px-2 py-1 text-sm bg-white"
                  >
                    <option value={10}>10</option>
                    <option value={25}>25</option>
                    <option value={50}>50</option>
                    <option value={100}>100</option>
                  </select>
                </label>
              </div>

              <Table>
                <thead>
                  <tr>
                    <Th>{copy.table.columns.role}</Th>
                    <Th>{copy.table.columns.location}</Th>
                    <Th>{copy.table.columns.start}</Th>
                    <Th>{copy.table.columns.end}</Th>
                    <Th>{copy.table.columns.headcount}</Th>
                    <Th>{copy.table.columns.status}</Th>
                  </tr>
                </thead>
                <tbody>
                  {paginatedRows.map((s: any) => (
                    <tr key={s.id}>
                      <Td className="font-semibold">{s.role}</Td>
                      <Td className="text-slate-600">{s.locationId}</Td>
                      <Td>{formatDateTime(s.startAt)}</Td>
                      <Td>{formatDateTime(s.endAt)}</Td>
                      <Td>{s.headcount}</Td>
                      <Td><Badge tone={statusTone(s.status) as any}>{s.status}</Badge></Td>
                    </tr>
                  ))}
                </tbody>
              </Table>

              {totalPages > 1 && (
                <div className="flex items-center justify-between text-sm text-slate-600 mt-4 pt-4 border-t border-slate-100">
                  <div>
                    Page <span className="font-semibold">{currentPage + 1}</span> of{" "}
                    <span className="font-semibold">{totalPages}</span>
                  </div>
                  <div className="flex gap-2">
                    <Button
                      variant="secondary"
                      disabled={currentPage === 0}
                      onClick={() => setPage((p) => Math.max(0, p - 1))}
                    >
                      Previous
                    </Button>
                    <Button
                      variant="secondary"
                      disabled={currentPage >= totalPages - 1}
                      onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                    >
                      Next
                    </Button>
                  </div>
                </div>
              )}
            </>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
