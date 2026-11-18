import axios from "axios";
import { getToken } from "../state/authStorage";

/** HRM product API (mock or ayanlab-hrm-backend). */
export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8104/hrm/api";

/**
 * Platform auth — ayanlab-platform-backend.
 * Empty string = same-origin; Vite proxies `/auth` → http://localhost:8102 (no CORS).
 */
export const authApiBaseUrl = import.meta.env.VITE_AUTH_API_BASE_URL ?? "";

/** StaffIQ / HRM app id from platform apps catalog. */
export const appId =
  import.meta.env.VITE_APP_ID ?? "f01ae844-85fd-48a0-90ed-8bdd7e0e48a6";

export const appKey = import.meta.env.VITE_APP_KEY ?? "staffiq";

export const http = axios.create({
  baseURL: apiBaseUrl,
  timeout: 15000
});

export const authHttp = axios.create({
  baseURL: authApiBaseUrl,
  timeout: 15000
});

function correlationId() {
  return `${Date.now()}-${Math.random().toString(36).slice(2, 10)}`;
}

http.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  config.headers["X-App-Id"] = appId;
  config.headers["X-Client-Type"] = "Web";
  config.headers["X-Correlation-Id"] = correlationId();
  return config;
});

authHttp.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  config.headers["X-App-Id"] = appId;
  config.headers["X-Client-Type"] = "Web";
  config.headers["X-Client-Language"] =
    (typeof window !== "undefined" && window.localStorage.getItem("language")) || "en";
  config.headers["X-Correlation-Id"] = correlationId();
  return config;
});
