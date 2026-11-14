#!/usr/bin/env bash
set -euo pipefail
TARGET="$1"
SERVICE_NAME="$2"
VITE_PORT="$3"

mkdir -p "${TARGET}/src"

cat > "${TARGET}/package.json" <<EOF
{
  "name": "${SERVICE_NAME}",
  "private": true,
  "version": "0.1.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc -b && vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^19.0.0",
    "react-dom": "^19.0.0"
  },
  "devDependencies": {
    "@types/react": "^19.0.0",
    "@types/react-dom": "^19.0.0",
    "@vitejs/plugin-react": "^4.3.4",
    "typescript": "~5.7.2",
    "vite": "^6.0.0"
  }
}
EOF

cat > "${TARGET}/tsconfig.json" <<'EOF'
{
  "compilerOptions": {
    "target": "ES2022",
    "lib": ["ES2022", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "jsx": "react-jsx",
    "strict": true,
    "noEmit": true
  },
  "include": ["src"]
}
EOF

cat > "${TARGET}/tsconfig.node.json" <<'EOF'
{
  "compilerOptions": {
    "composite": true,
    "module": "ESNext",
    "moduleResolution": "bundler"
  },
  "include": ["vite.config.ts"]
}
EOF

cat > "${TARGET}/vite.config.ts" <<EOF
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  server: { port: ${VITE_PORT}, host: true },
  preview: { port: ${VITE_PORT}, host: true }
});
EOF

cat > "${TARGET}/index.html" <<EOF
<!doctype html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>${SERVICE_NAME}</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.tsx"></script>
  </body>
</html>
EOF

cat > "${TARGET}/src/main.tsx" <<'EOF'
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App";
import "./index.css";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
  </StrictMode>
);
EOF

cat > "${TARGET}/src/App.tsx" <<EOF
export default function App() {
  return (
    <main style={{ fontFamily: "system-ui", padding: "2rem" }}>
      <h1>${SERVICE_NAME}</h1>
      <p>React + Vite shell — connect to backend via API gateway or env.</p>
    </main>
  );
}
EOF

cat > "${TARGET}/src/index.css" <<'EOF'
* { box-sizing: border-box; }
body { margin: 0; background: #0f1419; color: #e7ecf1; }
EOF

cat > "${TARGET}/src/vite-env.d.ts" <<'EOF'
/// <reference types="vite/client" />
EOF

cat > "${TARGET}/nginx.conf" <<'EOF'
server {
    listen 80;
    root /usr/share/nginx/html;
    index index.html;
    location / {
        try_files $uri $uri/ /index.html;
    }
}
EOF

cat > "${TARGET}/Dockerfile" <<'EOF'
# syntax=docker/dockerfile:1
FROM node:22-alpine AS build
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
EOF

cat > "${TARGET}/.dockerignore" <<'EOF'
node_modules
dist
EOF