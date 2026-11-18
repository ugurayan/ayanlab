import axios, { type AxiosResponse, type InternalAxiosRequestConfig } from "axios";
import { getToken, setToken, setUser } from "../state/authStorage";

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

/** Sent by the backend when the access token predates a role/permission change. */
const AUTHZ_STALE_HEADER = "x-authz-stale";
export const AUTH_USER_REFRESHED_EVENT = "staffiq:auth-user-refreshed";

let refreshInFlight: Promise<boolean> | null = null;

/** Exchanges the refresh token for a token with current permissions and reloads the user. */
function refreshAuthorization(): Promise<boolean> {
  if (refreshInFlight) return refreshInFlight;
  refreshInFlight = (async () => {
    const refreshToken = localStorage.getItem("refreshToken");
    if (!refreshToken) return false;
    try {
      const res = await axios.post(
        `${authApiBaseUrl}/auth/api/refresh`,
        { refreshToken },
        { headers: { "X-App-Id": appId, "X-Client-Type": "Web", "X-Correlation-Id": correlationId() } }
      );
      const data = res.data?.data ?? res.data;
      if (!data?.accessToken) return false;
      setToken(data.accessToken);
      if (data.refreshToken) localStorage.setItem("refreshToken", data.refreshToken);
      const { apiMe } = await import("./services/auth");
      setUser(await apiMe());
      window.dispatchEvent(new Event(AUTH_USER_REFRESHED_EVENT));
      return true;
    } catch {
      return false;
    }
  })().finally(() => {
    refreshInFlight = null;
  });
  return refreshInFlight;
}

type RetriableConfig = InternalAxiosRequestConfig & { _authzRetried?: boolean };

for (const instance of [http, authHttp]) {
  const retryIfStale = async (response: AxiosResponse | undefined): Promise<AxiosResponse | null> => {
    const config = response?.config as RetriableConfig | undefined;
    if (!response || !config || config._authzRetried || response.headers[AUTHZ_STALE_HEADER] !== "true") {
      return null;
    }
    if (!(await refreshAuthorization())) return null;
    config._authzRetried = true;
    return instance.request(config);
  };
  instance.interceptors.response.use(
    async (response) => (await retryIfStale(response)) ?? response,
    async (error) => {
      const retried = await retryIfStale(error?.response);
      if (retried) return retried;
      throw error;
    }
  );
}
