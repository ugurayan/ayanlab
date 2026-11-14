# RideshareController

- Module: `app-staffiq`
- Source: `app-staffiq/src/main/java/com/ayanlabs/rideshare/controller/RideshareController.java`

## Endpoints

### `POST /ayanlabs/api/rideshare/rides/request`

- Handler: `requestRide`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/rideshare/rides/request" \
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

### `GET /ayanlabs/api/rideshare/health`

- Handler: `health`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/rideshare/health" \
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

