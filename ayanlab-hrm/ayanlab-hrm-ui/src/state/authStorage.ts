const TOKEN_KEY = "token";
const USER_KEY = "ui";

// Token storage
export function setToken(token: string) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY);
}

// User data storage
export function setUser(user: import("../types/auth").User) {
  try {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  } catch (error) {
    console.error("Failed to store user data:", error);
  }
}

export function getUser(): import("../types/auth").User | null {
  try {
    const stored = localStorage.getItem(USER_KEY);
    if (!stored) return null;
    return JSON.parse(stored) as import("../types/auth").User;
  } catch (error) {
    console.error("Failed to retrieve user data:", error);
    return null;
  }
}

export function clearUser() {
  localStorage.removeItem(USER_KEY);
}

// Clear all auth data
export function clearAuth() {
  clearToken();
  clearUser();
}

// ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
// add  key
export function setKey(key: string, value: string) {
  localStorage.setItem(key, value);
}

// get key from localStorage
export function getKey(key: string): string | null {
  return localStorage.getItem(key);
}

// clear key from localStorage
export function clearKey(key: string) {
  localStorage.removeItem(key);
}

