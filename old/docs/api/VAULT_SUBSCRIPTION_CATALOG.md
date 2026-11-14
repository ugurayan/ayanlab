# Vault abonelik kataloğu — şema, 1Password karşılaştırması ve seed

Bu belge, Vault uygulamasının PostgreSQL `core` şemasındaki abonelik tablolarının, paylaştığınız **aylık** ve **yıllık** fiyat ekran görüntülerindeki (Individual, Families, Teams Starter Pack, Business) planları destekleyip desteklemediğini özetler.

**Kısa cevap:** Evet — katalog modeli bu planları, iki faturalama dönemini, koltuk limitlerini, deneme süresini ve özellik listelerini destekler. Seed verisi `subscription-data.json` içinde tanımlıdır; uygulama her açılışta `SubscriptionDataLoader` ile **oluşturur veya günceller** (idempotent upsert).

---

## 1. Veritabanı modeli

Flyway: `platform/src/main/resources/db/V2__core_subscription_tables.sql`

| Tablo | Amaç |
|-------|------|
| `core.subscription_plan` | Plan tanımı (kod, ad, `pricing_unit`, `max_seats`, `trial_days`, üst plan mirası) |
| `core.subscription_plan_price` | Plan başına fiyat satırı (`billing_period`, `amount_cents`, `list_amount_cents`, para birimi, mağaza SKU) |
| `core.subscription_plan_feature` | Plan özellik maddeleri (UI bullet list) |
| `core.profile_subscription` | Kullanıcı profilinin **aktif** aboneliği (katalogdan ayrı) |

İlgili enum’lar:

- `BillingPeriodEnum`: `MONTHLY`, `YEARLY`
- `SubscriptionPricingUnitEnum`: `FLAT` (sabit paket), `PER_USER` (kullanıcı başı)

---

## 2. Fiyat gösterim kuralı (önemli)

Ekranlarda gördüğünüz **“$X / ay”** ifadesi, hem aylık hem yıllık sekmede **aylık eşdeğer** olarak gösterilir.

| `billing_period` | `amount_cents` anlamı | Örnek |
|------------------|----------------------|--------|
| `MONTHLY` | Gerçek aylık ücret (sent) | Individual → `499` → **$4.99/ay** |
| `YEARLY` | Yıllık faturalamada gösterilen **aylık** ücret (sent) | Individual → `240` → **$2.40/ay** (yıllık ödeme) |

Yıllık planda üstü çizili “eski aylık fiyat” için `list_amount_cents` kullanılır (yalnızca Individual ve Families seed’inde dolu):

| Plan | YEARLY `amount_cents` | YEARLY `list_amount_cents` | UI karşılığı |
|------|----------------------|----------------------------|--------------|
| Individual | 240 ($2.40) | 399 ($3.99) | İndirimli yıllık / üstü çizili aylık |
| Families | 360 ($3.60) | 599 ($5.99) | Aynı mantık |
| Teams Starter | 1995 ($19.95) | — | Yıllıkta indirim satırı yok |
| Business | 799 ($7.99) kullanıcı başı | — | `pricing_unit = PER_USER` |

**Yıllık toplam fatura** (ör. 12 × $2.40) veritabanında ayrı bir alan olarak tutulmaz; istemci `amount_cents × 12` hesaplayabilir veya yalnızca aylık eşdeğeri gösterir (1Password UI ile uyumlu).

---

## 3. Ekran görüntüleri ↔ seed karşılaştırması

Kaynak: `platform/src/main/resources/db/subscription-data.json` (`appCode`: `vault`).

### 3.1 Aylık (Monthly) — USD

| Plan | Ekran | Seed `MONTHLY` `amount_cents` | Eşleşme |
|------|-------|------------------------------|---------|
| Individual | $4.99/ay | 499 | Evet |
| Families | $7.99/ay | 799 | Evet |
| Teams Starter Pack | $24.95/ay (10 kullanıcıya kadar) | 2495 | Evet |
| Business | $9.99/kullanıcı/ay | 999 + `PER_USER` | Evet |

### 3.2 Yıllık (Annually) — USD, “billed annually”

