# AuthorizationController

- Module: `app-ipms`
- Source: `app-ipms/src/main/java/com/ayanlabs/propertymgmt/controller/AuthorizationController.java`

## Endpoints

### `GET /ayanlabs/api/property-mgmt/authorization/context`

- Handler: `context`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/property-mgmt/authorization/context" \
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

