import React, { useEffect, useMemo, useState } from "react";
import { useTasks, useWorkers } from "../../data/hooks";
import type { Task } from "../../types/tasks";
import { Card, CardContent } from "../../ui/components/Card";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import { Badge } from "../../ui/components/Badge";
import { cn } from "../../lib/cn";
import { useTranslations } from "../../state/i18n";
import { TEXTS } from "./i18n";

export function TasksPage() {
  const { data: tasksData } = useTasks();
  const { data: workers } = useWorkers();
  const copy = useTranslations(TEXTS);

  const [search, setSearch] = useState("");
  const [tasks, setTasks] = useState<Task[]>([]);
  const [selectedId, setSelectedId] = useState<string | null>(null);

  const [assigneeQuery, setAssigneeQuery] = useState("");
  const [debouncedQuery, setDebouncedQuery] = useState("");

  useEffect(() => {
    setTasks((tasksData as Task[] | undefined) ?? []);
  }, [tasksData]);

  useEffect(() => {
    if (assigneeQuery.length < 3) {
      setDebouncedQuery("");
      return;
    }
    const handle = setTimeout(() => setDebouncedQuery(assigneeQuery), 500);
    return () => clearTimeout(handle);
  }, [assigneeQuery]);

  useEffect(() => {
    setPage(0);
  }, [search]);

  const filteredTasks = useMemo(() => {
    const term = search.trim().toLowerCase();
    let list = tasks;
    if (term) {
      list = list.filter((t) =>
        t.title.toLowerCase().includes(term) || (t.notes ?? "").toLowerCase().includes(term)
      );
    }
    return list;
  }, [tasks, search]);

  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const totalRows = filteredTasks.length;
  const totalPages = Math.max(1, Math.ceil(totalRows / pageSize));
  const currentPage = Math.min(page, Math.max(0, totalPages - 1));
  const paginatedTasks = useMemo(() => {
    const start = currentPage * pageSize;
    return filteredTasks.slice(start, start + pageSize);
  }, [filteredTasks, currentPage, pageSize]);

  const selectedTask = useMemo(
    () => filteredTasks.find((t) => t.id === selectedId) ?? filteredTasks[0],
    [filteredTasks, selectedId]
  );

  const remainingCount = tasks.filter((t) => t.status === "open").length;

  const assigneeResults = useMemo(() => {
    if (!debouncedQuery || !workers) return [];
    const term = debouncedQuery.toLowerCase();
    return (workers as any[]).filter(
      (w) =>
        w.displayName.toLowerCase().includes(term) ||
        w.id.toLowerCase().includes(term)
    );
  }, [debouncedQuery, workers]);

  function toggleComplete(task: Task) {
    setTasks((prev) =>
      prev.map((t) =>
        t.id === task.id
          ? {
              ...t,
              status: t.status === "open" ? "completed" : "open",
              completedAt:
                t.status === "open" ? new Date().toISOString() : undefined
            }
          : t
      )
    );
  }

  function renderAssigneeName(task?: Task) {
    if (!task || !workers) return copy.common.unassigned;
    const w = (workers as any[]).find((p) => p.id === task.assignedWorkerId);
    return w ? `${w.displayName} (${w.id})` : copy.common.unassigned;
  }

  return (
    <div className="grid gap-4 lg:grid-cols-[minmax(0,1.2fr)_minmax(0,1fr)] h-[calc(100vh-140px)]">
      <div className="flex flex-col rounded-2xl border border-slate-200 bg-white overflow-hidden">
        <div className="px-4 py-3 border-b border-slate-100 flex items-center justify-between">
          <div>
            <div className="text-xs text-slate-500 uppercase tracking-wide">{copy.list.title}</div>
            <div className="text-sm font-semibold text-slate-900">
              {copy.list.remainingLabel.replace("{count}", remainingCount.toString())}
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Input
              placeholder={copy.list.searchPlaceholder}
              className="h-8 w-40 text-xs"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
            <Button variant="secondary">{copy.list.addSection}</Button>
            <Button>{copy.list.addTask}</Button>
          </div>
        </div>

        <div className="flex-1 overflow-auto">
            <div className="px-4 py-3 text-xs font-semibold text-slate-500 border-b border-slate-100 flex items-center justify-between">
              <span>{copy.list.projectTitle}</span>
              <span className="text-[11px] font-normal text-slate-400">
                {totalRows} {totalRows === 1 ? "task" : "tasks"}
                {totalRows > 0 && (
                  <> (showing {currentPage * pageSize + 1}–{Math.min((currentPage + 1) * pageSize, totalRows)})</>
                )}
              </span>
            </div>

            <div className="px-4 py-2 border-b border-slate-100 flex items-center justify-end gap-2 text-[11px] text-slate-500">
              <label className="flex items-center gap-1">
                <span>Per page</span>
                <select
                  value={pageSize}
                  onChange={(e) => {
                    setPageSize(Number(e.target.value));
                    setPage(0);
                  }}
                  className="rounded border border-slate-200 px-1.5 py-0.5 text-[11px] bg-white"
                >
                  <option value={10}>10</option>
                  <option value={25}>25</option>
                  <option value={50}>50</option>
                  <option value={100}>100</option>
                </select>
              </label>
            </div>

          <div className="divide-y divide-slate-100">
            {paginatedTasks.map((task) => (
              <button
                key={task.id}
                type="button"
                onClick={() => setSelectedId(task.id)}
                className={cn(
                  "w-full px-4 py-3 flex items-center justify-between text-left hover:bg-slate-50",
                  selectedTask?.id === task.id && "bg-slate-50"
                )}
              >
                <div className="flex items-center gap-3">
                  <button
                    type="button"
                    onClick={(e) => {
                      e.stopPropagation();
                      toggleComplete(task);
                    }}
                    className={cn(
                      "h-5 w-5 rounded-full border flex items-center justify-center",
                      task.status === "completed"
                        ? "border-blue-500 bg-blue-500 text-white"
                        : "border-slate-300"
                    )}
                  >
                    {task.status === "completed" && <span className="text-[11px]">✓</span>}
                  </button>
                  <div>
                    <div
                      className={cn(
                        "text-sm text-slate-900",
                        task.status === "completed" && "line-through text-slate-400"
                      )}
                    >
                      {task.title}
                    </div>
                    <div className="text-xs text-slate-500">
                      {renderAssigneeName(task)}
                    </div>
                  </div>
                </div>

                <div className="flex items-center gap-2 text-xs text-slate-500">
                  {task.dueDate && (
                    <span>{new Date(task.dueDate).toLocaleDateString()}</span>
                  )}
                  <Badge>{task.priority.toUpperCase()}</Badge>
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
      </div>

      <div className="flex flex-col rounded-2xl border border-slate-200 bg-white overflow-hidden">
        <div className="px-4 py-3 border-b border-slate-100 flex items-center justify-between">
          <div className="text-xs font-semibold text-slate-500 uppercase tracking-wide">
            {selectedTask?.status === "completed" ? copy.detail.completed : copy.detail.markComplete}
          </div>
          {selectedTask && (
            <Button onClick={() => toggleComplete(selectedTask)}>
              {selectedTask.status === "completed" ? copy.detail.reopen : copy.detail.completeCta}
            </Button>
          )}
        </div>

        {selectedTask ? (
          <div className="flex-1 overflow-auto p-4 space-y-4 text-sm">
            <div>
              <div className="text-xs text-slate-500 mb-1">{copy.detail.taskTitle}</div>
              <Input value={selectedTask.title} readOnly />
            </div>

            <div>
              <div className="text-xs text-slate-500 mb-1">{copy.detail.dueDate}</div>
              <Input
                value={selectedTask.dueDate ? new Date(selectedTask.dueDate).toLocaleString() : copy.detail.notSet}
                readOnly
              />
            </div>

            <div>
              <div className="text-xs text-slate-500 mb-1">{copy.detail.priority}</div>
              <div>
                <Badge>{selectedTask.priority.toUpperCase()}</Badge>
              </div>
            </div>

            <div>
              <div className="text-xs text-slate-500 mb-1">{copy.detail.assigned}</div>
              <Input
                placeholder={copy.detail.assigneePlaceholder}
                value={assigneeQuery}
                onChange={(e) => setAssigneeQuery(e.target.value)}
              />
              {assigneeQuery.length > 0 && assigneeQuery.length < 3 && (
                <div className="mt-1 text-[11px] text-slate-500">
                  {copy.detail.searchHint}
                </div>
              )}
              {debouncedQuery && (
                <Card className="mt-2">
                  <CardContent className="p-2 space-y-1 text-sm">
                    {assigneeResults.length === 0 && (
                      <div className="text-xs text-slate-500">{copy.detail.noMatches}</div>
                    )}
                    {assigneeResults.map((w: any) => (
                      <button
                        key={w.id}
                        type="button"
                        className="w-full text-left px-2 py-1 rounded-lg hover:bg-slate-50 text-xs"
                        onClick={() => {
                          setTasks((prev) =>
                            prev.map((t) =>
                              t.id === selectedTask.id
                                ? { ...t, assignedWorkerId: w.id }
                                : t
                            )
                          );
                          setAssigneeQuery("");
                          setDebouncedQuery("");
                        }}
                      >
                        {w.displayName} ({w.id})
                      </button>
                    ))}
                  </CardContent>
                </Card>
              )}
              <div className="mt-1 text-xs text-slate-500">
                {copy.detail.current} {renderAssigneeName(selectedTask)}
              </div>
            </div>

            <div>
              <div className="text-xs text-slate-500 mb-1">{copy.detail.notes}</div>
              <textarea
                className="w-full min-h-[120px] rounded-xl border border-slate-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
                value={selectedTask.notes ?? ""}
                readOnly
              />
            </div>
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