| Plan | Ekran (aylık gösterim) | Seed `YEARLY` `amount_cents` | `list_amount_cents` | Eşleşme |
|------|------------------------|------------------------------|---------------------|---------|
| Individual | $2.40/ay (~~$3.99~~) | 240 | 399 | Evet |
| Families | $3.60/ay (~~$5.99~~) | 360 | 599 | Evet |
| Teams Starter Pack | $19.95/ay | 1995 | — | Evet |
| Business | $7.99/kullanıcı/ay | 799 | — | Evet |

### 3.3 Plan meta verisi

| Özellik | Ekran | Seed / şema |
|---------|-------|-------------|
| 14 gün deneme | Tüm ücretli planlar | `trialDays: 14` |
| Families — 5 üye | “Invite up to 5 family members” | `maxSeats: 6` (5 üye + yönetici hesabı) |
| Teams — 10 kullanıcı | “up to 10 users” | `maxSeats: 10`, `pricingUnit: FLAT` |
| Business — ölçeklenebilir | kullanıcı başı | `pricingUnit: PER_USER` |
| Özellik mirası | “Everything in X, plus” | `inheritsFromPlanCode` → DB’de `inherits_from_plan_id` |
| Özellik maddeleri | Bullet list | `subscription_plan_feature` satırları |

### 3.4 Vault’a özel ek plan

| Plan | Açıklama |
|------|----------|
| `FREE` | Pazarlama sayfasında yok; uygulama içi ücretsiz katman (`isFree: true`, fiyat satırı yok) |

---

## 4. Desteklenen ve desteklenmeyen UI öğeleri

### Desteklenen

- Aylık / yıllık geçiş → istemci `prices[]` içinden `billingPeriod` filtreler
- USD fiyatlar → `currency: "USD"` (aynı plana farklı para birimi için ek `subscription_plan_price` satırı eklenebilir)
- Sabit paket vs kullanıcı başı fiyat → `pricingUnit`
- Paket koltuk limiti → `maxSeats`
- Yıllık indirim çizgisi → `listAmountCents` (YEARLY satırında)
- Mağaza IAP → `appleStoreProductId`, `googlePlayProductId`
- Özellik listesi ve “üst planda her şey var” metni → `features` + `inheritsFromPlanId`

### Şu an modelde olmayan (istemci / ayrı süreç)

| UI öğesi | Not |
|---------|-----|
| “Password Manager” vs “Extended Access Management” sekmeleri | Ayrı ürün hattı / katalog yok; tek Vault kataloğu |
| Para birimi seçici (EUR, GBP, …) | Şema uygun; seed yalnızca USD |
| “Talk to sales” | CRM / iletişim akışı; katalog alanı değil |
| Yıllık **toplam** fatura tutarı | Hesaplanır; DB’de ayrı kolon yok |

---

## 5. Seed ve data loader

### Dosyalar

| Dosya | Rol |
|-------|-----|
| `platform/src/main/resources/db/subscription-data.json` | Tek kaynak: planlar, fiyatlar, özellikler, demo `profileSubscriptions` |
| `platform/.../SubscriptionDataLoader.java` | `@Order(15)` — `DefaultDataLoader` sonrası çalışır |

### Davranış (idempotent upsert)

1. **Plan yoksa** → oluşturur (sabit `id` JSON’dan gelir).
2. **Plan varsa** → ad, açıklama, `sortOrder`, `isFree`, `pricingUnit`, `maxSeats`, `trialDays` JSON’dan **güncellenir**.
3. **Fiyat yoksa** → oluşturur; **varsa** → `amountCents`, `listAmountCents`, `currency`, mağaza SKU’ları güncellenir.
4. **Özellik yoksa** → oluşturur; **varsa** (`featureCode` ile) → başlık, açıklama, sıra güncellenir.
5. **`inheritsFromPlanCode`** → ilk seferde `inherits_from_plan_id` set edilir (zaten doluysa dokunulmaz).
6. **`profileSubscriptions`** → yalnızca yoksa eklenir (mevcut aktif abonelik varsa atlanır).

### Yapılandırma

```properties
# application.properties / ortam
platform.db.load-subscription-data=true   # false → loader atlanır
platform.db.load-default-data=true        # app satırı yoksa katalog da atlanır
```

Fiyat veya özellik değiştirmek için **yalnızca** `subscription-data.json` düzenleyip uygulamayı yeniden başlatmanız yeterlidir; manuel SQL gerekmez.

### Log örneği

```
[SubscriptionDataLoader] Summary:
  Plans created: 0, updated: 5
  Prices created: 0, updated: 8
  Features created: 0, updated: 22
  Profile subscriptions created: 0
```

