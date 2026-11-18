# Task 5: Update Entry Points and HTML

**Goal:** Configure proper React entry points (HTML and main.tsx) for the application.

## Requirements

### Update index.html
Replace `ayanlab-hrm/ayanlab-hrm-ui/index.html` with:

```html
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
```

### Update src/main.tsx
Replace `ayanlab-hrm/ayanlab-hrm-ui/src/main.tsx` with:

```typescript
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
```

### Verification
- Verify `index.html` has correct Vite module script reference
- Verify `src/main.tsx` has proper React.StrictMode wrapping
- Verify `src/App.tsx` exists and is the staff_iq_web app component
- Verify `src/index.css` exists

## Success Criteria

- index.html updated with Vite module entry point
- main.tsx configured for React mounting
- App.tsx exists with proper React component
- index.css exists for styling
- Ready for Task 6 (TypeScript error fixes)

## Commit Message
`feat: configure entry points for React application`
