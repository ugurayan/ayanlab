import React, { createContext, useContext, useMemo, useState, useEffect, useCallback } from "react";
import type { AuthSession, Permission, User } from "../types/auth";
import { clearAuth, setToken, setUser, getUser, getToken } from "./authStorage";
import { hasPermission } from "../security/permissions";
import { AUTH_USER_REFRESHED_EVENT, http } from "../api/client";
import { clearProfileAuthz, ensureProfileAuthz, type ProfileAuthzSnapshot } from "../auth/profileAuthz";

type AuthCtx = {
  user: User | null;
  userApproved: boolean;
  profileApproved: boolean;
  permissions: Set<Permission>;
  isAuthed: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  has: (p: Permission) => boolean;
};

const Ctx = createContext<AuthCtx | undefined>(undefined);

const USE_MOCKS = (import.meta.env.VITE_USE_MOCKS ?? "false") === "true";

async function loadHrmAuthz(profileId?: string): Promise<ProfileAuthzSnapshot | null> {
  return ensureProfileAuthz("hrm", profileId, async () => {
    const res = await http.get("/authorization/snapshot");
    return (res.data?.data ?? null) as ProfileAuthzSnapshot | null;
  });
}

function withSnapshot(user: User, snapshot: ProfileAuthzSnapshot | null): User {
  if (!snapshot) return user;
  return {
    ...user,
    profileId: snapshot.profileId,
    permissions: snapshot.permissions ?? user.permissions,
    roles: snapshot.roles ?? user.roles
  };
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  // Initialize user from storage on mount
  const [user, setUserState] = useState<User | null>(() => {
    // Try to restore user from localStorage on initial mount
    const storedUser = getUser();
    const storedToken = getToken();
    
    // Only restore if both user and token exist
    if (storedUser && storedToken) {
      return storedUser;
    }
    
    // If token exists but user doesn't, clear token (inconsistent state)
    if (storedToken && !storedUser) {
      clearAuth();
    }
    
    return null;
  });

  const permissions = useMemo(() => new Set<Permission>(user?.permissions ?? []), [user]);

  // Verify token validity on mount if user is restored (non-blocking)
  useEffect(() => {
    if (user && getToken()) {
      // Optionally verify token with backend
      // For now, we'll trust the stored data
      // In production, you might want to call /me endpoint to verify
      // This is intentionally non-blocking to avoid delaying initial render
      if (!USE_MOCKS) {
        // Fire and forget - don't block initial render
        import("../api/services/auth")
          .then((m) => m.apiMe())
          .then(async (freshUser) => {
            const snapshot = await loadHrmAuthz(freshUser.profileId);
            const next = withSnapshot(freshUser, snapshot);
            setUserState(next);
            setUser(next);
          })
          .catch(() => {
            // Token invalid, clear auth (but don't block render)
            clearAuth();
            clearProfileAuthz("hrm");
            setUserState(null);
          });
      }
    }
  }, []); // Only run on mount

  const login = useCallback(async (email: string, password: string) => {
    // Get current language
    const currentLang = typeof window !== "undefined" 
      ? (window.localStorage.getItem("language") || "en")
      : "en";
    
    // Always use real backend login (no mock token)
    const session: AuthSession = await import("../api/services/auth").then((m) =>
      m.apiLogin(email, password, currentLang)
    );

    // Store token
    setToken(session.accessToken);

    const snapshot = await loadHrmAuthz(session.user.profileId);
    const next = withSnapshot(session.user, snapshot);
    setUser(next);
    setUserState(next);
  }, []);

  const logout = useCallback(() => {
    clearAuth();
    clearProfileAuthz("hrm");
    setUserState(null);
  }, []);

  const has = useCallback((p: Permission) => hasPermission(permissions, p), [permissions]);

  useEffect(() => {
    const reload = () => {
      const stored = getUser();
      setUserState(stored);
      if (!stored || USE_MOCKS) return;
      void loadHrmAuthz(stored.profileId).then((snapshot) => {
        const next = withSnapshot(stored, snapshot);
        setUser(next);
        setUserState(next);
      });
    };
    window.addEventListener(AUTH_USER_REFRESHED_EVENT, reload);
    return () => window.removeEventListener(AUTH_USER_REFRESHED_EVENT, reload);
  }, []);

  const value: AuthCtx = useMemo(() => ({
    user,
    userApproved: user?.profileApproved !== false, // Default to true if not set
    profileApproved: user?.profileApproved !== false, // Default to true if not set
    permissions,
    isAuthed: !!user,
    login,
    logout,
    has
  }), [user, permissions, login, logout, has]);

  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}

export function useAuth() {
  const ctx = useContext(Ctx);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
