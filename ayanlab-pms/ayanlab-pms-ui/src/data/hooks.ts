import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchDashboardSummary } from "../api/services/dashboard";
import { getPropertyDetail, listProperties } from "../api/services/properties";
import {
  getArchitectural,
  listArchitectural,
  updateArchitecturalStatus
} from "../api/services/associations";
import { listRentRoll } from "../api/services/rentRoll";
import {
  getViolation,
  listBalances,
  listLeases,
  listTenants,
  listViolations,
  updateViolationStatus
} from "../api/services/rentals";
import {
  getLatePenaltyRule,
  listAtsCodes,
  listEnforcementCases,
  listMeterReadings
} from "../api/services/trModules";
import { USE_MOCKS } from "../env";
import {
  addGlTransaction,
  approveEft,
  computeLatePenaltyPreview,
  createProperty,
  getLease,
  getPortalOwnerPreview,
  getPortalTenantPreview,
  getResidentCenterBranding,
  getTenant,
  listBankAccounts,
  listBankLines,
  listBills,
  listBudget,
  listChartOfAccounts,
  listEftApprovals,
  listGlTransactions,
  listRecurring,
  listRentalOwners,
  matchBankLine,
  payBill,
  previewMeterAllocation,
  regenerateAtsCode,
  saveLatePenaltyRule,
  saveLease,
  saveResidentCenterBranding,
  type LeaseInput,
  type PropertyInput
} from "../mock/mockApi";
import type { LatePenaltyRule } from "../types/tr";

export function useDashboardSummary() {
  return useQuery({ queryKey: ["dashboard", USE_MOCKS], queryFn: fetchDashboardSummary });
}

export function useProperties() {
  return useQuery({ queryKey: ["properties", USE_MOCKS], queryFn: listProperties });
}

export function usePropertyDetail(id: string) {
  return useQuery({
    queryKey: ["property", id, USE_MOCKS],
    queryFn: () => getPropertyDetail(id),
    enabled: !!id
  });
}

export function useCreateProperty() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (input: PropertyInput) => createProperty(input),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["properties"] })
  });
}

export function useRentRoll() {
  return useQuery({ queryKey: ["rentRoll", USE_MOCKS], queryFn: listRentRoll });
}

export function useTenants() {
  return useQuery({ queryKey: ["tenants", USE_MOCKS], queryFn: listTenants });
}

export function useTenant(id: string) {
  return useQuery({ queryKey: ["tenant", id], queryFn: () => getTenant(id), enabled: !!id });
}

export function useRentalOwners() {
  return useQuery({ queryKey: ["rentalOwners"], queryFn: listRentalOwners });
}

export function useBalances() {
  return useQuery({ queryKey: ["balances", USE_MOCKS], queryFn: listBalances });
}

export function useLeases() {
  return useQuery({ queryKey: ["leases", USE_MOCKS], queryFn: listLeases });
}

export function useLease(id: string | undefined) {
  return useQuery({
    queryKey: ["lease", id],
    queryFn: () => getLease(id!),
    enabled: !!id && id !== "new"
  });
}

export function useSaveLease() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, input }: { id: string | "new"; input: LeaseInput }) => saveLease(id, input),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["leases"] });
      qc.invalidateQueries({ queryKey: ["rentRoll"] });
      qc.invalidateQueries({ queryKey: ["dashboard"] });
    }
  });
}

export function useViolations() {
  return useQuery({ queryKey: ["violations", USE_MOCKS], queryFn: listViolations });
}

export function useViolation(id: string) {
  return useQuery({
    queryKey: ["violation", id, USE_MOCKS],
    queryFn: () => getViolation(id),
    enabled: !!id
  });
}

export function useUpdateViolation() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, status }: { id: string; status: Parameters<typeof updateViolationStatus>[1] }) =>
      updateViolationStatus(id, status),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: ["violations"] });
      qc.invalidateQueries({ queryKey: ["violation", id] });
    }
  });
}

export function useArchitecturalList() {
  return useQuery({ queryKey: ["architectural", USE_MOCKS], queryFn: listArchitectural });
}

