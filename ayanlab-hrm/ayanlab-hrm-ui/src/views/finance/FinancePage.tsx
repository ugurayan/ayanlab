import React, { useMemo, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Table, Td, Th } from "../../ui/components/Table";
import { Badge } from "../../ui/components/Badge";
import { Button } from "../../ui/components/Button";
import { useInvoices, useTransactions, useWorkers, useJobs } from "../../data/hooks";
import { RequirePermission } from "../../router/guards";
import { formatDate, formatMoney } from "../../lib/format";
import { useTranslations } from "../../state/i18n";
import { useAuth } from "../../state/auth";
import type { Transaction } from "../../types/transactions";
import { MoreVertical, Edit, CheckCircle, ChevronLeft, ChevronRight } from "lucide-react";
import { cn } from "../../lib/cn";
import { P } from "../../security/permissions";

type FinanceRecordType = "invoice" | "payout" | "adjustment" | "export" | "import" | "job_payment";

type FinanceRecord = {
  id: string;
  type: FinanceRecordType;
  date: string;
  orgId?: string;
  orgName?: string;
  workerId?: string;
  workerName?: string;
  jobId?: string;
  jobTitle?: string;
  amountCents: number;
  currency: string;
  status: string;
  description: string;
  reference?: string;
  invoiceId?: string;
  paymentType?: string;
  feesCents?: number;
  direction?: "debit" | "credit";
  approved?: boolean;
  approvedBy?: string;
  approvedAt?: string;
};

function tone(s: string) {
  switch (s) {
    case "PAID":
    case "completed":
    case "approved":
      return "green";
    case "VOID":
    case "refunded":
    case "cancelled":
      return "gray";
    case "failed":
    case "rejected":
      return "red";
    default:
      return "amber";
  }
}

const ITEMS_PER_PAGE = 20;

export function FinancePage() {
  return (
    <RequirePermission anyOf={[P.financeRead]}>
      <FinanceInner />
    </RequirePermission>
  );
}

