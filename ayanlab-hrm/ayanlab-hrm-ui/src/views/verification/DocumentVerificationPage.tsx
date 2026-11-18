import React, { useState, useMemo, useCallback } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Button } from "../../ui/components/Button";
import { Badge } from "../../ui/components/Badge";
import { Table, Th, Td } from "../../ui/components/Table";
import { RequirePermission } from "../../router/guards";
import { useTranslations } from "../../state/i18n";
import { TEXTS } from "./i18n";
import { useVerificationTasks, useUpdateVerificationTask, useAssignVerificationTask } from "../../data/hooks";
import { useAuth } from "../../state/auth";
import type { VerificationTask, VerificationTaskStatus } from "../../types/verification";
import { Eye, CheckCircle2, XCircle, AlertCircle, MessageSquare, X, Columns2 } from "lucide-react";
import { LoadingSpinner } from "../../ui/components/LoadingSpinner";
import { ColumnsPanel } from "../../ui/components/ColumnsPanel";
import {
  VERIFICATION_COLUMN_IDS,
  VERIFICATION_COLUMN_LABELS,
  getVisibleColumnsInOrder,
  loadVerificationColumnConfig,
  saveVerificationColumnConfig,
  type VerificationColumnConfig,
  type VerificationColumnId,
} from "./columnConfig";
import { P } from "../../security/permissions";

export function DocumentVerificationPage() {
  return (
    <RequirePermission anyOf={[P.verificationRead]}>
      <DocumentVerificationInner />
    </RequirePermission>
  );
}

