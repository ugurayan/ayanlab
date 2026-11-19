import type { AtsDebtCode, EnforcementCase, LatePenaltyRule, MeterReading } from "../../types/tr";
import { USE_MOCKS } from "../../env";
import {
  getLatePenaltyRule as mockGetLatePenaltyRule,
  listAtsCodes as mockListAtsCodes,
  listEnforcementCases as mockListEnforcementCases,
  listMeterReadings as mockListMeterReadings
} from "../../mock/mockApi";
import { unwrapData } from "../apiEnvelope";
import { endpoints } from "../endpoints";
import { http } from "../pmsClient";

type ApiLatePenaltyRule = {
  id?: string;
  ratePercentMonthly?: number;
  rateBps?: number;
  graceDays?: number;
  compound?: boolean;
  name?: string;
};

type ApiMeterReading = {
  id: string;
  unitLabel: string;
  meterType: string;
  previousValue?: number;
  currentValue?: number;
  previous?: number;
  current?: number;
};

type ApiEnforcementCase = {
  id: string;
  ownerName: string;
  unitLabel: string;
  amountCents: number;
  status: string;
  openedAt?: string;
  createdAt?: string;
  timelineJson?: string;
};

type ApiDebtCode = {
  id: string;
  partyName: string;
  code10: string;
  unitLabel: string;
};

function mapMeter(row: ApiMeterReading): MeterReading {
  return {
    id: row.id,
    unitLabel: row.unitLabel,
    meterType: row.meterType as MeterReading["meterType"],
    previous: row.previousValue ?? row.previous ?? 0,
    current: row.currentValue ?? row.current ?? 0
  };
}

function mapEnforcement(row: ApiEnforcementCase): EnforcementCase {
  let timeline: EnforcementCase["timeline"] = [];
  if (row.timelineJson) {
    try {
      timeline = JSON.parse(row.timelineJson) as EnforcementCase["timeline"];
    } catch {
      timeline = [];
    }
  }
  return {
    id: row.id,
    ownerName: row.ownerName,
    unitLabel: row.unitLabel,
    amountCents: row.amountCents,
    status: row.status as EnforcementCase["status"],
    openedAt: row.openedAt ?? row.createdAt ?? new Date().toISOString(),
    timeline
  };
}

export async function getLatePenaltyRule(): Promise<LatePenaltyRule> {
  if (USE_MOCKS) {
    return mockGetLatePenaltyRule();
  }
  const res = await http.get(endpoints.pms.latePenaltyRules);
  const rows = unwrapData<ApiLatePenaltyRule[]>(res);
  const first = rows[0];
  if (!first) {
    return { ratePercentMonthly: 1.5, graceDays: 5, compound: false };
  }
  const ratePercentMonthly =
    first.ratePercentMonthly ?? (first.rateBps != null ? first.rateBps / 100 : 1.5);
  return {
    ratePercentMonthly,
    graceDays: first.graceDays ?? 5,
    compound: first.compound ?? false
  };
}

export async function listMeterReadings(): Promise<MeterReading[]> {
  if (USE_MOCKS) {
    return mockListMeterReadings();
  }
  const res = await http.get(endpoints.pms.meterReadings);
  return unwrapData<ApiMeterReading[]>(res).map(mapMeter);
}

export async function listEnforcementCases(): Promise<EnforcementCase[]> {
  if (USE_MOCKS) {
    return mockListEnforcementCases();
  }
  const res = await http.get(endpoints.pms.enforcementCases);
  return unwrapData<ApiEnforcementCase[]>(res).map(mapEnforcement);
}

export async function listAtsCodes(): Promise<AtsDebtCode[]> {
  if (USE_MOCKS) {
    return mockListAtsCodes();
  }
  const res = await http.get(endpoints.pms.debtInquiryCodes);
  return unwrapData<ApiDebtCode[]>(res);
}