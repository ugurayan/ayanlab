# Task 3: Merge Dependencies and Update package.json

**Goal:** Consolidate npm dependencies from staff_iq_web into ayanlab-hrm-ui and install them.

## Requirements

### Merged Dependencies
Create a new `package.json` in `ayanlab-hrm/ayanlab-hrm-ui/` with these exact contents:

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

### Installation
- Run `npm install` in `ayanlab-hrm/ayanlab-hrm-ui/`
- Verify all dependencies are installed correctly

### Verification
- Verify react and react-dom are installed at ^18.3.1
- List top-level dependencies to confirm
- Verify package-lock.json is created

## Success Criteria

- package.json updated with merged dependencies
- npm install completes successfully
- All expected packages are present in node_modules
- package-lock.json exists
- Ready for Task 4 (Vite and TypeScript configuration)

## Commit Message
`feat: merge dependencies from staff_iq_web into ayanlab-hrm-ui`
