# Platform — curl örnekleri

Genel platform endpoint'leri (Swagger, actuator).

Önce: `source docs/curl/env.example.sh`

---

## Swagger UI (tarayıcı)

```
http://localhost:8080/swagger-ui.html
```

---

## OpenAPI JSON

```bash
curl -s "${BASE_URL}/v3/api-docs" | jq '.info'
```

---

## Actuator health (auth gerekmez)

```bash
curl -s "${BASE_URL}/actuator/health" | jq .
```
