# SubscriptionController

- Module: `app-ipms`
- Source: `app-ipms/src/main/java/com/ayanlabs/propertymgmt/controller/SubscriptionController.java`

## Endpoints

### `GET /ayanlabs/api/propertymgmt/subscriptions/plans`

- Handler: `plans`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/propertymgmt/subscriptions/plans" \
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

### `GET /ayanlabs/api/propertymgmt/subscriptions/users/me`

- Handler: `mySubscriptions`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/propertymgmt/subscriptions/users/me" \
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

### `POST /ayanlabs/api/propertymgmt/subscriptions/users/me`

- Handler: `startMySubscription`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/propertymgmt/subscriptions/users/me" \
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

### `GET /ayanlabs/api/propertymgmt/subscriptions/organizations/{organizationId}`

- Handler: `organizationSubscriptions`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/propertymgmt/subscriptions/organizations/{organizationId}" \
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

### `POST /ayanlabs/api/propertymgmt/subscriptions/organizations/{organizationId}`

- Handler: `startOrganizationSubscription`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/propertymgmt/subscriptions/organizations/{organizationId}" \
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

