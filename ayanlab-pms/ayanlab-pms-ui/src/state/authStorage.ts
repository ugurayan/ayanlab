import type { User } from "../types/auth";

const TOKEN = "pms.access_token";
const LEGACY_TOKEN = "pms.mock.token";
const USER = "pms.user";
const LEGACY_USER = "pms.mock.user";

export function getToken(): string | null {
  return localStorage.getItem(TOKEN) ?? localStorage.getItem(LEGACY_TOKEN);
}

export function setToken(token: string) {
  localStorage.removeItem(LEGACY_TOKEN);
  localStorage.setItem(TOKEN, token);
}

export function getUser(): User | null {
  const raw = localStorage.getItem(USER) ?? localStorage.getItem(LEGACY_USER);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as User;
  } catch {
    return null;
  }
}

export function setUser(user: User) {
  localStorage.removeItem(LEGACY_USER);
  localStorage.setItem(USER, JSON.stringify(user));
}

export function clearAuth() {
  localStorage.removeItem(TOKEN);
  localStorage.removeItem(LEGACY_TOKEN);
  localStorage.removeItem(USER);
  localStorage.removeItem(LEGACY_USER);
  localStorage.removeItem("pms.refresh_token");
}