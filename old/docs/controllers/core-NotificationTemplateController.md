# NotificationTemplateController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/NotificationTemplateController.java`

## Endpoints

### `POST /api/core/templates`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/templates" \
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

### `PUT /api/core/templates/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/templates/{id}" \
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

### `GET /api/core/templates/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/templates/{id}" \
  $(ayanlabs_headers_auth "${APP_ID}")
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

### `DELETE /api/core/templates/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/templates/{id}" \
  $(ayanlabs_headers_auth "${APP_ID}")
```

#### Example Response

```json
{
  "success": true,
  "message": "OK",
  "data": null
}
```

### `GET /api/core/templates`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/api/core/templates" \
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

