import { useEffect, useState } from "react";
import { NavLink, Outlet, useLocation, useNavigate } from "react-router-dom";
import { Building2, ChevronDown, LayoutDashboard, LogOut, Menu, User, X } from "lucide-react";
import { cn } from "../../lib/cn";
import { NAV_SECTIONS, DEMO_ACCOUNTS } from "../../config/nav";
import { useAuth } from "../../state/auth";
import { useLiteMode } from "../../state/liteMode";
import { useLocale } from "../../state/locale";
import type { Locale } from "../../i18n/types";
import { LOCALE_OPTIONS, localeOptionLabel } from "../../i18n/localeMeta";
import { Button } from "../components/Button";
import { Badge } from "../components/Badge";
import { ThemeToggle } from "../components/ThemeToggle";
import { Select } from "../components/Select";
import { MOCK_ONLY } from "../../env";
import { interactiveRowClass } from "../../lib/uiClasses";

function useNavExpanded(sectionIds: string[]) {
  const { pathname } = useLocation();
  const [expanded, setExpanded] = useState<Record<string, boolean>>(() =>
    Object.fromEntries(sectionIds.map((id) => [id, false]))
  );

  useEffect(() => {
    for (const section of NAV_SECTIONS) {
      const hit = section.items.some((item) => pathname.includes(`/app/${item.path}`));
      if (hit) {
        setExpanded((prev) => ({ ...prev, [section.id]: true }));
      }
    }
  }, [pathname]);

  const toggle = (id: string) => setExpanded((prev) => ({ ...prev, [id]: !prev[id] }));

  return { expanded, toggle };
}

export function AppShell() {
  const { user, logout, impersonate } = useAuth();
  const { lite, setLite } = useLiteMode();
  const { locale, setLocale, t } = useLocale();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);

  const sections = NAV_SECTIONS.map((section) => ({
    ...section,
    items: section.items.filter((item) => !lite || !item.proOnly)
  })).filter((s) => s.items.length > 0);

  const { expanded, toggle } = useNavExpanded(sections.map((s) => s.id));

  return (
    <div className="flex min-h-screen bg-[rgb(var(--surface-muted))]">
      <aside
        className={cn(
          "fixed inset-y-0 left-0 z-40 flex w-72 flex-col border-r border-[rgb(var(--border))] bg-[rgb(var(--surface))] shadow-sm transition-transform lg:static lg:translate-x-0 dark:shadow-none",
          open ? "translate-x-0" : "-translate-x-full"
        )}
      >
        <div className="flex items-center gap-2 border-b border-[rgb(var(--border))] bg-gradient-to-r from-teal-900/5 to-transparent p-4 dark:from-teal-400/10">
          <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-teal-800 text-white dark:bg-teal-600">
            <Building2 size={18} />
          </div>
          <div className="min-w-0 flex-1">
            <div className="truncate text-sm font-bold text-[rgb(var(--text))]">AyanLab PMS</div>
            <div className="truncate text-[10px] text-[rgb(var(--text-muted))]">{user?.orgName}</div>
          </div>
          <button type="button" className="ml-auto text-[rgb(var(--text-muted))] lg:hidden" onClick={() => setOpen(false)}>
            <X size={18} />
          </button>
        </div>

        <nav className="flex-1 overflow-y-auto p-3 text-sm">
          <NavLink
            to="/app/dashboard"
            onClick={() => setOpen(false)}
            className={({ isActive }) =>
              cn(
                "mb-3 flex items-center gap-2 rounded-xl px-3 py-2.5 font-medium transition-colors",
                isActive ? "nav-item-active" : "text-[rgb(var(--text))] hover:bg-[rgb(var(--surface-muted))]"
              )
            }
          >
            <LayoutDashboard size={16} /> {t("shell.dashboard")}
          </NavLink>

          {sections.map((section) => {
            const isOpen = expanded[section.id] ?? section.items.length === 1;
            return (
              <div key={section.id} className="mb-1">
                <button
                  type="button"
                  className="flex w-full items-center justify-between gap-2 rounded-lg px-3 py-2 text-left text-[11px] font-bold uppercase tracking-wide text-[rgb(var(--text-muted))] hover:bg-[rgb(var(--surface-muted))]"
                  onClick={() => toggle(section.id)}
                  aria-expanded={isOpen}
                >
                  <span>{t(section.labelKey)}</span>
                  <ChevronDown
                    size={14}
                    className={cn("shrink-0 transition-transform", isOpen ? "rotate-180" : "rotate-0")}
                  />
                </button>
                {isOpen && (
                  <ul className="mb-3 ml-3 space-y-0.5 border-l border-[rgb(var(--border))] pl-2">
                    {section.items.map((item) => (
                      <li key={item.path}>
                        <NavLink
                          to={`/app/${item.path}`}
                          onClick={() => setOpen(false)}
                          className={({ isActive }) =>
                            cn(
                              "block rounded-lg px-3 py-1.5 text-[rgb(var(--text))] transition-colors",
                              isActive ? "nav-item-active" : "hover:bg-[rgb(var(--surface-muted))]"
                            )
                          }
                        >
                          {t(item.labelKey)}
                        </NavLink>
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            );
          })}
        </nav>

        <div className="space-y-2 border-t border-[rgb(var(--border))] p-3">
          <label className={cn(interactiveRowClass, "flex cursor-pointer items-center justify-between gap-2 text-xs")}>
            <span>{t("shell.apsisLite")}</span>
            <input type="checkbox" checked={lite} onChange={(e) => setLite(e.target.checked)} />
          </label>
          {MOCK_ONLY && <Badge tone="ok">{t("shell.mockOnly")}</Badge>}
        </div>
      </aside>

      <div className="flex min-w-0 flex-1 flex-col">
        <header className="glass-header sticky top-0 z-30 flex items-center gap-2 px-4 py-3">
          <button type="button" className="text-[rgb(var(--text))] lg:hidden" onClick={() => setOpen(true)}>
            <Menu size={20} />
          </button>
          <button
            type="button"
            className="flex min-w-0 flex-1 items-center gap-2 text-left text-sm font-medium text-[rgb(var(--text))] hover:text-teal-700 dark:hover:text-teal-300"
            onClick={() => navigate("/app/settings/profile")}
          >
            <User size={16} className="shrink-0 text-[rgb(var(--text-muted))]" />
            <span className="truncate">{user?.displayName}</span>
          </button>
          <ThemeToggle />
          <Select
            density="sm"
            className="max-w-[min(100%,13rem)]"
            value={locale}
            onChange={(e) => setLocale(e.target.value as Locale)}
            aria-label={t("common.language")}
          >
            {LOCALE_OPTIONS.map((o) => (
              <option key={o.code} value={o.code}>
                {localeOptionLabel(o)}
              </option>
            ))}
          </Select>
          {MOCK_ONLY && (
            <Select
              density="sm"
              className="max-w-[9.5rem]"
              value={user?.email ?? ""}
              onChange={async (e) => {
                await impersonate(e.target.value);
              }}
            >
              {DEMO_ACCOUNTS.map((a) => (
                <option key={a.email} value={a.email}>
                  {a.label}
                </option>
              ))}
            </Select>
          )}
          <Button
            variant="ghost"
            size="sm"
            title={t("shell.signOut")}
            onClick={() => {
              logout();
              navigate("/login");
            }}
          >
            <LogOut size={16} />
          </Button>
        </header>
        <main className="flex-1 p-4 md:p-6 lg:p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
}