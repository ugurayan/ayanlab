# IPMS domain baseline (P1–P6)

CRUD + dashboard endpoints mevcut; UI mock (M0) tamam. Aşağıdaki tablo **sonraki faz** kapsamında “baseline hazır” anlamına gelir — tam iş kuralları ve portal deep-dive hâlâ epik backlog’tur.

| Plan | Backend (örnek) | Web mock | API swap (B2) |
|------|-----------------|----------|----------------|
| **P1** rental | `Property*`, `Lease*`, `Tenant*`, `Maintenance*`, `WorkOrder*`, `Task*` | `views/rentals`, `leasing`, `maintenance` | properties, rent roll, tenants, leases |
| **P2** accounting | `ChartOfAccount*`, `IpmsTransaction*`, `Bill*`, `BankAccount*`, `Budget*` | `views/accounting/*` | partial (accounting.ts backlog) |
| **P3** HOA | `Association*`, `Violation*`, `ArchitecturalRequest*` | `views/associations/*` | violations, architectural |
| **P4** comm | `Announcement*`, `Message*`, `ResidentCenter*` | portals + comm mock pages | backlog |
| **P5** enterprise | `Addon*`, `Analytics*`, webhooks, flows, `CommercialLeaseTerms*`, `CooperativeShareRecord*`, `CoopBoardPacket*`, `POST /payments/intents` | marketplace + commercial/co-op mock | marketplace/billing-bridge |
| **P6** quality | permission tests, flow unit test | `npm run build` | `IpmsLeaseChargePortalFlowServiceTest` |

Orchestrasyon (B2.4): `POST /pms/api/flows/lease-charge-portal`.

OpenAPI grupları: `IpmsOpenApiConfiguration` (`01-rental` … `05-integrations`).