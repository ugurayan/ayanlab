# PropertyTypeController

- Module: `app-ipms`
- Source: `app-ipms/src/main/java/com/ayanlabs/propertymgmt/controller/PropertyTypeController.java`

## Endpoints

### `GET /ayanlabs/api/property-mgmt/property-types`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/property-mgmt/property-types" \
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

### `GET /ayanlabs/api/property-mgmt/property-types/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/property-mgmt/property-types/{id}" \
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

### `POST /ayanlabs/api/property-mgmt/property-types`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/property-types" \
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

### `PUT /ayanlabs/api/property-mgmt/property-types/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/ayanlabs/api/property-mgmt/property-types/{id}" \
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

### `DELETE /ayanlabs/api/property-mgmt/property-types/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/ayanlabs/api/property-mgmt/property-types/{id}" \
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

