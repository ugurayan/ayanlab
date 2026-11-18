import axios from "axios";
import { getToken } from "../state/authStorage";

export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api";
export const authApiBaseUrl = import.meta.env.VITE_AUTH_API_BASE_URL ?? "http://localhost:1453/api/v1";

export const http = axios.create({
  baseURL: apiBaseUrl,
  timeout: 15000
});

// Separate HTTP client for auth endpoints (signup, validation)
export const authHttp = axios.create({
  baseURL: authApiBaseUrl,
  timeout: 15000
});

http.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Add token interceptor for authHttp as well
authHttp.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
