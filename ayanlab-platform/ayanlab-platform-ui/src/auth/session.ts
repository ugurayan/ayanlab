import type { TokenResponse } from "../api/types";

export type AuthSession = {
  accessToken: string;
  refreshToken: string;
  appId: string;
  profileId?: string;
  orgId?: string;
  userId?: string;
  email?: string;
};

const STORAGE_KEY = "ayanlabs.platform.session";

export function getSession(): AuthSession | null {
  const raw = sessionStorage.getItem(STORAGE_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as AuthSession;
  } catch {
    return null;
  }
}

export function saveSessionFromToken(
  appId: string,
  token: TokenResponse,
  email?: string,
): AuthSession {
  const session: AuthSession = {
    accessToken: token.accessToken,
    refreshToken: token.refreshToken ?? "",
    appId: token.appId ? String(token.appId) : appId,
    profileId: token.profileId != null ? String(token.profileId) : undefined,
    orgId: token.orgId != null ? String(token.orgId) : undefined,
    userId: token.userId != null ? String(token.userId) : undefined,
    email,
  };
  sessionStorage.setItem(STORAGE_KEY, JSON.stringify(session));
  return session;
}

export function clearSession(): void {
  sessionStorage.removeItem(STORAGE_KEY);
}