function FinanceInner() {
  const { user } = useAuth();
  const invoicesQuery = useInvoices();
  const transactionsQuery = useTransactions();
  const workersQuery = useWorkers();
  const jobsQuery = useJobs();
  const copy = useTranslations(TEXTS);

  const [currentPage, setCurrentPage] = useState(1);
  const [openActionsId, setOpenActionsId] = useState<string | null>(null);

  const isAdmin = user?.roles.includes("PLATFORM_ADMIN") || user?.roles.includes("ORG_ADMIN");

  if (!isAdmin) {
    return (
      <div className="space-y-6">
        <div>
          <div className="text-2xl font-bold">{copy.hero.title}</div>
          <div className="text-slate-500">{copy.hero.subtitle}</div>
        </div>
        <Card>
          <CardContent className="p-8 text-center text-slate-500">
            {copy.hero.adminOnly}
          </CardContent>
        </Card>
      </div>
    );
  }

  const invoices = (invoicesQuery.data ?? []) as any[];
  const transactions = (transactionsQuery.data ?? []) as Transaction[];
  const workers = (workersQuery.data ?? []) as any[];
  const jobs = (jobsQuery.data ?? []) as any[];

  // Combine all financial records into a unified list
  const allRecords: FinanceRecord[] = useMemo(() => {
    const records: FinanceRecord[] = [];

    // Add invoices
    invoices.forEach((inv) => {
      records.push({
        id: inv.id,
        type: "invoice",
        date: inv.issuedAt,
        orgId: inv.orgId,
        amountCents: inv.amountCents,
        currency: inv.currency || "CHF",
        status: inv.status,
        description: `Invoice ${inv.id} for organization ${inv.orgId}`,
        reference: inv.id,
        approved: inv.status === "PAID",
        approvedAt: inv.status === "PAID" ? inv.issuedAt : undefined
      });
    });

    // Add transactions (payouts, payments)
    transactions.forEach((tx) => {
      const worker = tx.workerId ? workers.find((w) => w.id === tx.workerId) : null;
      const job = tx.jobId ? jobs.find((j) => j.id === tx.jobId) : null;
      
      records.push({
        id: tx.id,
        type: tx.direction === "credit" ? "payout" : "job_payment",
        date: tx.createdAt,
        orgId: tx.orgId,
        workerId: tx.workerId,
        workerName: worker?.displayName,
        jobId: tx.jobId,
        jobTitle: tx.jobTitle || job?.title,
        amountCents: tx.amountCents,
        currency: tx.currency,
        status: tx.status,
        description: tx.description,
        reference: tx.externalReferenceId || tx.id,
        invoiceId: tx.relatedInvoiceId,
        paymentType: tx.paymentType,
        feesCents: tx.feesCents,
        direction: tx.direction,
        approved: tx.status === "completed",
        approvedAt: tx.processedAt
      });
    });

    // Sort by date (newest first)
    return records.sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
  }, [invoices, transactions, workers, jobs]);

  // Pagination
  const totalPages = Math.ceil(allRecords.length / ITEMS_PER_PAGE);
  const paginatedRecords = useMemo(() => {
    const start = (currentPage - 1) * ITEMS_PER_PAGE;
    const end = start + ITEMS_PER_PAGE;
    return allRecords.slice(start, end);
  }, [allRecords, currentPage]);

  // Calculate summary statistics
  const summary = useMemo(() => {
    const completedTxs = transactions.filter((t) => t.status === "completed");
    const pendingTxs = transactions.filter((t) => t.status === "pending");
    const failedTxs = transactions.filter((t) => t.status === "failed");

    const totalRevenue = completedTxs
      .filter((t) => t.direction === "debit")
      .reduce((sum, t) => sum + t.amountCents, 0);
    const totalPayouts = completedTxs
      .filter((t) => t.direction === "credit")
      .reduce((sum, t) => sum + t.amountCents, 0);
    const totalFees = completedTxs.reduce((sum, t) => sum + (t.feesCents || 0), 0);
    const netRevenue = totalRevenue - totalPayouts - totalFees;

    const paidInvoices = invoices.filter((i) => i.status === "PAID");
    const dueInvoices = invoices.filter((i) => i.status === "DUE");
    const totalInvoiceAmount = invoices.reduce((sum, i) => sum + i.amountCents, 0);
    const paidInvoiceAmount = paidInvoices.reduce((sum, i) => sum + i.amountCents, 0);

    return {
      totalTransactions: transactions.length,
      completedTransactions: completedTxs.length,
      pendingTransactions: pendingTxs.length,
      failedTransactions: failedTxs.length,
      totalRevenue,
      totalPayouts,
      totalFees,
      netRevenue,
      totalInvoices: invoices.length,
      paidInvoices: paidInvoices.length,
      dueInvoices: dueInvoices.length,
      totalInvoiceAmount,
      paidInvoiceAmount
    };
  }, [transactions, invoices]);

  const handleEdit = (record: FinanceRecord) => {
    // TODO: Implement edit functionality
    console.log("Edit record:", record);
    setOpenActionsId(null);
  };

  const handleApprove = (record: FinanceRecord) => {
    // TODO: Implement approve functionality
    console.log("Approve record:", record);
    setOpenActionsId(null);
  };

  return (
    <div className="space-y-6">
      <div>
        <div className="text-2xl font-bold">{copy.hero.title}</div>
        <div className="text-slate-500">{copy.hero.subtitle}</div>
      </div>

      {/* Summary Cards */}
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
        <Card>
          <CardContent className="p-5">
            <div className="text-xs font-semibold text-slate-500 mb-1">
              {copy.summary.totalRevenue}
            </div>
            <div className="text-2xl font-bold">
              {formatMoney(summary.totalRevenue, "CHF")}
            </div>
            <div className="text-xs text-slate-500 mt-1">
              {copy.summary.fromCompleted}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-5">
            <div className="text-xs font-semibold text-slate-500 mb-1">
              {copy.summary.totalPayouts}
            </div>
            <div className="text-2xl font-bold">
              {formatMoney(summary.totalPayouts, "CHF")}
            </div>
            <div className="text-xs text-slate-500 mt-1">
              {copy.summary.toWorkers}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-5">
            <div className="text-xs font-semibold text-slate-500 mb-1">
              {copy.summary.totalFees}
            </div>
            <div className="text-2xl font-bold">
              {formatMoney(summary.totalFees, "CHF")}
            </div>
            <div className="text-xs text-slate-500 mt-1">
              {copy.summary.transactionFees}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-5">
            <div className="text-xs font-semibold text-slate-500 mb-1">
              {copy.summary.netRevenue}
            </div>
            <div className="text-2xl font-bold">
              {formatMoney(summary.netRevenue, "CHF")}
            </div>
            <div className="text-xs text-slate-500 mt-1">
              {copy.summary.afterFees}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Comprehensive Finance Table */}
      <Card>
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle>{copy.table.title}</CardTitle>
            <div className="text-sm text-slate-500">
              {copy.table.showing} {allRecords.length} {copy.table.records}
            </div>
          </div>
        </CardHeader>
        <CardContent>
          {invoicesQuery.isLoading || transactionsQuery.isLoading ? (
            <div className="text-slate-500">{copy.table.loading}</div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <Table>
                  <thead>
                    <tr>
                      <Th>{copy.table.columns.type}</Th>
                      <Th>{copy.table.columns.date}</Th>
                      <Th>{copy.table.columns.org}</Th>
                      <Th>{copy.table.columns.worker}</Th>
                      <Th>{copy.table.columns.job}</Th>
                      <Th>{copy.table.columns.description}</Th>
                      <Th>{copy.table.columns.amount}</Th>
                      <Th>{copy.table.columns.fees}</Th>
                      <Th>{copy.table.columns.paymentType}</Th>
                      <Th>{copy.table.columns.reference}</Th>
                      <Th>{copy.table.columns.status}</Th>
                      <Th className="text-right">{copy.table.columns.actions}</Th>
                    </tr>
                  </thead>
                  <tbody>
                    {paginatedRecords.map((record) => (
                      <tr key={record.id}>
                        <Td>
                          <Badge tone="blue" className="capitalize">
                            {record.type.replace("_", " ")}
                          </Badge>
                        </Td>
                        <Td className="text-sm text-slate-600 whitespace-nowrap">
                          {formatDate(record.date)}
                        </Td>
                        <Td className="text-sm">
                          {record.orgId ? (
                            <div>
                              <div className="font-semibold">{record.orgId}</div>
                              {record.orgName && (
                                <div className="text-xs text-slate-500">{record.orgName}</div>
                              )}
                            </div>
                          ) : (
                            <span className="text-slate-400">-</span>
                          )}
                        </Td>
                        <Td className="text-sm">
                          {record.workerName ? (
                            <div>
                              <div className="font-semibold">{record.workerName}</div>
                              {record.workerId && (
                                <div className="text-xs text-slate-500">{record.workerId}</div>
                              )}
                            </div>
                          ) : (
                            <span className="text-slate-400">-</span>
                          )}
                        </Td>
                        <Td className="text-sm">
                          {record.jobTitle ? (
                            <div>
                              <div className="font-semibold truncate max-w-xs" title={record.jobTitle}>
                                {record.jobTitle}
                              </div>
                              {record.jobId && (
                                <div className="text-xs text-slate-500">{record.jobId}</div>
                              )}
                            </div>
                          ) : (
                            <span className="text-slate-400">-</span>
                          )}
                        </Td>
                        <Td className="text-sm text-slate-700 max-w-xs truncate" title={record.description}>
                          {record.description}
                        </Td>
                        <Td className="text-sm font-semibold text-right whitespace-nowrap">
                          {record.direction === "debit" ? "-" : record.direction === "credit" ? "+" : ""}
                          {formatMoney(record.amountCents, record.currency)}
                        </Td>
                        <Td className="text-sm text-slate-500 text-right whitespace-nowrap">
                          {record.feesCents ? formatMoney(record.feesCents, record.currency) : "-"}
                        </Td>
                        <Td className="text-sm text-slate-600 capitalize">
                          {record.paymentType ? record.paymentType.replace("_", " ") : "-"}
                        </Td>
                        <Td className="text-sm text-slate-600">
                          <div className="space-y-0.5">
                            {record.reference && (
                              <div className="text-xs font-mono">{record.reference}</div>
                            )}
                            {record.invoiceId && (
                              <div className="text-xs text-slate-500">Invoice: {record.invoiceId}</div>
                            )}
                            {!record.reference && !record.invoiceId && (
                              <span className="text-slate-400">-</span>
                            )}
                          </div>
                        </Td>
                        <Td>
                          <Badge tone={tone(record.status) as any}>{record.status}</Badge>
                        </Td>
                        <Td className="text-right">
                          <div className="relative inline-block">
                            <Button
                              variant="ghost"
                              onClick={() => setOpenActionsId(openActionsId === record.id ? null : record.id)}
                              className="px-2 py-1"
                            >
                              <MoreVertical size={16} />
                            </Button>
                            {openActionsId === record.id && (
                              <>
                                <div
                                  className="fixed inset-0 z-10"
                                  onClick={() => setOpenActionsId(null)}
                                />
                                <div className="absolute right-0 top-full mt-1 z-20 w-48 rounded-xl border border-slate-200 bg-white shadow-lg">
                                  <div className="p-1">
                                    <button
                                      onClick={() => handleEdit(record)}
                                      className="w-full flex items-center gap-2 rounded-lg px-3 py-2 text-sm text-slate-700 hover:bg-slate-50 text-left"
                                    >
                                      <Edit size={14} />
                                      {copy.actions.edit}
                                    </button>
                                    {!record.approved && (
                                      <button
                                        onClick={() => handleApprove(record)}
                                        className="w-full flex items-center gap-2 rounded-lg px-3 py-2 text-sm text-slate-700 hover:bg-slate-50 text-left"
                                      >
                                        <CheckCircle size={14} />
                                        {copy.actions.approve}
                                      </button>
                                    )}
                                  </div>
                                </div>
                              </>
                            )}
                          </div>
                        </Td>
                      </tr>
                    ))}
                  </tbody>
                </Table>
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="mt-4 flex items-center justify-between border-t border-slate-200 pt-4">
                  <div className="text-sm text-slate-600">
                    {copy.pagination.showing} {(currentPage - 1) * ITEMS_PER_PAGE + 1} - {Math.min(currentPage * ITEMS_PER_PAGE, allRecords.length)} {copy.pagination.of} {allRecords.length}
                  </div>
                  <div className="flex items-center gap-2">
                    <Button
                      variant="secondary"
                      onClick={() => setCurrentPage((p) => Math.max(1, p - 1))}
                      disabled={currentPage === 1}
                      className="px-3 py-1"
                    >
                      <ChevronLeft size={16} />
                    </Button>
                    <div className="flex items-center gap-1">
                      {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                        let pageNum;
                        if (totalPages <= 5) {
                          pageNum = i + 1;
                        } else if (currentPage <= 3) {
                          pageNum = i + 1;
                        } else if (currentPage >= totalPages - 2) {
                          pageNum = totalPages - 4 + i;
                        } else {
                          pageNum = currentPage - 2 + i;
                        }
                        return (
                          <Button
                            key={pageNum}
                            variant={currentPage === pageNum ? "primary" : "secondary"}
                            onClick={() => setCurrentPage(pageNum)}
                            className="px-3 py-1 min-w-[2.5rem]"
                          >
                            {pageNum}
                          </Button>
                        );
                      })}
                    </div>
                    <Button
                      variant="secondary"
                      onClick={() => setCurrentPage((p) => Math.min(totalPages, p + 1))}
                      disabled={currentPage === totalPages}
                      className="px-3 py-1"
                    >
                      <ChevronRight size={16} />
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

const TEXTS = {
  en: {
    hero: {
      title: "Finance",
      subtitle: "Invoices, payouts, adjustments, exports, imports, and job details.",
      adminOnly: "This page is only accessible to administrators."
    },
    summary: {
      totalRevenue: "Total Revenue",
      fromCompleted: "From completed transactions",
      totalPayouts: "Total Payouts",
      toWorkers: "Paid to workers",
      totalFees: "Total Fees",
      transactionFees: "Transaction processing fees",
      netRevenue: "Net Revenue",
      afterFees: "After fees and payouts"
    },
    table: {
      title: "All Financial Records",
      showing: "Showing",
      records: "records",
      loading: "Loading…",
      columns: {
        type: "Type",
        date: "Date",
        org: "Organization",
        worker: "Worker",
        job: "Job",
        description: "Description",
        amount: "Amount",
        fees: "Fees",
        paymentType: "Payment Type",
        reference: "Reference",
        status: "Status",
        actions: "Actions"
      }
    },
    actions: {
      edit: "Edit",
      approve: "Approve"
    },
    pagination: {
      showing: "Showing",
      of: "of"
    }
  },
  de: {
    hero: {
      title: "Finanzen",
      subtitle: "Rechnungen, Auszahlungen, Anpassungen, Exporte, Importe und Job-Details.",
      adminOnly: "Diese Seite ist nur für Administratoren zugänglich."
    },
    summary: {
      totalRevenue: "Gesamteinnahmen",
      fromCompleted: "Von abgeschlossenen Transaktionen",
      totalPayouts: "Gesamtauszahlungen",
      toWorkers: "An Worker gezahlt",
      totalFees: "Gesamtgebühren",
      transactionFees: "Transaktionsgebühren",
      netRevenue: "Nettoumsatz",
      afterFees: "Nach Gebühren und Auszahlungen"
    },
    table: {
      title: "Alle Finanzaufzeichnungen",
      showing: "Anzeigen",
      records: "Aufzeichnungen",
      loading: "Lädt…",
      columns: {
        type: "Typ",
        date: "Datum",
        org: "Organisation",
        worker: "Worker",
        job: "Job",
        description: "Beschreibung",
        amount: "Betrag",
        fees: "Gebühren",
        paymentType: "Zahlungsart",
        reference: "Referenz",
        status: "Status",
        actions: "Aktionen"
      }
    },
    actions: {
      edit: "Bearbeiten",
      approve: "Genehmigen"
    },
    pagination: {
      showing: "Anzeigen",
      of: "von"
    }
  },
  tr: {
    hero: {
      title: "Finans",
      subtitle: "Faturalar, ödemeler, düzeltmeler, dışa aktarımlar, içe aktarımlar ve iş detayları.",
      adminOnly: "Bu sayfa yalnızca yöneticiler tarafından erişilebilir."
    },
    summary: {
      totalRevenue: "Toplam Gelir",
      fromCompleted: "Tamamlanan işlemlerden",
      totalPayouts: "Toplam Ödemeler",
      toWorkers: "Çalışanlara ödendi",
      totalFees: "Toplam Ücretler",
      transactionFees: "İşlem ücretleri",
      netRevenue: "Net Gelir",
      afterFees: "Ücretler ve ödemelerden sonra"
    },
    table: {
      title: "Tüm Finansal Kayıtlar",
      showing: "Gösteriliyor",
      records: "kayıt",
      loading: "Yükleniyor…",
      columns: {
        type: "Tür",
        date: "Tarih",
        org: "Kuruluş",
        worker: "Çalışan",
        job: "İş",
        description: "Açıklama",
        amount: "Tutar",
        fees: "Ücretler",
        paymentType: "Ödeme Türü",
        reference: "Referans",
        status: "Durum",
        actions: "İşlemler"
      }
    },
    actions: {
      edit: "Düzenle",
      approve: "Onayla"
    },
    pagination: {
      showing: "Gösteriliyor",
      of: "/"
    }
  }
} as const;
