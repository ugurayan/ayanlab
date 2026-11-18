import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import type { DashboardConfig, SaveConfigRequest } from "../../types/dashboard";
import {
  mockGetWidgets,
  mockGetDefaults,
  mockGetConfig,
  mockSaveConfig,
} from "../../mock/dashboardApi";
import {
  apiGetWidgets,
  apiGetDefaults,
  apiGetDashboardConfig,
  apiSaveDashboardConfig,
} from "../../api/services/dashboard";

const USE_MOCKS = (import.meta.env.VITE_USE_MOCKS ?? "false") === "true";

/** Available widgets (optionally filtered by role) */
export function useDashboardWidgets(roleId: string | null) {
  return useQuery({
    queryKey: ["dashboard", "widgets", roleId ?? "all"],
    queryFn: () =>
      USE_MOCKS
        ? mockGetWidgets(roleId ?? undefined).then((r) => r.data)
        : apiGetWidgets(roleId ?? undefined),
    staleTime: 5 * 60 * 1000,
  });
}

/** Default widget IDs for a role (used when user has not customized) */
export function useDashboardDefaults(roleId: string | null) {
  return useQuery({
    queryKey: ["dashboard", "defaults", roleId],
    queryFn: async () => {
      if (!roleId) return { roleId: "", widgetIds: [] };
      return USE_MOCKS
        ? (await mockGetDefaults(roleId)).data
        : apiGetDefaults(roleId);
    },
    enabled: !!roleId,
    staleTime: 5 * 60 * 1000,
  });
}

/** User's saved dashboard config by profileId */
export function useDashboardConfig(profileId: string | null) {
  return useQuery({
    queryKey: ["dashboard", "config", profileId],
    queryFn: async (): Promise<DashboardConfig | null> => {
      if (!profileId) return null;
      const data = USE_MOCKS
        ? (await mockGetConfig(profileId)).data
        : await apiGetDashboardConfig(profileId);
      return data;
    },
    enabled: !!profileId,
    staleTime: 2 * 60 * 1000,
  });
}

/** Save dashboard customization for profileId */
export function useSaveDashboardConfig() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (payload: SaveConfigRequest) =>
      USE_MOCKS
        ? mockSaveConfig(payload).then((r) => r.data)
        : apiSaveDashboardConfig(payload),
    onSuccess: (_, variables) => {
      qc.invalidateQueries({ queryKey: ["dashboard", "config", variables.profileId] });
    },
  });
}
