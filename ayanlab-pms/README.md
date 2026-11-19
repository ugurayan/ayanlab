# AyanLab IPMS (Property Management)

| Bileşen | Klasör |
|---------|--------|
| Web UI | [`ayanlab-pms-ui/`](ayanlab-pms-ui/) |
| Backend | [`ayanlab-pms-backend/`](ayanlab-pms-backend/) |
| Mobile | [`ayanlab-pms-mobile/`](ayanlab-pms-mobile/) |

## Hızlı demo (mock)

```bash
cd ayanlab-pms-ui
npm install && npm run dev
```

Login ekranında **Demo hesapları** ile tek tık giriş. Şifre: `password`.

Uçtan uca inceleme rehberi: [`../docs/pms-demo-walkthrough.md`](../docs/pms-demo-walkthrough.md)

## Plan & entegrasyon

- Yol haritası: [`../docs/ayanlab-pms-plan.md`](../docs/ayanlab-pms-plan.md)
- Domain baseline (P1–P6): [`../docs/pms-domain-baseline.md`](../docs/pms-domain-baseline.md)
- Platform auth / proxy: [`../docs/pms-platform-integration.md`](../docs/pms-platform-integration.md)

### API öne çıkanlar (B faz)

| Endpoint | Açıklama |
|----------|----------|
| `POST /pms/api/flows/lease-charge-portal` | B2.4 lease → charge → portal bakiye |
| `POST /pms/api/webhooks/*` | eSign, screening, pre-lease pay, IVR stub |
| `POST /pms/api/shortcuts/*` | Mgmt fee, owner draw, passthrough stub |
| `GET /pms/api/marketplace/billing-bridge` | Platform fatura köprüsü |
| `GET /pms/api/org-whatsapp-settings` | WhatsApp org config stub |
| `GET/POST /pms/api/commercial-lease-terms` | P5.8 ticari kira şartları |
| `GET/POST /pms/api/cooperative-share-records` | P5.9 pay senedi |
| `GET/POST /pms/api/coop-board-packets` | Co-op board paketleri |
| `POST /pms/api/payments/intents` | Ödeme sağlayıcı stub (P2.13) |