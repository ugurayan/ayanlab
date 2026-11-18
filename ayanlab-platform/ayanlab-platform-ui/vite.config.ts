import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3102,
    host: true,
    proxy: {
      "/auth": { target: process.env.VITE_PROXY_TARGET ?? "http://localhost:8102", changeOrigin: true },
      "/core": { target: process.env.VITE_PROXY_TARGET ?? "http://localhost:8102", changeOrigin: true },
      "/swagger-ui": { target: process.env.VITE_PROXY_TARGET ?? "http://localhost:8102", changeOrigin: true },
      "/v3": { target: process.env.VITE_PROXY_TARGET ?? "http://localhost:8102", changeOrigin: true },
    },
  },
  preview: { port: 3102, host: true },
});
