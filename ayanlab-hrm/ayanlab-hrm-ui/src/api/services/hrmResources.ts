import { http } from "../client";
import { endpoints } from "../endpoints";
import { unwrapHrmData } from "../hrmApi";
import type { Job } from "../../types/jobs";
import type { Task } from "../../types/tasks";
import type { Transaction } from "../../types/transactions";
import type { VerificationTask, VerificationTaskStatus } from "../../types/verification";

export async function apiListJobs(): Promise<Job[]> {
  const res = await http.get(endpoints.resources.jobs);
  return unwrapHrmData<Job[]>(res.data);
}

export async function apiListTasks(): Promise<Task[]> {
  const res = await http.get(endpoints.resources.tasks);
  return unwrapHrmData<Task[]>(res.data);
}

export async function apiListTransactions(): Promise<Transaction[]> {
  const res = await http.get(endpoints.resources.transactions);
  return unwrapHrmData<Transaction[]>(res.data);
}

export async function apiListVerificationTasks(): Promise<VerificationTask[]> {
  const res = await http.get(endpoints.resources.documentVerifications);
  return unwrapHrmData<VerificationTask[]>(res.data);
}

export async function apiGetVerificationTask(id: string): Promise<VerificationTask | null> {
  const res = await http.get(endpoints.resources.documentVerification(id));
  return unwrapHrmData<VerificationTask>(res.data);
}

export async function apiUpdateVerificationTask(
  id: string,
  patch: Partial<VerificationTask>
): Promise<VerificationTask> {
  const res = await http.put(endpoints.resources.documentVerification(id), { id, ...patch });
  return unwrapHrmData<VerificationTask>(res.data);
}

export async function apiUpdateVerificationStatus(
  id: string,
  status: VerificationTaskStatus,
  reviewedBy: string,
  reviewNotes?: string
): Promise<VerificationTask> {
  return apiUpdateVerificationTask(id, {
    status,
    reviewedBy,
    reviewNotes,
    reviewedAt: new Date().toISOString(),
  });
}

export type InvoiceRow = {
  id: string;
  orgId: string;
  amountCents: number;
  currency: string;
  status: string;
  issuedAt: string;
};

export async function apiListInvoicesFromFinanceRecords(): Promise<InvoiceRow[]> {
  const res = await http.get(endpoints.resources.financeRecords);
  const rows = unwrapHrmData<InvoiceRow[]>(res.data);
  return rows.map((r) => ({
    id: r.id,
    orgId: r.orgId,
    amountCents: r.amountCents,
    currency: r.currency,
    status: r.status,
    issuedAt: r.issuedAt ?? (r as { createdAt?: string }).createdAt ?? "",
  }));
}

export type AuditRow = {
  id: string;
  actorEmail: string;
  action: string;
  target: string;
  createdAt: string;
};

export async function apiListUserAudits(): Promise<AuditRow[]> {
  const res = await http.get(endpoints.resources.userAudits);
  return unwrapHrmData<AuditRow[]>(res.data);
}

export async function apiListSystemAudits(): Promise<AuditRow[]> {
  const res = await http.get(endpoints.resources.systemAudits);
  return unwrapHrmData<AuditRow[]>(res.data);
}

export async function apiListDepartments(): Promise<unknown[]> {
  const res = await http.get(endpoints.resources.departments);
  return unwrapHrmData<unknown[]>(res.data);
}

export async function apiListProfiles(workerId?: string): Promise<unknown[]> {
  const res = await http.get(endpoints.resources.profiles);
  const all = unwrapHrmData<Array<{ workerId?: string }>>(res.data);
  return workerId ? all.filter((p) => p.workerId === workerId) : all;
}