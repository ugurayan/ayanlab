import { authHttp } from "../client";
import { endpoints } from "../endpoints";
import type { JobRole, CreateJobRoleDto, UpdateJobRoleDto } from "../../types/jobRoles";

const generateCorrelationId = () => `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;

type JobRoleApi = {
  id: string;
  name: string;
  description: string;
  categoryId: string;
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
  data: PageMeta<JobRoleApi>;
  timestamp: number;
};

type MutateResponse<T> = {
  correlationId: string;
  success: boolean;
  message: string;
  data: T;
  timestamp: number;
};

const mapApiToDomain = (api: JobRoleApi): JobRole => ({
  id: api.id,
  name: api.name,
  description: api.description,
  categoryId: api.categoryId,
  requiredSkills: [], // Backend does not yet expose skills on this endpoint
  isActive: true, // Treat created/updated roles as active for now
  createdAt: new Date(api.createdAt).getTime(),
  updatedAt: new Date(api.updatedAt).getTime(),
  createdBy: api.createdBy,
  updatedBy: api.updatedBy ?? ""
});

export type JobRolePage = {
  items: JobRole[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
};

export async function apiListJobRoles(params: {
  page: number;
  size: number;
  sortBy?: string;
  order?: "asc" | "desc";
}): Promise<JobRolePage> {
  const correlationId = generateCorrelationId();
  const { page, size, sortBy = "name", order = "asc" } = params;

  const res = await authHttp.get<ListResponse>(endpoints.jobRoles.list, {
    params: { page, size, sortBy, order },
    headers: {
      "X-Correlation-ID": correlationId,
      accept: "application/json"
    }
  });

  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message || "Failed to fetch job roles");
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

export async function apiCreateJobRole(payload: CreateJobRoleDto): Promise<JobRole> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.post<MutateResponse<JobRoleApi>>(endpoints.jobRoles.create, payload, {
    headers: {
      "X-Correlation-ID": correlationId,
      accept: "application/json",
      "Content-Type": "application/json"
    }
  });

  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message || "Failed to create job role");
  }

  return mapApiToDomain(res.data.data);
}

export async function apiUpdateJobRole(id: string, payload: UpdateJobRoleDto): Promise<JobRole> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.put<MutateResponse<JobRoleApi>>(endpoints.jobRoles.update(id), payload, {
    headers: {
      "X-Correlation-ID": correlationId,
      accept: "application/json",
      "Content-Type": "application/json"
    }
  });

  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message || "Failed to update job role");
  }

  return mapApiToDomain(res.data.data);
}

export async function apiDeleteJobRole(id: string): Promise<string> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.delete<MutateResponse<string>>(endpoints.jobRoles.delete(id), {
    headers: {
      "X-Correlation-ID": correlationId,
      accept: "application/json"
    }
  });

  if (!res.data.success) {
    throw new Error(res.data.message || "Failed to delete job role");
  }

  return res.data.data;
}


