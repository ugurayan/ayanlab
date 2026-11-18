import React, { useMemo, useState, useCallback } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Table, Td, Th } from "../../ui/components/Table";
import { useAudit } from "../../data/hooks";
import { RequirePermission } from "../../router/guards";
import { formatDateTime } from "../../lib/format";
import { useTranslations } from "../../state/i18n";
import { P } from "../../security/permissions";

export function AuditPage() {
  return (
    <RequirePermission anyOf={[P.auditRead]}>
      <AuditInner />
    </RequirePermission>
  );
}

function AuditInner() {
  const q = useAudit();
  const copy = useTranslations(TEXTS);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState<number>(10);

  const handlePageSizeChange = useCallback((e: React.ChangeEvent<HTMLSelectElement>) => {
    const next = Number(e.target.value);
    setPageSize(next);
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
          ) : totalRows === 0 ? (
            <div className="text-slate-500 text-sm">No audit events found.</div>
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
                    <Th>{copy.table.time}</Th>
                    <Th>{copy.table.actor}</Th>
                    <Th>{copy.table.action}</Th>
                    <Th>{copy.table.target}</Th>
                  </tr>
                </thead>
                <tbody>
                  {paginatedRows.map((a: any) => (
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
    </div>
  );
}

const TEXTS = {
  en: {
    hero: {
      title: "Audit log",
      subtitle: "Security-sensitive actions and platform operations."
    },
    table: {
      title: "Events",
      loading: "Loading…",
      time: "Time",
      actor: "Actor",
      action: "Action",
      target: "Target"
    }
  },
  de: {
    hero: {
      title: "Audit-Log",
      subtitle: "Sicherheitsrelevante Aktionen und Plattformvorgänge."
    },
    table: {
      title: "Ereignisse",
      loading: "Lädt…",
      time: "Zeit",
      actor: "Akteur",
      action: "Aktion",
      target: "Ziel"
    }
  },
  tr: {
    hero: {
      title: "Denetim kaydı",
      subtitle: "Güvenlik hassas eylemler ve platform işlemleri."
    },
    table: {
      title: "Olaylar",
      loading: "Yükleniyor…",
      time: "Zaman",
      actor: "Kullanıcı",
      action: "İşlem",
      target: "Hedef"
    }
  }
} as const;
