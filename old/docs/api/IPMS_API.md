# IPMS API — Property Management (Buildium-style CRUD)

Base path: `/ayanlabs/api/property-mgmt`  
App ID: `ad94b2d2-5022-4818-a785-55807ab139ba` (`APP_ID_IPMS`)

Tüm endpoint'ler standart [client header](../../CONTROLLER_API_HEADERS.md) seti gerektirir (health ve subscription planları hariç).  
Organizasyon kapsamı JWT / context üzerinden `orgId` ile uygulanır; kayıtlar soft-delete (`isActive=false`).

## Ortak CRUD şablonu

Her kaynak için:

| Method | Path | Açıklama |
|--------|------|----------|
| `GET` | `/{resource}` | Organizasyondaki aktif kayıtları listele |
| `GET` | `/{resource}/{id}` | Tek kayıt |
| `POST` | `/{resource}` | Oluştur (`organizationId` context'ten atanır) |
| `PUT` | `/{resource}/{id}` | Güncelle |
| `DELETE` | `/{resource}/{id}` | Soft delete |

---

## Rentals

| Resource | Path |
|----------|------|
| Properties | `/properties` |
| Property units | `/property-units` | `?propertyId=` |
| Property types | `/property-types` *(sistem + org özel; özel CRUD kuralları)* |
| Rental owners | `/rental-owners` |
| Outstanding balances | `/outstanding-balances` | `?leaseId=` |

## Leasing

| Resource | Path | List filters |
|----------|------|--------------|
| Tenants | `/tenants` | `?propertyId=`, `?unitId=`, `?leaseId=` |
| Listings | `/listings` | `?propertyId=` |
| Applicants | `/applicants` | `?listingId=` |
| Draft leases | `/draft-leases` | `?propertyId=`, `?unitId=` |
| Leases | `/leases` | `?propertyId=`, `?unitId=` |
| Lease renewals | `/lease-renewals` | `?leaseId=` |

## Maintenance

| Resource | Path |
|----------|------|
| Maintenance requests | `/maintenance-requests` | `?propertyId=`, `?unitId=` |
| Work orders | `/work-orders` | `?maintenanceRequestId=` |
| Vendors | `/vendors` |

## Associations (HOA)

| Resource | Path |
|----------|------|
| Associations | `/associations` |
| Ownership accounts | `/ownership-accounts` | `?associationId=` |
| Violations | `/violations` | `?associationId=` |

## Communication

| Resource | Path |
|----------|------|
| Announcements | `/announcements` | `?associationId=` |
| Messages | `/messages` |
| Communication emails | `/communication-emails` |

## Accounting

| Resource | Path |
|----------|------|
| Bank accounts | `/bank-accounts` |
| Bills | `/bills` | `?propertyId=`, `?vendorId=` |
| Financial records | `/financial-records` | `?propertyId=` |
| Transactions | `/transactions` | `?propertyId=` |

## Tasks & files

| Resource | Path |
|----------|------|
| Tasks | `/tasks` |
| Incoming requests | `/incoming-requests` |
| Stored files | `/stored-files` | `?entityType=`, `?entityId=` |

---

## Diğer endpoint'ler

| Method | Path | Açıklama |
|--------|------|----------|
| `GET` | `/health` | Modül durumu (token gerekmez) |
| `GET` | `/authorization/context` | JWT principal özeti |
| `POST` | `/settings/security/change-password` | Current/new/confirm ile güvenli şifre güncelleme |
| `GET` | `/api/core/authorization/effective` | Efektif yetkiler (core) |
| `GET` | `/ayanlabs/api/propertymgmt/subscriptions/plans` | Abonelik planları (public) |

---

## Mimari notlar

- **Service:** `AbstractIpmsOrgService` — org kapsamı, audit (`BaseMapper`), soft delete.
- **Property types:** Sistem varsayılanları seed (`property-type-catalog.json`); API ile sadece org özel tipler oluşturulur.
- **İlişkiler:** FK alanları (`propertyId`, `unitId`, `leaseId`, vb.) istek gövdesinde UUID olarak gönderilir; JPA association yok.
- **Filtreleme:** Child kaynaklarda `?propertyId=`, `?unitId=`, `?leaseId=` vb. query parametreleri desteklenir.

curl örnekleri: [curl/ipms.md](../curl/ipms.md)

---

## Sign Up (çok adımlı kayıt)

Base: `/ayanlabs/api/property-mgmt/signup`

| Adım | Method | Path | Açıklama |
|------|--------|------|----------|
| 1 | `POST` | `/step-1/register` | Kişisel kullanıcı kaydı + email/SMS doğrulama kodu gönderimi (token gerekmez) |
| 1 | `GET` | `/step-1/check-email?email=` | Email müsait mi (`available: true/false`) |
| 1 | `GET` | `/step-1/check-phone?phone=` | Telefon müsait mi (`available: true/false`) |
| 1 | `POST` | `/step-1/verify-email` | Email doğrulama |
| 1 | `POST` | `/step-1/verify-mobile` | Mobil doğrulama |
| 1 | `POST` | `/step-1/resend-email` | Email kodu yeniden gönder |
| 1 | `POST` | `/step-1/resend-mobile` | SMS kodu yeniden gönder |
| — | `GET` | `/session` | Mevcut kayıt oturumu durumu (`nextStep` alanı ile) |
| 2 | `POST` | `/step-2/profile-type` | `PERSONAL` veya `ORGANIZATION` seçimi |
| 3a | `POST` | `/step-3/personal` | Kişisel hesap: `TENANT_RENTER`, `PROPERTY_OWNER`, `VENDOR`, `MAINTENANCE` |
| 3b | `POST` | `/step-3/organization/new-company` | Yeni şirket + şirket tipi; kayıt yapan `ORGANIZATION_ADMINISTRATOR` olur |
| 3b | `POST` | `/step-3/organization/join/invite` | Davet kodu ile mevcut şirkete katılma |
| 3b | `GET` | `/step-3/organization/search?name=` | Şirket adı arama |
| 3b | `POST` | `/step-3/organization/join/request` | Admin onayına join isteği gönder |
| 4 | `POST` | `/complete` | Kaydı tamamla; dashboard token + `redirectTo: /dashboard` |

**Organizasyon şirket tipleri:** `LANDLORD_PROPERTY_OWNER`, `HOA`, `REAL_ESTATE`, `VENDOR`, `MAINTENANCE`

**Kişisel hesap tipleri:** şirket ilişkisi yok; profil `organizationId = null`.

**Join request** yolu seçilirse `complete` çağrılamaz; admin onayı sonrası kullanıcı `complete` ile dashboard'a geçebilir.

### Sign Up Admin (organizasyon yöneticisi)

Base: `/ayanlabs/api/property-mgmt/signup/admin` — `ORGANIZATION_ADMINISTRATOR` veya `SYSTEM_ADMINISTRATOR` gerekir.

| Method | Path | Açıklama |
|--------|------|----------|
| `POST` | `/invites` | Davet kodu oluştur (opsiyonel `invitedEmail`, `invitedUserId`, `roleCode`, `expiresInDays`) |
| `GET` | `/invites` | Organizasyon davet kodlarını listele |
| `GET` | `/join-requests?status=PENDING` | Join isteklerini listele |
| `POST` | `/join-requests/{id}/approve` | Join isteğini onayla; kullanıcı `complete` adımına geçebilir |
| `POST` | `/join-requests/{id}/reject` | Join isteğini reddet; kullanıcı step 2'ye döner |
