# IpmsHealthController

- Module: `app-ipms`
- Source: `app-ipms/src/main/java/com/ayanlabs/propertymgmt/controller/IpmsHealthController.java`

## Endpoints

### `GET /ayanlabs/api/property-mgmt/health`

- Handler: `health`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/property-mgmt/health" \
  $(ayanlabs_headers_auth "${APP_ID}")
```

#### Example Response

```json
{
  "success": true,
  "message": "OK",
  "data": {
  "status": "UP"
}
}
```

