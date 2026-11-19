import axios, { type InternalAxiosRequestConfig } from "axios";
import { MOCK_ONLY } from "../env";

/** PMS product API (`ayanlab-pms-backend`). */
export const pmsApiBaseUrl =
  import.meta.env.VITE_PMS_API_BASE_URL ?? "http://localhost:8105/pms/api";

/** Platform auth — proxied in dev via Vite (`/auth` → 8102). */
export const authApiBaseUrl = import.meta.env.VITE_AUTH_API_BASE_URL ?? "";

export const appId =
  import.meta.env.VITE_APP_ID ?? "ad94b2d2-5022-4818-a785-55807ab139ba";

export const appKey = import.meta.env.VITE_APP_KEY ?? "ipms";

function correlationId() {
  return `${Date.now()}-${Math.random().toString(36).slice(2, 10)}`;
}

function attachHeaders(config: InternalAxiosRequestConfig) {
  const token =
    typeof window !== "undefined" ? localStorage.getItem("pms.access_token") : null;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  config.headers["X-App-Id"] = appId;
  config.headers["X-App-Key"] = appKey;
  config.headers["X-Client-Type"] = "Web";
  config.headers["X-Correlation-Id"] = correlationId();
  return config;
}

export const http = axios.create({
  baseURL: pmsApiBaseUrl,
  timeout: 20000
});

export const authHttp = axios.create({
  baseURL: authApiBaseUrl,
  timeout: 20000
});

http.interceptors.request.use(attachHeaders);
authHttp.interceptors.request.use(attachHeaders);

export function createPmsClient() {
  if (MOCK_ONLY) {
    throw new Error("PMS HTTP client disabled while VITE_MOCK_ONLY=true");
  }
  return http;
}