export function useArchitectural(id: string) {
  return useQuery({
    queryKey: ["architectural", id, USE_MOCKS],
    queryFn: () => getArchitectural(id),
    enabled: !!id
  });
}

export function useUpdateArchitectural() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      id,
      status,
      committeeNote
    }: {
      id: string;
      status: Parameters<typeof updateArchitecturalStatus>[1];
      committeeNote?: string;
    }) => updateArchitecturalStatus(id, status, committeeNote),
    onSuccess: (_, { id }) => {
      qc.invalidateQueries({ queryKey: ["architectural"] });
      qc.invalidateQueries({ queryKey: ["architectural", id] });
    }
  });
}

export function usePortalTenant() {
  return useQuery({ queryKey: ["portalTenant"], queryFn: getPortalTenantPreview });
}

export function usePortalOwner() {
  return useQuery({ queryKey: ["portalOwner"], queryFn: getPortalOwnerPreview });
}

export function useResidentBranding() {
  return useQuery({ queryKey: ["residentBranding"], queryFn: getResidentCenterBranding });
}

export function useSaveResidentBranding() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: saveResidentCenterBranding,
    onSuccess: () => qc.invalidateQueries({ queryKey: ["residentBranding"] })
  });
}

export function useChartOfAccounts() {
  return useQuery({ queryKey: ["coa"], queryFn: listChartOfAccounts });
}

export function useGlTransactions(scope: "PROPERTY" | "COMPANY" | "ALL") {
  return useQuery({ queryKey: ["gl", scope], queryFn: () => listGlTransactions(scope) });
}

export function useAddGlTransaction() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: addGlTransaction,
    onSuccess: () => qc.invalidateQueries({ queryKey: ["gl"] })
  });
}

export function useBankAccounts() {
  return useQuery({ queryKey: ["banks"], queryFn: listBankAccounts });
}

export function useBankLines(accountId: string) {
  return useQuery({ queryKey: ["bankLines", accountId], queryFn: () => listBankLines(accountId), enabled: !!accountId });
}

export function useMatchBankLine() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: matchBankLine,
    onSuccess: () => qc.invalidateQueries({ queryKey: ["bankLines"] })
  });
}

export function useBills() {
  return useQuery({ queryKey: ["bills"], queryFn: listBills });
}

export function usePayBill() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: payBill,
    onSuccess: () => qc.invalidateQueries({ queryKey: ["bills"] })
  });
}

export function useRecurring() {
  return useQuery({ queryKey: ["recurring"], queryFn: listRecurring });
}

export function useEftApprovals() {
  return useQuery({ queryKey: ["eft"], queryFn: listEftApprovals });
}

export function useApproveEft() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: approveEft,
    onSuccess: () => qc.invalidateQueries({ queryKey: ["eft"] })
  });
}

export function useBudget() {
  return useQuery({ queryKey: ["budget"], queryFn: listBudget });
}

export function useLatePenaltyRule() {
  return useQuery({ queryKey: ["latePenalty", USE_MOCKS], queryFn: getLatePenaltyRule });
}

export function useSaveLatePenaltyRule() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (rule: LatePenaltyRule) => saveLatePenaltyRule(rule),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["latePenalty"] })
  });
}

export function useMeterReadings() {
  return useQuery({ queryKey: ["meters", USE_MOCKS], queryFn: listMeterReadings });
}

export function useMeterAllocation(readings: Parameters<typeof previewMeterAllocation>[0]) {
  return useQuery({
    queryKey: ["meterAlloc", readings],
    queryFn: () => previewMeterAllocation(readings),
    enabled: readings.length > 0
  });
}

export function useEnforcementCases() {
  return useQuery({ queryKey: ["enforcement", USE_MOCKS], queryFn: listEnforcementCases });
}

export function useAtsCodes() {
  return useQuery({ queryKey: ["ats", USE_MOCKS], queryFn: listAtsCodes });
}

export function useRegenerateAts() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: regenerateAtsCode,
    onSuccess: () => qc.invalidateQueries({ queryKey: ["ats"] })
  });
}

export { computeLatePenaltyPreview };