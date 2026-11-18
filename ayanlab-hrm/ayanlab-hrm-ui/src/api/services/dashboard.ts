import { authHttp } from "../client";
import { endpoints } from "../endpoints";
import type {
  DashboardWidget,
  GetWidgetsResponse,
  GetDefaultsResponse,
  GetConfigResponse,
  SaveConfigRequest,
  SaveConfigResponse,
} from "../../types/dashboard";

/** GET /api/v1/dashboard/widgets?roleId=... */
export async function apiGetWidgets(roleId?: string): Promise<DashboardWidget[]> {
  const url = roleId
    ? `${endpoints.dashboard.widgets}?roleId=${encodeURIComponent(roleId)}`
    : endpoints.dashboard.widgets;
  const res = await authHttp.get<GetWidgetsResponse>(url);
  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message ?? "Failed to fetch widgets");
  }
  return res.data.data;
}

/** GET /api/v1/dashboard/defaults?roleId=... */
export async function apiGetDefaults(roleId: string): Promise<{ roleId: string; widgetIds: string[] }> {
  const res = await authHttp.get<GetDefaultsResponse>(
    `${endpoints.dashboard.defaults}?roleId=${encodeURIComponent(roleId)}`
  );
  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message ?? "Failed to fetch defaults");
  }
  return res.data.data;
}

/** GET /api/v1/dashboard/config?profileId=... */
export async function apiGetDashboardConfig(profileId: string): Promise<GetConfigResponse["data"]> {
  const res = await authHttp.get<GetConfigResponse>(
    `${endpoints.dashboard.config}?profileId=${encodeURIComponent(profileId)}`
  );
  if (!res.data.success) {
    throw new Error(res.data.message ?? "Failed to fetch dashboard config");
  }
  return res.data.data;
}

/** PUT /api/v1/dashboard/config */
export async function apiSaveDashboardConfig(
  payload: SaveConfigRequest
): Promise<SaveConfigResponse["data"]> {
  const res = await authHttp.put<SaveConfigResponse>(endpoints.dashboard.config, payload);
  if (!res.data.success || !res.data.data) {
    throw new Error(res.data.message ?? "Failed to save dashboard config");
  }
  return res.data.data;
}
