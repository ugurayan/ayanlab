import React, { useState } from "react";
import { Outlet, NavLink, useNavigate } from "react-router-dom";
import {
  Building2,
  CalendarClock,
  ClipboardList,
  FileCheck2,
  LayoutDashboard,
  LogOut,
  MapPin,
  Receipt,
  ShieldCheck,
  CheckCircle2,
  Menu,
  X,
  LayoutPanelLeft,
  Calendar,
  Mail,
  Briefcase,
  Users,
  Star,
  Globe2,
  Type,
  Maximize2,
  SunMedium,
  Search,
  Bookmark,
  Bell,
  Settings,
  Tag,
  UserCircle,
  ChevronDown
} from "lucide-react";
import { cn } from "../../lib/cn";
import { useAuth } from "../../state/auth";
import { Language, useLanguage, useTranslations } from "../../state/i18n";
import { Button } from "../components/Button";
import { TEXTS } from "./i18n";

type NavItem = {
  to: string;
  labelKey: keyof typeof TEXTS.en.nav;
  icon: React.ReactNode;
  perm: string[];
};



const nav = [
  { to: "/app/dashboard", labelKey: "dashboard", icon: <LayoutDashboard size={18} />, perm: ["94a79627-93b0-4827-a30b-eff05c085774"] },
  { to: "/app/jobs", labelKey: "jobs", icon: <Briefcase size={18} />, perm: ["7c3a23c7-3e59-4224-9ae7-64428d3382ba"] },
  { to: "/app/tasks", labelKey: "tasks", icon: <ClipboardList size={18} />, perm: ["bb80d360-7663-4fb4-b423-641231c4540b"] },
  { to: "/app/inbox", labelKey: "inbox", icon: <Mail size={18} />, perm: ["63991c90-bfe6-4eca-8730-bf66b50325da"] },
  { to: "/app/orgs", labelKey: "orgs", icon: <Building2 size={18} />, perm: ["9d3964a7-5381-416d-8f9e-baafeabd682f"] },
  { to: "/app/locations", labelKey: "locations", icon: <MapPin size={18} />, perm: ["a3b4c5d6-e7f8-9012-3456-01234567890a"] },
  { to: "/app/shifts", labelKey: "shifts", icon: <CalendarClock size={18} />, perm: ["c5d6e7f8-a9b0-1234-5678-234567890abc"] },
  { to: "/app/applications", labelKey: "applications", icon: <ClipboardList size={18} />, perm: ["d6e7f8a9-b0c1-2345-6789-34567890abcd"] },
  { to: "/app/workers", labelKey: "workers", icon: <FileCheck2 size={18} />, perm: ["7b932ee5-31ff-499f-bbb3-df50313215fd"] },
  { to: "/app/documents", labelKey: "documents", icon: <FileCheck2 size={18} />, perm: ["d8694403-dd7a-4aa4-9571-d84986e0c2f2"] },
  { to: "/app/finance", labelKey: "finance", icon: <Receipt size={18} />, perm: ["eb34fad5-c218-4dda-9147-1290a1d8c419"] },
  { to: "/app/transactions", labelKey: "transactions", icon: <Receipt size={18} />, perm: ["d258b8ec-a7d5-4912-8b33-aeac498d6f54"] },
  { to: "/app/audit/user", labelKey: "userAudit", icon: <ShieldCheck size={18} />, perm: ["eecbbe51-6997-4b24-bdac-94a39928a9f9"] },
  { to: "/app/audit/system", labelKey: "systemAudit", icon: <ShieldCheck size={18} />, perm: ["eecbbe51-6997-4b24-bdac-94a39928a9f9"] },
  { to: "/app/verification", labelKey: "verification", icon: <CheckCircle2 size={18} />, perm: ["960e176b-26a4-4184-a9d3-39072919518c"] },
  { to: "/app/support", labelKey: "support", icon: <Star size={18} />, perm: ["9b8fb86e-9ad5-4141-a392-553223883a98"] },
  { to: "/app/settings", labelKey: "settings", icon: <Settings size={18} />, perm: ["42475b12-bf6e-4c46-81b3-6994da58e366"] },
  { to: "/app/profile", labelKey: "profile", icon: <Users size={18} />, perm: ["94a79627-93b0-4827-a30b-eff05c085774"] },
  { to: "/app/job-roles", labelKey: "jobRoles", icon: <Briefcase size={18} />, perm: ["56dd1aa3-8e89-43a2-b932-06d3b915b2ce"] },
  { to: "/app/industries", labelKey: "industries", icon: <Tag size={18} />, perm: ["980d999f-b05a-420d-91fc-8a1bd072052e"] },
  // Admin taxonomy pages (share permission with job roles for now)
  { to: "/app/skills", labelKey: "skills", icon: <Star size={18} />, perm: ["56dd1aa3-8e89-43a2-b932-06d3b915b2ce"] },
  { to: "/app/certificates", labelKey: "certificates", icon: <FileCheck2 size={18} />, perm: ["56dd1aa3-8e89-43a2-b932-06d3b915b2ce"] },
  { to: "/app/document-types", labelKey: "documentTypes", icon: <FileCheck2 size={18} />, perm: ["56dd1aa3-8e89-43a2-b932-06d3b915b2ce"] }
] as const;

