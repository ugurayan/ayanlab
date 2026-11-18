import { authHttp } from "../client";
import { endpoints } from "../endpoints";

const generateCorrelationId = () => `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;

export type SkillApi = {
  id: string;
  name: string;
  description: string | null;
  isPredefined: boolean;
  jobRoleId: string;
  isSystemDefined: boolean;
  isApproved: boolean;
  approvedBy: string;
  approvedAt: number;
  createdAt: string;
  updatedAt: string | null;
  createdBy: string;
  updatedBy: string | null;
  createdIp: string;
  updatedIp: string | null;
  deleted: boolean;
  version: number;
};

export type Skill = {
  id: string;
  name: string;
  description: string | null;
  createdAt: number;
  updatedAt: number;
  createdBy: string;
  updatedBy: string | null;
};

export type SkillPage = {
  items: Skill[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
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
  data: PageMeta<SkillApi>;
  timestamp: number;
};

type MutateResponse<T> = {
  correlationId: string;
  success: boolean;
  message: string;
  data: T;
  timestamp: number;
};

const mapApiToDomain = (api: SkillApi): Skill => ({
  id: api.id,
  name: api.name,
  description: api.description,
  createdAt: new Date(api.createdAt).getTime(),
  updatedAt: api.updatedAt ? new Date(api.updatedAt).getTime() : new Date(api.createdAt).getTime(),
  createdBy: api.createdBy,
  updatedBy: api.updatedBy,
});

export type CreateSkillDto = {
  name: string;
  description?: string;
};

export type UpdateSkillDto = Partial<CreateSkillDto>;

export async function apiListSkills(params: {
  page: number;
  size: number;
  sortBy?: string;
  order?: "asc" | "desc";
}): Promise<SkillPage> {
  const correlationId = generateCorrelationId();
  const { page, size, sortBy = "name", order = "asc" } = params;

  const res = await authHttp.get<ListResponse>(endpoints.skills.list, {
    params: { page, size, sortBy, order },
    headers: {
      "X-Correlation-ID": correlationId,
      accept: "application/json",
    },
  });

  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message || "Failed to fetch skills");
  }

  return {
    items: res.data.data.content.map(mapApiToDomain),
    totalElements: res.data.data.totalElements,
    totalPages: res.data.data.totalPages,
    page: res.data.data.number,
    size: res.data.data.size,
  };
}

export async function apiCreateSkill(data: CreateSkillDto): Promise<Skill> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.post<MutateResponse<SkillApi>>(
    endpoints.skills.create,
    data,
    {
      headers: {
        "X-Correlation-ID": correlationId,
        accept: "application/json",
        "Content-Type": "application/json",
      },
    }
  );

  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message || "Failed to create skill");
  }

  return mapApiToDomain(res.data.data);
}

export async function apiUpdateSkill(
  id: string,
  data: UpdateSkillDto
): Promise<Skill> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.put<MutateResponse<SkillApi>>(
    endpoints.skills.update(id),
    { id, ...data },
    {
      headers: {
        "X-Correlation-ID": correlationId,
        accept: "application/json",
        "Content-Type": "application/json",
      },
    }
  );

  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message || "Failed to update skill");
  }

  return mapApiToDomain(res.data.data);
}

export async function apiDeleteSkill(id: string): Promise<string> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.delete<{ correlationId: string; success: boolean; message: string; data: string; timestamp: number }>(
    endpoints.skills.delete(id),
    {
      headers: {
        "X-Correlation-ID": correlationId,
        accept: "application/json",
      },
    }
  );

  if (!res.data.success) {
    throw new Error(res.data.message || "Failed to delete skill");
  }

  return res.data.data;
}

