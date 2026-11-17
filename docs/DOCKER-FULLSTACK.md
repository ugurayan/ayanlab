# UI + backend in one Docker image?

**Evet, yaygın ve mümkün.** Üç tipik model:

## 1. Tek container: nginx + JAR (önerilen “tek image”)

- nginx → statik React (`/`)
- nginx → `proxy_pass` → Spring Boot (`/ayanlabs/`, `/api/`)
- Entrypoint: arka planda `java -jar`, önde `nginx`

Avantaj: tek image, tek port (ör. 8101). Dezavantaj: UI ve API aynı lifecycle.

Şablon: `docker/Dockerfile.fullstack` (auth için kullanılabilir).

## 2. Spring Boot statik dosya

`npm run build` çıktısını `src/main/resources/static` içine koy; tek JAR/image.

Avantaj: basit deploy. Dezavantaj: her UI değişiminde backend image yeniden build.

## 3. İki container (şu anki varsayılan)

`ayanlab-platform-ui` (nginx) + `ayanlab-platform-backend` — ölçekleme ve bağımsız release için daha iyi.

---

**Özet:** Tek image istiyorsan auth’ta **nginx + backend** pattern’i kullan; vault/pms için de aynı şablon kopyalanır. Geliştirme ortamında iki container genelde daha rahattır.