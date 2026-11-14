# AuthorizationController

- Module: `app-crm`
- Source: `app-crm/src/main/java/com/ayanlabs/accounting/controller/AuthorizationController.java`

## Endpoints

### `GET /ayanlabs/api/accounting/authorization/context`

- Handler: `context`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/accounting/authorization/context" \
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

