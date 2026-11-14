# AccountingController

- Module: `app-crm`
- Source: `app-crm/src/main/java/com/ayanlabs/accounting/controller/AccountingController.java`

## Endpoints

### `GET /ayanlabs/api/accounting/accounts`

- Handler: `getAccounts`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/accounting/accounts" \
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

### `GET /ayanlabs/api/accounting/health`

- Handler: `health`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/accounting/health" \
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

