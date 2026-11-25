import { http } from "../client";
import { unwrapHrmData } from "../hrmApi";

export type OrganizationProfile = {
  id: string;
  platformOrganizationId: string;
  name: string;
  email: string;
  phone: string;
  timezone: string;
  currency: string;
};

export type LegalEntity = {
  id: string;
  platformOrganizationId: string;
  legalName: string;
  entityType: string;
  vatNumber: string;
  email: string;
  phone: string;
};

export type Brand = {
  id: string;
  platformOrganizationId: string;
  name: string;
  area: string;
};

export type WorkLocation = {
  id: string;
  referenceId: string;
  referenceEntity: string;
  placeId?: string;
  name: string;
  formattedAddress: string;
  street?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
  latitude?: number;
  longitude?: number;
  notes?: string;
  timezone?: string;
};

export type ShiftRequirement = {
  kind: string;
  skillId?: string;
  language?: string;
  level?: string;
  certificateId?: string;
  documentId?: string;
  contactId?: string;
  customName?: string;
};

export type ShiftTemplate = {
  id: string;
  platformOrganizationId: string;
  locationId: string;
  name: string;
  staffingType: string;
  roleId: string;
  description: string;
  recurring: boolean;
  daysOfWeek?: string;
  startTime: string;
  endTime: string;
  unpaidBreakMinutes: number;
  wageType: string;
  wageAmountMinor: number;
  currency: string;
  employeesNeeded: number;
  staffSelectionType: string;
  requirements?: ShiftRequirement[];
};

export type ShiftInstance = {
  id: string;
  platformOrganizationId: string;
  templateId?: string;
  locationId: string;
  roleId: string;
  name: string;
  description: string;
  startsAt: string;
  endsAt: string;
  unpaidBreakMinutes: number;
  wageType: string;
  wageAmountMinor: number;
  currency: string;
  employeesNeeded: number;
  staffSelectionType: string;
  status: string;
};

async function list<T>(path: string) {
  const res = await http.get(path);
  return unwrapHrmData<T[]>(res.data);
}

function normalize(body: unknown) {
  if (body == null || typeof body !== "object") return body;
  const next: Record<string, unknown> = { ...(body as Record<string, unknown>) };
  for (const [key, value] of Object.entries(next)) {
    if (typeof value === "string" && value.trim() !== "" && /Minutes|Amount|Needed|latitude|longitude/.test(key) && !Number.isNaN(Number(value))) {
      next[key] = Number(value);
    }
    if ((key === "startsAt" || key === "endsAt") && typeof value === "string" && /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}$/.test(value)) {
      next[key] = new Date(value).toISOString();
    }
    if (key === "recurring" && typeof value === "string") {
      next[key] = value === "true";
    }
  }
  return next;
}

async function save<T>(path: string, body: unknown, id?: string) {
  const payload = normalize(body);
  const res = id ? await http.put(`${path}/${id}`, payload) : await http.post(path, payload);
  return unwrapHrmData<T>(res.data);
}

export const staffingApi = {
  listOrgs: () => list<OrganizationProfile>("/organizations"),
  saveOrg: (body: Partial<OrganizationProfile>, id?: string) => save<OrganizationProfile>("/organizations", body, id),
  deleteOrg: (id: string) => http.delete(`/organizations/${id}`),
  listLegal: () => list<LegalEntity>("/legal-entities"),
  saveLegal: (body: Partial<LegalEntity>, id?: string) => save<LegalEntity>("/legal-entities", body, id),
  deleteLegal: (id: string) => http.delete(`/legal-entities/${id}`),
  listBrands: () => list<Brand>("/brands"),
  saveBrand: (body: Partial<Brand>, id?: string) => save<Brand>("/brands", body, id),
  deleteBrand: (id: string) => http.delete(`/brands/${id}`),
  listLocations: (referenceEntity?: string, referenceId?: string) => {
    const params = new URLSearchParams();
    if (referenceEntity && referenceId) {
      params.set("referenceEntity", referenceEntity);
      params.set("referenceId", referenceId);
    }
    const query = params.toString();
    return list<WorkLocation>(`/locations${query ? `?${query}` : ""}`);
  },
  saveLocation: (body: Partial<WorkLocation>, id?: string) => save<WorkLocation>("/locations", body, id),
  deleteLocation: (id: string) => http.delete(`/locations/${id}`),
  listTemplates: () => list<ShiftTemplate>("/shift-templates"),
  saveTemplate: (body: Partial<ShiftTemplate>, id?: string) => save<ShiftTemplate>("/shift-templates", body, id),
  deleteTemplate: (id: string) => http.delete(`/shift-templates/${id}`),
  generate: (id: string, body: { dates?: string[]; from?: string; to?: string }) =>
    http.post(`/shift-templates/${id}/instances`, body).then((res) => unwrapHrmData<ShiftInstance[]>(res.data)),
  listShifts: () => list<ShiftInstance>("/shifts"),
  saveShift: (body: Partial<ShiftInstance>, id?: string) => save<ShiftInstance>("/shifts", body, id),
  deleteShift: (id: string) => http.delete(`/shifts/${id}`)
};

export function apiErrorMessage(error: unknown) {
  const err = error as { response?: { data?: { message?: string } }; message?: string };
  return err.response?.data?.message || err.message || "Request failed";
}
