import React from "react";
import { Card, CardContent } from "../../ui/components/Card";
import { Button } from "../../ui/components/Button";
import { useDocuments, useWorkers } from "../../data/hooks";
import { RequirePermission } from "../../router/guards";
import { formatDate } from "../../lib/format";
import { useTranslations } from "../../state/i18n";
import { TEXTS } from "./i18n";
import { useNavigate } from "react-router-dom";

export function DocumentsPage() {
  return (
    <RequirePermission anyOf={["d8694403-dd7a-4aa4-9571-d84986e0c2f2"]}>
      <DocumentsInner />
    </RequirePermission>
  );
}

function DocumentsInner() {
  const navigate = useNavigate();
  const q = useDocuments();
  const workersQuery = useWorkers();
  const copy = useTranslations(TEXTS);
  const folders = [
    { id: "personal", name: copy.folders.personal, count: 57 },
    { id: "photos", name: copy.folders.photos, count: 907 },
    { id: "work", name: copy.folders.work, count: 24 }
  ];

  const files = (q.data ?? []) as any[];
  const workers = (workersQuery.data ?? []) as any[];
  const folderCount = folders.length;
  const fileCount = files.length;

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <div className="text-2xl font-bold">{copy.hero.title}</div>
          <div className="text-slate-500 text-sm">
            {copy.hero.summary
              .replace("{folders}", folderCount.toString())
              .replace("{files}", fileCount.toString())}
          </div>
        </div>
        <Button type="button" onClick={() => navigate("/app/documents/upload")}>
          {copy.hero.upload}
        </Button>
      </div>

      <Card>
        <CardContent className="p-5 space-y-6">
          {q.isLoading ? (
            <div className="text-slate-500 text-sm">{copy.hero.loading}</div>
          ) : (
            <>
              <div>
                <div className="text-xs font-semibold text-slate-500 mb-2">{copy.folders.title}</div>
                <div className="flex flex-wrap gap-3">
                  {folders.map((f) => (
                    <button
                      key={f.id}
                      type="button"
                      className="w-32 h-24 rounded-2xl border border-slate-200 bg-slate-50 px-3 py-2 text-left flex flex-col justify-between hover:bg-slate-100"
                    >
                      <div className="flex flex-col gap-1">
                        <div className="h-8 w-9 rounded-md bg-slate-200" />
                        <div className="text-xs font-semibold text-slate-800 truncate">{f.name}</div>
                      </div>
                      <div className="text-[11px] text-slate-500">
                        {copy.folders.count.replace("{count}", f.count.toString())}
                      </div>
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <div className="text-xs font-semibold text-slate-500 mb-2">{copy.files.title}</div>
                <div className="flex flex-wrap gap-3">
                  {files.map((file) => {
                    const worker = workers.find((w) => w.id === file.workerId);
                    const workerName = worker?.displayName ?? file.workerId;
                    const statusClass =
                      file.status === "APPROVED"
                        ? "border-emerald-500 text-emerald-500"
                        : file.status === "REJECTED"
                        ? "border-rose-500 text-rose-500"
                        : "border-sky-500 text-sky-500";
                    const statusSymbol =
                      file.status === "APPROVED" ? "✓" : file.status === "REJECTED" ? "✕" : "…";

                    return (
                    <div
                      key={file.id}
                      className="w-36 h-24 rounded-2xl border border-slate-200 bg-white px-3 py-2 flex flex-col justify-between text-left hover:bg-slate-50"
                      title={copy.files.tooltip
                        .replace("{type}", file.type ?? copy.defaults.document)
                        .replace("{worker}", workerName)
                        .replace("{status}", file.status ?? "")
                        .replace("{created}", file.createdAt ? formatDate(file.createdAt) : copy.defaults.unknown)
                        .replace("{expires}", file.expiresAt ? formatDate(file.expiresAt) : copy.defaults.none)
                      }
                    >
                      <div className="flex items-start gap-2">
                        <div className="h-8 w-6 rounded-sm bg-slate-100 border border-slate-200" />
                        <div className="text-xs font-semibold text-slate-800 truncate">
                          {file.type ?? copy.defaults.document}
                        </div>
                      </div>
                      <div className="text-[11px] text-slate-500 truncate">{workerName}</div>
                      <div className="flex items-center justify-between text-[11px] mt-0.5">
                        <span
                          className={`inline-flex h-5 w-5 items-center justify-center rounded-full border text-[11px] font-semibold ${statusClass}`}
                        >
                          {statusSymbol}
                        </span>
                        {file.createdAt && (
                          <span className="text-slate-400 truncate ml-1">
                            {formatDate(file.createdAt)}
                          </span>
                        )}
                      </div>
                    </div>
                  );
                  })}
                  {files.length === 0 && (
                    <div className="text-xs text-slate-500">{copy.files.empty}</div>
                  )}
                </div>
              </div>
            </>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
