import { useMemo, useState } from "react";
import { cn } from "../../lib/cn";
import { interactiveRowClass } from "../../lib/uiClasses";
import { useDashboardSummary } from "../../data/hooks";
import { useAuth } from "../../state/auth";
import { useLocale } from "../../state/locale";
import { USE_MOCKS } from "../../env";
import {
  DASHBOARD_WIDGETS,
  primaryPersona,
  widgetsForRole,
  type DashboardWidgetId
} from "../../config/dashboardWidgets";
import { loadDashboardLayout, resetDashboardLayout, saveDashboardLayout } from "../../state/dashboardLayout";
import { Button } from "../../ui/components/Button";
import { Badge } from "../../ui/components/Badge";
import { DashboardWidgetCard, renderDashboardWidget } from "./DashboardWidgets";
import { DashboardSkeleton } from "../../ui/components/Skeleton";
import { surfacePanelClass } from "../../lib/uiClasses";

export function DashboardPage() {
  const { user } = useAuth();
  const { t } = useLocale();
  const { data, isLoading } = useDashboardSummary();
  const role = primaryPersona(user?.roles ?? ["PROPERTY_MANAGER"]);
  const userKey = user?.email ?? "anonymous";

  const [prefs, setPrefs] = useState(() => loadDashboardLayout(userKey, user?.roles ?? ["PROPERTY_MANAGER"]));
  const [customizeOpen, setCustomizeOpen] = useState(false);

  const allowed = useMemo(() => widgetsForRole(role), [role]);
  const allowedIds = new Set(allowed.map((w) => w.id));

  const visibleIds = prefs.order.filter((id) => allowedIds.has(id) && !prefs.hidden.includes(id));

  if (isLoading || !data) {
    return <DashboardSkeleton />;
  }

  function persist(next: typeof prefs) {
    setPrefs(next);
    saveDashboardLayout(userKey, next);
  }

  function toggleHidden(id: DashboardWidgetId) {
    const hidden = prefs.hidden.includes(id) ? prefs.hidden.filter((x) => x !== id) : [...prefs.hidden, id];
    persist({ ...prefs, hidden });
  }

  function move(id: DashboardWidgetId, dir: -1 | 1) {
    const order = [...prefs.order];
    const idx = order.indexOf(id);
    if (idx < 0) return;
    const swap = idx + dir;
    if (swap < 0 || swap >= order.length) return;
    [order[idx], order[swap]] = [order[swap], order[idx]];
    persist({ ...prefs, order });
  }

  function handleReset() {
    const fresh = resetDashboardLayout(userKey, user?.roles ?? ["PROPERTY_MANAGER"]);
    setPrefs(fresh);
    setCustomizeOpen(false);
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold tracking-tight text-[rgb(var(--text))]">{t("dashboard.title")}</h1>
          <p className="text-sm text-[rgb(var(--text-muted))]">
            {USE_MOCKS ? t("dashboard.subtitleMock") : t("dashboard.subtitleApi")} —{" "}
            <Badge tone="neutral">{role}</Badge>
          </p>
        </div>
        <Button type="button" variant="secondary" size="sm" onClick={() => setCustomizeOpen((v) => !v)}>
          {t("dashboard.customize")}
        </Button>
      </div>

      {customizeOpen && (
        <CustomizePanel
          allowed={allowed}
          prefs={prefs}
          t={t}
          onToggle={toggleHidden}
          onMove={move}
          onReset={handleReset}
          onClose={() => setCustomizeOpen(false)}
        />
      )}

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
        {visibleIds.map((id) => {
          const def = DASHBOARD_WIDGETS.find((w) => w.id === id);
          if (!def) return null;
          return (
            <DashboardWidgetCard
              key={id}
              title={t(def.titleKey)}
              href={def.href}
              viewLabel={t("dashboard.view")}
            >
              {renderDashboardWidget(id, data, t)}
            </DashboardWidgetCard>
          );
        })}
      </div>
    </div>
  );
}

function CustomizePanel({
  allowed,
  prefs,
  t,
  onToggle,
  onMove,
  onReset,
  onClose
}: {
  allowed: ReturnType<typeof widgetsForRole>;
  prefs: ReturnType<typeof loadDashboardLayout>;
  t: (k: string) => string;
  onToggle: (id: DashboardWidgetId) => void;
  onMove: (id: DashboardWidgetId, dir: -1 | 1) => void;
  onReset: () => void;
  onClose: () => void;
}) {
  return (
    <div className={cn(surfacePanelClass, "p-4 md:p-5")}>
      <p className="mb-3 text-sm font-semibold text-[rgb(var(--text))]">{t("dashboard.customize")}</p>
      <ul className="space-y-2 text-sm">
        {allowed.map((w) => {
          const hidden = prefs.hidden.includes(w.id);
          return (
            <li key={w.id} className={cn(interactiveRowClass, "flex flex-wrap items-center justify-between gap-2")}>
              <label className="flex items-center gap-2">
                <input type="checkbox" checked={!hidden} onChange={() => onToggle(w.id)} />
                {t(w.titleKey)}
              </label>
              <span className="flex gap-1">
                <Button type="button" size="sm" variant="ghost" onClick={() => onMove(w.id, -1)}>
                  ↑
                </Button>
                <Button type="button" size="sm" variant="ghost" onClick={() => onMove(w.id, 1)}>
                  ↓
                </Button>
              </span>
            </li>
          );
        })}
      </ul>
      <div className="mt-3 flex flex-wrap gap-2">
        <Button type="button" size="sm" onClick={onClose}>
          {t("common.save")}
        </Button>
        <Button type="button" size="sm" variant="secondary" onClick={onReset}>
          {t("dashboard.reset")}
        </Button>
      </div>
    </div>
  );
}