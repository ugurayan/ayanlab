# Task 2: Merge staff_iq_web Source Code into ayanlab-hrm-ui

**Goal:** Copy all source code, documentation, and configuration files from staff_iq_web into ayanlab-hrm-ui project structure.

## Requirements

### Source Code
- Remove existing minimal `ayanlab-hrm/ayanlab-hrm-ui/src`
- Copy entire `staff_iq_web/src/**` to `ayanlab-hrm/ayanlab-hrm-ui/src/**`

### Documentation
- Create `ayanlab-hrm/ayanlab-hrm-ui/docs` directory if it doesn't exist
- Copy all files from `staff_iq_web/docs/**` to `ayanlab-hrm/ayanlab-hrm-ui/docs/**`

### Configuration Files
- Copy `staff_iq_web/eslint.config.js` → `ayanlab-hrm/ayanlab-hrm-ui/`
- Copy `staff_iq_web/tailwind.config.cjs` → `ayanlab-hrm/ayanlab-hrm-ui/`
- Copy `staff_iq_web/postcss.config.cjs` → `ayanlab-hrm/ayanlab-hrm-ui/`
- Copy `staff_iq_web/.vscode/` directory (if exists) → `ayanlab-hrm/ayanlab-hrm-ui/.vscode/`
- Copy `staff_iq_web/.env` → `ayanlab-hrm/ayanlab-hrm-ui/.env.example`

### Verification
- Verify all files are copied: check `ayanlab-hrm/ayanlab-hrm-ui/src/` contains staff_iq_web's structure
- Verify config files exist in target location
- Verify docs/ directory exists with content

## Success Criteria

- All source code transferred
- All configuration files in place
- Documentation copied
- Directory structure verified
- Ready for Task 3 (dependency merge)

## Commit Message
`feat: copy staff_iq_web source code into ayanlab-hrm-ui`
