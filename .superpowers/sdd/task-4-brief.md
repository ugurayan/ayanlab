# Task 4: Update Build Configuration (vite.config.ts and tsconfig.json)

**Goal:** Configure Vite and TypeScript for React development with proper JSX and module resolution.

## Requirements

### Update vite.config.ts
Replace `ayanlab-hrm/ayanlab-hrm-ui/vite.config.ts` with:

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

### Update tsconfig.json
Replace `ayanlab-hrm/ayanlab-hrm-ui/tsconfig.json` with:

```json
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
```

### Verification
- Run `npx tsc --noEmit` to verify TypeScript configuration
- Confirm no blocking TypeScript errors (some errors expected, will be fixed in Task 6)
- Verify both files are properly formatted

## Success Criteria

- vite.config.ts updated with React plugin configuration
- tsconfig.json updated with JSX and module resolution settings
- TypeScript type checking runs without blocking errors
- Ready for Task 5 (entry point configuration)

## Commit Message
`feat: configure Vite and TypeScript for React`
