import React, { useMemo, useState, useCallback } from "react";
import { RequirePermission } from "../../router/guards";
import { useSystemAudit } from "../../data/hooks";
import { useTranslations } from "../../state/i18n";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Table, Td, Th } from "../../ui/components/Table";
import { formatDateTime } from "../../lib/format";

const TEXTS = {
  en: {
    hero: {
      title: "System audit",
      subtitle: "Platform and system-level events: orgs, shifts, workers and automated actions."
    },
    table: {
      title: "System events",
      loading: "Loading…",
      time: "Time",
      className: "Class",
      methodName: "Method",
      url: "URL",
      status: "Status"
    }
  },
  de: {
    hero: {
      title: "System-Audit",
      subtitle: "Plattform- und Systemereignisse: Organisationen, Schichten, Mitarbeiter und automatisierte Aktionen."
    },
    table: {
      title: "Systemereignisse",
      loading: "Lädt…",
      time: "Zeit",
      className: "Klasse",
      methodName: "Methode",
      url: "URL",
      status: "Status"
    }
  },
  tr: {
    hero: {
      title: "Sistem denetimi",
      subtitle: "Platform ve sistem düzeyinde olaylar: organizasyonlar, vardiyalar, çalışanlar ve otomatik işlemler."
    },
    table: {
      title: "Sistem olayları",
      loading: "Yükleniyor…",
      time: "Zaman",
      className: "Sınıf",
      methodName: "Metot",
      url: "URL",
      status: "Durum"
    }
  }
} as const;

export function SystemAuditPage() {
  return (
    <RequirePermission anyOf={["eecbbe51-6997-4b24-bdac-94a39928a9f9"]}>
      <SystemAuditInner />
    </RequirePermission>
  );
}

function SystemAuditInner() {
  const [page, setPage] = useState(0); // 0-based
  const [pageSize, setPageSize] = useState(10);

  const q = useSystemAudit(page, pageSize);
  const copy = useTranslations(TEXTS);
  const pageData = q.data as
    | {
        items: {
          id: string;
          version: number;
          createdAt: string;
          updatedAt: string | null;
          createdBy: string | null;
          updatedBy: string | null;
          createdIp: string | null;
          updatedIp: string | null;
          deleted: boolean;
          correlationId: string;
          className: string;
          methodName: string;
          remoteAddr: string;
          remoteHost: string;
          url: string;
          responseStatus: string;
          headers: string | null;
          request: string | null;
          response: string | null;
        }[];
        totalElements: number;
        totalPages: number;
        page: number;
        size: number;
      }
    | undefined;

  const rows = pageData?.items ?? [];

  const handlePageSizeChange = useCallback((e: React.ChangeEvent<HTMLSelectElement>) => {
    setPageSize(Number(e.target.value));
    setPage(0);
  }, []);

  const totalRows = pageData?.totalElements ?? rows.length;
  const totalPages = pageData?.totalPages ?? 1;
  const currentPage = pageData?.page ?? page;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">{copy.hero.title}</h1>
        <p className="text-slate-500 text-sm">{copy.hero.subtitle}</p>
      </div>
      <Card>
        <CardHeader>
          <CardTitle>{copy.table.title}</CardTitle>
        </CardHeader>
        <CardContent>
          {q.isLoading ? (
            <div className="text-slate-500">{copy.table.loading}</div>
          ) : totalRows === 0 ? (
            <div className="text-slate-500 text-sm">No system audit events found.</div>
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

              <div className="overflow-x-auto overflow-y-auto">
                <Table className="w-auto min-w-[1600px]">
                  <thead>
                    <tr>
                      <Th>{copy.table.time}</Th>
                      <Th>Id</Th>
                      <Th>Version</Th>
                      <Th>Created by</Th>
                      <Th>Updated by</Th>
                      <Th>Created IP</Th>
                      <Th>Updated IP</Th>
                      <Th>Deleted</Th>
                      <Th>Correlation Id</Th>
                      <Th>{copy.table.className}</Th>
                      <Th>{copy.table.methodName}</Th>
                      <Th>Remote addr</Th>
                      <Th>Remote host</Th>
                      <Th>{copy.table.url}</Th>
                      <Th>{copy.table.status}</Th>
                      <Th>Headers</Th>
                      <Th>Request</Th>
                      <Th>Response</Th>
                    </tr>
                  </thead>
                  <tbody>
                    {rows.map((a) => (
                      <tr key={a.id}>
                        <Td className="text-slate-600 whitespace-nowrap">{formatDateTime(a.createdAt)}</Td>
                        <Td className="text-xs text-slate-700">{a.id}</Td>
                        <Td className="text-xs text-slate-700 text-right">{a.version}</Td>
                        <Td className="text-xs text-slate-700">{a.createdBy ?? "-"}</Td>
                        <Td className="text-xs text-slate-700">{a.updatedBy ?? "-"}</Td>
                        <Td className="text-xs text-slate-700">{a.createdIp ?? "-"}</Td>
                        <Td className="text-xs text-slate-700">{a.updatedIp ?? "-"}</Td>
                        <Td className="text-xs text-slate-700">{a.deleted ? "true" : "false"}</Td>
                        <Td className="text-xs text-slate-700">{a.correlationId}</Td>
                        <Td className="font-mono text-xs text-slate-800">{a.className}</Td>
                        <Td className="font-mono text-xs text-slate-800">{a.methodName}</Td>
                        <Td className="text-xs text-slate-700">{a.remoteAddr}</Td>
                        <Td className="text-xs text-slate-700">{a.remoteHost}</Td>
                        <Td className="text-slate-700">{a.url}</Td>
                        <Td className="text-slate-700">{a.responseStatus || "-"}</Td>
                        <Td className="text-[11px] text-slate-700 max-w-xs truncate" title={a.headers ?? ""}>
                          {a.headers ?? "-"}
                        </Td>
                        <Td className="text-[11px] text-slate-700 max-w-xs truncate" title={a.request ?? ""}>
                          {a.request ?? "-"}
                        </Td>
                        <Td className="text-[11px] text-slate-700 max-w-xs truncate" title={a.response ?? ""}>
                          {a.response ?? "-"}
                        </Td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </div>

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
