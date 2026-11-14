# VaultController

- Module: `app-vault`
- Source: `app-vault/src/main/java/com/ayanlabs/vault/controller/VaultController.java`

## Endpoints

### `GET /ayanlabs/api/vault/me`

- Handler: `getProfile`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/vault/me" \
  $(ayanlabs_headers_auth "${APP_ID}")
```

#### Example Response

```json
{
  "success": true,
  "message": "OK",
  "data": [
  {
    "id": "uuid",
    "isActive": true
  }
]
}
```

