import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import type { User } from "../types/auth";
import { apiLogin } from "../api/services/auth";
import { http } from "../api/pmsClient";
import { MOCK_ONLY } from "../env";
import { mockLogin } from "../mock/mockApi";
import { clearProfileAuthz, ensureProfileAuthz } from "../auth/profileAuthz";
import { clearAuth, getToken, getUser, setToken, setUser as persistUser } from "./authStorage";

type AuthCtx = {
  user: User | null;
  isAuthed: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  impersonate: (email: string) => Promise<void>;
};

const Ctx = createContext<AuthCtx | undefined>(undefined);

function restoreUser(): User | null {
  if (!getToken()) return null;
  return getUser();
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUserState] = useState<User | null>(restoreUser);

  const login = useCallback(async (email: string, password: string) => {
    const session = MOCK_ONLY ? await mockLogin(email, password) : await apiLogin(email, password);
    setToken(session.accessToken);
    persistUser(session.user);
    setUserState(session.user);
  }, []);

  const impersonate = useCallback(
    async (email: string) => {
      if (!MOCK_ONLY) {
        throw new Error("Persona switch is only available in mock mode");
      }
      await login(email, "password");
    },
    [login]
  );

  const logout = useCallback(() => {
    clearAuth();
    clearProfileAuthz("pms");
    setUserState(null);
  }, []);

  useEffect(() => {
    if (MOCK_ONLY || !user) return;
    const token = getToken();
    const refresh = localStorage.getItem("pms.refresh_token") ?? "";
    if (!token || !refresh) return;
    void ensureProfileAuthz("pms", undefined, async () => {
      const res = await http.get("/authorization/snapshot", {
        headers: {
          "X-Client-Token": token,
          "X-Client-Refresh-Token": refresh
        }
      });
      return res.data?.data ?? null;
    });
  }, [user]);

  const value = useMemo(
    () => ({
      user,
      isAuthed: !!user,
      login,
      logout,
      impersonate
    }),
    [user, login, logout, impersonate]
  );

  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}

export function useAuth() {
  const ctx = useContext(Ctx);
  if (!ctx) throw new Error("useAuth outside AuthProvider");
  return ctx;
}