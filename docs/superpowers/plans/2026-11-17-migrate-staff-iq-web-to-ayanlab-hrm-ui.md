# Migrate staff_iq_web into ayanlab-hrm-ui Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Integrate the complete staff_iq_web project into the ayanlab-hrm-ui project structure, consolidating the web application code and preparing for future development.

**Architecture:** The migration will preserve staff_iq_web's source code structure (src/, docs/, configuration files) while integrating it into ayanlab-hrm-ui. We'll merge dependencies, update import paths, maintain git history via git-subtree, and create a unified build pipeline.

**Tech Stack:** 
- React 18/19 (staff_iq_web uses 18, ayanlab-hrm-ui uses 19)
- TypeScript 5.x
- Vite (both use it)
- Tailwind CSS + PostCSS
- Git worktrees for isolated migration

## Global Constraints

- Both projects are Node.js/React-based web applications
- Staff_iq_web has extensive source code (~50+ components, views, data hooks, API services)
- Ayanlab-hrm-ui is currently a minimal scaffold with just basic setup
- Both use Vite as the build tool
- Preserve all git history from staff_iq_web
- No breaking changes to existing ayanlab-hrm-ui minimal setup until integration is complete

---

## Task 1: Backup and Create Integration Branch

**Files:**
- No files created/modified (git preparation)

**Interfaces:**
- Produces: Integration branch ready for migration work

- [ ] **Step 1: Check current git status**

```bash
cd /Users/ugurayan/Projects/ayanlab
git status
```

Expected: Clean working directory or document any uncommitted changes that need to be stashed

- [ ] **Step 2: Create an integration branch**

```bash
cd /Users/ugurayan/Projects/ayanlab
git checkout -b feature/integrate-staff-iq-web
```

Expected: Successfully created and switched to new branch

- [ ] **Step 3: Verify branch was created**

```bash
git branch
```

Expected: Shows `* feature/integrate-staff-iq-web` (current branch)

- [ ] **Step 4: Commit**

```bash
git commit --allow-empty -m "feat: start staff_iq_web migration to ayanlab-hrm-ui"
```

---

## Task 2: Merge staff_iq_web Source Code into ayanlab-hrm-ui

**Files:**
- Copy: `staff_iq_web/src/**` → `ayanlab-hrm/ayanlab-hrm-ui/src/**`
- Copy: `staff_iq_web/docs/**` → `ayanlab-hrm/ayanlab-hrm-ui/docs/**`
- Copy: Configuration files: `.env`, `.eslintrc.config.js`, `tailwind.config.cjs`, `postcss.config.cjs`, `.vscode/`
- Modify: `ayanlab-hrm/ayanlab-hrm-ui/package.json`

**Interfaces:**
- Consumes: Existing ayanlab-hrm-ui minimal structure
- Produces: Merged source code with consolidated dependencies

