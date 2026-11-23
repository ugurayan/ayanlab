import { http } from "../client";
import { endpoints } from "../endpoints";
import { unwrapHrmData } from "../hrmApi";
import { jobApi } from "../../mock/jobApi";
import { JOB_INDUSTRIES, JOB_ROLES, SKILLS } from "../../mock/jobData";
import { TEXTS } from "../../views/jobs/i18n";

// Generate correlation ID
const generateCorrelationId = () => {
  return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
};

// Get language from localStorage or default to "en"
const getLanguage = (): string => {
  if (typeof window !== "undefined") {
    return window.localStorage.getItem("language") || "en";
  }
  return "en";
};

const ONE_DAY_MS = 24 * 60 * 60 * 1000;
type CachedEntry<T> = { data: T; timestamp: number };

const getCache = <T>(key: string, maxAge = ONE_DAY_MS): T | null => {
  if (typeof window === "undefined") return null;
  try {
    const raw = window.localStorage.getItem(key);
    if (!raw) return null;
    const parsed = JSON.parse(raw) as CachedEntry<T>;
    const isExpired = Date.now() - parsed.timestamp > maxAge;
    if (isExpired) {
      window.localStorage.removeItem(key);
      return null;
    }
    return parsed.data;
  } catch {
    return null;
  }
};

const setCache = <T>(key: string, data: T) => {
  if (typeof window === "undefined") return;
  try {
    const entry: CachedEntry<T> = { data, timestamp: Date.now() };
    window.localStorage.setItem(key, JSON.stringify(entry));
  } catch {
    // ignore storage failures
  }
};

export type JobIndustry = {
  id: string;
  name: string;
};

type CatalogRow = {
  id: string;
  details?: Record<string, unknown>;
};

function detailText(row: CatalogRow, key: string, fallback = ""): string {
  const value = row.details?.[key];
  return typeof value === "string" && value.trim() ? value : fallback;
}

function catalogRows(payload: unknown): CatalogRow[] {
  const rows = unwrapHrmData<CatalogRow[]>(payload);
  return Array.isArray(rows) ? rows : [];
}

function detailStringArray(row: CatalogRow | undefined, key: string): string[] {
  const value = row?.details?.[key];
  if (!Array.isArray(value)) return [];
  return value.filter((item): item is string => typeof item === "string");
}

function skillLabel(id: string, language: string): string {
  const lang = (language in TEXTS ? language : "en") as keyof typeof TEXTS;
  const fromI18n = TEXTS[lang].skills[id as keyof (typeof TEXTS)["en"]["skills"]];
  if (typeof fromI18n === "string") return fromI18n;
  const row = SKILLS.find((s) => s.id === id);
  if (row?.name) return row.name;
  return id.replace(/_/g, " ");
}

function skillsForRoleFromMock(jobRoleId: string, language: string): SkillByJobRole[] {
  const role = JOB_ROLES.find((r) => r.id === jobRoleId);
  if (!role) return [];
  return role.requiredSkills.map((id) => ({ id, name: skillLabel(id, language) }));
}

export type ApiJobRole = {
  id: string;
  name: string;
  description: string;
};

export async function apiGetIndustries(language?: string): Promise<JobIndustry[]> {
  const lang = language || getLanguage();
  const cacheKey = `job-industries:hrm:${lang}`;

  const cached = getCache<JobIndustry[]>(cacheKey);
  if (cached?.length) return cached;

  try {
    const res = await http.get(endpoints.jobs.industries);
    const sorted = catalogRows(res.data)
      .map((row) => ({ id: row.id, name: detailText(row, "name", row.id) }))
      .filter((row) => row.id)
      .sort((a, b) => a.name.localeCompare(b.name));
    if (!sorted.length) return JOB_INDUSTRIES;
    setCache(cacheKey, sorted);
    return sorted;
  } catch (error) {
    console.error("Error fetching industries:", error);
    return JOB_INDUSTRIES;
  }
}

function fallbackRoles(categoryId: string): ApiJobRole[] {
  return JOB_ROLES.filter((role) => role.categoryId === categoryId).map((role) => ({
    id: role.id,
    name: role.name || role.nameKey,
    description: role.description || role.descriptionKey
  }));
}

