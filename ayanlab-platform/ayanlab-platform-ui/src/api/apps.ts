import type { ApiResponse } from "./types";
import { clientHeaders } from "./headers";

export type AppCatalogEntry = { id: string; code: string; name: string };

export async function fetchAppCatalog(): Promise<AppCatalogEntry[]> {
  const res = await fetch("/auth/api/apps/catalog", {
    headers: clientHeaders({ requireTokens: false, requireAppId: false }),
  });
  const json = (await res.json()) as ApiResponse<AppCatalogEntry[]>;
  if (!res.ok || !json.success) {
    throw new Error(json.message ?? "Could not load applications");
  }
  return json.data ?? [];
}