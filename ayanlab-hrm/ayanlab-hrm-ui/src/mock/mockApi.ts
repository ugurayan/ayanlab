import type { AuthSession, User } from "../types/auth";
import { computePermissions } from "../security/permissions";
import { createShift, updateWorker, db, mockUsers, type Shift, type Worker } from "./mockDb";
import type { Task } from "../types/tasks";
import type { Job } from "../types/jobs";
import type { Transaction } from "../types/transactions";

const delay = (ms = 350) => new Promise((r) => setTimeout(r, ms));
const token = () => `mock.${Math.random().toString(16).slice(2)}`;

export async function mockLogin(email: string, password: string): Promise<AuthSession> {
  await delay();
  const rec = mockUsers[email];
  if (!rec || rec.password !== password) {
    throw new Error("Invalid email or password");
  }
  const user: User = {
    ...rec.user,
    permissions: [...new Set([...rec.user.permissions, ...computePermissions(rec.user.roles)])]
  };
  return { accessToken: token(), user };
}

export async function mockMe(user: User): Promise<User> {
  await delay(120);
  return user;
}

export async function listOrgs() {
  await delay(200);
  return db.orgs;
}

export async function listLocations(orgId?: string) {
  await delay(200);
  return orgId ? db.locations.filter((l) => l.orgId === orgId) : db.locations;
}

export async function listShifts(orgId?: string) {
  await delay(250);
  return orgId ? db.shifts.filter((s) => s.orgId === orgId) : db.shifts;
}

export async function createShiftApi(input: Omit<Shift, "id" | "status"> & { status?: Shift["status"] }) {
  await delay(250);
  return createShift(input);
}

export async function listApplications() {
  await delay(200);
  return db.applications;
}

export async function listWorkers() {
  await delay(200);
  return db.workers;
}

export async function updateWorkerApi(workerId: string, patch: Partial<Worker>) {
  await delay(220);
  return updateWorker(workerId, patch);
}

export async function listJobProfiles(workerId?: string) {
  await delay(200);
  return workerId ? db.jobProfiles.filter((p) => p.workerId === workerId) : db.jobProfiles;
}

export async function listDocuments() {
  await delay(220);
  return db.documents;
}

export async function listInvoices(orgId?: string) {
  await delay(220);
  return orgId ? db.invoices.filter((i) => i.orgId === orgId) : db.invoices;
}

export async function listAudit() {
  await delay(220);
  return db.audit;
}

export async function listUserAudit() {
  await delay(220);
  return db.auditUser;
}

export async function listSystemAudit() {
  await delay(220);
  return db.auditSystem;
}

export async function listTasks(): Promise<Task[]> {
  await delay(220);
  return db.tasks;
}

export async function listJobs(): Promise<Job[]> {
  await delay(220);
  return db.jobs;
}

export async function listTransactions(): Promise<Transaction[]> {
  await delay(220);
  return db.transactions;
}
