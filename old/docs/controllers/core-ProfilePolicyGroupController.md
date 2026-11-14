# ProfilePolicyGroupController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/ProfilePolicyGroupController.java`

## Endpoints

### `POST /api/core/profile-policy-groups`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/profile-policy-groups" \
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

### `PUT /api/core/profile-policy-groups/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/profile-policy-groups/{id}" \
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

### `GET /api/core/profile-policy-groups`

- Handler: `listByProfile`

```bash
curl -X GET "${BASE_URL}/api/core/profile-policy-groups" \
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

### `GET /api/core/profile-policy-groups/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/profile-policy-groups/{id}" \
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

### `DELETE /api/core/profile-policy-groups/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/profile-policy-groups/{id}" \
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

