import React, { useMemo, useState, useCallback, useEffect } from "react";
import { useJobs } from "../../data/hooks";
import type { Job } from "../../types/jobs";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import { cn } from "../../lib/cn";
import { useNavigate } from "react-router-dom";
import { useTranslations } from "../../state/i18n";
import { TEXTS } from "./i18n";

const PAGE_SIZE_OPTIONS = [10, 25, 50, 100];

export function JobsExplorePage() {
  const { data } = useJobs();
  const jobs = (data as Job[] | undefined) ?? [];
  const [selectedId, setSelectedId] = useState<string | null>(jobs[0]?.id ?? null);
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const navigate = useNavigate();
  const copy = useTranslations(TEXTS);

  const handlePageSizeChange = useCallback((e: React.ChangeEvent<HTMLSelectElement>) => {
    setPageSize(Number(e.target.value));
    setPage(0);
  }, []);

  const filtered = useMemo(() => {
    const term = search.trim().toLowerCase();
    if (!term) return jobs;
    return jobs.filter((j) =>
      j.title.toLowerCase().includes(term) ||
      j.company.toLowerCase().includes(term) ||
      (j.location ?? "").toLowerCase().includes(term)
    );
  }, [jobs, search]);

  useEffect(() => {
    setPage(0);
  }, [search]);

  const totalRows = filtered.length;
  const totalPages = Math.max(1, Math.ceil(totalRows / pageSize));
  const currentPage = Math.min(page, Math.max(0, totalPages - 1));
  const pageItems = useMemo(
    () => filtered.slice(currentPage * pageSize, currentPage * pageSize + pageSize),
    [filtered, currentPage, pageSize]
  );

  const selected = useMemo(
    () => filtered.find((j) => j.id === selectedId) ?? filtered[0],
    [filtered, selectedId]
  );

  return (
    <div className="grid gap-4 lg:grid-cols-[360px_minmax(0,1fr)] h-[calc(100vh-140px)]">
      <div className="flex flex-col rounded-2xl border border-slate-200 bg-white overflow-hidden">
        <div className="px-4 py-3 border-b border-slate-100 flex items-center justify-between">
          <div>
            <div className="text-xs text-slate-500 uppercase tracking-wide">{copy.list.title}</div>
            <div className="text-sm font-semibold text-slate-900">
              {copy.list.openCount.replace("{count}", jobs.length.toString())}
            </div>
          </div>
          <Button onClick={() => navigate("/app/jobs/new")}>{copy.list.create}</Button>
        </div>

        <div className="px-4 py-3 border-b border-slate-100 space-y-2">
          <Input
            placeholder={copy.list.searchPlaceholder}
            className="h-8 text-xs"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <div className="flex items-center justify-between text-[11px] text-slate-500">
            <span>
              {totalRows} {totalRows === 1 ? "job" : "jobs"}
              {totalRows > 0 && (
                <> (showing {currentPage * pageSize + 1}–{Math.min((currentPage + 1) * pageSize, totalRows)})</>
              )}
            </span>
            <label className="flex items-center gap-1">
              <span>Per page</span>
              <select
                value={pageSize}
                onChange={handlePageSizeChange}
                className="rounded border border-slate-200 px-1.5 py-0.5 text-[11px] bg-white"
              >
                {PAGE_SIZE_OPTIONS.map((n) => (
                  <option key={n} value={n}>{n}</option>
                ))}
              </select>
            </label>
          </div>
        </div>

        <div className="flex-1 overflow-auto">
          {pageItems.map((job) => (
            <button
              key={job.id}
              type="button"
              onClick={() => setSelectedId(job.id)}
              className={cn(
                "w-full px-4 py-3 flex gap-3 text-left hover:bg-slate-50 border-b border-slate-100",
                selected?.id === job.id && "bg-slate-50"
              )}
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-lg border border-slate-200 bg-slate-50 text-[11px] font-semibold">
                {job.logoText ?? job.company[0]}
              </div>
              <div className="flex-1 min-w-0">
                <div className="text-[11px] text-slate-500 mb-0.5">
                  {(job.type === "temporary" ? copy.list.temporary : copy.list.permanent)} · {(job.schedule === "full-time" ? copy.list.fullTime : copy.list.partTime)} ({job.workloadLabel})
                </div>
                <div className="text-sm font-semibold text-slate-900 truncate">
                  {job.title}
                </div>
                <div className="text-[11px] text-slate-500 truncate">
                  {job.company}
                </div>
                <div className="text-[11px] text-slate-500 truncate mt-0.5">
                  {job.distanceLabel} · {job.address}
                </div>
              </div>
            </button>
          ))}
        </div>
        {totalPages > 1 && (
          <div className="px-4 py-2 border-t border-slate-100 flex items-center justify-between text-[11px] text-slate-500">
            <div>
              Page <span className="font-semibold">{currentPage + 1}</span> of{" "}
              <span className="font-semibold">{totalPages}</span>
            </div>
            <div className="flex items-center gap-2">
              <Button
                variant="secondary"
                disabled={currentPage === 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
              >
                {copy.list.previous}
              </Button>
              <Button
                variant="secondary"
                disabled={currentPage >= totalPages - 1}
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
              >
                {copy.list.next}
              </Button>
            </div>
          </div>
        )}
      </div>

      <div className="flex flex-col rounded-2xl border border-slate-200 bg-white overflow-hidden">
        <div className="px-4 py-3 border-b border-slate-100 flex items-center justify-between">
          <div className="flex flex-col">
            <div className="text-xs text-slate-500 uppercase tracking-wide">
              {copy.detail.subtitle}
            </div>
            {selected && (
              <div className="text-sm font-semibold text-slate-900">
                {selected.title}
              </div>
            )}
          </div>
          <Button>{copy.detail.start}</Button>
        </div>

        {selected ? (
          <div className="flex-1 overflow-auto p-4 space-y-4 text-sm">
            <div className="space-y-1 text-sm">
              <div className="font-semibold text-slate-900">{selected.company}</div>
              <div className="text-xs text-slate-500">
                {selected.address}
              </div>
              <div className="text-xs text-slate-500">
                {selected.distanceLabel}
              </div>
            </div>

            <div className="flex flex-wrap gap-2 text-xs">
              {selected.salaryLabel && (
                <span className="rounded-full border border-slate-200 px-2 py-1">
                  {selected.salaryLabel}
                </span>
              )}
              {selected.startDateLabel && (
                <span className="rounded-full border border-slate-200 px-2 py-1">
                  {selected.startDateLabel}
                </span>
              )}
                <span className="rounded-full border border-slate-200 px-2 py-1">
                  {selected.schedule === "full-time" ? copy.list.fullTime : copy.list.partTime}
                </span>
                <span className="rounded-full border border-slate-200 px-2 py-1">
                  {selected.contractLabel ?? copy.detail.temporaryContract}
                </span>
            </div>

            <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">
                  {copy.detail.experience}
                </div>
              <div className="text-sm text-slate-800">{selected.experienceLabel ?? "0 years"}</div>
            </div>

            <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">
                  {copy.detail.description}
                </div>
              <div className="text-sm text-slate-700 leading-relaxed whitespace-pre-line">
                {selected.description}
              </div>
            </div>

            {selected.venueMapLabel && (
              <div>
                <div className="text-xs font-semibold text-slate-500 mb-1">{copy.detail.venue}</div>
                <div className="rounded-2xl border border-slate-200 bg-slate-50 px-3 py-2 text-xs text-slate-600">
                  {selected.venueMapLabel}
                </div>
              </div>
            )}
          </div>
        ) : (
          <div className="flex-1 flex items-center justify-center text-xs text-slate-500">
            {copy.detail.empty}
          </div>
        )}
      </div>
    </div>
  );
}
