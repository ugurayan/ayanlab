# AuthorizationController

- Module: `app-vault`
- Source: `app-vault/src/main/java/com/ayanlabs/vault/controller/AuthorizationController.java`

## Endpoints

### `GET /ayanlabs/api/vault/authorization/context`

- Handler: `context`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/vault/authorization/context" \
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

