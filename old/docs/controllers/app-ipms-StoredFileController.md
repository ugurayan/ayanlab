# StoredFileController

- Module: `app-ipms`
- Source: `app-ipms/src/main/java/com/ayanlabs/propertymgmt/controller/StoredFileController.java`

## Endpoints

### `GET /ayanlabs/api/property-mgmt/stored-files`

- Handler: `list`

```bash
curl -X GET "${BASE_URL}/ayanlabs/api/property-mgmt/stored-files" \
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

### `POST /ayanlabs/api/property-mgmt/stored-files/upload`

- Handler: `upload`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/property-mgmt/stored-files/upload" \\
  $(ayanlabs_headers_auth "${APP_ID}") \\
  -F "file=@/tmp/sample.pdf" \\
  -F "folder=common"
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

