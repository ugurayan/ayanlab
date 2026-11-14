# AuthorizationController

- Module: `app-staffiq`
- Source: `app-staffiq/src/main/java/com/ayanlabs/rideshare/controller/AuthorizationController.java`

## Endpoints

### `GET /ayanlabs/api/rideshare/authorization/context`

- Handler: `context`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/rideshare/authorization/context" \
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

