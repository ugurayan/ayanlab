import { authHttp } from "../client";
import { endpoints } from "../endpoints";

const generateCorrelationId = () => `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;

export type CertificateApi = {
  id: string;
  name: string;
  description: string | null;
  issuingOrganization: string | null;
  expirationDate: string | null;
  expirationInDays: number;
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

export type Certificate = {
  id: string;
  name: string;
  description: string | null;
  createdAt: number;
  updatedAt: number;
  createdBy: string;
  updatedBy: string | null;
};

export type CertificatePage = {
  items: Certificate[];
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
  data: PageMeta<CertificateApi>;
  timestamp: number;
};

type MutateResponse<T> = {
  correlationId: string;
  success: boolean;
  message: string;
  data: T;
  timestamp: number;
};

const mapApiToDomain = (api: CertificateApi): Certificate => ({
  id: api.id,
  name: api.name,
  description: api.description,
  createdAt: new Date(api.createdAt).getTime(),
  updatedAt: api.updatedAt ? new Date(api.updatedAt).getTime() : new Date(api.createdAt).getTime(),
  createdBy: api.createdBy,
  updatedBy: api.updatedBy,
});

export type CreateCertificateDto = {
  name: string;
  description?: string;
};

export type UpdateCertificateDto = Partial<CreateCertificateDto>;

export async function apiListCertificates(params: {
  page: number;
  size: number;
  sortBy?: string;
  order?: "asc" | "desc";
}): Promise<CertificatePage> {
  const correlationId = generateCorrelationId();
  const { page, size, sortBy = "name", order = "asc" } = params;

  const res = await authHttp.get<ListResponse>(endpoints.certificates.list, {
    params: { page, size, sortBy, order },
    headers: {
      "X-Correlation-ID": correlationId,
      accept: "application/json",
    },
  });

  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message || "Failed to fetch certificates");
  }

  return {
    items: res.data.data.content.map(mapApiToDomain),
    totalElements: res.data.data.totalElements,
    totalPages: res.data.data.totalPages,
    page: res.data.data.number,
    size: res.data.data.size,
  };
}

export async function apiCreateCertificate(data: CreateCertificateDto): Promise<Certificate> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.post<MutateResponse<CertificateApi>>(
    endpoints.certificates.create,
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
    throw new Error(res.data.message || "Failed to create certificate");
  }

  return mapApiToDomain(res.data.data);
}

export async function apiUpdateCertificate(
  id: string,
  data: UpdateCertificateDto
): Promise<Certificate> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.put<MutateResponse<CertificateApi>>(
    endpoints.certificates.update(id),
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
    throw new Error(res.data.message || "Failed to update certificate");
  }

  return mapApiToDomain(res.data.data);
}

export async function apiDeleteCertificate(id: string): Promise<string> {
  const correlationId = generateCorrelationId();

  const res = await authHttp.delete<{ correlationId: string; success: boolean; message: string; data: string; timestamp: number }>(
    endpoints.certificates.delete(id),
    {
      headers: {
        "X-Correlation-ID": correlationId,
        accept: "application/json",
      },
    }
  );

  if (!res.data.success) {
    throw new Error(res.data.message || "Failed to delete certificate");
  }

  return res.data.data;
}

