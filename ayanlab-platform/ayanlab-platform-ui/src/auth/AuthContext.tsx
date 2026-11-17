import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import { apiRequest } from "../api/client";
import type { ApiResponse, CurrentSession, TokenResponse } from "../api/types";
import { clientHeaders } from "../api/headers";
import { correlationId } from "../api/headers";
import {
  clearSession,
  getSession,
  saveSessionFromToken,
  type AuthSession,
} from "./session";

type AuthContextValue = {
  session: AuthSession | null;
  me: CurrentSession | null;
  login: (appId: string, appKey: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  refresh: () => void;
  reloadMe: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<AuthSession | null>(() => getSession());
  const [me, setMe] = useState<CurrentSession | null>(null);

  const refresh = useCallback(() => setSession(getSession()), []);

  const login = useCallback(async (appId: string, appKey: string, email: string, password: string) => {
    const res = await fetch("/auth/api/login", {
      method: "POST",
      headers: clientHeaders({ appId, requireTokens: false }),
      body: JSON.stringify({ email, password, appKey }),
    });
    const json = (await res.json()) as ApiResponse<TokenResponse>;
    if (!res.ok || !json.success) {
      throw new Error(json.message ?? "Login failed");
    }
    const saved = saveSessionFromToken(appId, json.data, email);
    setSession(saved);
    try {
      const meRes = await apiRequest<CurrentSession>("/auth/api/me");
      setMe(meRes.data);
    } catch {
      setMe(null);
    }
  }, []);

  const logout = useCallback(() => {
    const s = getSession();
    if (s?.accessToken) {
      fetch("/auth/api/logout", {
        method: "POST",
        headers: {
          ...clientHeaders({
            appId: s.appId,
            accessToken: s.accessToken,
            refreshToken: s.refreshToken,
            requireTokens: false,
          }),
          Authorization: `Bearer ${s.accessToken}`,
          "X-Correlation-Id": correlationId(),
        },
      }).catch(() => undefined);
    }
    clearSession();
    setSession(null);
    setMe(null);
  }, []);

  useEffect(() => {
    if (!session?.accessToken) return;
    void apiRequest<CurrentSession>("/auth/api/me")
      .then((r) => setMe(r.data))
      .catch(() => setMe(null));
  }, [session?.accessToken]);

  const reloadMe = useCallback(async () => {
    if (!getSession()?.accessToken) {
      setMe(null);
      return;
    }
    try {
      const meRes = await apiRequest<CurrentSession>("/auth/api/me");
      setMe(meRes.data);
    } catch {
      setMe(null);
    }
  }, []);

  const value = useMemo(
    () => ({ session, me, login, logout, refresh, reloadMe }),
    [session, me, login, logout, refresh, reloadMe],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth outside AuthProvider");
  return ctx;
}