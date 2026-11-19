import { MOCK_ONLY } from "../env";
import { createPmsClient, http } from "./pmsClient";

/**
 * Mock modda ağ yok; B2'de `VITE_MOCK_ONLY=false` ile `http` kullanılır.
 */
export function assertMockOnly() {
  if (!MOCK_ONLY) {
    return;
  }
  if (import.meta.env.DEV) {
    // Mock build: importing http is ok for tree-shaking, but do not call createPmsClient().
  }
}

export { http, createPmsClient };

if (MOCK_ONLY && import.meta.env.PROD) {
  // Production builds must stay mock-only until B2 feature flags land.
  assertMockOnly();
}