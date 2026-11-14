# SubscriptionPlanPriceController

- Module: `core`
- Source: `core/src/main/java/com/ayanlabs/core/controller/SubscriptionPlanPriceController.java`

## Endpoints

### `POST /api/core/subscription-plan-prices`

- Handler: `create`

```bash
curl -X POST "${BASE_URL}/api/core/subscription-plan-prices" \
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

### `PUT /api/core/subscription-plan-prices/{id}`

- Handler: `update`

```bash
curl -X PUT "${BASE_URL}/api/core/subscription-plan-prices/{id}" \
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

### `GET /api/core/subscription-plan-prices/{id}`

- Handler: `getById`

```bash
curl -X GET "${BASE_URL}/api/core/subscription-plan-prices/{id}" \
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

### `GET /api/core/subscription-plan-prices`

- Handler: `listByPlan`

```bash
curl -X GET "${BASE_URL}/api/core/subscription-plan-prices" \
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

### `DELETE /api/core/subscription-plan-prices/{id}`

- Handler: `deleteById`

```bash
curl -X DELETE "${BASE_URL}/api/core/subscription-plan-prices/{id}" \
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

