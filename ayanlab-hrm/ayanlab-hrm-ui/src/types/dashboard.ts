/**
 * Dashboard dynamic widgets: types, format, and API shapes.
 *
 * 1. Widget catalog: all available widgets with role eligibility.
 * 2. Default layout: per-role ordered list of widget IDs (when user has not customized).
 * 3. User config: per profileId, saved widget order + optional grid layout.
 */

/** Unique widget identifier (e.g. "recent-shifts", "earnings-summary") */
export type WidgetId = string;

/** Role ID that can see this widget (main role UUID or legacy name) */
export type WidgetRoleId = string;

export type WidgetSize = {
  w: number; // grid columns
  h: number; // grid rows
};

/** Single widget definition in the catalog (available to certain roles) */
export type DashboardWidget = {
  id: WidgetId;
  name: string;
  description?: string;
  /** Role IDs that can add this widget (main role UUIDs from mainRoles) */
  roles: WidgetRoleId[];
  /** Suggested order in default layout (lower = first) */
  defaultOrder: number;
  /** Default grid size for layout */
  defaultSize: WidgetSize;
};

/** Default dashboard layout for a role: ordered list of widget IDs */
export type DashboardDefaultLayout = {
  roleId: string;
  widgetIds: WidgetId[];
};

/** Per-widget layout override (optional; for drag-and-drop grid) */
export type WidgetLayoutItem = {
  widgetId: WidgetId;
  x: number;
  y: number;
  w: number;
  h: number;
};

/** User's saved dashboard customization keyed by profileId */
export type DashboardConfig = {
  profileId: string;
  /** Ordered list of widget IDs to show (user can reorder and hide) */
  widgetIds: WidgetId[];
  /** Optional grid positions; if present, used for layout */
  layout?: WidgetLayoutItem[];
  updatedAt: string; // ISO
};

// ---------- API request/response types ----------

/** GET /api/v1/dashboard/widgets?roleId=... */
export type GetWidgetsRequest = {
  roleId?: string; // if provided, filter to widgets available for this role
};

export type GetWidgetsResponse = {
  success: boolean;
  message?: string;
  data: DashboardWidget[];
};

/** GET /api/v1/dashboard/defaults?roleId=... */
export type GetDefaultsRequest = {
  roleId: string;
};

export type GetDefaultsResponse = {
  success: boolean;
  message?: string;
  data: {
    roleId: string;
    widgetIds: WidgetId[];
  };
};

/** GET /api/v1/dashboard/config?profileId=... */
export type GetConfigRequest = {
  profileId: string;
};

export type GetConfigResponse = {
  success: boolean;
  message?: string;
  data: DashboardConfig | null; // null if user has never saved (use defaults)
};

/** PUT /api/v1/dashboard/config */
export type SaveConfigRequest = {
  profileId: string;
  widgetIds: WidgetId[];
  layout?: WidgetLayoutItem[];
};

export type SaveConfigResponse = {
  success: boolean;
  message?: string;
  data: DashboardConfig;
};
