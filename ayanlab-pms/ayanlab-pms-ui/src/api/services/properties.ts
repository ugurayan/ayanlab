import type { Property, PropertyDetail } from "../../types/pms";
import { USE_MOCKS } from "../../env";
import { getPropertyDetail as mockGetPropertyDetail, listProperties as mockListProperties } from "../../mock/mockApi";
import { unwrapData } from "../apiEnvelope";
import { endpoints } from "../endpoints";
import { http } from "../pmsClient";

type ApiProperty = {
  id: string;
  organizationId: string;
  name: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  zipCode: string;
  totalUnits?: number;
};

function formatAddress(p: ApiProperty): string {
  const line2 = p.addressLine2 ? `, ${p.addressLine2}` : "";
  return `${p.addressLine1}${line2}, ${p.city}, ${p.state} ${p.zipCode}`;
}

function mapProperty(p: ApiProperty): Property {
  return {
    id: p.id,
    orgId: p.organizationId,
    name: p.name,
    mode: "RENTAL",
    address: formatAddress(p),
    unitCount: p.totalUnits ?? 1
  };
}

export async function listProperties(): Promise<Property[]> {
  if (USE_MOCKS) {
    return mockListProperties();
  }
  const res = await http.get(endpoints.pms.properties);
  const rows = unwrapData<ApiProperty[]>(res);
  return rows.map(mapProperty);
}

export async function getPropertyDetail(id: string): Promise<PropertyDetail | undefined> {
  if (USE_MOCKS) {
    return mockGetPropertyDetail(id);
  }
  const res = await http.get(`${endpoints.pms.properties}/${id}`);
  const row = unwrapData<ApiProperty>(res);
  const base = mapProperty(row);
  return {
    ...base,
    units: [],
    ownerNames: [],
    notes: [],
    files: []
  };
}