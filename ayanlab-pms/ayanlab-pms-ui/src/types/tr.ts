export type LatePenaltyRule = {
  ratePercentMonthly: number;
  graceDays: number;
  compound: boolean;
};

export type LatePenaltyExample = {
  principalCents: number;
  daysLate: number;
  penaltyCents: number;
};

export type MeterReading = {
  id: string;
  unitLabel: string;
  meterType: "WATER" | "ELECTRIC" | "GAS";
  previous: number;
  current: number;
};

export type MeterAllocation = {
  unitLabel: string;
  consumption: number;
  shareCents: number;
};

export type EnforcementCase = {
  id: string;
  ownerName: string;
  unitLabel: string;
  amountCents: number;
  status: "OPEN" | "LAWYER" | "CLOSED";
  openedAt: string;
  timeline: { at: string; note: string }[];
};

export type AtsDebtCode = {
  id: string;
  partyName: string;
  code10: string;
  unitLabel: string;
};