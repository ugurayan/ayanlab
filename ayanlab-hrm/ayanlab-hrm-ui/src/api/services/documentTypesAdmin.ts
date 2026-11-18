import { authHttp } from "../client";
import { endpoints } from "../endpoints";

const generateCorrelationId = () =>
  `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;

export type DocumentTypeApi = {
  id: string;
  name: string;
  description: string | null;
  jobRoleId: string | null;
  isSystemDefined: boolean;
  isApproved: boolean;
  approvedBy: string | null;
  approvedAt: number | null;
  createdAt: string;
  updatedAt: string | null;
  createdBy: string;
  updatedBy: string | null;
  createdIp: string;
  updatedIp: string | null;
  deleted: boolean;
  version: number;
};

export type DocumentType = {
  id: string;
  name: string;
  description: string | null;
  createdAt: number;
  updatedAt: number;
  createdBy: string;
  updatedBy: string | null;
};

export type DocumentTypePage = {
  items: DocumentType[];
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
  data: PageMeta<DocumentTypeApi>;
  timestamp: number;
};

type MutateResponse<T> = {
  correlationId: string;
  success: boolean;
  message: string;
  data: T;
  timestamp: number;
};

const mapApiToDomain = (api: DocumentTypeApi): DocumentType => ({
  id: api.id,
  name: api.name,
  description: api.description,
  createdAt: new Date(api.createdAt).getTime(),
  updatedAt: api.updatedAt
    ? new Date(api.updatedAt).getTime()
    : new Date(api.createdAt).getTime(),
  createdBy: api.createdBy,
  updatedBy: api.updatedBy,
});

export type CreateDocumentTypeDto = {
  name: string;
  description?: string;
};

export type UpdateDocumentTypeDto = Partial<CreateDocumentTypeDto>;

export async function apiListDocumentTypes(params: {
  page: number;
  size: number;
  sortBy?: string;
  order?: "asc" | "desc";
}): Promise<DocumentTypePage> {
  const correlationId = generateCorrelationId();
  const { page, size, sortBy = "name", order = "asc" } = params;

  const res = await authHttp.get<ListResponse>(endpoints.documentTypes.list, {
    params: { page, size, sortBy, order },
    headers: {
      "X-Correlation-ID": correlationId,
      accept: "application/json",
    },
  });

  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message || "Failed to fetch document types");
  }

  return {
    items: res.data.data.content.map(mapApiToDomain),
    totalElements: res.data.data.totalElements,
    totalPages: res.data.data.totalPages,
    page: res.data.data.number,
    size: res.data.data.size,
  };
}

export async function apiCreateDocumentType(
  data: CreateDocumentTypeDto
): Promise<DocumentType> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.post<MutateResponse<DocumentTypeApi>>(
    endpoints.documentTypes.create,
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
    throw new Error(res.data.message || "Failed to create document type");
  }

  return mapApiToDomain(res.data.data);
}

export async function apiUpdateDocumentType(
  id: string,
  data: UpdateDocumentTypeDto
): Promise<DocumentType> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.put<MutateResponse<DocumentTypeApi>>(
    endpoints.documentTypes.update(id),
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
    throw new Error(res.data.message || "Failed to update document type");
  }

  return mapApiToDomain(res.data.data);
}

export async function apiDeleteDocumentType(id: string): Promise<string> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.delete<{
    correlationId: string;
    success: boolean;
    message: string;
    data: string;
    timestamp: number;
  }>(endpoints.documentTypes.delete(id), {
    headers: {
      "X-Correlation-ID": correlationId,
      accept: "application/json",
    },
  });

  if (!res.data.success) {
    throw new Error(res.data.message || "Failed to delete document type");
  }

  return res.data.data;
}


