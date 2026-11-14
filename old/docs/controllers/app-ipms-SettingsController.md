# SettingsController

- Module: `app-ipms`
- Source: `app-ipms/src/main/java/com/ayanlabs/propertymgmt/controller/SettingsController.java`

## Endpoints

### `POST /ayanlabs/api/property-mgmt/settings/security/change-password`

- Handler: `changeMyPassword`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/settings/security/change-password" \
  $(ayanlabs_headers_auth "${APP_ID}") \
  -H "Content-Type: application/json" \
  -d '{}'
```

#### Example Response

```json
{
  "success": true,
  "message": "OK",
  "data": {
  "id": "uuid",
  "isActive": true
}
}
```

