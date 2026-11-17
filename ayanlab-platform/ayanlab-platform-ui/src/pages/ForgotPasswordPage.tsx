import { FormEvent, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { clientHeaders } from "../api/headers";
import { fetchAppCatalog, type AppCatalogEntry } from "../api/apps";
import type { ApiResponse } from "../api/types";
import { PrefsControls, useI18n } from "../i18n/Preferences";

export function ForgotPasswordPage() {
  const { t } = useI18n();
  const [apps, setApps] = useState<AppCatalogEntry[]>([]);
  const [appCode, setAppCode] = useState("core");
  useEffect(() => {
    fetchAppCatalog().then(setApps).catch(() => undefined);
  }, []);
  const selected = apps.find((a) => a.code === appCode) ?? apps[0];
  const [emailOrMobile, setEmailOrMobile] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    setMessage(null);
    const res = await fetch("/auth/api/forgot-password", {
      method: "POST",
      headers: clientHeaders({ appId: selected?.id, requireTokens: false }),
      body: JSON.stringify({ emailOrMobile }),
    });
    const json = (await res.json()) as ApiResponse<string>;
    if (!res.ok || !json.success) {
      setError(json.message ?? "Request failed");
      return;
    }
    setMessage(json.data ?? json.message ?? "Check your email or SMS.");
  }

  return (
    <div className="auth-shell">
      <div className="auth-prefs"><PrefsControls /></div>
      <form className="auth-card" onSubmit={onSubmit}>
        <h1>{t("auth.forgot")}</h1>
        <label>
          {t("auth.application")}
          <select value={appCode} onChange={(e) => setAppCode(e.target.value)}>
            {apps.map((a) => (
              <option key={a.id} value={a.code}>
                {a.name}
              </option>
            ))}
          </select>
        </label>
        <label>
          {t("auth.emailOrMobile")}
          <input value={emailOrMobile} onChange={(e) => setEmailOrMobile(e.target.value)} required />
        </label>
        {error && <p className="error">{error}</p>}
        {message && <p className="success">{message}</p>}
        <button type="submit">{t("auth.sendReset")}</button>
        <Link to="/login">{t("auth.signIn")}</Link>
      </form>
    </div>
  );
}