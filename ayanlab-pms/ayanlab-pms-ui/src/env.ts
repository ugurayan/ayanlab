/** When true, login uses local mock users (no platform JWT). */
export const MOCK_ONLY = (import.meta.env.VITE_MOCK_ONLY ?? "true") === "true";
/** When true, business data comes from `mockApi`; when false, PMS HTTP APIs are used (requires `MOCK_ONLY=false` for auth token). */
export const USE_MOCKS = (import.meta.env.VITE_USE_MOCKS ?? "true") === "true";