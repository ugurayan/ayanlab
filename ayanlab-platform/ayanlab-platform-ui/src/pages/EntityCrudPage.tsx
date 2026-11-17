import { useCallback, useEffect, useMemo, useState } from "react";
import { Navigate, useParams } from "react-router-dom";
import { apiRequest } from "../api/client";
import type { PagedResponse } from "../api/types";
import { useAuth } from "../auth/AuthContext";
import { canAccess } from "../auth/permissions";
import { ColumnCustomize } from "../components/ColumnCustomize";
import { IconEye, IconPencil, IconPower, IconTrash } from "../components/Icons";
import { PageLoader, useMinimumLoading } from "../components/PageLoader";
import { useToast } from "../components/ToastHost";
import { entityById } from "../config/entities";
import { catalogFor, humanizeColumn } from "../config/tableColumns";
import { useColumnPrefs } from "../hooks/useColumnPrefs";
import { getSession } from "../auth/session";
import { useI18n } from "../i18n/Preferences";

type Row = Record<string, unknown>;

function rowsFromList(data: unknown): { rows: Row[]; total?: number } {
  if (data == null) return { rows: [] };
  if (Array.isArray(data)) return { rows: data as Row[] };
  if (typeof data === "object") {
    const paged = data as PagedResponse<Row>;
    if (Array.isArray(paged.content)) return { rows: paged.content, total: paged.totalElements };
    const keys = Object.keys(data as object);
    if (keys.length > 0 && !("content" in (data as object))) return { rows: [data as Row] };
  }
  return { rows: [] };
}

function cell(v: unknown): string {
  if (v == null) return "—";
  if (typeof v === "boolean") return v ? "Yes" : "No";
  if (typeof v === "object") return JSON.stringify(v);
  return String(v);
}

const SKIP = new Set(["password", "mfaSecret", "passwordResetToken", "emailConfirmationCode", "mobileVerificationCode"]);

