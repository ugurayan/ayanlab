import type { ArchitecturalRequest } from "../../types/pms";
import { USE_MOCKS } from "../../env";
import {
  getArchitectural as mockGetArchitectural,
  listArchitectural as mockListArchitectural,
  updateArchitecturalStatus as mockUpdateArchitecturalStatus
} from "../../mock/mockApi";
import { unwrapData } from "../apiEnvelope";
import { endpoints } from "../endpoints";
import { http } from "../pmsClient";

type ApiArchitectural = {
  id: string;
  associationId: string;
  unitLabel?: string;
  title: string;
  description?: string;
  status?: string;
  submittedAt?: string;
  createdAt?: string;
  committeeNote?: string;
};

function mapArchitectural(row: ApiArchitectural): ArchitecturalRequest {
  let status: ArchitecturalRequest["status"] = "SUBMITTED";
  if (row.status === "COMMITTEE" || row.status === "UNDER_REVIEW") status = "COMMITTEE";
  else if (row.status === "APPROVED") status = "APPROVED";
  else if (row.status === "DENIED") status = "DENIED";
  return {
    id: row.id,
    associationId: row.associationId,
    unitLabel: row.unitLabel ?? "—",
    title: row.title,
    description: row.description ?? row.title,
    status,
    submittedAt: row.submittedAt ?? row.createdAt ?? new Date().toISOString(),
    committeeNote: row.committeeNote
  };
}

export async function listArchitectural(): Promise<ArchitecturalRequest[]> {
  if (USE_MOCKS) return mockListArchitectural();
  const res = await http.get(endpoints.pms.architecturalRequests);
  return unwrapData<ApiArchitectural[]>(res).map(mapArchitectural);
}

export async function getArchitectural(id: string): Promise<ArchitecturalRequest | undefined> {
  if (USE_MOCKS) return mockGetArchitectural(id);
  const res = await http.get(`${endpoints.pms.architecturalRequests}/${id}`);
  return mapArchitectural(unwrapData<ApiArchitectural>(res));
}

export async function updateArchitecturalStatus(
  id: string,
  status: ArchitecturalRequest["status"],
  committeeNote?: string
): Promise<ArchitecturalRequest> {
  if (USE_MOCKS) return mockUpdateArchitecturalStatus(id, status, committeeNote);
  const res = await http.put(`${endpoints.pms.architecturalRequests}/${id}`, {
    status,
    committeeNote
  });
  return mapArchitectural(unwrapData<ApiArchitectural>(res));
}