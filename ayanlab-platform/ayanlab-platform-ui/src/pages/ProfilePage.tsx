import { useEffect, useState } from "react";
import { useAuth } from "../auth/AuthContext";
import { PageLoader, useMinimumLoading } from "../components/PageLoader";
import { useI18n } from "../i18n/Preferences";

export function ProfilePage() {
  const { me, session, reloadMe } = useAuth();
  const { t } = useI18n();
  const [loading, setLoading] = useState(true);
  const showLoader = useMinimumLoading(loading);

  useEffect(() => {
    void reloadMe().finally(() => setLoading(false));
  }, [reloadMe]);

  if (showLoader) return <PageLoader label={t("profile.loading")} />;

  const name = [me?.firstName, me?.lastName].filter(Boolean).join(" ") || session?.email || "—";

  return (
    <section className="page">
      <header className="page-head">
        <div>
          <p className="eyebrow">{t("profile.eyebrow")}</p>
          <h1>{t("profile.title")}</h1>
        </div>
      </header>
      <div className="card-grid">
        <article className="card">
          <h2>{t("profile.identity")}</h2>
          <dl className="meta-list">
            <div>
              <dt>{t("profile.name")}</dt>
              <dd>{name}</dd>
            </div>
            <div>
              <dt>Email</dt>
              <dd>{me?.email ?? session?.email}</dd>
            </div>
            <div>
              <dt>User ID</dt>
              <dd className="mono">{me?.userId ?? session?.userId}</dd>
            </div>
            <div>
              <dt>App ID</dt>
              <dd className="mono">{me?.appId ?? session?.appId}</dd>
            </div>
            <div>
              <dt>Active profile</dt>
              <dd className="mono">{me?.profileId ?? session?.profileId ?? "—"}</dd>
            </div>
          </dl>
        </article>
        <article className="card">
          <h2>{t("profile.profiles")}</h2>
          {!me?.profiles?.length ? (
            <p className="muted">{t("common.empty")}</p>
          ) : (
            <ul className="stack-list">
              {me.profiles.map((p) => (
                <li key={p.id}>
                  <strong>{p.profileSlug ?? p.id}</strong>
                  <span className={`badge ${p.isActive === false ? "badge-warn" : "badge-ok"}`}>
                    {p.isActive === false ? t("common.inactive") : t("common.active")}
                  </span>
                </li>
              ))}
            </ul>
          )}
        </article>
        <article className="card">
          <h2>{t("profile.roles")}</h2>
          <p className="muted">{t("home.subtitle")}</p>
          <div className="chip-row">
            {(me?.authorization?.roleCodes ?? []).map((r) => (
              <span key={r} className="chip">
                {r}
              </span>
            ))}
          </div>
          <div className="chip-row">
            {(me?.authorization?.permissionCodes ?? []).slice(0, 40).map((p) => (
              <span key={p} className="chip chip-muted">
                {p}
              </span>
            ))}
          </div>
        </article>
      </div>
    </section>
  );
}
