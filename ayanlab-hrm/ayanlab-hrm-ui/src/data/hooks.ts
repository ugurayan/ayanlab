import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "../state/auth";
import { listApplications, listAudit, listUserAudit, listSystemAudit, listDocuments, listInvoices, listJobProfiles, listLocations, listOrgs, listShifts, listWorkers, listTasks, listJobs, listTransactions, createShiftApi, updateWorkerApi } from "../mock/mockApi";
import { listVerificationTasks, getVerificationTask, updateVerificationTaskStatus, assignVerificationTask } from "../mock/verificationApi";
import { listDepartments, getIssuesByDepartment } from "../mock/departmentApi";
import type { Shift, Worker } from "../mock/mockDb";
import type { VerificationTask, VerificationTaskStatus } from "../types/verification";

const USE_MOCKS = (import.meta.env.VITE_USE_MOCKS ?? "true") === "true";

export function useOrgs() {
  return useQuery({ queryKey: ["orgs"], queryFn: async () => {
    if (USE_MOCKS) return listOrgs();
    const { apiListOrgs } = await import("../api/services/orgs");
    return apiListOrgs();
  }});
}

export function useLocations() {
  // Safely access auth context - may not be available during initial render
  let user: any = null;
  let contextAvailable = false;
  try {
    const auth = useAuth();
    user = auth.user;
    contextAvailable = true;
  } catch (error) {
    // Context not available yet - this is OK during initial render
    contextAvailable = false;
  }
  
  return useQuery({ 
    queryKey: ["locations", user?.scope?.orgId], 
    queryFn: async () => {
      if (USE_MOCKS) return listLocations(user?.scope?.orgId);
      const { apiListLocations } = await import("../api/services/locations");
      return apiListLocations();
    },
    enabled: contextAvailable
  });
}

export function useShifts() {
  // Safely access auth context - may not be available during initial render
  let user: any = null;
  let contextAvailable = false;
  try {
    const auth = useAuth();
    user = auth.user;
    contextAvailable = true;
  } catch (error) {
    // Context not available yet - this is OK during initial render
    // The query will be disabled until context is available
    contextAvailable = false;
  }
  
  return useQuery({ 
    queryKey: ["shifts", user?.scope?.orgId], 
    queryFn: async () => {
      if (USE_MOCKS) return listShifts(user?.scope?.orgId);
      const { apiListShifts } = await import("../api/services/shifts");
      return apiListShifts();
    },
    enabled: contextAvailable // Only enable when context is available
  });
}

export function useCreateShift() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (input: Omit<Shift, "id" | "status"> & { status?: Shift["status"] }) => {
      if (USE_MOCKS) return createShiftApi(input);
      const { apiCreateShift } = await import("../api/services/shifts");
      return apiCreateShift(input);
    },
    onSuccess: async () => {
      await qc.invalidateQueries({ queryKey: ["shifts"] });
    }
  });
}

export function useApplications() {
  return useQuery({ queryKey: ["applications"], queryFn: async () => {
    if (USE_MOCKS) return listApplications();
    const { apiListApplications } = await import("../api/services/applications");
    return apiListApplications();
  }});
}

export function useWorkers() {
  return useQuery({ queryKey: ["workers"], queryFn: async () => {
    if (USE_MOCKS) return listWorkers();
    const { apiListWorkers } = await import("../api/services/workers");
    return apiListWorkers();
  }});
}

export function useUpdateWorker() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ workerId, patch }: { workerId: string; patch: Partial<Worker> }) => {
      if (USE_MOCKS) return updateWorkerApi(workerId, patch);
      throw new Error("Not implemented");
    },
    onSuccess: async () => {
      await qc.invalidateQueries({ queryKey: ["workers"] });
    },
  });
}

export function useJobProfiles(workerId?: string) {
  return useQuery({ queryKey: ["jobProfiles", workerId], queryFn: async () => {
    if (USE_MOCKS) return listJobProfiles(workerId);
    return [];
  }});
}

