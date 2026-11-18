import React, { useMemo, useState, useCallback } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Table, Td, Th } from "../../ui/components/Table";
import { formatDateTime } from "../../lib/format";

type AuditRow = { id: string; actorEmail: string; action: string; target: string; createdAt: string };

type TableCopy = {
  title: string;
  loading: string;
  time: string;
  actor: string;
  action: string;
  target: string;
};

type AuditLogTableProps = {
  rows: AuditRow[];
  copy: TableCopy;
  loading: boolean;
  emptyMessage?: string;
};

export function AuditLogTable({ rows, copy, loading, emptyMessage = "No audit events found." }: AuditLogTableProps) {
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  const handlePageSizeChange = useCallback((e: React.ChangeEvent<HTMLSelectElement>) => {
    setPageSize(Number(e.target.value));
    setPage(0);
  }, []);

  const totalRows = rows.length;
  const totalPages = Math.max(1, Math.ceil(totalRows / pageSize));
  const currentPage = Math.min(page, Math.max(0, totalPages - 1));
  const paginatedRows = useMemo(() => {
    const start = currentPage * pageSize;
    return rows.slice(start, start + pageSize);
  }, [rows, currentPage, pageSize]);

  return (
    <Card>
      <CardHeader>
        <CardTitle>{copy.title}</CardTitle>
      </CardHeader>
      <CardContent>
        {loading ? (
          <div className="text-slate-500">{copy.loading}</div>
        ) : totalRows === 0 ? (
          <div className="text-slate-500 text-sm">{emptyMessage}</div>
        ) : (
          <>
            <div className="flex items-center justify-between text-sm text-slate-600 mb-3">
              <span>
                {totalRows} {totalRows === 1 ? "event" : "events"}
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
                  <Th>{copy.time}</Th>
                  <Th>{copy.actor}</Th>
                  <Th>{copy.action}</Th>
                  <Th>{copy.target}</Th>
                </tr>
              </thead>
              <tbody>
                {paginatedRows.map((a) => (
                  <tr key={a.id}>
                    <Td className="text-slate-600 whitespace-nowrap">{formatDateTime(a.createdAt)}</Td>
                    <Td className="font-semibold">{a.actorEmail}</Td>
                    <Td>{a.action}</Td>
                    <Td className="text-slate-600">{a.target}</Td>
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
                  <button
                    type="button"
                    className="rounded-xl border border-slate-200 px-3 py-1.5 text-xs font-medium text-slate-700 disabled:opacity-50 disabled:cursor-not-allowed"
                    disabled={currentPage === 0}
                    onClick={() => setPage((p) => Math.max(0, p - 1))}
                  >
                    Previous
                  </button>
                  <button
                    type="button"
                    className="rounded-xl border border-slate-200 px-3 py-1.5 text-xs font-medium text-slate-700 disabled:opacity-50 disabled:cursor-not-allowed"
                    disabled={currentPage >= totalPages - 1}
                    onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                  >
                    Next
                  </button>
                </div>
              </div>
            )}
          </>
        )}
      </CardContent>
    </Card>
  );
}
