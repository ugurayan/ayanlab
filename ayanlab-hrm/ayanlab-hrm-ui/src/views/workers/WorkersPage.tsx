import React, { useCallback, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Table, Td, Th } from "../../ui/components/Table";
import { Badge } from "../../ui/components/Badge";
import { Button } from "../../ui/components/Button";
import { ColumnsPanel } from "../../ui/components/ColumnsPanel";
import { useWorkers } from "../../data/hooks";
import { RequirePermission } from "../../router/guards";
import { useTranslations } from "../../state/i18n";
import { TEXTS } from "./i18n";
import type { Worker } from "../../mock/mockDb";
import { Columns2 } from "lucide-react";
import {
  WORKER_COLUMN_IDS,
  WORKER_COLUMN_LABELS,
  getVisibleColumnsInOrder,
  loadWorkerColumnConfig,
  saveWorkerColumnConfig,
  type WorkerColumnConfig,
  type WorkerColumnId,
} from "./columnConfig";
import { WorkerDetail } from "./WorkerDetail";
import { P } from "../../security/permissions";

export function WorkersPage() {
  return (
    <RequirePermission anyOf={[P.workersRead]}>
      <WorkersInner />
    </RequirePermission>
  );
}

function WorkersInner() {
  const q = useWorkers();
  const copy = useTranslations(TEXTS);
  const navigate = useNavigate();
  const [columnConfig, setColumnConfig] = useState<WorkerColumnConfig>(() => loadWorkerColumnConfig());
  const [columnsPanelOpen, setColumnsPanelOpen] = useState(false);
  const [openMenuId, setOpenMenuId] = useState<string | null>(null);
  const [selectedWorker, setSelectedWorker] = useState<Worker | null>(null);
  const [detailTab, setDetailTab] = useState<"overview" | "incidents" | "punctuality" | "earnings">("overview");
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  const handlePageSizeChange = useCallback((e: React.ChangeEvent<HTMLSelectElement>) => {
    setPageSize(Number(e.target.value));
    setPage(0);
  }, []);

  const visibleColumns = useMemo(() => getVisibleColumnsInOrder(columnConfig), [columnConfig]);
  const handleColumnConfigSave = useCallback((config: WorkerColumnConfig) => {
    saveWorkerColumnConfig(config);
    setColumnConfig(config);
  }, []);

  const workers: Worker[] = (q.data ?? []) as Worker[];
  const totalRows = workers.length;
  const totalPages = Math.max(1, Math.ceil(totalRows / pageSize));
  const currentPage = Math.min(page, Math.max(0, totalPages - 1));
  const paginatedWorkers = useMemo(() => {
    const start = currentPage * pageSize;
    return workers.slice(start, start + pageSize);
  }, [workers, currentPage, pageSize]);

  function toPercent(n: number) {
    return `${Math.round((n ?? 0) * 100)}%`;
  }

  function money(cents: number, currency: string) {
    const value = (cents ?? 0) / 100;
    return new Intl.NumberFormat(undefined, { style: "currency", currency }).format(value);
  }

  function incidentTone(sev: string) {
    if (sev === "critical") return "red";
    if (sev === "high") return "amber";
    return "blue";
  }

  function openDetail(w: Worker, tab: typeof detailTab) {
    setSelectedWorker(w);
    setDetailTab(tab);
    setOpenMenuId(null);
  }

  function renderCell(w: Worker, columnId: WorkerColumnId) {
    switch (columnId) {
      case "name": {
        const incidents = (w.lastIncidents ?? []).slice(0, 5);
        const currency = w.earnings?.currency ?? "CHF";
        return (
          <Td className="font-semibold">
            <div className="relative group inline-flex items-center gap-2">
              <span className="whitespace-nowrap">{w.displayName}</span>
              {w.criticalIncidents > 0 && (
                <Badge tone="red">{w.criticalIncidents} critical</Badge>
              )}

              {/* Hover details */}
              <div className="hidden group-hover:block absolute left-0 top-full mt-2 w-[420px] z-20">
                <div className="rounded-2xl border border-slate-200 bg-white shadow-lg p-4">
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <div className="text-sm font-semibold text-slate-900">{w.displayName}</div>
                      <div className="text-xs text-slate-500">
                        {w.jobProfiles?.length ? w.jobProfiles.join(", ") : "No job profiles"}
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge tone="blue">{w.rating.toFixed(1)} / 5</Badge>
                      <Badge tone={w.reliability >= 0.9 ? "green" : w.reliability >= 0.8 ? "amber" : "red"}>
                        {toPercent(w.reliability)}
                      </Badge>
                    </div>
                  </div>

                  <div className="mt-3 grid grid-cols-2 gap-2 text-xs text-slate-700">
                    <div>
                      <span className="text-slate-500">Acceptance</span>{" "}
                      <span className="font-semibold">{toPercent(w.acceptanceRate)}</span>
                    </div>
                    <div>
                      <span className="text-slate-500">Avg rate</span>{" "}
                      <span className="font-semibold">{money(w.avgHourlyRateCents, currency)}/h</span>
                    </div>
                    <div>
                      <span className="text-slate-500">Jobs completed</span>{" "}
                      <span className="font-semibold">{w.jobsCompleted}</span>
                    </div>
                    <div>
                      <span className="text-slate-500">No-shows</span>{" "}
                      <span className="font-semibold">{w.noShows}</span>
                    </div>
                    <div>
                      <span className="text-slate-500">Late starts</span>{" "}
                      <span className="font-semibold">{w.lateStarts}</span>
                    </div>
                    <div>
                      <span className="text-slate-500">On-time starts</span>{" "}
                      <span className="font-semibold">{w.onTimeStarts}</span>
                    </div>
                    <div className="col-span-2">
                      <span className="text-slate-500">Earnings</span>{" "}
                      <span className="font-semibold">{money(w.earnings?.lastMonthCents ?? 0, currency)}</span>{" "}
                      <span className="text-slate-500">(last month)</span>
                    </div>
                  </div>

                  <div className="mt-3">
                    <div className="text-xs font-semibold text-slate-700 mb-2">Incidents (last 5)</div>
                    <div className="space-y-2">
                      {incidents.length === 0 ? (
                        <div className="text-xs text-slate-500">No incidents recorded.</div>
                      ) : (
                        incidents.map((inc) => (
                          <div key={inc.id} className="flex items-start gap-2 text-xs">
                            <Badge tone={incidentTone(inc.severity)}>{inc.type.replace("_", " ")}</Badge>
                            <div className="flex-1 text-slate-700">
                              <div>{inc.summary}</div>
                              <div className="text-[11px] text-slate-400">{new Date(inc.occurredAt).toLocaleString()}</div>
                            </div>
                          </div>
                        ))
                      )}
                    </div>
                  </div>

                  <div className="mt-3 flex items-center justify-end gap-2">
                    <Button
                      type="button"
                      variant="secondary"
                      onClick={() => openDetail(w, "incidents")}
                    >
                      View incidents
                    </Button>
                    <Button type="button" onClick={() => openDetail(w, "overview")}>
                      Edit worker
                    </Button>
                  </div>
                </div>
              </div>
            </div>
          </Td>
        );
      }
      case "jobProfiles":
        return <Td className="text-slate-600">{w.jobProfiles?.join(", ") || "-"}</Td>;
      case "skills":
        return <Td className="text-slate-600">{w.skills?.join(", ") || "-"}</Td>;
      case "rating":
        return (
          <Td>
            <Badge tone="blue">{w.rating.toFixed(1)}</Badge>
          </Td>
        );
      case "ratingCount":
        return <Td className="text-slate-600">{w.ratingCount ?? 0}</Td>;
      case "reliability":
        return (
          <Td>
            <Badge tone={w.reliability >= 0.9 ? "green" : w.reliability >= 0.8 ? "amber" : "red"}>
              {toPercent(w.reliability)}
            </Badge>
          </Td>
        );
      case "acceptanceRate":
        return (
          <Td>
            <Badge tone={w.acceptanceRate >= 0.85 ? "green" : w.acceptanceRate >= 0.7 ? "amber" : "red"}>
              {toPercent(w.acceptanceRate)}
            </Badge>
          </Td>
        );
      case "onTimeStarts":
        return <Td className="text-slate-600">{w.onTimeStarts ?? 0}</Td>;
      case "lateStarts":
        return <Td className="text-slate-600">{w.lateStarts ?? 0}</Td>;
      case "jobsCompleted":
        return <Td className="text-slate-600">{w.jobsCompleted ?? 0}</Td>;
      case "jobsNotCompleted":
        return <Td className="text-slate-600">{w.jobsNotCompleted ?? 0}</Td>;
      case "noShows":
        return <Td className="text-slate-600">{w.noShows ?? 0}</Td>;
      case "criticalIncidents":
        return (
          <Td>
            <Badge tone={w.criticalIncidents > 0 ? "red" : "green"}>{w.criticalIncidents ?? 0}</Badge>
          </Td>
        );
      case "reliabilityIncidents":
        return (
          <Td>
            <Badge tone={w.reliabilityIncidents > 0 ? "amber" : "green"}>{w.reliabilityIncidents ?? 0}</Badge>
          </Td>
        );
      case "avgHourlyRate": {
        const currency = w.earnings?.currency ?? "CHF";
        return <Td className="text-slate-600">{money(w.avgHourlyRateCents ?? 0, currency)}/h</Td>;
      }
      case "earningsLastMonth": {
        const currency = w.earnings?.currency ?? "CHF";
        return <Td className="text-slate-600">{money(w.earnings?.lastMonthCents ?? 0, currency)}</Td>;
      }
      case "earningsLast3Months": {
        const currency = w.earnings?.currency ?? "CHF";
        return <Td className="text-slate-600">{money(w.earnings?.last3MonthsCents ?? 0, currency)}</Td>;
      }
      case "earningsLast6Months": {
        const currency = w.earnings?.currency ?? "CHF";
        return <Td className="text-slate-600">{money(w.earnings?.last6MonthsCents ?? 0, currency)}</Td>;
      }
      case "earningsLast12Months": {
        const currency = w.earnings?.currency ?? "CHF";
        return <Td className="text-slate-600">{money(w.earnings?.last12MonthsCents ?? 0, currency)}</Td>;
      }
      case "notes":
        return <Td className="text-slate-600">{w.notes || "-"}</Td>;
      default:
        return <Td>-</Td>;
    }
  }

  const thClass = (id: WorkerColumnId) => (id === "name" ? "w-[240px]" : undefined);

  return (
    <div className="h-full flex flex-col relative">
      <div className="flex-shrink-0 p-6 pb-4">
        <div className="flex justify-between items-center mb-2">
          <div>
            <h1 className="text-2xl font-bold">{copy.hero.title}</h1>
            <div className="text-slate-500">{copy.hero.subtitle}</div>
          </div>
          <div className="flex items-center gap-2">
            <Button
              variant="secondary"
              onClick={() => setColumnsPanelOpen(true)}
              className="inline-flex items-center gap-2"
            >
              <Columns2 size={18} />
              Columns
            </Button>
          </div>
        </div>
      </div>

      <div className="flex-1 min-h-0 px-6 pb-6">
        <Card className="h-full flex flex-col overflow-hidden">
          <CardHeader className="flex-shrink-0">
            <CardTitle>{copy.table.title}</CardTitle>
          </CardHeader>
          <CardContent className="flex-1 min-h-0">
            {q.isLoading ? (
              <div className="text-slate-500">{copy.table.loading}</div>
            ) : (
              <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden h-full flex flex-col">
                {totalRows > 0 && (
                  <div className="flex items-center justify-between text-sm text-slate-600 px-4 py-2 border-b border-slate-100 flex-shrink-0">
                    <span>
                      {totalRows} {totalRows === 1 ? "worker" : "workers"}
                      (showing {currentPage * pageSize + 1}–{Math.min((currentPage + 1) * pageSize, totalRows)})
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
                )}
                <div className="overflow-auto flex-1 min-h-0">
                  <Table className="min-w-max">
                    <thead className="bg-slate-50 sticky top-0 z-10">
                      <tr>
                        {visibleColumns.map((id) => (
                          <Th key={id} className={thClass(id)}>
                            {WORKER_COLUMN_LABELS[id]}
                          </Th>
                        ))}
                        <Th className="text-right w-[90px]">Actions</Th>
                      </tr>
                    </thead>
                    <tbody>
                      {paginatedWorkers.map((w) => (
                        <tr key={w.id} className="hover:bg-slate-50">
                          {visibleColumns.map((id) => (
                            <React.Fragment key={id}>{renderCell(w, id)}</React.Fragment>
                          ))}
                          <Td className="text-right relative">
                            <button
                              type="button"
                              className="inline-flex h-8 w-8 items-center justify-center rounded-full border border-slate-200 text-slate-600 hover:bg-slate-100"
                              onClick={() => setOpenMenuId((cur) => (cur === w.id ? null : w.id))}
                            >
                              …
                            </button>
                            {openMenuId === w.id && (
                              <div className="absolute right-0 mt-2 w-52 rounded-xl border border-slate-200 bg-white shadow-md z-20 overflow-hidden">
                                <button
                                  type="button"
                                  className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50"
                                  onClick={() => openDetail(w, "overview")}
                                >
                                  Edit details
                                </button>
                                <button
                                  type="button"
                                  className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50"
                                  onClick={() => openDetail(w, "incidents")}
                                >
                                  Go to incidents
                                </button>
                                <button
                                  type="button"
                                  className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50"
                                  onClick={() => openDetail(w, "punctuality")}
                                >
                                  Go to late/on-time actions
                                </button>
                                <button
                                  type="button"
                                  className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50"
                                  onClick={() => openDetail(w, "earnings")}
                                >
                                  Go to earnings
                                </button>
                                <button
                                  type="button"
                                  className="w-full px-3 py-2 text-left text-sm hover:bg-slate-50"
                                  onClick={() => {
                                    setOpenMenuId(null);
                                    navigate(`/app/profile?workerId=${encodeURIComponent(w.id)}`);
                                  }}
                                >
                                  Go to profile
                                </button>
                              </div>
                            )}
                          </Td>
                        </tr>
                      ))}

                      {paginatedWorkers.length === 0 && (
                        <tr>
                          <Td colSpan={visibleColumns.length + 1} className="text-center text-slate-500 py-6">
                            No workers found.
                          </Td>
                        </tr>
                      )}
                    </tbody>
                  </Table>
                </div>
                {totalRows > 0 && totalPages > 1 && (
                  <div className="flex items-center justify-between text-sm text-slate-600 px-4 py-3 border-t border-slate-100 flex-shrink-0">
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
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      <ColumnsPanel
        isOpen={columnsPanelOpen}
        onClose={() => setColumnsPanelOpen(false)}
        columnIds={WORKER_COLUMN_IDS}
        labels={WORKER_COLUMN_LABELS}
        currentConfig={columnConfig}
        onSave={handleColumnConfigSave}
        title="Worker table columns"
      />

      {selectedWorker && (
        <WorkerDetail
          worker={selectedWorker}
          isOpen={!!selectedWorker}
          onClose={() => setSelectedWorker(null)}
          initialTab={detailTab}
        />
      )}
    </div>
  );
}
