# FinancialRecordController

- Module: `app-ipms`
- Source: `app-ipms/src/main/java/com/ayanlabs/propertymgmt/controller/FinancialRecordController.java`

## Endpoints

### `GET /ayanlabs/api/property-mgmt/financial-records`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/property-mgmt/financial-records" \
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

