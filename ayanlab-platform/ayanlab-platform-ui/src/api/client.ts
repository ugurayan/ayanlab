import type { ApiResponse } from "./types";
import { clientHeaders } from "./headers";
import { getSession } from "../auth/session";

const API_BASE = import.meta.env.VITE_API_BASE ?? "";

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

export async function apiRequest<T>(
  path: string,
  { method = "GET", body, requireTokens = true, searchParams }: RequestOpts = {},
): Promise<ApiResponse<T>> {
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

  const json = (await res.json().catch(() => null)) as ApiResponse<T> | null;
  if (!res.ok) {
    throw new ApiError(json?.message ?? res.statusText, res.status, json);
  }
  if (json && json.success === false) {
    throw new ApiError(json.message ?? "Request failed", res.status, json);
  }
  return json as ApiResponse<T>;
}