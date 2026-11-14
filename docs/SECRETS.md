# Secrets and GitHub push protection

Do **not** commit:

- GCP service account JSON (use `GOOGLE_APPLICATION_CREDENTIALS` pointing to a file **outside** the repo)
- Twilio SID / auth token
- `JWT_SECRET`, DB passwords, mail passwords

Configure via repo-root `.env` (gitignored) and optionally `config/secrets/` (gitignored). Docker Compose loads `../.env` for each product.

```bash
cp .env.example .env
# edit .env — Twilio, JWT, POSTGIS_PASSWORD, GOOGLE_APPLICATION_CREDENTIALS, etc.
```

## If push was blocked (GH013)

GitHub scans **all commits** in the push. Removing files in the latest commit is not enough if an older commit still contains secrets.

For this repo, the problematic commit is usually `e25abed` (`core/...json`, `platform/...application.yml`).

```bash
# After sanitizing files in the working tree:
git reset --soft 785d2c6    # drops e25abed from history, keeps files staged
git add -A
git commit -m "Platform snapshot (secrets removed from repo)"
git push origin-github main
```

If `e25abed` was already pushed somewhere, rotate **GCP** and **Twilio** credentials — treat them as exposed.