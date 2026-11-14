# ProfileAuthorizationController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/ProfileAuthorizationController.java`

## Endpoints

### `GET /api/core/authorization/effective`

- Handler: `effectiveForCurrentProfile`

```bash
curl -X GET "${BASE_URL}/api/core/authorization/effective" \
  $(ayanlabs_headers_auth "${APP_ID}")
```

#### Example Response

```json
{
  "success": true,
  "message": "OK",
  "data": {
  "userId": "uuid",
  "profileId": "uuid",
  "organizationId": "uuid",
  "roles": []
}
}
```

### `GET /api/core/authorization/profiles/{profileId}/effective`

- Handler: `effectiveForProfile`

```bash
curl -X GET "${BASE_URL}/api/core/authorization/profiles/{profileId}/effective" \
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