function DocumentVerificationInner() {
  const copy = useTranslations(TEXTS);
  const { user } = useAuth();
  const { data: tasks = [], isLoading } = useVerificationTasks();
  const updateTask = useUpdateVerificationTask();
  const assignTask = useAssignVerificationTask();
  const [selectedTask, setSelectedTask] = useState<VerificationTask | null>(null);
  const [reviewNotes, setReviewNotes] = useState("");
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [columnConfig, setColumnConfig] = useState<VerificationColumnConfig>(() => loadVerificationColumnConfig());
  const [columnsPanelOpen, setColumnsPanelOpen] = useState(false);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  const handlePageSizeChange = useCallback((e: React.ChangeEvent<HTMLSelectElement>) => {
    setPageSize(Number(e.target.value));
    setPage(0);
  }, []);

  // Sort: unverified first, then by priority and date
  const sortedTasks = useMemo(() => {
    return [...tasks].sort((a, b) => {
      if (a.status === "pending" && b.status !== "pending") return -1;
      if (a.status !== "pending" && b.status === "pending") return 1;
      if (a.priority === "high" && b.priority !== "high") return -1;
      if (a.priority !== "high" && b.priority === "high") return 1;
      return new Date(b.submittedAt).getTime() - new Date(a.submittedAt).getTime();
    });
  }, [tasks]);

  const totalRows = sortedTasks.length;
  const totalPages = Math.max(1, Math.ceil(totalRows / pageSize));
  const currentPage = Math.min(page, Math.max(0, totalPages - 1));
  const paginatedTasks = useMemo(() => {
    const start = currentPage * pageSize;
    return sortedTasks.slice(start, start + pageSize);
  }, [sortedTasks, currentPage, pageSize]);

  const visibleColumns = useMemo(
    () => getVisibleColumnsInOrder(columnConfig),
    [columnConfig]
  );

  const handleColumnConfigSave = useCallback((config: VerificationColumnConfig) => {
    saveVerificationColumnConfig(config);
    setColumnConfig(config);
  }, []);

  const handleSelectTask = async (task: VerificationTask) => {
    // Auto-assign if not already reviewing
    if (task.status === "pending" && user?.id) {
      await assignTask.mutateAsync({ id: task.id, assignedTo: user.id });
    }
    setSelectedTask(task);
    setReviewNotes(task.reviewNotes || "");
    setShowReviewModal(true);
  };

  const handleSubmitReview = async (status: VerificationTaskStatus) => {
    if (!selectedTask || !user?.id) return;

    await updateTask.mutateAsync({
      id: selectedTask.id,
      status,
      reviewedBy: user.id,
      reviewNotes: reviewNotes.trim() || undefined
    });

    setShowReviewModal(false);
    setSelectedTask(null);
    setReviewNotes("");
  };

  const getStatusBadge = (status: VerificationTask["status"]) => {
    const statusConfig = copy.document.statuses[status];
    const colors: Record<VerificationTask["status"], string> = {
      pending: "bg-yellow-100 text-yellow-800",
      reviewing: "bg-blue-100 text-blue-800",
      accepted: "bg-green-100 text-green-800",
      rejected: "bg-rose-100 text-rose-800",
      needs_attention: "bg-orange-100 text-orange-800",
      needs_interview: "bg-purple-100 text-purple-800"
    };
    return <Badge className={colors[status]}>{statusConfig}</Badge>;
  };

  const getPriorityBadge = (priority: VerificationTask["priority"]) => {
    const priorityConfig = copy.document.priorities[priority];
    const colors: Record<VerificationTask["priority"], string> = {
      low: "bg-slate-100 text-slate-800",
      medium: "bg-blue-100 text-blue-800",
      high: "bg-rose-100 text-rose-800"
    };
    return <Badge className={colors[priority]}>{priorityConfig}</Badge>;
  };

  const getTypeLabel = (type: VerificationTask["type"]) => {
    return copy.document.types[type] || type;
  };

  const renderCell = (task: VerificationTask, columnId: VerificationColumnId) => {
    switch (columnId) {
      case "worker":
        return (
          <Td>
            <div>
              <div className="font-semibold">{task.workerName}</div>
              <div className="text-xs text-slate-500">{task.workerEmail}</div>
            </div>
          </Td>
        );
      case "type":
        return <Td>{getTypeLabel(task.type)}</Td>;
      case "category":
        return <Td>{copy.document.categories[task.category]}</Td>;
      case "status":
        return <Td>{getStatusBadge(task.status)}</Td>;
      case "priority":
        return <Td>{getPriorityBadge(task.priority)}</Td>;
      case "submittedAt":
        return (
          <Td className="text-xs text-slate-600">
            {new Date(task.submittedAt).toLocaleDateString()}
          </Td>
        );
      case "assignedTo":
        return (
          <Td className="text-xs text-slate-600">
            {task.assignedTo ? task.assignedTo : copy.document.unassigned ?? "Unassigned"}
          </Td>
        );
      default:
        return null;
    }
  };

  return (
    <div className="space-y-4 relative">
      {isLoading && <LoadingSpinner type="pulse" overlay text="Loading verification tasks..." />}
      <div className="flex items-center justify-between">
        <div>
          <div className="text-2xl font-bold">{copy.document.title}</div>
          <div className="text-slate-500 text-sm">{copy.document.subtitle}</div>
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

      <Card>
        <CardContent className="p-0">
          {sortedTasks.length === 0 && !isLoading ? (
            <div className="text-center text-slate-500 py-8 px-4">{copy.document.empty}</div>
          ) : (
            <>
              <div className="flex items-center justify-between text-sm text-slate-600 px-4 py-3 border-b border-slate-100">
                <span>
                  {totalRows} {totalRows === 1 ? "record" : "records"}
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
                    {visibleColumns.map((id) => (
                      <Th key={id}>{VERIFICATION_COLUMN_LABELS[id]}</Th>
                    ))}
                    <Th>{copy.document.table.actions}</Th>
                  </tr>
                </thead>
                <tbody>
                  {paginatedTasks.map((task) => (
                    <tr key={task.id}>
                      {visibleColumns.map((id) => (
                        <React.Fragment key={id}>{renderCell(task, id)}</React.Fragment>
                      ))}
                      <Td>
                        <Button
                          variant="ghost"
                          onClick={() => handleSelectTask(task)}
                          className="px-2 py-1"
                        >
                          <Eye size={16} />
                        </Button>
                      </Td>
                    </tr>
                  ))}
                </tbody>
              </Table>
              {totalPages > 1 && (
                <div className="flex items-center justify-between text-sm text-slate-600 px-4 py-3 mt-4 border-t border-slate-100">
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

      {/* Review Modal */}
      {showReviewModal && selectedTask && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4">
          <Card className="w-full max-w-3xl max-h-[90vh] overflow-y-auto">
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>{copy.document.modal.title}</CardTitle>
              <Button variant="ghost" onClick={() => setShowReviewModal(false)} className="px-2 py-1">
                <X size={16} />
              </Button>
            </CardHeader>
            <CardContent className="space-y-4">

            <div className="space-y-3 text-sm">
              <div>
                <span className="font-semibold">{copy.document.modal.worker}:</span> {selectedTask.workerName}
              </div>
              <div>
                <span className="font-semibold">{copy.document.modal.type}:</span> {getTypeLabel(selectedTask.type)}
              </div>
              <div>
                <span className="font-semibold">{copy.document.modal.category}:</span> {copy.document.categories[selectedTask.category]}
              </div>
              <div>
                <span className="font-semibold">{copy.document.modal.submittedAt}:</span> {new Date(selectedTask.submittedAt).toLocaleString()}
              </div>
            </div>

            {/* Document Images */}
            <div className="space-y-2">
              <div className="font-semibold text-sm">{copy.document.modal.documents}:</div>
              <div className="grid grid-cols-2 gap-2">
                {selectedTask.documentUrls.map((url, idx) => (
                  <a
                    key={idx}
                    href={url}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="block rounded-lg border border-slate-200 overflow-hidden hover:border-slate-400 transition"
                  >
                    <img src={url} alt={`Document ${idx + 1}`} className="w-full h-32 object-cover" />
                  </a>
                ))}
              </div>
            </div>

            {/* Review Notes */}
            <div className="space-y-2">
              <label className="text-sm font-semibold">{copy.document.modal.notes}:</label>
              <textarea
                className="w-full rounded-lg border border-slate-200 px-3 py-2 text-sm resize-none"
                rows={3}
                value={reviewNotes}
                onChange={(e) => setReviewNotes(e.target.value)}
                placeholder={copy.document.modal.notesPlaceholder}
              />
            </div>

            {/* Action Buttons */}
            <div className="flex flex-wrap gap-2 pt-2">
              <Button
                onClick={() => handleSubmitReview("accepted")}
                disabled={updateTask.isPending}
                className="flex-1 min-w-[120px]"
              >
                <CheckCircle2 size={16} className="mr-2" />
                {copy.document.actions.accept}
              </Button>
              <Button
                variant="danger"
                onClick={() => handleSubmitReview("rejected")}
                disabled={updateTask.isPending}
                className="flex-1 min-w-[120px]"
              >
                <XCircle size={16} className="mr-2" />
                {copy.document.actions.reject}
              </Button>
              <Button
                variant="secondary"
                onClick={() => handleSubmitReview("needs_attention")}
                disabled={updateTask.isPending}
                className="flex-1 min-w-[120px]"
              >
                <AlertCircle size={16} className="mr-2" />
                {copy.document.actions.needsAttention}
              </Button>
              <Button
                variant="secondary"
                onClick={() => handleSubmitReview("needs_interview")}
                disabled={updateTask.isPending}
                className="flex-1 min-w-[120px]"
              >
                <MessageSquare size={16} className="mr-2" />
                {copy.document.actions.needsInterview}
              </Button>
            </div>
            </CardContent>
          </Card>
        </div>
      )}
      <ColumnsPanel
        isOpen={columnsPanelOpen}
        onClose={() => setColumnsPanelOpen(false)}
        columnIds={VERIFICATION_COLUMN_IDS}
        labels={VERIFICATION_COLUMN_LABELS}
        currentConfig={columnConfig}
        onSave={handleColumnConfigSave}
        title="Verification table columns"
      />
    </div>
  );
}

