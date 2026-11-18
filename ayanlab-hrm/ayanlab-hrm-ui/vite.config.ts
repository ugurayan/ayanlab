import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

const platformTarget = process.env.VITE_PROXY_TARGET ?? 'http://localhost:8102'

export default defineConfig({
  plugins: [react()],
  server: {
    // docs/PORTS.md — host never uses 8080
    port: 3104,
    host: '0.0.0.0',
    // Same-origin proxy avoids browser CORS while still hitting platform backend
    proxy: {
      '/auth': { target: platformTarget, changeOrigin: true },
    },
  },
})
