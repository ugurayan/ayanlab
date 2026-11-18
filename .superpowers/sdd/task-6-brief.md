# Task 6: Fix TypeScript and Import Issues

**Goal:** Fix TypeScript compilation errors and import issues so the project builds successfully.

## Requirements

### Analysis Phase
1. Run `npx tsc --noEmit 2>&1 | head -50` to identify TypeScript errors
2. Run `npm run build 2>&1` to identify build-time errors
3. Document the top 10-15 errors found

### Common Fixes to Apply
These are typical errors from staff_iq_web integration:

1. **Missing type definitions** — Install @types/node if needed:
   ```bash
   npm install --save-dev @types/node
   ```

2. **Mock API import issues** — If errors reference mock data, ensure imports exist
3. **Missing environment types** — Add vite-env.d.ts types if needed
4. **React Router version compatibility** — May need to update router setup
5. **API endpoint paths** — Verify api/endpoints.ts references work

### Fix Strategy
- Fix high-impact errors first (ones blocking compilation)
- Fix errors preventing build from completing
- Document any errors that are non-blocking (can be fixed in future PRs)
- Do NOT modify the architecture or add major refactoring

### Verification
- Run `npx tsc --noEmit` and verify success (exit code 0)
- Run `npm run build` and verify build completes
- Document any remaining warnings (acceptable for this phase)

## Success Criteria

- TypeScript compilation succeeds (npx tsc --noEmit = exit code 0)
- npm run build completes successfully
- dist/ directory exists with built output
- Ready for Task 7 (verify build and dev server)

## Commit Message
`fix: resolve TypeScript compilation errors`
