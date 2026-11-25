import { useEffect, useMemo, useState } from "react";
import { Link, NavLink, Outlet, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { canAccess } from "../auth/permissions";
import {
  AUTH_NAV_SEQUENCE,
  ENTITY_RESOURCES,
  NAV_FOLDERS,
  type EntityResource,
  type NavFolderId,
} from "../config/entities";
import { IconChevron } from "./Icons";
import { PrefsControls, useI18n } from "../i18n/Preferences";

const groups = ["Auth & RBAC", "Core business"] as const;

export function AppLayout() {
  const { logout, session, me } = useAuth();
  const { t, entityName } = useI18n();
  const location = useLocation();
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const [mobileNav, setMobileNav] = useState(false);
  const [query, setQuery] = useState("");
  const [openFolders, setOpenFolders] = useState<Record<NavFolderId, boolean>>({
    applications: true,
    profile: false,
    role: false,
    policyGroup: false,
    audit: false,
  });

  useEffect(() => {
    setMenuOpen(false);
    setMobileNav(false);
  }, [location.pathname]);

  const activeEntityId = ENTITY_RESOURCES.find((e) => location.pathname === `/entities/${e.id}`)?.id;

  useEffect(() => {
    const folder = NAV_FOLDERS.find(
      (f) =>
        Boolean(activeEntityId) &&
        (f.pageId === activeEntityId || f.childIds.includes(activeEntityId ?? "")),
    );
    if (folder) {
      setOpenFolders((cur) => ({ ...cur, [folder.id]: true }));
    }
  }, [activeEntityId]);

  const accessible = useMemo(
    () => ENTITY_RESOURCES.filter((e) => canAccess(me, e.permission)),
    [me],
  );

  const nav = useMemo(() => {
    const q = query.trim().toLowerCase();
    if (!q) return accessible;
    return accessible.filter(
      (e) => entityName(e.id, e.label).toLowerCase().includes(q) || e.id.toLowerCase().includes(q),
    );
  }, [accessible, query, entityName]);

  const initials = (me?.firstName?.[0] ?? session?.email?.[0] ?? "A").toUpperCase();
  const current = ENTITY_RESOURCES.find((e) => location.pathname === `/entities/${e.id}`);
  const title = current
    ? entityName(current.id, current.label)
    : location.pathname === "/profile"
      ? t("nav.profile")
      : t("nav.overview");

  function toggleFolder(id: NavFolderId) {
    setOpenFolders((cur) => ({ ...cur, [id]: !cur[id] }));
  }

  function openFolder(id: NavFolderId) {
    setOpenFolders((cur) => ({ ...cur, [id]: true }));
  }

  function renderLink(e: EntityResource, nested = false) {
    const label = entityName(e.id, e.label);
    return (
      <li key={nested ? `nav-child-${e.id}` : `nav-leaf-${e.id}`}>
        <NavLink
          to={`/entities/${e.id}`}
          title={label}
          className={({ isActive }) =>
            ["nav-item", nested ? "nav-child" : "nav-leaf", isActive ? "is-active" : ""].filter(Boolean).join(" ")
          }
        >
          {collapsed ? (
            <span className="nav-abbrev">{label.charAt(0)}</span>
          ) : (
            <>
              {!nested && <span className="nav-leaf-gutter" aria-hidden />}
              <span className="nav-item-label">{label}</span>
            </>
          )}
        </NavLink>
      </li>
    );
  }

  return (
    <div className={`shell ${collapsed ? "collapsed" : ""} ${mobileNav ? "nav-open" : ""}`}>
      {mobileNav && <button type="button" className="nav-scrim" aria-label="Close menu" onClick={() => setMobileNav(false)} />}
      <aside className="sidebar">
        <div className="brand-row">
          <Link to="/" className="brand">
            <span className="logo-mark">A</span>
            {!collapsed && (
              <span>
                <strong>AyanLab</strong>
                <em>Platform</em>
              </span>
            )}
          </Link>
          <button type="button" className="icon-btn" onClick={() => setCollapsed((c) => !c)} aria-label="Toggle sidebar">
            ☰
          </button>
        </div>
        {groups.map((group) => {
          const items = nav.filter((e) => e.group === group);
          const q = query.trim().toLowerCase();
          const folders =
            group === "Auth & RBAC"
              ? NAV_FOLDERS.map((folder) => {
                  const page = folder.pageId ? accessible.find((e) => e.id === folder.pageId) : undefined;
                  const allChildren = folder.childIds
                    .map((id) => accessible.find((e) => e.id === id))
                    .filter((e): e is EntityResource => Boolean(e));
                  const titleHit = Boolean(q) && t(folder.titleKey).toLowerCase().includes(q);
                  const pageHit = Boolean(
                    page &&
                      (!q ||
                        titleHit ||
                        entityName(page.id, page.label).toLowerCase().includes(q) ||
                        page.id.toLowerCase().includes(q)),
                  );
                  const children =
                    !q || titleHit
                      ? allChildren
                      : allChildren.filter(
                          (e) => entityName(e.id, e.label).toLowerCase().includes(q) || e.id.toLowerCase().includes(q),
                        );
                  return { folder, page, children, visible: children.length > 0 || pageHit || titleHit };
                }).filter((x) => x.visible)
              : [];
          const nestedIds = new Set(
            NAV_FOLDERS.flatMap((folder) => folder.childIds.concat(folder.pageId ? [folder.pageId] : [])),
          );
          const sequencedEntityIds = new Set(
            AUTH_NAV_SEQUENCE.filter((e) => e.type === "entity").map((e) => e.id),
          );
          const rest = items.filter((e) => !nestedIds.has(e.id) && !sequencedEntityIds.has(e.id));
          if (!folders.length && !rest.length && !items.some((e) => sequencedEntityIds.has(e.id))) return null;
          const folderById = new Map(folders.map((f) => [f.folder.id, f]));

          function renderFolder(folderId: NavFolderId) {
            const row = folderById.get(folderId);
            if (!row) return null;
            const { folder, page, children } = row;
            const searching = Boolean(query.trim());
            const pageActive = Boolean(folder.pageId) && folder.pageId === activeEntityId;
            const childActive = children.some((c) => c.id === activeEntityId);
            const selected = pageActive || childActive;
            const open = searching || openFolders[folder.id] || childActive;
            const folderTitle = t(folder.titleKey);
            const folderClass = `nav-item nav-folder-btn ${open ? "is-open" : ""} ${selected ? "is-active" : ""}`;
            const body = (
              <>
                <span className={`nav-chevron ${open ? "is-open" : ""}`}>
                  <IconChevron size={14} />
                </span>
                {collapsed ? (
                  <span className="nav-abbrev">{folderTitle.charAt(0)}</span>
                ) : (
                  <span className="nav-item-label">{folderTitle}</span>
                )}
              </>
            );
            return (
              <li key={folder.id} className={`nav-folder ${open ? "is-open" : ""} ${selected ? "is-active" : ""}`}>
                {page ? (
                  <NavLink
                    to={`/entities/${page.id}`}
                    title={folderTitle}
                    className={folderClass}
                    aria-expanded={open}
                    onClick={() => {
                      if (pageActive) toggleFolder(folder.id);
                      else openFolder(folder.id);
                    }}
                  >
                    {body}
                  </NavLink>
                ) : (
                  <button
                    type="button"
                    className={folderClass}
                    onClick={() => toggleFolder(folder.id)}
                    title={folderTitle}
                    aria-expanded={open}
                  >
                    {body}
                  </button>
                )}
                {open && !collapsed && <ul className="nav-sub">{children.map((e) => renderLink(e, true))}</ul>}
              </li>
            );
          }

          return (
            <div key={group}>
              {!collapsed && <h3 className="nav-group">{group === "Auth & RBAC" ? t("nav.auth") : t("nav.core")}</h3>}
              <ul className="nav">
                {group === "Auth & RBAC"
                  ? AUTH_NAV_SEQUENCE.map((entry) => {
                      if (entry.type === "folder") return renderFolder(entry.id);
                      const entity = items.find((e) => e.id === entry.id);
                      return entity ? renderLink(entity) : null;
                    })
                  : null}
                {rest.map((e) => renderLink(e))}
              </ul>
            </div>
          );
        })}
        <div className="sidebar-foot">
          <Link to="/profile" className="foot-profile">
            <span className="avatar">{initials}</span>
            {!collapsed && <span className="foot-meta">{me?.email ?? session?.email}</span>}
          </Link>
        </div>
      </aside>
      <div className="main-col">
        <header className="topbar">
          <button type="button" className="icon-btn mobile-nav-btn" aria-label="Open menu" onClick={() => setMobileNav(true)}>
            ☰
          </button>
          <div className="crumbs">
            <span>AyanLab</span>
            <span>/</span>
            <strong>{title}</strong>
          </div>
          <input
            className="global-search"
            placeholder={t("nav.filter")}
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
          <div className="top-actions">
            <PrefsControls />
            <button type="button" className="btn" onClick={() => window.open("/swagger-ui/index.html", "_blank")}>
              {t("nav.api")}
            </button>
            <div className="avatar-wrap">
              <button type="button" className="avatar" onClick={() => setMenuOpen((o) => !o)} aria-haspopup="menu">
                {initials}
              </button>
              {menuOpen && (
                <div className="dropdown" role="menu">
                  <button type="button" className="btn" onClick={() => navigate("/profile")}>
                    {t("nav.profile")}
                  </button>
                  <button type="button" className="btn btn-danger" onClick={logout}>
                    {t("nav.signOut")}
                  </button>
                </div>
              )}
            </div>
          </div>
        </header>
        <main className="content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
