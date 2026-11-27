import type { ApiResponse, TokenResponse } from "./types";
import { clientHeaders } from "./headers";
import { clearProfileAuthz } from "../auth/profileAuthz";
import { getSession, saveSessionFromToken } from "../auth/session";

const API_BASE = import.meta.env.VITE_API_BASE ?? "";

/** Sent by the backend when the access token predates a role/permission change. */
const AUTHZ_STALE_HEADER = "X-Authz-Stale";
export const SESSION_REFRESHED_EVENT = "ayanlab:session-refreshed";

export class ApiError extends Error {
  constructor(
    message: string,
    readonly status: number,
    readonly body?: unknown,
  ) {
    super(message);
  }
}

type RequestOpts = {
  method?: string;
  body?: unknown;
  requireTokens?: boolean;
  searchParams?: Record<string, string | number | undefined>;
};

let refreshInFlight: Promise<boolean> | null = null;

/** Exchanges the refresh token for a new access token carrying the current authorization. */
function refreshSession(): Promise<boolean> {
  if (refreshInFlight) return refreshInFlight;
  refreshInFlight = (async () => {
    const session = getSession();
    if (!session?.refreshToken) return false;
    clearProfileAuthz("platform");
    const res = await fetch(API_BASE + "/auth/api/refresh", {
      method: "POST",
      headers: clientHeaders({ appId: session.appId, requireTokens: false }),
      body: JSON.stringify({ refreshToken: session.refreshToken }),
    });
    const json = (await res.json().catch(() => null)) as ApiResponse<TokenResponse> | null;
    if (!res.ok || !json?.success || !json.data?.accessToken) return false;
    saveSessionFromToken(session.appId, json.data, session.email);
    window.dispatchEvent(new Event(SESSION_REFRESHED_EVENT));
    return true;
  })().finally(() => {
    refreshInFlight = null;
  });
  return refreshInFlight;
}

export async function apiRequest<T>(
  path: string,
  opts: RequestOpts = {},
  retried = false,
): Promise<ApiResponse<T>> {
  const { method = "GET", body, requireTokens = true, searchParams } = opts;
  const session = getSession();
  if (requireTokens && !session?.accessToken) {
    throw new ApiError("Not signed in", 401);
  }
  if (!session?.appId) {
    throw new ApiError("No app context", 400);
  }

  const url = new URL(API_BASE + path, window.location.origin);
  if (searchParams) {
    for (const [k, v] of Object.entries(searchParams)) {
      if (v !== undefined && v !== "") url.searchParams.set(k, String(v));
    }
  }

  const res = await fetch(url.toString(), {
    method,
    headers: clientHeaders({
      appId: session.appId,
      accessToken: session.accessToken,
      refreshToken: session.refreshToken,
      profileId: session.profileId,
      orgId: session.orgId,
      requireTokens,
    }),
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (!retried && res.headers.get(AUTHZ_STALE_HEADER) === "true" && (await refreshSession())) {
    return apiRequest<T>(path, opts, true);
  }

  const json = (await res.json().catch(() => null)) as ApiResponse<T> | null;
  if (!res.ok) {
    throw new ApiError(json?.message ?? res.statusText, res.status, json);
  }
  if (json && json.success === false) {
    throw new ApiError(json.message ?? "Request failed", res.status, json);
  }
  return json as ApiResponse<T>;
}