---

## 6. API (katalog)

### Genel katalog (JWT gerekmez — GET)

```http
GET /api/core/subscription-plans?appId=0cd4d187-f53e-4ebc-94cd-a601ab467094
```

Gerekli istemci başlıkları: [CONTROLLER_API_HEADERS.md](../../CONTROLLER_API_HEADERS.md) (`X-App-Id`, `X-Correlation-Id`, vb.).

Yanıt her planda `prices` ve `features` dizilerini içerir (`SubscriptionPlanView`).

### Vault modülü (JWT gerekir)

```http
GET /ayanlabs/api/vault/subscriptions/plans
```

Aynı katalog verisini uygulama modülü üzerinden sunar; mobil istemci genelde core veya vault yolundan birini seçer.

### Kullanıcı aboneliği (katalogdan farklı)

`profile_subscription` — kullanıcının hangi planda olduğu, deneme/bitiş, yenileme. Başlatma: `POST /api/core/profile-subscriptions/...` (ilgili controller ve `StartProfileSubscriptionRequest`).

---

## 7. İstemci (mobil / web) önerilen eşleme

```
billingToggle = MONTHLY | YEARLY
plans.forEach(plan => {
  price = plan.prices.find(p => p.billingPeriod === billingToggle)
  displayAmount = price.amountCents / 100
  if (billingToggle === YEARLY && price.listAmountCents) {
    strikeAmount = price.listAmountCents / 100   // üstü çizili aylık
  }
  suffix = plan.pricingUnit === PER_USER ? " per user" : ""
  seatHint = plan.maxSeats ? ` up to ${plan.maxSeats} users` : ""
})
```

Özellik mirası: `inheritsFromPlanId` dolu planlarda üst planın özelliklerini API’de birleştirmek istemci veya backend tarafında yapılabilir; şu an API düz liste döner, “INCLUDES_INDIVIDUAL” gibi açıklayıcı satırlar seed’de mevcuttur.

---

## 8. Özet tablo

| Gereksinim (ekran) | Tablo / alan | Durum |
|--------------------|--------------|--------|
| 4 ücretli plan + deneme | `subscription_plan`, `trial_days` | Tamam |
| Aylık / yıllık fiyat | `subscription_plan_price.billing_period` | Tamam |
| Yıllık indirim çizgisi | `list_amount_cents` | Individual, Families |
| 10 kullanıcılı sabit paket | `max_seats` + `FLAT` | Teams Starter |
| Kullanıcı başı iş | `PER_USER` | Business |
| Özellik listesi | `subscription_plan_feature` | Tamam |
| Plan hiyerarşisi | `inherits_from_plan_id` | Families → Individual; Business → Teams |
| IAP SKU | `apple_store_product_id`, `google_play_product_id` | Seed’de tanımlı |
| Otomatik seed | `SubscriptionDataLoader` + JSON | Tamam (upsert) |
| Ücretsiz katman | `FREE` plan | Vault’a özel |
| İkinci ürün sekmesi (EAM) | — | Yok (bilinçli boşluk) |
| Çoklu para birimi | `currency` | Şema var; seed yalnız USD |

---

## 9. Değişiklik yapmak

1. `platform/src/main/resources/db/subscription-data.json` dosyasını düzenleyin.
2. Platform’u yeniden başlatın (`platform.db.load-subscription-data=true`).
3. Katalog API’sini doğrulayın:

```bash
curl -s "http://localhost:8080/api/core/subscription-plans?appId=0cd4d187-f53e-4ebc-94cd-a601ab467094" \
  -H "X-App-Id: 0cd4d187-f53e-4ebc-94cd-a601ab467094" \
  -H "X-Correlation-Id: doc-check-1" \
  -H "X-Client-Type: WEB" \
  -H "X-Client-Version: 1.0" \
  -H "X-Client-Platform: macOS" \
  -H "X-Client-Locale: tr-TR" \
  -H "X-Client-Timezone: Europe/Istanbul" \
  -H "X-Client-Device-Id: doc-device" \
  -H "X-Client-Session-Id: doc-session" \
  -H "X-Forwarded-For: 127.0.0.1"
```

Yeni plan eklerken JSON’da sabit UUID (`id`) kullanmak, ortamlar arası tutarlılık için önerilir.
