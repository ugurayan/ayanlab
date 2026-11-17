import { FormEvent, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { clientHeaders } from "../api/headers";
import { fetchAppCatalog, type AppCatalogEntry } from "../api/apps";
import type { ApiResponse, TokenResponse } from "../api/types";
import { saveSessionFromToken } from "../auth/session";
import { useAuth } from "../auth/AuthContext";
import { PrefsControls, useI18n } from "../i18n/Preferences";

export function RegisterPage() {
  const navigate = useNavigate();
  const { refresh } = useAuth();
  const { t } = useI18n();
  const [apps, setApps] = useState<AppCatalogEntry[]>([]);
  const [appCode, setAppCode] = useState("core");
  useEffect(() => {
    fetchAppCatalog().then(setApps).catch(() => undefined);
  }, []);
  const selected = apps.find((a) => a.code === appCode) ?? apps[0];
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [phone, setPhone] = useState("+10000000099");
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    setError(null);
    const res = await fetch("/auth/api/register", {
      method: "POST",
      headers: clientHeaders({ appId: selected?.id, requireTokens: false }),
      body: JSON.stringify({ email, password, firstName, lastName, phone }),
    });
    const json = (await res.json()) as ApiResponse<TokenResponse>;
    if (!res.ok || !json.success) {
      setError(json.message ?? "Registration failed");
      return;
    }
    if (!selected) return;
    saveSessionFromToken(selected.id, json.data, email);
    refresh();
    navigate("/");
  }

  return (
    <div className="auth-shell">
      <div className="auth-prefs"><PrefsControls /></div>
      <form className="auth-card" onSubmit={onSubmit}>
        <h1>{t("auth.createAccount")}</h1>
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
          {t("auth.firstName")}
          <input value={firstName} onChange={(e) => setFirstName(e.target.value)} required />
        </label>
        <label>
          {t("auth.lastName")}
          <input value={lastName} onChange={(e) => setLastName(e.target.value)} required />
        </label>
        <label>
          {t("auth.email")}
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
        </label>
        <label>
          {t("auth.password")}
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
        </label>
        <label>
          {t("auth.phone")}
          <input value={phone} onChange={(e) => setPhone(e.target.value)} required />
        </label>
        {error && <p className="error">{error}</p>}
        <button type="submit">{t("auth.register")}</button>
        <Link to="/login">{t("auth.haveAccount")}</Link>
      </form>
    </div>
  );
}