# AuthController

- Module: `app-vault`
- Source: `app-vault/src/main/java/com/ayanlabs/vault/controller/AuthController.java`

## Endpoints

### `POST /ayanlabs/api/vault/auth/register`

- Handler: `register`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/vault/auth/register" \
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

### `POST /ayanlabs/api/vault/auth/login`

- Handler: `login`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/vault/auth/login" \
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

### `POST /ayanlabs/api/vault/auth/confirm-email`

- Handler: `confirmEmail`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/vault/auth/confirm-email" \
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

### `POST /ayanlabs/api/vault/auth/confirm-sms`

- Handler: `confirmSms`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/vault/auth/confirm-sms" \
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

### `POST /ayanlabs/api/vault/auth/forgot-password`

- Handler: `forgotPassword`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/vault/auth/forgot-password" \
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

### `POST /ayanlabs/api/vault/auth/reset-password`

- Handler: `resetPassword`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/vault/auth/reset-password" \
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

### `POST /ayanlabs/api/vault/auth/forgot-username`

- Handler: `forgotUsername`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/vault/auth/forgot-username" \
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

### `POST /ayanlabs/api/vault/auth/change-password`

- Handler: `changePassword`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/vault/auth/change-password" \
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

### `POST /ayanlabs/api/vault/auth/enroll`

- Handler: `enrollToApp`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/vault/auth/enroll" \
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

### `POST /ayanlabs/api/vault/auth/refresh`

- Handler: `refreshToken`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/vault/auth/refresh" \
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

### `POST /ayanlabs/api/vault/auth/logout`

- Handler: `logout`

```bash
curl -X POST "${BASE_URL}/ayanlabs/api/vault/auth/logout" \
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