- [ ] **Step 1: Backup current ayanlab-hrm-ui structure**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm
mkdir -p ../migration-backups
cp -r ayanlab-hrm-ui ../migration-backups/ayanlab-hrm-ui-backup-$(date +%s)
echo "Backup created at ../migration-backups/"
```

Expected: Backup directory created successfully

- [ ] **Step 2: Copy staff_iq_web src into ayanlab-hrm-ui**

```bash
cd /Users/ugurayan/Projects/ayanlab
# Remove existing minimal src and replace with staff_iq_web's src
rm -rf ayanlab-hrm/ayanlab-hrm-ui/src
cp -r staff_iq_web/src ayanlab-hrm/ayanlab-hrm-ui/src
echo "✓ Source code copied"
```

Expected: Existing src/ removed, staff_iq_web/src copied to ayanlab-hrm-ui/src

- [ ] **Step 3: Copy docs directory**

```bash
cd /Users/ugurayan/Projects/ayanlab
# Create docs if it doesn't exist
mkdir -p ayanlab-hrm/ayanlab-hrm-ui/docs
# Copy staff_iq_web docs
cp -r staff_iq_web/docs/* ayanlab-hrm/ayanlab-hrm-ui/docs/
echo "✓ Docs copied"
```

Expected: staff_iq_web/docs contents copied to ayanlab-hrm-ui/docs/

- [ ] **Step 4: Copy configuration files**

```bash
cd /Users/ugurayan/Projects/ayanlab
# Copy ESLint config
cp staff_iq_web/eslint.config.js ayanlab-hrm/ayanlab-hrm-ui/ 2>/dev/null || echo "Note: eslint.config.js not in staff_iq_web root"

# Copy Tailwind and PostCSS configs
cp staff_iq_web/tailwind.config.cjs ayanlab-hrm/ayanlab-hrm-ui/
cp staff_iq_web/postcss.config.cjs ayanlab-hrm/ayanlab-hrm-ui/

# Copy .vscode settings if exists
if [ -d "staff_iq_web/.vscode" ]; then
  cp -r staff_iq_web/.vscode ayanlab-hrm/ayanlab-hrm-ui/
fi

echo "✓ Configuration files copied"
```

Expected: All config files copied successfully

- [ ] **Step 5: Copy .env template**

```bash
cd /Users/ugurayan/Projects/ayanlab
if [ -f "staff_iq_web/.env" ]; then
  cp staff_iq_web/.env ayanlab-hrm/ayanlab-hrm-ui/.env.example
  echo "✓ .env template copied"
fi
```

Expected: .env copied as .env.example (don't commit actual secrets)

- [ ] **Step 6: Verify copied structure**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
ls -la src/ | head -10
echo "---"
ls -la docs/ 2>/dev/null || echo "docs/ may not exist yet"
```

Expected: src/ contains staff_iq_web's source structure (views, api, data, etc.)

- [ ] **Step 7: Stage and commit**

```bash
cd /Users/ugurayan/Projects/ayanlab
git add ayanlab-hrm/ayanlab-hrm-ui/
git commit -m "feat: copy staff_iq_web source code into ayanlab-hrm-ui"
```

---

## Task 3: Merge Dependencies and Update package.json

**Files:**
- Modify: `ayanlab-hrm/ayanlab-hrm-ui/package.json` - consolidate scripts and dependencies

**Interfaces:**
- Consumes: Both staff_iq_web and ayanlab-hrm-ui package.json files
- Produces: Unified package.json with all required dependencies

- [ ] **Step 1: Examine staff_iq_web dependencies**

```bash
cd /Users/ugurayan/Projects/ayanlab
cat staff_iq_web/package.json | grep -A 50 "dependencies"
```

Expected: List of dependencies including axios, react-query, react-hook-form, zod, lucide-react, etc.

- [ ] **Step 2: Update ayanlab-hrm-ui package.json with merged dependencies**

Replace the package.json content with:

```json
{
  "name": "ayanlab-hrm-ui",
  "private": true,
  "version": "0.1.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc -b && vite build",
    "preview": "vite preview",
    "lint": "eslint .",
    "format": "prettier . --write"
  },
  "dependencies": {
    "@hookform/resolvers": "^3.9.1",
    "@tanstack/react-query": "^5.59.0",
    "axios": "^1.7.7",
    "clsx": "^2.1.1",
    "lucide-react": "^0.469.0",
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "react-hook-form": "^7.53.2",
    "react-icons": "^5.5.0",
    "react-router-dom": "^6.27.0",
    "react-spinners": "^0.17.0",
    "zod": "^3.23.8"
  },
  "devDependencies": {
    "@eslint/js": "^9.15.0",
    "@types/react": "^18.3.12",
    "@types/react-dom": "^18.3.1",
    "@vitejs/plugin-react": "^4.3.3",
    "autoprefixer": "^10.4.20",
    "eslint": "^9.15.0",
    "eslint-plugin-react-hooks": "^5.0.0",
    "eslint-plugin-react-refresh": "^0.4.14",
    "postcss": "^8.4.49",
    "prettier": "^3.3.3",
    "tailwindcss": "^3.4.15",
    "typescript": "^5.6.3",
    "vite": "^7.2.6"
  }
}
```

Command:
```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
cat > package.json << 'EOF'
{
  "name": "ayanlab-hrm-ui",
  "private": true,
  "version": "0.1.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc -b && vite build",
    "preview": "vite preview",
    "lint": "eslint .",
    "format": "prettier . --write"
  },
  "dependencies": {
    "@hookform/resolvers": "^3.9.1",
    "@tanstack/react-query": "^5.59.0",
    "axios": "^1.7.7",
    "clsx": "^2.1.1",
    "lucide-react": "^0.469.0",
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "react-hook-form": "^7.53.2",
    "react-icons": "^5.5.0",
    "react-router-dom": "^6.27.0",
    "react-spinners": "^0.17.0",
    "zod": "^3.23.8"
  },
  "devDependencies": {
    "@eslint/js": "^9.15.0",
    "@types/react": "^18.3.12",
    "@types/react-dom": "^18.3.1",
    "@vitejs/plugin-react": "^4.3.3",
    "autoprefixer": "^10.4.20",
    "eslint": "^9.15.0",
    "eslint-plugin-react-hooks": "^5.0.0",
    "eslint-plugin-react-refresh": "^0.4.14",
    "postcss": "^8.4.49",
    "prettier": "^3.3.3",
    "tailwindcss": "^3.4.15",
    "typescript": "^5.6.3",
    "vite": "^7.2.6"
  }
}
EOF
```

Expected: package.json updated with merged dependencies

- [ ] **Step 3: Install dependencies in ayanlab-hrm-ui**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
npm install
```

Expected: Dependencies installed successfully, package-lock.json created

- [ ] **Step 4: Verify installation**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
npm list react react-dom | head -10
```

Expected: Shows installed versions of react and react-dom

- [ ] **Step 5: Commit**

```bash
cd /Users/ugurayan/Projects/ayanlab
git add ayanlab-hrm/ayanlab-hrm-ui/package.json ayanlab-hrm/ayanlab-hrm-ui/package-lock.json
git commit -m "feat: merge dependencies from staff_iq_web into ayanlab-hrm-ui"
```

---

## Task 4: Update Build Configuration (vite.config.ts)

**Files:**
- Modify: `ayanlab-hrm/ayanlab-hrm-ui/vite.config.ts` - ensure React plugin is configured

**Interfaces:**
- Consumes: Current vite.config.ts
- Produces: Updated Vite config compatible with staff_iq_web's React setup

- [ ] **Step 1: Check current vite config**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
cat vite.config.ts
```

Expected: Shows minimal Vite config

- [ ] **Step 2: Check staff_iq_web's vite config (if exists)**

```bash
cd /Users/ugurayan/Projects/ayanlab
cat staff_iq_web/vite.config.ts 2>/dev/null || echo "No vite.config.ts in staff_iq_web"
```

Expected: Either shows config or indicates it doesn't exist

- [ ] **Step 3: Update vite.config.ts with React plugin**

Replace content with:

```typescript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: '0.0.0.0'
  }
})
```

Command:
```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
cat > vite.config.ts << 'EOF'
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: '0.0.0.0'
  }
})
EOF
```

Expected: vite.config.ts updated with React plugin configuration

- [ ] **Step 4: Update tsconfig.json for React JSX**

Check current tsconfig.json:

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
cat tsconfig.json
```

Expected: Shows TypeScript configuration

- [ ] **Step 5: Ensure tsconfig.json has proper React settings**

Update if needed to include JSX support:

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "esModuleInterop": true,

    /* Bundler mode */
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx"
  },
  "include": ["src"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

Command:
```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
cat > tsconfig.json << 'EOF'
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "esModuleInterop": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx"
  },
  "include": ["src"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
EOF
```

Expected: tsconfig.json updated with JSX and proper module resolution

- [ ] **Step 6: Verify type checking works**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
npx tsc --noEmit
```

Expected: No TypeScript errors or minimal errors (errors are expected at this stage due to missing types, will be addressed in next task)

- [ ] **Step 7: Commit**

```bash
cd /Users/ugurayan/Projects/ayanlab
git add ayanlab-hrm/ayanlab-hrm-ui/vite.config.ts ayanlab-hrm/ayanlab-hrm-ui/tsconfig.json
git commit -m "feat: configure Vite and TypeScript for React"
```

---

## Task 5: Update Entry Points and HTML

**Files:**
- Modify: `ayanlab-hrm/ayanlab-hrm-ui/index.html` - ensure it references correct entry point
- Modify: `ayanlab-hrm/ayanlab-hrm-ui/src/main.tsx` - verify it mounts React app
- Verify: `ayanlab-hrm/ayanlab-hrm-ui/src/App.tsx` exists and renders staff_iq_web

**Interfaces:**
- Consumes: Current index.html and main.tsx
- Produces: Working entry point configuration

- [ ] **Step 1: Check current index.html**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
cat index.html
```

Expected: Shows minimal index.html

- [ ] **Step 2: Check staff_iq_web's index.html for reference**

```bash
cd /Users/ugurayan/Projects/ayanlab
cat staff_iq_web/index.html 2>/dev/null || echo "Checking structure..."
```

Expected: Reference implementation for index.html

- [ ] **Step 3: Update index.html with proper Vite setup**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
cat > index.html << 'EOF'
<!doctype html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/vite.svg" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>AyanLab HRM</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.tsx"></script>
  </body>
</html>
EOF
```

Expected: index.html updated with Vite module script

- [ ] **Step 4: Update src/main.tsx**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
cat > src/main.tsx << 'EOF'
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
EOF
```

Expected: main.tsx updated with proper React mounting

- [ ] **Step 5: Verify src/App.tsx exists with staff_iq_web's content**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
ls -la src/App.tsx
head -20 src/App.tsx
```

Expected: App.tsx exists and shows staff_iq_web's app component

- [ ] **Step 6: Commit**

```bash
cd /Users/ugurayan/Projects/ayanlab
git add ayanlab-hrm/ayanlab-hrm-ui/index.html ayanlab-hrm/ayanlab-hrm-ui/src/main.tsx
git commit -m "feat: configure entry points for React application"
```

---

## Task 6: Fix TypeScript and Import Issues

**Files:**
- Modify: Multiple source files as needed for type compatibility
- Verify: All imports resolve correctly

**Interfaces:**
- Consumes: staff_iq_web's source code in src/
- Produces: Type-checked, buildable source code

- [ ] **Step 1: Run TypeScript type check**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
npx tsc --noEmit 2>&1 | head -50
```

Expected: May show type errors (will be addressed)

- [ ] **Step 2: Run Vite build to check for compilation errors**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
npm run build 2>&1 | head -100
```

Expected: Build may fail with errors, output shows what needs fixing

- [ ] **Step 3: Document errors found**

Create a summary file of errors to address:

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
npm run build 2>&1 > /tmp/build-errors.log
echo "Build errors logged to /tmp/build-errors.log"
cat /tmp/build-errors.log | grep -i error | head -20
```

Expected: List of specific errors to fix

- [ ] **Step 4: Fix missing type definitions or import issues**

Common fixes:

For missing types, install or add type definitions:
```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
npm install --save-dev @types/node
```

- [ ] **Step 5: Commit once type issues are resolved**

```bash
cd /Users/ugurayan/Projects/ayanlab
git add ayanlab-hrm/ayanlab-hrm-ui/
git commit -m "fix: resolve TypeScript compilation errors"
```

---

## Task 7: Verify Build and Test Dev Server

**Files:**
- No files created/modified (verification only)

**Interfaces:**
- Consumes: Complete migrated codebase
- Produces: Confirmed working build and dev server

- [ ] **Step 1: Clean install node_modules and rebuild**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
rm -rf node_modules package-lock.json
npm install
```

Expected: Clean install completes successfully

- [ ] **Step 2: Run type check**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
npx tsc --noEmit
```

Expected: No TypeScript errors (or only expected warnings)

- [ ] **Step 3: Run production build**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
npm run build
```

Expected: Build completes successfully, output in dist/ directory

- [ ] **Step 4: Verify build output exists**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
ls -lh dist/
```

Expected: dist/ directory contains index.html, assets/, etc.

- [ ] **Step 5: Test dev server (30 second check)**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
timeout 30 npm run dev 2>&1 | head -20 || true
```

Expected: Shows "Local: http://localhost:5173/" or similar

- [ ] **Step 6: Final commit**

```bash
cd /Users/ugurayan/Projects/ayanlab
git add ayanlab-hrm/ayanlab-hrm-ui/
git commit -m "feat: verify successful build and dev server configuration"
```

---

## Task 8: Create Migration Completion Summary

**Files:**
- Create: `ayanlab-hrm/ayanlab-hrm-ui/MIGRATION.md` - document what was migrated

**Interfaces:**
- Produces: Documentation of migration for future reference

- [ ] **Step 1: Create MIGRATION.md**

```bash
cd /Users/ugurayan/Projects/ayanlab/ayanlab-hrm/ayanlab-hrm-ui
cat > MIGRATION.md << 'EOF'
# Staff iQ Web Migration to AyanLab HRM UI

## Overview

This document records the migration of the `staff_iq_web` project into the `ayanlab-hrm-ui` project completed on November 17, 2026.

## What Was Migrated

- **Source Code**: Complete `src/` directory from staff_iq_web including:
  - Views (dashboard, workers, jobs, locations, shifts, tasks, orgs, etc.)
  - API services and endpoints
  - Data hooks and state management
  - UI components and layouts
  - Types and utilities
  - Mock data and configurations

- **Configuration Files**:
  - ESLint configuration
  - Tailwind CSS configuration
  - PostCSS configuration
  - VS Code workspace settings

- **Documentation**:
  - docs/ folder with setup and feature documentation

- **Dependencies**: All npm dependencies from staff_iq_web were merged into ayanlab-hrm-ui

## Build System

- **Tool**: Vite (both projects use Vite)
- **Framework**: React 18.3.1
- **TypeScript**: 5.6.3
- **Key Dependencies**: 
  - react-hook-form for forms
  - @tanstack/react-query for data fetching
  - react-router-dom for routing
  - Tailwind CSS for styling
  - zod for validation

## Next Steps

1. Update any API endpoints to point to the correct backend services
2. Configure environment variables in `.env` for the HRM backend integration
3. Test all views and functionality with real backend APIs
4. Update routing if needed to align with HRM structure
5. Integrate with ayanlab-hrm-backend services

## Files Changed

- Merged: `staff_iq_web/src/**` → `ayanlab-hrm/ayanlab-hrm-ui/src/**`
- Merged: `staff_iq_web/docs/**` → `ayanlab-hrm/ayanlab-hrm-ui/docs/**`
- Updated: `ayanlab-hrm/ayanlab-hrm-ui/package.json`
- Updated: `ayanlab-hrm/ayanlab-hrm-ui/vite.config.ts`
- Updated: `ayanlab-hrm/ayanlab-hrm-ui/tsconfig.json`
- Updated: `ayanlab-hrm/ayanlab-hrm-ui/index.html`
- Updated: `ayanlab-hrm/ayanlab-hrm-ui/src/main.tsx`

## Verification

✅ All source code copied
✅ Dependencies installed and merged
✅ TypeScript type checking passes
✅ Production build completes successfully
✅ Dev server starts without errors

EOF
cat MIGRATION.md
```

Expected: MIGRATION.md created with complete documentation

- [ ] **Step 2: Commit**

```bash
cd /Users/ugurayan/Projects/ayanlab
git add ayanlab-hrm/ayanlab-hrm-ui/MIGRATION.md
git commit -m "docs: add migration documentation from staff_iq_web"
```

---

## Summary

This plan migrates the complete `staff_iq_web` project into the `ayanlab-hrm-ui` project by:

1. Creating an integration branch for isolated work
2. Copying all source code, docs, and configuration
3. Merging npm dependencies
4. Configuring Vite and TypeScript for React
5. Setting up proper entry points (HTML and main.tsx)
6. Fixing TypeScript compilation issues
7. Verifying successful build and dev server
8. Documenting the migration

The result is a unified `ayanlab-hrm-ui` project ready for future development with all staff_iq_web functionality integrated.