export function EntityCrudPage() {
  const { entityId } = useParams<{ entityId: string }>();
  const resource = entityId ? entityById(entityId) : undefined;
  const { me } = useAuth();
  const { t, entityName } = useI18n();
  const toast = useToast();

  const [rows, setRows] = useState<Row[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const showLoader = useMinimumLoading(loading, undefined, entityId);

  useEffect(() => {
    setLoading(true);
  }, [entityId]);
  const [error, setError] = useState<string | null>(null);
  const [q, setQ] = useState("");
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);
  const [includeInactive, setIncludeInactive] = useState(false);
  const [selectedIds, setSelectedIds] = useState<Set<string>>(new Set());
  const [drawer, setDrawer] = useState<"create" | "edit" | "view" | null>(null);
  const [draft, setDraft] = useState<Row>({});
  const [confirm, setConfirm] = useState<{ row: Row; hard: boolean } | null>(null);
  const [sortKey, setSortKey] = useState<string | null>(null);
  const [sortDir, setSortDir] = useState<"asc" | "desc">("asc");

  const catalog = useMemo(
    () => (resource ? catalogFor(resource, rows) : { defaults: [], all: [], labels: {} }),
    [resource, rows],
  );
  const { selected: prefCols, save: saveCols, reset: resetCols } = useColumnPrefs(resource?.id, catalog.defaults);

  const load = useCallback(async () => {
    if (!resource) return;
    setLoading(true);
    setError(null);
    try {
      const session = getSession();
      const isSingleObject = resource.path.endsWith("/effective");
      const res = await apiRequest<PagedResponse<Row> | Row[] | Row>(resource.path, {
        searchParams: isSingleObject
          ? undefined
          : {
              page,
              size,
              q: q || undefined,
              includeInactive: includeInactive ? "true" : undefined,
              ...((resource.id === "users" || resource.id.startsWith("application-")) && session?.appId
                ? { appId: session.appId }
                : {}),
              ...(resource.profileScoped && session?.profileId ? { profileId: session.profileId } : {}),
              ...resource.listQuery,
            },
      });
      const parsed = rowsFromList(res.data);
      setRows(parsed.rows);
      setTotal(parsed.total ?? parsed.rows.length);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Load failed");
      setRows([]);
    } finally {
      setLoading(false);
    }
  }, [resource, page, size, q, includeInactive]);

  useEffect(() => {
    void load();
  }, [load]);

  const columns = useMemo(() => {
    const allowed = new Set(catalog.all);
    const picked = prefCols.filter((c) => allowed.has(c));
    return picked.length ? picked : catalog.defaults;
  }, [catalog, prefCols]);

  const visible = useMemo(() => {
    let list = rows;
    if (q) {
      const n = q.toLowerCase();
      list = list.filter((r) => JSON.stringify(r).toLowerCase().includes(n));
    }
    if (sortKey) {
      list = [...list].sort((a, b) => {
        const av = cell(a[sortKey]);
        const bv = cell(b[sortKey]);
        return sortDir === "asc" ? av.localeCompare(bv) : bv.localeCompare(av);
      });
    }
    return list;
  }, [rows, q, sortKey, sortDir]);

  if (!resource) return <p className="error">Unknown resource.</p>;
  if (!canAccess(me, resource.permission)) return <Navigate to="/" replace />;

  function openCreate() {
    const fields = resource?.createFields ?? columns;
    const next: Row = {};
    for (const f of fields) next[f] = "";
    setDraft(next);
    setDrawer("create");
  }

  function openEdit(row: Row, mode: "edit" | "view") {
    const copy = { ...row };
    for (const k of SKIP) delete copy[k];
    setDraft(copy);
    setDrawer(mode);
  }

  async function save() {
    if (!resource) return;
    try {
      if (drawer === "create") {
        await apiRequest(resource.path, { method: "POST", body: draft });
        toast("success", t("common.created"));
      } else if (drawer === "edit" && draft.id) {
        await apiRequest(`${resource.path}/${draft.id}`, { method: "PUT", body: draft });
        toast("success", t("common.updated"));
      }
      setDrawer(null);
      await load();
    } catch (e) {
      toast("error", e instanceof Error ? e.message : "Save failed");
    }
  }

  async function runDelete(row: Row, hard: boolean) {
    if (!resource || !row.id) return;
    try {
      await apiRequest(`${resource.path}/${row.id}`, {
        method: "DELETE",
        searchParams: hard ? { hard: "true" } : undefined,
      });
      toast("success", hard ? t("common.deleted") : t("common.deactivated"));
      setConfirm(null);
      await load();
    } catch (e) {
      toast("error", e instanceof Error ? e.message : "Delete failed");
    }
  }

  async function restore(row: Row) {
    if (!resource || !row.id) return;
    try {
      await apiRequest(`${resource.path}/${row.id}/activation`, {
        method: "PUT",
        searchParams: { active: "true" },
      });
      toast("success", t("common.restored"));
      await load();
    } catch (e) {
      toast("error", e instanceof Error ? e.message : "Restore failed");
    }
  }

  function toggleSort(col: string) {
    if (sortKey === col) setSortDir((d) => (d === "asc" ? "desc" : "asc"));
    else {
      setSortKey(col);
      setSortDir("asc");
    }
  }

  const allIds = visible.map((r) => String(r.id ?? "")).filter(Boolean);
  const allChecked = allIds.length > 0 && allIds.every((id) => selectedIds.has(id));

  return (
    <section className="page">
      <header className="page-head">
        <div>
          <p className="eyebrow">{resource.group}</p>
          <h1>{entityName(resource.id, resource.label)}</h1>
          <p className="muted mono">{resource.path}</p>
        </div>
        <div className="head-actions">
          <button type="button" className="btn" onClick={() => void load()}>
            {t("common.refresh")}
          </button>
          {!resource.readOnly && (
            <button type="button" className="btn btn-primary" onClick={openCreate}>
              {t("common.addNew")}
            </button>
          )}
        </div>
      </header>

      <div className="toolbar">
        <input className="toolbar-search" placeholder={t("common.search")} value={q} onChange={(e) => setQ(e.target.value)} />
        <div className="toolbar-meta">
          <label className="check">
            <input type="checkbox" checked={includeInactive} onChange={(e) => setIncludeInactive(e.target.checked)} />
            {t("common.includeInactive")}
          </label>
          <span className="toolbar-count muted">{total} {t("common.records")}</span>
        </div>
        <div className="toolbar-right">
          <ColumnCustomize
            all={catalog.all}
            selected={columns}
            labels={catalog.labels}
            onSave={saveCols}
            onReset={resetCols}
            labelOf={(key) => t(`col.${key}`) === `col.${key}` ? humanizeColumn(key, catalog.labels) : t(`col.${key}`)}
          />
        </div>
      </div>

      {error && <p className="error">{error}</p>}

      {showLoader ? (
        <PageLoader label={t("common.loading")} />
      ) : visible.length === 0 ? (
        <div className="empty">
          <h3>{t("common.empty")}</h3>
          <p className="muted">{t("common.emptyHint")}</p>
          {!resource.readOnly && (
            <button type="button" className="btn btn-primary" onClick={openCreate}>
              {t("common.addNew")}
            </button>
          )}
        </div>
      ) : (
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr>
                <th>
                  <input
                    type="checkbox"
                    checked={allChecked}
                    onChange={(e) => setSelectedIds(e.target.checked ? new Set(allIds) : new Set())}
                  />
                </th>
                {columns.map((c) => (
                  <th key={c}>
                    <button type="button" className="th-btn" onClick={() => toggleSort(c)}>
                      {t(`col.${c}`) === `col.${c}` ? humanizeColumn(c, catalog.labels) : t(`col.${c}`)}
                    </button>
                  </th>
                ))}
                <th>{t("common.actions")}</th>
              </tr>
            </thead>
            <tbody>
              {visible.map((row) => {
                const id = String(row.id ?? "");
                const inactive = row.isActive === false;
                return (
                  <tr key={id || cell(row)} className={inactive ? "row-inactive" : ""}>
                    <td>
                      <input
                        type="checkbox"
                        checked={selectedIds.has(id)}
                        onChange={(e) => {
                          const next = new Set(selectedIds);
                          if (e.target.checked) next.add(id);
                          else next.delete(id);
                          setSelectedIds(next);
                        }}
                      />
                    </td>
                    {columns.map((c) => (
                      <td key={c} onClick={() => openEdit(row, "view")}>
                        {c === "isActive" ? (
                          <span className={`badge ${inactive ? "badge-warn" : "badge-ok"}`}>
                            {inactive ? t("common.inactive") : t("common.active")}
                          </span>
                        ) : (
                          cell(row[c])
                        )}
                      </td>
                    ))}
                    <td className="actions">
                      <button type="button" className="icon-action" title={t("common.view")} aria-label={t("common.view")} onClick={() => openEdit(row, "view")}>
                        <IconEye />
                      </button>
                      {!resource.readOnly && (
                        <>
                          <button type="button" className="icon-action" title={t("common.edit")} aria-label={t("common.edit")} onClick={() => openEdit(row, "edit")}>
                            <IconPencil />
                          </button>
                          <button
                            type="button"
                            className="icon-action"
                            title={inactive ? t("common.restore") : t("common.deactivate")}
                            aria-label={inactive ? t("common.restore") : t("common.deactivate")}
                            onClick={() => (inactive ? void restore(row) : setConfirm({ row, hard: false }))}
                          >
                            <IconPower />
                          </button>
                          <button
                            type="button"
                            className="icon-action icon-action-danger"
                            title={t("common.delete")}
                            aria-label={t("common.delete")}
                            onClick={() => setConfirm({ row, hard: true })}
                          >
                            <IconTrash />
                          </button>
                        </>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      )}

      <footer className="pager">
        <label>
          {t("common.rows")}
          <select
            value={size}
            onChange={(e) => {
              setSize(Number(e.target.value));
              setPage(0);
            }}
          >
            {[10, 20, 50].map((n) => (
              <option key={n} value={n}>
                {n}
              </option>
            ))}
          </select>
        </label>
        <button type="button" className="btn btn-sm" disabled={page === 0} onClick={() => setPage((p) => p - 1)}>
          {t("common.prev")}
        </button>
        <span>
          Page {page + 1}
        </span>
        <button type="button" className="btn btn-sm" disabled={(page + 1) * size >= total && rows.length < size} onClick={() => setPage((p) => p + 1)}>
          {t("common.next")}
        </button>
      </footer>

      {drawer && (
        <div className="sheet-backdrop" onClick={() => setDrawer(null)}>
          <aside className="sheet sheet-drawer" onClick={(e) => e.stopPropagation()}>
            <header className="sheet-head">
              <h2>{drawer === "create" ? t("crud.create") : drawer === "edit" ? t("common.edit") : t("crud.details")}</h2>
              <button type="button" className="icon-btn" onClick={() => setDrawer(null)}>
                ×
              </button>
            </header>
            <div className="sheet-body drawer-fields">
              {Object.keys(draft).length === 0 && <p className="muted">Empty payload</p>}
              {Object.entries(draft)
                .filter(([k]) => drawer === "create" || !SKIP.has(k))
                .map(([k, v]) => (
                  <label key={k}>
                    {t(`col.${k}`) === `col.${k}` ? humanizeColumn(k, catalog.labels) : t(`col.${k}`)}
                    <input
                      disabled={drawer === "view" || k === "id"}
                      value={typeof v === "object" && v !== null ? JSON.stringify(v) : String(v ?? "")}
                      onChange={(e) => setDraft((d) => ({ ...d, [k]: e.target.value }))}
                    />
                  </label>
                ))}
            </div>
            {drawer !== "view" && (
              <footer className="sheet-foot">
                <button type="button" className="btn" onClick={() => setDrawer(null)}>
                  {t("common.cancel")}
                </button>
                <button type="button" className="btn btn-primary" onClick={() => void save()}>
                  {t("common.save")}
                </button>
              </footer>
            )}
          </aside>
        </div>
      )}

      {confirm && (
        <div className="sheet-backdrop" onClick={() => setConfirm(null)}>
          <div className="sheet sheet-dialog" onClick={(e) => e.stopPropagation()}>
            <header className="sheet-head">
              <h2>{confirm.hard ? t("crud.deleteTitle") : t("crud.deactivateTitle")}</h2>
              <button type="button" className="icon-btn" onClick={() => setConfirm(null)} aria-label="Close">
                ×
              </button>
            </header>
            <div className="sheet-body">
            <p className="muted">
              {confirm.hard
                ? t("crud.deleteBody")
                : t("crud.deactivateBody")}
            </p>
            </div>
            <div className="sheet-foot">
              <button type="button" className="btn" onClick={() => setConfirm(null)}>
                {t("common.cancel")}
              </button>
              <button type="button" className="btn btn-danger" onClick={() => void runDelete(confirm.row, confirm.hard)}>
                {confirm.hard ? t("crud.deleteForever") : t("common.deactivate")}
              </button>
            </div>
          </div>
        </div>
      )}
    </section>
  );
}