export function AppShell() {
  const { user, logout, has } = useAuth();
  const navigate = useNavigate();
  const [isNavOpen, setIsNavOpen] = useState(false);
  const { language, setLanguage } = useLanguage();
  const copy = useTranslations(TEXTS);

  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const [isNotificationsOpen, setIsNotificationsOpen] = useState(false);
  const [isProfileSwitcherOpen, setIsProfileSwitcherOpen] = useState(false);
  const [isProfileChangeOpen, setIsProfileChangeOpen] = useState(false);
  const [textScale, setTextScale] = useState<"sm" | "md" | "lg">("md");

  // Close overlays when clicking outside
  React.useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      const target = e.target as HTMLElement;
      if (!target.closest('[data-notifications-panel]') && !target.closest('[data-notifications-button]')) {
        setIsNotificationsOpen(false);
      }
      if (!target.closest('[data-profile-change-panel]') && !target.closest('[data-profile-change-button]')) {
        setIsProfileChangeOpen(false);
      }
    };

    if (isNotificationsOpen || isProfileChangeOpen) {
      document.addEventListener("mousedown", handleClickOutside);
      return () => document.removeEventListener("mousedown", handleClickOutside);
    }
  }, [isNotificationsOpen, isProfileChangeOpen]);

  // Size of the page text
  React.useEffect(() => {
    try {
      const stored = window.localStorage.getItem("app-text-scale");
      if (stored === "sm" || stored === "md" || stored === "lg") {
        setTextScale(stored);
      }
    } catch {
      // ignore
    }
  }, []);

  // Save the size of the page text to localStorage
  React.useEffect(() => {
    try {
      window.localStorage.setItem("app-text-scale", textScale);
    } catch {
      // ignore
    }
  }, [textScale]);

  // Filter nav items based on permissions and profile approval

  const isProfileApproved = user?.profileApproved !== false; // Default to true if not set
  const isUserApproved = user?.userApproved !== false; // Default to true if not set
  const visible = nav.filter((n) => {
    // Settings and Logout are always visible
    if (n.to === "/app/settings" || n.labelKey === "settings") {
      return true;
    }
    // If profile not approved, hide all other items
    if (!isProfileApproved) {
      return false;
    }
    // Otherwise, filter by permissions
    return n.perm.some((p) => has(p as any));
  });

  // Group admin-related items for sidebar
  const adminSectionRoutes = new Set<string>([
    "/app/verification",
    "/app/job-roles",
    "/app/industries",
    "/app/skills",
    "/app/certificates",
    "/app/document-types"
  ]);
  const primaryNav = visible.filter((n) => !adminSectionRoutes.has(n.to));
  const adminNav = visible.filter((n) => adminSectionRoutes.has(n.to));

  console.log(user);
  const isOrgOrAdmin =
    user?.roles.includes("4a3207bf-cc00-46db-8db7-42e8d9568b8b") ||
    user?.roles.includes("a0fdb0b8-a079-4315-ae21-31e68cd80778") ||
    user?.roles.includes("4b029d24-e658-4a65-8d76-ddbc2627a05c");

  return (
    <div className="h-screen grid grid-cols-1 md:grid-cols-[280px_1fr] pt-[56px] md:pt-0 overflow-hidden">

      {/* Mobile top bar */}
      <header className="fixed top-0 left-0 right-0 z-30 flex items-center justify-between px-4 py-3 border-b border-slate-100 md:hidden bg-white">
        <div>
          <div className="text-sm font-semibold text-slate-900">{copy.portalName}</div>
          <div className="text-xs text-slate-500">{copy.portalSubtitle}</div>
        </div>
        <button
          type="button"
          className="inline-flex items-center justify-center rounded-full p-2 text-slate-700 hover:bg-slate-100"
          onClick={() => setIsNavOpen(true)}
        >
          <Menu size={20} />
        </button>
      </header>

      <aside className="hidden md:flex flex-col bg-white border-r border-slate-100 p-4 h-full overflow-hidden">
        <div className="px-3 py-2 flex-shrink-0">
          <div className="text-sm font-semibold text-slate-900">Staffing Portal</div>
          <div className="text-xs text-slate-500">Employer/Admin Console</div>
        </div>

        <nav className="mt-4 flex flex-col gap-1 flex-1 min-h-0 overflow-y-auto">
          {primaryNav.map((item) => {
            const isDisabled =
              !isProfileApproved && item.to !== "/app/settings" && item.labelKey !== "settings";
            return (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) =>
                  cn(
                    "flex items-center gap-3 rounded-2xl px-3 py-2 text-sm font-semibold",
                    isDisabled
                      ? "text-slate-400 cursor-not-allowed opacity-50"
                      : "text-slate-700 hover:bg-slate-50",
                    isActive && !isDisabled && "bg-slate-900 text-white hover:bg-slate-900"
                  )
                }
                onClick={(e) => {
                  if (isDisabled) {
                    e.preventDefault();
                  }
                }}
              >
                {item.icon}
                {copy.nav[item.labelKey]}
              </NavLink>
            );
          })}

          {isOrgOrAdmin && adminNav.length > 0 && (
            <>
              <div className="mt-4 mb-1 px-3 text-[11px] font-semibold uppercase tracking-wide text-slate-400">
                Admin
              </div>
              {adminNav.map((item) => {
                const isDisabled =
                  !isProfileApproved && item.to !== "/app/settings" && item.labelKey !== "settings";
                return (
                  <NavLink
                    key={item.to}
                    to={item.to}
                    className={({ isActive }) =>
                      cn(
                        "flex items-center gap-3 rounded-2xl px-3 py-2 text-sm font-semibold",
                        isDisabled
                          ? "text-slate-400 cursor-not-allowed opacity-50"
                          : "text-slate-700 hover:bg-slate-50",
                        isActive && !isDisabled && "bg-slate-900 text-white hover:bg-slate-900"
                      )
                    }
                    onClick={(e) => {
                      if (isDisabled) {
                        e.preventDefault();
                      }
                    }}
                  >
                    {item.icon}
                    {copy.nav[item.labelKey]}
                  </NavLink>
                );
              })}
            </>
          )}
        </nav>

        <div className="mt-auto pt-6 flex-shrink-0">
          <div className="rounded-2xl border border-slate-100 p-3">
            <div className="text-sm font-semibold">{user?.displayName}</div>
            <div className="text-xs text-slate-500">{user?.email}</div>
            {/* <div className="mt-2 flex flex-wrap gap-1">
              {user?.roles.map((r) => (
                <span key={r} className="text-[11px] px-2 py-1 rounded-full bg-slate-100 text-slate-700 font-semibold">
                  {r}
                </span>
              ))}
            </div> */}
            <div className="mt-3 flex gap-2">
              <Button
                variant="secondary"
                className="w-full"
                onClick={() => {
                  logout();
                  navigate("/login");
                }}
              >
                <LogOut size={16} /> {copy.profile.logout}
              </Button>
            </div>
          </div>
        </div>
      </aside>
      {/* Mobile drawer */}
      {isNavOpen && (
        <div className="fixed inset-0 z-40 flex md:hidden">
          {/* Overlay */}
          <div
            className="fixed inset-0 bg-black/30"
            onClick={() => setIsNavOpen(false)}
          />

          {/* Drawer */}
          <div className="relative z-50 w-72 max-w-full bg-white border-r border-slate-100 p-4 flex flex-col">
            <div className="flex items-center justify-between mb-3">
              <div>
                <div className="text-sm font-semibold text-slate-900">{copy.portalName}</div>
                <div className="text-xs text-slate-500">{copy.portalSubtitle}</div>
              </div>
              <button
                type="button"
                className="inline-flex items-center justify-center rounded-full p-2 text-slate-700 hover:bg-slate-100"
                onClick={() => setIsNavOpen(false)}
              >
                <X size={18} />
              </button>
            </div>

            <nav className="mt-1 flex flex-col gap-1">
              {primaryNav.map((item) => {
                const isDisabled =
                  !isProfileApproved && item.to !== "/app/settings" && item.labelKey !== "settings";
                return (
                  <NavLink
                    key={item.to}
                    to={item.to}
                    className={({ isActive }) =>
                      cn(
                        "flex items-center gap-3 rounded-2xl px-3 py-2 text-sm font-semibold",
                        isDisabled
                          ? "text-slate-400 cursor-not-allowed opacity-50"
                          : "text-slate-700 hover:bg-slate-50",
                        isActive && !isDisabled && "bg-slate-900 text-white hover:bg-slate-900"
                      )
                    }
                    onClick={(e) => {
                      if (isDisabled) {
                        e.preventDefault();
                      } else {
                        setIsNavOpen(false);
                      }
                    }}
                  >
                    {item.icon}
                    {copy.nav[item.labelKey]}
                  </NavLink>
                );
              })}

              {isOrgOrAdmin && adminNav.length > 0 && (
                <>
                  <div className="mt-4 mb-1 px-3 text-[11px] font-semibold uppercase tracking-wide text-slate-400">
                    Admin
                  </div>
                  {adminNav.map((item) => {
                    const isDisabled =
                      !isProfileApproved && item.to !== "/app/settings" && item.labelKey !== "settings";
                    return (
                      <NavLink
                        key={item.to}
                        to={item.to}
                        className={({ isActive }) =>
                          cn(
                            "flex items-center gap-3 rounded-2xl px-3 py-2 text-sm font-semibold",
                            isDisabled
                              ? "text-slate-400 cursor-not-allowed opacity-50"
                              : "text-slate-700 hover:bg-slate-50",
                            isActive && !isDisabled && "bg-slate-900 text-white hover:bg-slate-900"
                          )
                        }
                        onClick={(e) => {
                          if (isDisabled) {
                            e.preventDefault();
                          } else {
                            setIsNavOpen(false);
                          }
                        }}
                      >
                        {item.icon}
                        {copy.nav[item.labelKey]}
                      </NavLink>
                    );
                  })}
                </>
              )}
            </nav>

            <div className="mt-auto pt-6">
              <div className="rounded-2xl border border-slate-100 p-3">
                <div className="text-sm font-semibold">{user?.displayName}</div>
                <div className="text-xs text-slate-500">{user?.email}</div>
                <div className="mt-2 flex flex-wrap gap-1">
                  {user?.roles.map((r) => (
                    <span
                      key={r}
                      className="text-[11px] px-2 py-1 rounded-full bg-slate-100 text-slate-700 font-semibold"
                    >
                      {r}
                    </span>
                  ))}
                </div>
                <div className="mt-3 flex gap-2">
                  <Button
                    variant="secondary"
                    className="w-full"
                    onClick={() => {
                      logout();
                      navigate("/login");
                    }}
                  >
                    <LogOut size={16} /> {copy.profile.logout}
                  </Button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
            <main className="p-4 md:p-6 h-full flex flex-col min-h-0">
        {/* Global upper panel */}
        <div className="mb-4 flex items-center justify-between rounded-2xl border border-slate-200 bg-slate-50 px-4 py-2 text-slate-500 text-sm flex-shrink-0">
          {/* Left icons */}
          <div className="flex items-center gap-3">
            <button className="flex h-8 w-8 items-center justify-center rounded-xl border border-slate-300 bg-white">
              <LayoutPanelLeft size={16} />
            </button>

            <div className="h-5 w-px bg-slate-200" />

            <button
              className="hover:text-slate-900"
              onClick={() => navigate("/app/shifts")}
            >
              <Calendar size={16} />
            </button>
            <button
              className="hover:text-slate-900"
              onClick={() => navigate("/app/inbox")}
            >
              <Mail size={16} />
            </button>
            {isOrgOrAdmin && (
              <button
                className="hover:text-slate-900"
                onClick={() => navigate("/app/workers")}
              >
                <Users size={16} />
              </button>
            )}
            <button className="hover:text-slate-900">
              <Star size={16} />
            </button>
          </div>

          {/* Right controls */}
          <div className="flex items-center gap-3">
           <div className="flex items-center gap-1">
  <Globe2 size={14} className="text-slate-500" />
  <select
    value={language}
    onChange={(e) => setLanguage(e.target.value as Language)}
    className="bg-transparent text-xs font-medium text-slate-700 outline-none border-none focus:ring-0"
  >
    <option value="en">EN</option>
    <option value="de">DE</option>
    <option value="tr">TR</option>
  </select>
</div>

            <button
              className="hover:text-slate-900"
              onClick={() =>
                setTextScale((prev) =>
                  prev === "sm" ? "md" : prev === "md" ? "lg" : "sm"
                )
              }
            >
              <Type size={16} />
            </button>
            <button
              className="hover:text-slate-900"
              onClick={() => {
                const docEl = document.documentElement;
                if (!document.fullscreenElement) {
                  docEl.requestFullscreen?.();
                } else {
                  document.exitFullscreen?.();
                }
              }}
            >
              <Maximize2 size={16} />
            </button>
            <button
              className="hover:text-slate-900"
              onClick={() => {
                const root = document.documentElement;
                const isDark = root.classList.toggle("dark");
                try {
                  window.localStorage.setItem("theme", isDark ? "dark" : "light");
                } catch {
                  // ignore
                }
              }}
            >
              <SunMedium size={16} />
            </button>
            <button
              className="hover:text-slate-900"
              onClick={() => setIsSearchOpen((v) => !v)}
            >
              <Search size={16} />
            </button>
            <button
              className="hover:text-slate-900"
              onClick={() => setIsProfileSwitcherOpen((v) => !v)}
            >
              <Bookmark size={16} />
            </button>

            <button
              data-notifications-button
              className="relative hover:text-slate-900"
              onClick={() => setIsNotificationsOpen((v) => !v)}
            >
              <Bell size={16} />
              <span className="absolute -right-0.5 -top-0.5 h-2 w-2 rounded-full bg-blue-500"></span>
            </button>

            {/* Profile Change Button - Far Right */}
            <div className="relative">
              <button
                data-profile-change-button
                className="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-1.5 text-sm font-medium text-slate-700 hover:bg-slate-50 shadow-sm"
                onClick={() => setIsProfileChangeOpen((v) => !v)}
              >
                <UserCircle size={16} />
                <span className="hidden md:inline">{user?.displayName || "Profile"}</span>
                <ChevronDown size={14} className={cn("transition-transform", isProfileChangeOpen && "rotate-180")} />
              </button>

              {/* Profile Change Dropdown */}
              {isProfileChangeOpen && (
                <div
                  data-profile-change-panel
                  className="absolute right-0 top-full mt-2 w-64 rounded-2xl border border-slate-200 bg-white p-2 shadow-lg z-50"
                >
                  <div className="space-y-1">
                    <button
                      className="w-full rounded-xl px-3 py-2 text-left text-sm hover:bg-slate-50 flex items-center gap-2"
                      onClick={() => {
                        navigate("/app/profile");
                        setIsProfileChangeOpen(false);
                      }}
                    >
                      <Users size={16} />
                      <span>Worker Profile</span>
                    </button>
                    <button
                      className="w-full rounded-xl px-3 py-2 text-left text-sm hover:bg-slate-50 flex items-center gap-2"
                      onClick={() => {
                        // Switch to company admin profile
                        setIsProfileChangeOpen(false);
                      }}
                    >
                      <Building2 size={16} />
                      <span>Company Admin</span>
                    </button>
                    <button
                      className="w-full rounded-xl px-3 py-2 text-left text-sm hover:bg-slate-50 flex items-center gap-2"
                      onClick={() => {
                        // Switch to system admin profile
                        setIsProfileChangeOpen(false);
                      }}
                    >
                      <ShieldCheck size={16} />
                      <span>System Admin</span>
                    </button>
                    <div className="border-t border-slate-100 my-1"></div>
                    <button
                      className="w-full rounded-xl px-3 py-2 text-left text-sm hover:bg-slate-50 flex items-center gap-2 text-red-600"
                      onClick={() => {
                        logout();
                        navigate("/login");
                        setIsProfileChangeOpen(false);
                      }}
                    >
                      <LogOut size={16} />
                      <span>{copy.profile.logout}</span>
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Search panel */}
        {isSearchOpen && (
          <div className="mb-4 rounded-2xl border border-slate-200 bg-white p-3 text-sm shadow-sm">
            <div className="text-xs font-semibold text-slate-500 mb-2">Quick search (mock)</div>
            <div className="grid gap-1 md:grid-cols-3">
              {visible.map((item) => {
                const isDisabled = !isProfileApproved && item.to !== "/app/settings" && item.labelKey !== "settings";
                return (
                  <button
                    key={item.to}
                    type="button"
                    disabled={isDisabled}
                    className={cn(
                      "flex items-center gap-2 rounded-xl border border-slate-100 px-2 py-1 text-xs",
                      isDisabled
                        ? "text-slate-400 cursor-not-allowed opacity-50"
                        : "hover:bg-slate-50"
                    )}
                    onClick={() => {
                      if (!isDisabled) {
                        navigate(item.to);
                        setIsSearchOpen(false);
                      }
                    }}
                  >
                    {item.icon}
                    <span>{copy.nav[item.labelKey]}</span>
                  </button>
                );
              })}
            </div>
          </div>
        )}

        {/* Profile switcher panel */}
        {isProfileSwitcherOpen && (
          <div className="mb-4 rounded-2xl border border-slate-200 bg-white p-3 text-sm shadow-sm">
            <div className="text-xs font-semibold text-slate-500 mb-2">Switch profile (mock)</div>
            <div className="grid gap-2 md:grid-cols-3">
              <button className="rounded-xl border border-slate-100 px-3 py-2 text-left text-xs hover:bg-slate-50">
                Worker profile
              </button>
              <button className="rounded-xl border border-slate-100 px-3 py-2 text-left text-xs hover:bg-slate-50">
                Company admin profile
              </button>
              <button className="rounded-xl border border-slate-100 px-3 py-2 text-left text-xs hover:bg-slate-50">
                System admin profile
              </button>
            </div>
          </div>
        )}


        {/* Page content – scrollable when content is long */}
        <div
          className={cn(
            "w-full flex-1 min-h-0 overflow-y-auto",
            textScale === "sm" && "text-[13px]",
            textScale === "md" && "text-[14px]",
            textScale === "lg" && "text-[15px]"
          )}
        >
          <Outlet />
        </div>
      </main>

      {/* Notifications Overlay Panel */}
      {isNotificationsOpen && (
        <>
          {/* Backdrop */}
          <div
            className="fixed inset-0 bg-black/20 z-40"
            onClick={() => setIsNotificationsOpen(false)}
          />
          {/* Panel */}
          <div
            data-notifications-panel
            className="fixed top-16 right-4 w-96 max-w-[calc(100vw-2rem)] rounded-2xl border border-slate-200 bg-white shadow-xl z-50 max-h-[calc(100vh-5rem)] overflow-hidden flex flex-col"
          >
            <div className="flex items-center justify-between p-4 border-b border-slate-100">
              <h3 className="text-sm font-semibold text-slate-900">Notifications</h3>
              <button
                onClick={() => setIsNotificationsOpen(false)}
                className="text-slate-400 hover:text-slate-600"
              >
                <X size={16} />
              </button>
            </div>
            <div className="overflow-y-auto p-4">
              <ul className="space-y-3 text-sm">
                <li className="p-3 rounded-xl bg-slate-50 hover:bg-slate-100 cursor-pointer transition-colors">
                  <div className="font-medium text-slate-900">New worker applied</div>
                  <div className="text-xs text-slate-500 mt-1">A new worker applied to your job posting.</div>
                  <div className="text-xs text-slate-400 mt-1">2 minutes ago</div>
                </li>
                <li className="p-3 rounded-xl bg-slate-50 hover:bg-slate-100 cursor-pointer transition-colors">
                  <div className="font-medium text-slate-900">Shift completed</div>
                  <div className="text-xs text-slate-500 mt-1">Shift #1234 was completed and is ready for approval.</div>
                  <div className="text-xs text-slate-400 mt-1">1 hour ago</div>
                </li>
                <li className="p-3 rounded-xl bg-slate-50 hover:bg-slate-100 cursor-pointer transition-colors">
                  <div className="font-medium text-slate-900">Document verification</div>
                  <div className="text-xs text-slate-500 mt-1">Document verification pending for 2 workers.</div>
                  <div className="text-xs text-slate-400 mt-1">3 hours ago</div>
                </li>
                <li className="p-3 rounded-xl bg-slate-50 hover:bg-slate-100 cursor-pointer transition-colors">
                  <div className="font-medium text-slate-900">Payment processed</div>
                  <div className="text-xs text-slate-500 mt-1">Payment for shift #1234 has been processed.</div>
                  <div className="text-xs text-slate-400 mt-1">1 day ago</div>
                </li>
              </ul>
            </div>
            <div className="p-3 border-t border-slate-100">
              <button className="w-full text-xs font-medium text-blue-600 hover:text-blue-700">
                View all notifications
              </button>
            </div>
          </div>
        </>
      )}

    </div>
  );
}
