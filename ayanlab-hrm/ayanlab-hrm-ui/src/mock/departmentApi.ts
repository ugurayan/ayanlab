import { departments } from "./departmentData";
import type { Department } from "../types/departments";

const delay = (ms = 200) => new Promise((r) => setTimeout(r, ms));

export async function listDepartments(): Promise<Department[]> {
  await delay(200);
  return [...departments];
}

export async function getDepartmentById(departmentId: string): Promise<Department | null> {
  await delay(150);
  return departments.find((d) => d.department_id === departmentId) || null;
}

export async function getIssuesByDepartment(departmentId: string, language: "en" | "de" | "tr" = "en"): Promise<string[]> {
  await delay(150);
  const dept = departments.find((d) => d.department_id === departmentId);
  return dept ? dept.issues[language] : [];
}

