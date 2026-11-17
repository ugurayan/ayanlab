import { FormEvent, useEffect, useState } from "react";
import { Link, Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { fetchAppCatalog, type AppCatalogEntry } from "../api/apps";
import { PrefsControls, useI18n } from "../i18n/Preferences";

export function LoginPage() {
  const { session, login } = useAuth();
  const { t } = useI18n();
  const location = useLocation();
  const from = (location.state as { from?: string } | null)?.from ?? "/";

  const [apps, setApps] = useState<AppCatalogEntry[]>([]);
  const [appCode, setAppCode] = useState("core");
  const [email, setEmail] = useState("admin@core.local");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchAppCatalog()
      .then((list) => {
        setApps(list);
        if (list.some((a) => a.code === "core")) {
          setAppCode("core");
        } else if (list[0]) {
          setAppCode(list[0].code);
        }
      })
      .catch((e) => setError(e instanceof Error ? e.message : "Could not load apps"));
  }, []);

  if (session) return <Navigate to={from} replace />;

  const selected = apps.find((a) => a.code === appCode) ?? apps[0];

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    if (!selected) {
      setError("No application selected");
      return;
    }
    setError(null);
    setLoading(true);
    try {
      await login(selected.id, selected.code, email.trim(), password);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Login failed");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="auth-shell">
      <div className="auth-prefs">
        <PrefsControls />
      </div>
      <form className="auth-card" onSubmit={onSubmit}>
        <h1>{t("auth.title")}</h1>
        <p className="muted">{t("auth.subtitle")}</p>

        <label>
          {t("auth.application")}
          <select value={appCode} onChange={(e) => setAppCode(e.target.value)} required disabled={apps.length === 0}>
            {apps.map((a) => (
              <option key={a.id} value={a.code}>
                {a.name} ({a.code})
              </option>
            ))}
          </select>
        </label>

        <label>
          {t("auth.email")}
          <input
            type="email"
            autoComplete="username"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </label>

        <label>
          {t("auth.password")}
          <input
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>

        {error && <p className="error">{error}</p>}

        <button type="submit" disabled={loading || !selected}>
          {loading ? t("auth.signingIn") : t("auth.signIn")}
        </button>

        <nav className="auth-links">
          <Link to="/forgot-password">{t("auth.forgot")}</Link>
          <Link to="/register">{t("auth.register")}</Link>
        </nav>
      </form>
    </div>
  );
}