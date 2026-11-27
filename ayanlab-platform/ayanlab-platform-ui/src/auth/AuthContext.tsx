import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from "react";
import { apiRequest, SESSION_REFRESHED_EVENT } from "../api/client";
import type { ApiResponse, CurrentSession, TokenResponse } from "../api/types";
import { clientHeaders } from "../api/headers";
import { correlationId } from "../api/headers";
import { ensureProfileAuthz, clearProfileAuthz, type ProfileAuthzSnapshot } from "./profileAuthz";
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

async function loadPlatformAuthz(profileId?: string): Promise<ProfileAuthzSnapshot | null> {
  return ensureProfileAuthz("platform", profileId, async () => {
    const res = await apiRequest<ProfileAuthzSnapshot>("/auth/api/authorization/snapshot");
    return res.data ?? null;
  });
}

function applySnapshot(session: CurrentSession | null, snapshot: ProfileAuthzSnapshot | null): CurrentSession | null {
  if (!session || !snapshot) return session;
  return {
    ...session,
    profileId: snapshot.profileId ?? session.profileId,
    authorization: {
      roleCodes: snapshot.roles,
      permissionCodes: snapshot.permissions,
      policyGroupCodes: snapshot.policyGroups,
    },
  };
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<AuthSession | null>(() => getSession());
  const [me, setMe] = useState<CurrentSession | null>(null);

  const refresh = useCallback(() => setSession(getSession()), []);

  useEffect(() => {
    window.addEventListener(SESSION_REFRESHED_EVENT, refresh);
    return () => window.removeEventListener(SESSION_REFRESHED_EVENT, refresh);
  }, [refresh]);

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
      const snapshot = await loadPlatformAuthz(meRes.data?.profileId ?? saved.profileId);
      setMe(applySnapshot(meRes.data, snapshot));
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
    clearProfileAuthz("platform");
    setSession(null);
    setMe(null);
  }, []);

  useEffect(() => {
    if (!session?.accessToken) return;
    void apiRequest<CurrentSession>("/auth/api/me")
      .then(async (r) => {
        const snapshot = await loadPlatformAuthz(r.data?.profileId ?? session.profileId);
        setMe(applySnapshot(r.data, snapshot));
      })
      .catch(() => setMe(null));
  }, [session?.accessToken]);

  const reloadMe = useCallback(async () => {
    if (!getSession()?.accessToken) {
      setMe(null);
      return;
    }
    try {
      const meRes = await apiRequest<CurrentSession>("/auth/api/me");
      const snapshot = await loadPlatformAuthz(meRes.data?.profileId ?? getSession()?.profileId);
      setMe(applySnapshot(meRes.data, snapshot));
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