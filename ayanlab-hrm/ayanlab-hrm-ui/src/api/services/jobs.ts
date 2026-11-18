import { authHttp } from "../client";
import { endpoints } from "../endpoints";
import { getToken } from "../../state/authStorage";

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

export type IndustriesResponse = {
  correlationId: string;
  success: boolean;
  message: string;
  data: JobIndustry[];
};

export type ApiJobRole = {
  id: string;
  name: string;
  description: string;
};

export type JobRolesResponse = {
  correlationId: string;
  success: boolean;
  message: string;
  data: ApiJobRole[];
};

export async function apiGetIndustries(language?: string): Promise<JobIndustry[]> {
  const lang = language || getLanguage();
  const correlationId = generateCorrelationId();
  const cacheKey = `job-industries:${lang}`;

  const cached = getCache<JobIndustry[]>(cacheKey);
  if (cached) return cached;

  try {
    const res = await authHttp.get<IndustriesResponse>(
      `${endpoints.jobs.industries}?language=${lang}`,
      {
        headers: {
          'X-Correlation-ID': correlationId,
          'accept': 'application/json'
        }
      }
    );

    if (!res.data.success || !res.data.data) {
      throw new Error(res.data.message || "Failed to fetch industries");
    }

    // Always return industries sorted by name (ascending)
    const sorted = [...res.data.data].sort((a, b) => a.name.localeCompare(b.name));

    setCache(cacheKey, sorted);
    return sorted;
  } catch (error: any) {
    console.error("Error fetching industries:", error);
    if (error.response) {
      throw new Error(error.response.data?.message || "Failed to fetch industries");
    }
    throw new Error("Failed to fetch industries");
  }
}

export async function apiGetJobRolesByCategory(categoryId: string): Promise<ApiJobRole[]> {
  const correlationId = generateCorrelationId();
  const cacheKey = `job-roles-by-category:${categoryId}`;

  const cached = getCache<ApiJobRole[]>(cacheKey);
  if (cached) return cached;

  if (!categoryId) {
    throw new Error("Category ID is required");
  }

  try {
    const res = await authHttp.get<JobRolesResponse>(
      endpoints.jobs.rolesByCategory(categoryId),
      {
        headers: {
          'X-Correlation-ID': correlationId,
          'accept': 'application/json'
        }
      }
    );

    if (!res.data.success || !res.data.data) {
      throw new Error(res.data.message || "Failed to fetch job roles");
    }

    setCache(cacheKey, res.data.data);
    return res.data.data;
  } catch (error: any) {
    console.error("Error fetching job roles by category:", error);
    if (error.response) {
      throw new Error(error.response.data?.message || "Failed to fetch job roles");
    }
    throw new Error("Failed to fetch job roles");
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

export async function apiGetCertificatesByJobRole(
  jobRoleId: string,
  language: string
): Promise<Certificate[]> {
  const correlationId = generateCorrelationId();
  const token = getToken();
  const lang = language || getLanguage();

  if (!token) {
    throw new Error("Authentication token not found.");
  }

  if (!jobRoleId) {
    throw new Error("Job Role ID is required");
  }

  try {
    const response = await authHttp.get<CertificatesResponse>(
      endpoints.certificates.listByJobRole(jobRoleId, lang),
      {
        headers: {
          "X-Correlation-ID": correlationId,
          accept: "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!response.data.success || !response.data.data) {
      throw new Error(response.data.message || "Failed to fetch certificates");
    }

    return response.data.data;
  } catch (error: any) {
    console.error("Error fetching certificates by job role:", error);
    if (error.response) {
      throw new Error(error.response.data?.message || "Failed to fetch certificates");
    }
    throw new Error("Failed to fetch certificates");
  }
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

export async function apiGetSkillsByJobRole(
  jobRoleId: string,
  language: string
): Promise<SkillByJobRole[]> {
  const correlationId = generateCorrelationId();
  const token = getToken();

  if (!token) {
    throw new Error("Authentication token not found.");
  }

  if (!jobRoleId) {
    throw new Error("Job Role ID is required");
  }

  const lang = language || getLanguage();

  try {
    const response = await authHttp.get<SkillsByJobRoleResponse>(
      endpoints.skills.listByJobRole(jobRoleId, lang),
      {
        headers: {
          "X-Correlation-ID": correlationId,
          accept: "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!response.data.success || !response.data.data) {
      throw new Error(response.data.message || "Failed to fetch skills");
    }

    return response.data.data;
  } catch (error: any) {
    console.error("Error fetching skills by job role:", error);
    if (error.response) {
      throw new Error(error.response.data?.message || "Failed to fetch skills");
    }
    throw new Error("Failed to fetch skills");
  }
}
