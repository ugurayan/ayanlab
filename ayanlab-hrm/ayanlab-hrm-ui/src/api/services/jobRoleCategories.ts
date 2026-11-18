import { authHttp } from "../client";
import { endpoints } from "../endpoints";
import type { JobRoleCategory, CreateJobRoleCategoryDto, UpdateJobRoleCategoryDto } from "../../types/jobRoles";

const generateCorrelationId = () => `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;

type JobRoleCategoryApi = {
  id: string;
  name: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy: string | null;
};

type PageMeta<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
};

type ListResponse = {
  correlationId: string;
  success: boolean;
  message: string;
  data: PageMeta<JobRoleCategoryApi>;
  timestamp: number;
};

type MutateResponse = {
  correlationId: string;
  success: boolean;
  message: string;
  data: JobRoleCategoryApi;
  timestamp: number;
};

export type JobRoleCategoryPage = {
  items: JobRoleCategory[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
};

const mapApiToDomain = (api: JobRoleCategoryApi): JobRoleCategory => ({
  id: api.id,
  name: api.name,
  description: api.description,
  createdAt: new Date(api.createdAt).getTime(),
  updatedAt: new Date(api.updatedAt).getTime(),
  createdBy: api.createdBy,
  updatedBy: api.updatedBy ?? ""
});

export async function apiListJobRoleCategories(params: {
  page: number;
  size: number;
  sortBy?: string;
  order?: "asc" | "desc";
}): Promise<JobRoleCategoryPage> {
  const correlationId = generateCorrelationId();
  const { page, size, sortBy = "name", order = "asc" } = params;

  const res = await authHttp.get<ListResponse>(endpoints.jobCategories.listAll, {
    params: { page, size, sortBy, order },
    headers: {
      "X-Correlation-ID": correlationId,
      accept: "application/json"
    }
  });

  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message || "Failed to fetch job role categories");
  }

  const meta = res.data.data;
  const items = meta.content.map(mapApiToDomain);

  return {
    items,
    totalElements: meta.totalElements,
    totalPages: meta.totalPages,
    page: meta.number,
    size: meta.size
  };
}

export async function apiCreateJobRoleCategory(
  payload: CreateJobRoleCategoryDto
): Promise<JobRoleCategory> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.post<MutateResponse>(endpoints.jobCategories.create, payload, {
    headers: {
      "X-Correlation-ID": correlationId,
      accept: "application/json",
      "Content-Type": "application/json"
    }
  });

  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message || "Failed to create job role category");
  }

  return mapApiToDomain(res.data.data);
}

export async function apiUpdateJobRoleCategory(
  id: string,
  payload: UpdateJobRoleCategoryDto
): Promise<JobRoleCategory> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.put<MutateResponse>(endpoints.jobCategories.update(id), payload, {
    headers: {
      "X-Correlation-ID": correlationId,
      accept: "application/json",
      "Content-Type": "application/json"
    }
  });

  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message || "Failed to update job role category");
  }

  return mapApiToDomain(res.data.data);
}

export async function apiDeleteJobRoleCategory(id: string): Promise<string> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.delete<MutateResponse>(endpoints.jobCategories.delete(id), {
    headers: {
      "X-Correlation-ID": correlationId,
      accept: "application/json"
    }
  });

  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message || "Failed to delete job role category");
  }

  // API returns the deleted id in data
  return typeof res.data.data === "string" ? res.data.data : id;
}


