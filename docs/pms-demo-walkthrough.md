# IPMS — uçtan uca demo inceleme

Varsayılan: **Web mock** (`VITE_MOCK_ONLY=true`, `VITE_USE_MOCKS=true`). Production API URL yok; tüm menü maddeleri tıklanabilir.

## Web UI başlatma

```bash
cd ayanlab-pms/ayanlab-pms-ui
cp .env.example .env   # gerekirse
npm install
npm run dev
```

Tarayıcı: Vite’nin verdiği adres (genelde `http://localhost:5173`).

### Login

Login ekranının **üstünde** demo hesapları listelenir — **1 tıkla giriş**. Tüm hesaplar için şifre: `password`.

| Hesap | Rol | İnceleme notu |
|-------|-----|----------------|
| `pm@demo.ayanlab` | Property Manager | Dashboard + tüm menü |
| `accountant@demo.ayanlab` | Accountant | Accounting bölümü |
| `leasing@demo.ayanlab` | Leasing | Leasing / listings |
| `owner@demo.ayanlab` | Homeowner | Portals önizleme, HOA |
| `field@demo.ayanlab` | Field staff | TR tur (web) + mobil saha |

Üst çubukta (mock modda) persona switch ile hesap değiştirilebilir.

### Önerilen tur (PM)

1. **Dashboard** — widget’lar
2. **Rentals → Properties** — detay, notlar
3. **Leasing → Commercial terms** — NNN / gross mock (P5.8)
4. **Leasing → Leases** — draft / aktif
5. **Associations → Co-op shares / Board packets** — pay senedi & board paketi (P5.9)
6. **Associations → Violations & Architectural** — durum güncelleme (mock)
7. **Accounting → Transactions / Bank** — GL ve eşleştirme
8. **TR** — Gecikme cezası, sayaç, ATS, kargo, icra, geçiş kontrol
9. **Settings → Profile / Application** — profil, dil (EN|TR), bildirim mock
10. **Destek, Bilgi ve Yasal** — KVKK, gizlilik, çerez, mesafeli satış vb. (TR+EN içerik)
11. **Org settings** — kuruluş mock
12. **Apsis lite** (sidebar) — kısıtlı menü toggle; modül menüleri tıklanınca açılır

### E2E / load (P6)

```bash
cd ayanlab-pms/ayanlab-pms-ui
npm run test:e2e          # Playwright: login → dashboard → commercial mock
# Load smoke (API): ayanlab-pms/scripts/load-dashboard-smoke.sh
```

### Gerçek API modu (isteğe bağlı)

[`pms-platform-integration.md`](pms-platform-integration.md): Platform `:8102`, PMS `:8102` proxy ile `VITE_MOCK_ONLY=false`, `VITE_USE_MOCKS=false`.

Demo veri:

```bash
export IPMS_LOAD_DEMO_DATA=true
export IPMS_DEMO_ORG_ID=<demo_ayanlab org UUID>
```

## Mobil (Flutter)

```bash
cd ayanlab-pms/ayanlab-pms-mobile
flutter pub get
flutter run
```

Aynı demo e-postaları; persona’ya göre farklı shell. Detay: `ayanlab-pms/ayanlab-pms-mobile/README.md`.

## Build doğrulama

```bash
cd ayanlab-pms/ayanlab-pms-ui && npm run build
cd ayanlab-pms/ayanlab-pms-backend && mvn test
```