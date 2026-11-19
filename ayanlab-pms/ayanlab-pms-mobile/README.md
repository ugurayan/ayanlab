# ayanlab-pms-mobile

Property Management + HOA mobil mock (Flutter). **API yok** — backend (`8105`) bağlantısı bir sonraki fazda (M1.4 / B2).

## Çalıştırma

```bash
cd ayanlab-pms/ayanlab-pms-mobile
flutter pub get
flutter run
```

## Demo hesaplar (web `ayanlab-pms-ui` ile hizalı)

| Persona | E-posta | Şifre |
|---------|---------|-------|
| Yönetici | `pm@demo.ayanlab` | `password` |
| Sakin / malik | `owner@demo.ayanlab` | `password` |
| Saha | `field@demo.ayanlab` | `password` |

### API modu (M1.4)

Varsayılan mock. Gerçek backend için:

```bash
flutter run --dart-define=USE_PMS_API=true \
  --dart-define=PMS_API_BASE=http://localhost:8105/pms/api \
  --dart-define=AUTH_API_BASE=http://localhost:8102/auth/api
```

`lib/core/config/app_config.dart` sabitleri.

Giriş ekranında tek tık demo butonları ve çekmecede **Demo persona** radyo seçici vardır.

## Mock özellikler

- **Yönetici:** onaylar, ihtarlar, bakım özeti
- **Sakin:** aidat/borç, ATS 10 hane, kargo, duyuru, bakım talebi
- **Saha:** tur kontrol (QR mock), atanmış iş emirleri

## Notlar

- 11 dil (en, zh, hi, es, fr, ar, bn, pt, ru, tr, ur); `ar` ve `ur` RTL.
- Şablon kökü: `scripts/mobile-template` (`scripts/scaffold-mobile.sh`).