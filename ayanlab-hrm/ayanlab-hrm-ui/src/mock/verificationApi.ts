import type { VerificationTask, VerificationRequest, VerificationTaskStatus } from "../types/verification";
import { db } from "./mockDb";

const delay = (ms = 350) => new Promise((r) => setTimeout(r, ms));

// Verification Tasks (Admin)
export async function listVerificationTasks(): Promise<VerificationTask[]> {
  await delay(200);
  return [...db.verificationTasks];
}

export async function getVerificationTask(id: string): Promise<VerificationTask | null> {
  await delay(150);
  return db.verificationTasks.find((t) => t.id === id) || null;
}

export async function updateVerificationTaskStatus(
  id: string,
  status: VerificationTaskStatus,
  reviewedBy: string,
  reviewNotes?: string
): Promise<VerificationTask> {
  await delay(250);
  const task = db.verificationTasks.find((t) => t.id === id);
  if (!task) throw new Error("Task not found");

  task.status = status;
  task.reviewedAt = new Date().toISOString();
  task.reviewedBy = reviewedBy;
  task.reviewNotes = reviewNotes;
  if (status === "reviewing") {
    task.assignedTo = reviewedBy;
  }

  return task;
}

export async function assignVerificationTask(id: string, assignedTo: string): Promise<VerificationTask> {
  await delay(200);
  const task = db.verificationTasks.find((t) => t.id === id);
  if (!task) throw new Error("Task not found");

  task.status = "reviewing";
  task.assignedTo = assignedTo;
  return task;
}

// Mobile/Email Verification (Public)
export async function sendVerificationCode(type: "mobile" | "email", identifier: string): Promise<{ success: boolean; expiresIn: number }> {
  await delay(300);
  // Mock: generate a 6-digit code
  const code = Math.floor(100000 + Math.random() * 900000).toString();
  const expiresAt = new Date(Date.now() + 10 * 60 * 1000).toISOString(); // 10 minutes

  let request = db.verificationRequests.find((r) => r.type === type && r.identifier === identifier);
  if (!request) {
    request = {
      id: `vr_${Date.now()}`,
      type,
      identifier,
      attempts: 0,
      maxAttempts: 5
    };
    db.verificationRequests.push(request);
  }

  request.code = code;
  request.codeSentAt = new Date().toISOString();
  request.codeExpiresAt = expiresAt;
  request.attempts = 0;

  // In real app, send SMS/email here
  console.log(`[MOCK] ${type === "mobile" ? "SMS" : "Email"} code for ${identifier}: ${code}`);

  return { success: true, expiresIn: 600 };
}

export async function verifyCode(type: "mobile" | "email", identifier: string, code: string): Promise<{ success: boolean; message: string }> {
  await delay(200);
  const request = db.verificationRequests.find((r) => r.type === type && r.identifier === identifier);

  if (!request || !request.code) {
    return { success: false, message: "No verification code found. Please request a new code." };
  }

  if (request.attempts >= request.maxAttempts) {
    return { success: false, message: "Maximum attempts exceeded. Please request a new code." };
  }

  if (new Date(request.codeExpiresAt!) < new Date()) {
    return { success: false, message: "Verification code expired. Please request a new code." };
  }

  request.attempts += 1;

  if (request.code !== code) {
    return { success: false, message: "Invalid verification code. Please try again." };
  }

  request.verifiedAt = new Date().toISOString();
  return { success: true, message: "Verification successful!" };
}

export async function getVerificationRequest(type: "mobile" | "email", identifier: string): Promise<VerificationRequest | null> {
  await delay(100);
  return db.verificationRequests.find((r) => r.type === type && r.identifier === identifier) || null;
}