export function useDocuments() {
  return useQuery({ queryKey: ["documents"], queryFn: async () => {
    if (USE_MOCKS) return listDocuments();
    const { apiListDocuments } = await import("../api/services/documents");
    return apiListDocuments();
  }});
}

export function useInvoices() {
  // Safely access auth context - may not be available during initial render
  let user: any = null;
  let contextAvailable = false;
  try {
    const auth = useAuth();
    user = auth.user;
    contextAvailable = true;
  } catch (error) {
    // Context not available yet - this is OK during initial render
    contextAvailable = false;
  }
  
  return useQuery({ 
    queryKey: ["invoices", user?.scope?.orgId], 
    queryFn: async () => {
      if (USE_MOCKS) return listInvoices(user?.scope?.orgId);
      const { apiListInvoices } = await import("../api/services/finance");
      return apiListInvoices();
    },
    enabled: contextAvailable
  });
}

export function useAudit() {
  return useQuery({ queryKey: ["audit"], queryFn: async () => {
    if (USE_MOCKS) return listAudit();
    const { apiListAudit } = await import("../api/services/audit");
    return apiListAudit();
  }});
}

export function useUserAudit() {
  return useQuery({ queryKey: ["audit", "user"], queryFn: async () => {
    if (USE_MOCKS) return listUserAudit();
    const { apiListUserAudit } = await import("../api/services/audit");
    return apiListUserAudit();
  }});
}

export function useSystemAudit(page: number, size: number) {
  return useQuery({
    queryKey: ["audit", "system", page, size],
    queryFn: async () => {
      const { apiListSystemAudit } = await import("../api/services/audit");
      return apiListSystemAudit(page, size);
    },
  });
}

export function useTasks() {
  return useQuery({ queryKey: ["tasks"], queryFn: async () => {
    if (USE_MOCKS) return listTasks();
    return [];
  }});
}

export function useJobs() {
  return useQuery({ queryKey: ["jobs"], queryFn: async () => {
    if (USE_MOCKS) return listJobs();
    return [];
  }});
}

export function useTransactions() {
  return useQuery({ queryKey: ["transactions"], queryFn: async () => {
    if (USE_MOCKS) return listTransactions();
    return [];
  }});
}

export function useVerificationTasks() {
  return useQuery({ queryKey: ["verificationTasks"], queryFn: async () => {
    if (USE_MOCKS) return listVerificationTasks();
    return [];
  }});
}

export function useVerificationTask(id: string) {
  return useQuery({ queryKey: ["verificationTask", id], queryFn: async () => {
    if (USE_MOCKS) return getVerificationTask(id);
    return null;
  }});
}

export function useUpdateVerificationTask() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, status, reviewedBy, reviewNotes }: { id: string; status: VerificationTaskStatus; reviewedBy: string; reviewNotes?: string }) => {
      if (USE_MOCKS) return updateVerificationTaskStatus(id, status, reviewedBy, reviewNotes);
      throw new Error("Not implemented");
    },
    onSuccess: async () => {
      await qc.invalidateQueries({ queryKey: ["verificationTasks"] });
    }
  });
}

export function useAssignVerificationTask() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, assignedTo }: { id: string; assignedTo: string }) => {
      if (USE_MOCKS) return assignVerificationTask(id, assignedTo);
      throw new Error("Not implemented");
    },
    onSuccess: async () => {
      await qc.invalidateQueries({ queryKey: ["verificationTasks"] });
    }
  });
}

export function useDepartments() {
  return useQuery({ queryKey: ["departments"], queryFn: async () => {
    if (USE_MOCKS) return listDepartments();
    return [];
  }});
}

export function useDepartmentIssues(departmentId: string | null, language: "en" | "de" | "tr" = "en") {
  return useQuery({ 
    queryKey: ["departmentIssues", departmentId, language], 
    queryFn: async () => {
      if (USE_MOCKS && departmentId) return getIssuesByDepartment(departmentId, language);
      return [];
    },
    enabled: !!departmentId
  });
}
