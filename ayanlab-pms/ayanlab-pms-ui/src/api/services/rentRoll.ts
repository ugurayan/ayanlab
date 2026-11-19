import type { RentRollRow } from "../../types/pms";
import { USE_MOCKS } from "../../env";
import { listRentRoll as mockListRentRoll } from "../../mock/mockApi";
import { unwrapData } from "../apiEnvelope";
import { endpoints } from "../endpoints";
import { http } from "../pmsClient";

type ApiUnit = {
  id: string;
  propertyId: string;
  unitNumber: string;
  status?: string;
  marketRentCents?: number;
};

type ApiProperty = { id: string; name: string };

export async function listRentRoll(): Promise<RentRollRow[]> {
  if (USE_MOCKS) return mockListRentRoll();
  const [unitsRes, propsRes, leasesRes] = await Promise.all([
    http.get(endpoints.pms.propertyUnits),
    http.get(endpoints.pms.properties),
    http.get(endpoints.pms.leases)
  ]);
  const units = unwrapData<ApiUnit[]>(unitsRes);
  const props = unwrapData<ApiProperty[]>(propsRes);
  const propNames = new Map(props.map((p) => [p.id, p.name]));
  const leases = unwrapData<{ id: string; unitId: string; status?: string; endDate?: string; monthlyRentCents?: number }[]>(
    leasesRes
  );
  const leaseByUnit = new Map(leases.filter((l) => l.status !== "TERMINATED").map((l) => [l.unitId, l]));

  return units.map((u) => {
    const lease = leaseByUnit.get(u.id);
    let unitStatus: RentRollRow["unitStatus"] = "VACANT";
    if (u.status === "OCCUPIED") unitStatus = "OCCUPIED";
    else if (u.status === "NOTICE") unitStatus = "NOTICE";
    return {
      unitId: u.id,
      propertyId: u.propertyId,
      propertyName: propNames.get(u.propertyId) ?? "—",
      unitLabel: u.unitNumber,
      unitStatus,
      leaseId: lease?.id,
      leaseStatus: lease ? "ACTIVE" : undefined,
      rentCents: lease?.monthlyRentCents ?? u.marketRentCents ?? 0,
      leaseEnd: lease?.endDate
    };
  });
}