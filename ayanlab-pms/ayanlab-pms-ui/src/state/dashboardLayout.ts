import type { DashboardWidgetId } from "../config/dashboardWidgets";
import { defaultWidgetIds, primaryPersona } from "../config/dashboardWidgets";
import type { PmsPersona } from "../types/auth";

const layoutKey = (userKey: string) => `pms.dashboard.layout.${userKey}`;

export type DashboardLayoutPrefs = {
  order: DashboardWidgetId[];
  hidden: DashboardWidgetId[];
};

export function loadDashboardLayout(userKey: string, roles: PmsPersona[]): DashboardLayoutPrefs {
  const role = primaryPersona(roles);
  const baseOrder = defaultWidgetIds(role);
  try {
    const raw = localStorage.getItem(layoutKey(userKey));
    if (!raw) return { order: baseOrder, hidden: [] };
    const parsed = JSON.parse(raw) as DashboardLayoutPrefs;
    const allowed = new Set(baseOrder);
    const order = (parsed.order ?? []).filter((id) => allowed.has(id));
    for (const id of baseOrder) {
      if (!order.includes(id)) order.push(id);
    }
    const hidden = (parsed.hidden ?? []).filter((id) => allowed.has(id));
    return { order, hidden };
  } catch {
    return { order: baseOrder, hidden: [] };
  }
}

export function saveDashboardLayout(userKey: string, prefs: DashboardLayoutPrefs) {
  localStorage.setItem(layoutKey(userKey), JSON.stringify(prefs));
}

export function resetDashboardLayout(userKey: string, roles: PmsPersona[]) {
  localStorage.removeItem(layoutKey(userKey));
  return loadDashboardLayout(userKey, roles);
}