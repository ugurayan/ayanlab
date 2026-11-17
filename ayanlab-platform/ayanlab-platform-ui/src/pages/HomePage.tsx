import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { MIN_PAGE_LOADER_MS, PageLoader } from "../components/PageLoader";
import { useAuth } from "../auth/AuthContext";
import { canAccess } from "../auth/permissions";
import { ENTITY_RESOURCES } from "../config/entities";
import { useI18n } from "../i18n/Preferences";

export function HomePage() {
  const { me } = useAuth();
  const { t, entityName } = useI18n();
  const [ready, setReady] = useState(false);
  useEffect(() => {
    const t = window.setTimeout(() => setReady(true), MIN_PAGE_LOADER_MS);
    return () => window.clearTimeout(t);
  }, []);
  if (!ready) return <PageLoader label={t("common.opening")} />;
  const featured = ENTITY_RESOURCES.filter((e) =>
    ["users", "roles", "organizations", "permissions", "subscription-plans"].includes(e.id),
  ).filter((e) => canAccess(me, e.permission));

  return (
    <section className="page">
      <header className="page-head">
        <div>
          <p className="eyebrow">{t("home.eyebrow")}</p>
          <h1>{t("home.title")}</h1>
          <p className="muted">{t("home.subtitle")}</p>
        </div>
      </header>
      <div className="card-grid">
        {featured.map((e) => (
          <Link key={e.id} to={`/entities/${e.id}`} className="card card-link">
            <h2>{entityName(e.id, e.label)}</h2>
            <p className="muted mono">{e.path}</p>
          </Link>
        ))}
      </div>
    </section>
  );
}