export async function apiGetJobRolesByCategory(categoryId: string): Promise<ApiJobRole[]> {
  const cacheKey = `job-roles-by-category:hrm:${categoryId}`;

  const cached = getCache<ApiJobRole[]>(cacheKey);
  if (cached?.length) return cached;

  if (!categoryId) {
    throw new Error("Category ID is required");
  }

  try {
    const res = await http.get(endpoints.jobs.roles);
    const roles = catalogRows(res.data)
      .filter((row) => detailText(row, "categoryId") === categoryId)
      .map((row) => ({
        id: row.id,
        name: detailText(row, "name", row.id),
        description: detailText(row, "description", detailText(row, "descriptionKey"))
      }));
    if (!roles.length) return fallbackRoles(categoryId);
    setCache(cacheKey, roles);
    return roles;
  } catch (error) {
    console.error("Error fetching job roles by category:", error);
    return fallbackRoles(categoryId);
  }
}

export async function apiGetContractTypes(): Promise<{ id: string; nameKey: string }[]> {
  try {
    const res = await http.get(endpoints.jobs.contractTypes);
    const rows = catalogRows(res.data).map((row) => ({
      id: row.id,
      nameKey: detailText(row, "nameKey", detailText(row, "name", row.id))
    }));
    return rows.filter((row) => row.id);
  } catch (error) {
    console.error("Error fetching contract types:", error);
    return [];
  }
}

export type Certificate = {
  id: string;
  name: string;
};

export type CertificatesResponse = {
  correlationId: string;
  success: boolean;
  message: string;
  data: Certificate[];
  timestamp: number;
};

export async function apiGetCertificatesByJobRole(jobRoleId: string, _language: string): Promise<Certificate[]> {
  if (!jobRoleId) return [];
  try {
    const res = await http.get(endpoints.certificates.list);
    const mapped = catalogRows(res.data)
      .filter((row) => {
        const roleIds = detailStringArray(row, "roleIds");
        return roleIds.length === 0 || roleIds.includes(jobRoleId);
      })
      .map((row) => ({
        id: row.id,
        name: detailText(row, "name", row.id),
      }));
    if (mapped.length) return mapped;
  } catch (error) {
    console.error("Error fetching certificates:", error);
  }
  return jobApi.getCertificatesByRole(jobRoleId);
}

export async function apiGetJobDocumentsByRole(jobRoleId: string): Promise<Certificate[]> {
  if (!jobRoleId) return [];
  try {
    const res = await http.get(endpoints.jobs.jobDocuments);
    const mapped = catalogRows(res.data)
      .filter((row) => {
        const roleIds = detailStringArray(row, "roleIds");
        return roleIds.length === 0 || roleIds.includes(jobRoleId);
      })
      .map((row) => ({
        id: row.id,
        name: detailText(row, "name", row.id),
      }));
    if (mapped.length) return mapped;
  } catch (error) {
    console.error("Error fetching job documents:", error);
  }
  return jobApi.getDocumentsByRole(jobRoleId);
}

export type SkillByJobRole = {
  id: string;
  name: string;
};

export type SkillsByJobRoleResponse = {
  correlationId: string;
  success: boolean;
  message: string;
  data: SkillByJobRole[];
  timestamp: number;
};

export async function apiGetSkillsByJobRole(jobRoleId: string, language: string): Promise<SkillByJobRole[]> {
  if (!jobRoleId) return [];
  const lang = language || getLanguage();
  try {
    const [rolesRes, skillsRes] = await Promise.all([
      http.get(endpoints.jobs.roles),
      http.get(endpoints.skills.list),
    ]);
    const role = catalogRows(rolesRes.data).find((row) => row.id === jobRoleId);
    const requiredIds =
      detailStringArray(role, "requiredSkills").length > 0
        ? detailStringArray(role, "requiredSkills")
        : (JOB_ROLES.find((r) => r.id === jobRoleId)?.requiredSkills ?? []);
    const skillsById = new Map(
      catalogRows(skillsRes.data).map((row) => [row.id, detailText(row, "name", skillLabel(row.id, lang))])
    );
    const list = requiredIds.map((id) => ({
      id,
      name: skillsById.get(id) ?? skillLabel(id, lang),
    }));
    if (list.length) return list;
  } catch (error) {
    console.error("Error fetching skills by job role:", error);
  }
  return skillsForRoleFromMock(jobRoleId, lang);
}
