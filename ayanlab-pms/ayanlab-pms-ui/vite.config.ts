import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3105,
    host: true,
    proxy: {
      "/pms/api": {
        target: "http://localhost:8105",
        changeOrigin: true
      },
      "/auth": {
        target: "http://localhost:8102",
        changeOrigin: true
      }
    }
  },
  preview: { port: 3105, host: true }
});
