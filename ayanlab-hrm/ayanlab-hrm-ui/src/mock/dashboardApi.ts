import type {
  DashboardConfig,
  DashboardWidget,
  GetWidgetsResponse,
  GetDefaultsResponse,
  GetConfigResponse,
  SaveConfigRequest,
  SaveConfigResponse,
} from "../types/dashboard";
import {
  WIDGET_CATALOG,
  getWidgetsForRole,
  getDefaultWidgetIdsForRole,
} from "../data/constants/dashboardWidgets";

const delay = (ms = 200) => new Promise((r) => setTimeout(r, ms));

/** In-memory store: profileId -> DashboardConfig (persists only in session) */
const configStore = new Map<string, DashboardConfig>();

/** GET /api/v1/dashboard/widgets?roleId=... */
export async function mockGetWidgets(roleId?: string): Promise<GetWidgetsResponse> {
  await delay();
  const widgets: DashboardWidget[] = roleId
    ? getWidgetsForRole(roleId)
    : [...WIDGET_CATALOG].sort((a, b) => a.defaultOrder - b.defaultOrder);
  return {
    success: true,
    data: widgets,
  };
}

/** GET /api/v1/dashboard/defaults?roleId=... */
export async function mockGetDefaults(roleId: string): Promise<GetDefaultsResponse> {
  await delay();
  const widgetIds = getDefaultWidgetIdsForRole(roleId);
  return {
    success: true,
    data: {
      roleId,
      widgetIds,
    },
  };
}

/** GET /api/v1/dashboard/config?profileId=... */
export async function mockGetConfig(profileId: string): Promise<GetConfigResponse> {
  await delay();
  const config = configStore.get(profileId) ?? null;
  return {
    success: true,
    data: config,
  };
}

/** PUT /api/v1/dashboard/config */
export async function mockSaveConfig(
  payload: SaveConfigRequest
): Promise<SaveConfigResponse> {
  await delay();
  const now = new Date().toISOString();
  const config: DashboardConfig = {
    profileId: payload.profileId,
    widgetIds: payload.widgetIds,
    layout: payload.layout,
    updatedAt: now,
  };
  configStore.set(payload.profileId, config);
  return {
    success: true,
    data: config,
  };
}
