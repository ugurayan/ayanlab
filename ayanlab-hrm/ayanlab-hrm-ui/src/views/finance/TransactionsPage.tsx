import React, { useMemo, useState, useCallback } from "react";
import { useTransactions, useWorkers } from "../../data/hooks";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Table, Td, Th } from "../../ui/components/Table";
import { Button } from "../../ui/components/Button";
import { Badge } from "../../ui/components/Badge";
import { useTranslations } from "../../state/i18n";
import { useAuth } from "../../state/auth";
import { TEXTS } from "./i18n";
import type { Transaction } from "../../types/transactions";
import { formatDate, formatMoney } from "../../lib/format";
import { Edit2, Plus } from "lucide-react";
import { TransactionModal } from "./TransactionModal";

const PAGE_SIZE_OPTIONS = [10, 25, 50, 100] as const;
const DEFAULT_PAGE_SIZE = 10;

export function TransactionsPage() {
  const txQuery = useTransactions();
  const workersQuery = useWorkers();
  const { user } = useAuth();
  const [statusFilter, setStatusFilter] = useState<string>("all");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTx, setEditingTx] = useState<Transaction | null>(null);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);
  const copy = useTranslations(TEXTS);

  const handlePageSizeChange = useCallback((e: React.ChangeEvent<HTMLSelectElement>) => {
    const newSize = Number(e.target.value) as (typeof PAGE_SIZE_OPTIONS)[number];
    setPageSize(newSize);
    setPage(0);
  }, []);

  const isAdmin = user?.roles.includes("PLATFORM_ADMIN") || user?.roles.includes("ORG_ADMIN");
  const isWorker = user?.roles.includes("WORKER");
  const isCompany = !isAdmin && !isWorker;

  const rows = useMemo(() => {
    const txs = (txQuery.data ?? []) as Transaction[];
    const workers = (workersQuery.data ?? []) as any[];

    let filtered = txs;

    // Role-based filtering
    if (isWorker && user?.id) {
      // Workers only see payouts to them
      filtered = txs.filter((t) => t.workerId === user.id || t.direction === "credit");
    } else if (isCompany && user?.scope.orgId) {
      // Companies see what they pay for what job
      filtered = txs.filter((t) => t.orgId === user.scope.orgId);
    }
    // Admins see all (no filter)

    return filtered
      .filter((t) => (statusFilter === "all" ? true : t.status === statusFilter))
      .map((t) => {
        const worker = t.workerId ? workers.find((w) => w.id === t.workerId) : null;
        return {
          ...t,
          workerName: worker?.displayName ?? t.workerId ?? "-"
        };
      });
  }, [txQuery.data, workersQuery.data, statusFilter, isWorker, isCompany, user]);

  const totalRows = rows.length;
  const totalPages = Math.max(1, Math.ceil(totalRows / pageSize));
  const currentPage = Math.min(page, Math.max(0, totalPages - 1));
  const paginatedRows = useMemo(() => {
    const start = currentPage * pageSize;
    return rows.slice(start, start + pageSize);
  }, [rows, currentPage, pageSize]);

  // Reset to first page when filter changes
  React.useEffect(() => {
    setPage(0);
  }, [statusFilter]);

  const handleEdit = (tx: Transaction) => {
    setEditingTx(tx);
    setIsModalOpen(true);
  };

  const handleCreate = () => {
    setEditingTx(null);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setEditingTx(null);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <div className="text-2xl font-bold">{copy.hero.title}</div>
          <div className="text-sm text-slate-500">{copy.hero.subtitle}</div>
        </div>
        <div className="flex items-center gap-3">
          <select
            className="rounded-2xl border border-slate-200 px-3 py-1.5 text-sm bg-white"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="all">{copy.filters.all}</option>
            <option value="completed">{copy.filters.completed}</option>
            <option value="pending">{copy.filters.pending}</option>
            <option value="failed">{copy.filters.failed}</option>
            <option value="refunded">{copy.filters.refunded}</option>
          </select>
          {isAdmin && (
            <Button onClick={handleCreate}>
              <Plus size={16} className="mr-1" />
              {copy.actions.create}
            </Button>
          )}
        </div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>{copy.table.title}</CardTitle>
        </CardHeader>
        <CardContent>
          {txQuery.isLoading ? (
            <div className="text-sm text-slate-500">{copy.table.loading}</div>
          ) : rows.length === 0 ? (
            <div className="text-sm text-slate-500">{copy.table.empty}</div>
          ) : (
            <>
              <div className="flex items-center justify-between text-sm text-slate-600 mb-3">
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
                    {PAGE_SIZE_OPTIONS.map((n) => (
                      <option key={n} value={n}>
                        {n}
                      </option>
                    ))}
                  </select>
                </label>
              </div>
              <div className="overflow-x-auto">
                <Table>
                  <thead>
                    <tr>
                      <Th>{copy.table.columns.date}</Th>
                      <Th>{copy.table.columns.from}</Th>
                      <Th>{copy.table.columns.to}</Th>
                      <Th>{copy.table.columns.job}</Th>
                      <Th>{copy.table.columns.description}</Th>
                      <Th>{copy.table.columns.amount}</Th>
                      <Th>{copy.table.columns.fees}</Th>
                      <Th>{copy.table.columns.paymentType}</Th>
                      <Th>{copy.table.columns.paymentMethod}</Th>
                      <Th>{copy.table.columns.reference}</Th>
                      <Th>{copy.table.columns.status}</Th>
                      {isAdmin && <Th>{copy.table.columns.actions}</Th>}
                      {isAdmin && <Th>{copy.table.columns.audit}</Th>}
                    </tr>
                  </thead>
                  <tbody>
                    {paginatedRows.map((t) => (
                    <tr key={t.id}>
                      <Td className="text-sm text-slate-600 whitespace-nowrap">
                        {formatDate(t.createdAt)}
                      </Td>
                      <Td className="text-sm">
                        {t.direction === "credit" ? (
                          <div>
                            <div className="font-semibold">{t.orgId ?? "-"}</div>
                            {isAdmin && t.orgId && (
                              <div className="text-xs text-slate-500">Organization</div>
                            )}
                          </div>
                        ) : (
                          <div className="text-slate-400">-</div>
                        )}
                      </Td>
                      <Td className="text-sm">
                        {t.direction === "credit" ? (
                          <div>
                            <div className="font-semibold">{t.workerName}</div>
                            {isAdmin && t.workerId && (
                              <div className="text-xs text-slate-500">Worker</div>
                            )}
                          </div>
                        ) : (
                          <div>
                            <div className="font-semibold">{t.orgId ?? "-"}</div>
                            {isAdmin && t.orgId && (
                              <div className="text-xs text-slate-500">Organization</div>
                            )}
                          </div>
                        )}
                      </Td>
                      <Td className="text-sm">
                        {t.jobTitle ? (
                          <div>
                            <div className="font-semibold">{t.jobTitle}</div>
                            {t.shiftId && (
                              <div className="text-xs text-slate-500">{t.shiftId}</div>
                            )}
                          </div>
                        ) : (
                          <span className="text-slate-400">-</span>
                        )}
                      </Td>
                      <Td className="text-sm text-slate-700 max-w-xs truncate" title={t.description}>
                        {t.description}
                      </Td>
                      <Td className="text-sm font-semibold text-right whitespace-nowrap">
                        {t.direction === "debit" ? "-" : "+"}
                        {formatMoney(t.amountCents, t.currency)}
                      </Td>
                      <Td className="text-sm text-slate-500 text-right whitespace-nowrap">
                        {formatMoney(t.feesCents, t.currency)}
                      </Td>
                      <Td className="text-sm text-slate-600 capitalize">
                        {t.paymentType.replace("_", " ")}
                      </Td>
                      <Td className="text-sm text-slate-600">
                        {t.bankName && (
                          <div>
                            <div>{t.bankName}</div>
                            {t.bankAccountLast4 && (
                              <div className="text-xs text-slate-500">•••• {t.bankAccountLast4}</div>
                            )}
                          </div>
                        )}
                        {t.creditCardBrand && (
                          <div>
                            <div>{t.creditCardBrand}</div>
                            {t.creditCardLast4 && (
                              <div className="text-xs text-slate-500">•••• {t.creditCardLast4}</div>
                            )}
                          </div>
                        )}
                        {!t.bankName && !t.creditCardBrand && <span className="text-slate-400">-</span>}
                      </Td>
                      <Td className="text-sm text-slate-600">
                        <div className="space-y-0.5">
                          {t.externalReferenceId && (
                            <div className="text-xs">{t.externalReferenceId}</div>
                          )}
                          {t.relatedInvoiceId && (
                            <div className="text-xs text-slate-500">{t.relatedInvoiceId}</div>
                          )}
                          {!t.externalReferenceId && !t.relatedInvoiceId && (
                            <span className="text-slate-400">-</span>
                          )}
                        </div>
                      </Td>
                      <Td>
                        <Badge
                          tone={
                            t.status === "completed"
                              ? "green"
                              : t.status === "pending"
                              ? "yellow"
                              : t.status === "failed"
                              ? "red"
                              : "gray"
                          }
                        >
                          {t.status}
                        </Badge>
                      </Td>
                      {isAdmin && (
                        <Td>
                          <Button
                            variant="ghost"
                            onClick={() => handleEdit(t)}
                            className="text-xs px-2 py-1"
                          >
                            <Edit2 size={14} />
                          </Button>
                        </Td>
                      )}
                      {isAdmin && (
                        <Td className="text-xs text-slate-500">
                          <div className="space-y-0.5">
                            {t.createdByName && (
                              <div>Created: {t.createdByName}</div>
                            )}
                            {t.updatedByName && t.updatedAt && (
                              <div>Updated: {t.updatedByName}</div>
                            )}
                            {t.updatedAt && (
                              <div className="text-[10px]">{formatDate(t.updatedAt)}</div>
                            )}
                          </div>
                        </Td>
                      )}
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

      {isAdmin && isModalOpen && (
        <TransactionModal transaction={editingTx} onClose={handleCloseModal} />
      )}
    </div>
  );
}
