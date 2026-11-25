# Coople Business ve Berry — doğrulanmış fonksiyon haritası

Kaynak ekranlar: `~/Pictures/Instawork` (Coople Business, işveren) ve `~/Pictures/berry_recruitment` (Berry işçi kaydı; klasörün son kareleri Coople aday detayının devamı). Renk tonu bu turda değişmez.

Durum, 25 Kasım 2026 koduna göre:

- **var** — çalışan API veya ekran bu işi yapıyor
- **kısmi** — ilgili ekran veya kayıt var, Coople/Berry adımı yok
- **yok** — ne API ne ekran

Aktif profil kabuğu bu turda bağlandı. Bir kişi birden fazla kabuk taşıyabilir. Seçim oturumu kapatmaz; menü ve `/app/profile` içeriği değişir. İşveren, işçi kartını kendi profili olarak görmez.

## Kabuk

Kabuk, JWT’deki rol kodundan ve izinlerden gelir. `GET /hrm/api/navigation/shells` listeyi döner. UI aynı kuralı yerel olarak da hesaplar; istek başarısız olursa ona düşer. Seçim `hrm.activeShell` anahtarında durur.

| Kabuk | Kimde açılır | Menü | Profil |
| --- | --- | --- | --- |
| employer | `ORG_ADMIN`, `ORG_FINANCE`, `LOCATION_MANAGER`, `RECRUITER`, `BUSINESS_ADMIN`, `BUSINESS_MEMBER`, `EMPLOYER`, `AGENT_ADMIN`, `AGENT_USER` veya organizasyon/vardiya izni | iş, vardiya, başvuru, işçi, finans | hesap, şifre, silme ekranı |
| worker | `WORKER`, `RIDER` veya yalnız `staffiq:applications:create` | iş, vardiya, başvuru, belge | mevcut işçi kartı |
| admin | `PLATFORM_ADMIN`, `ADMIN`, `SYSTEM_ADMIN`, `STAFF`, `staffiq:*:*` veya `staffiq:job_roles:create` | doğrulama ve kataloglar | hesap ve katalog bağlantıları |

`PLATFORM_ADMIN`, `ADMIN` ve `staffiq:*:*` üç kabuğu birden açar. Yalnız `ORG_ADMIN` işçi kabuğu açmaz. İşçi kabuğu, `WORKER` rolü de varsa açılır. Platform oturumu profillere `kind` yazmaz; kabuk rol ailesinden türetilir.

## İşveren — iş ilanı

| Fonksiyon | Durum | Kod |
| --- | --- | --- |
| İş listesi, understaffed / fully staffed | kısmi | UI `/app/jobs`, `/app/shifts`. Sekme ve doluluk oranı yok |
| Şablon ve son ilanlar | kısmi | `ShiftTemplate` ve job template izinleri var. Coople şablon seçim ekranı yok |
| Rol kataloğu (ör. Barback) | kısmi | `JobRole` CRUD, `/app/job-roles`. İlan sihirbazı rol seçer |
| Saatlik ücret ve hesaplanan maliyet | yok | Ücret alanı ve maliyet formülü yok |
| Uzmanlık ve yetenek çipleri | kısmi | Skill kataloğu ve `WorkerSkillTag`. İlan adımındaki zorunlu çip seçimi yok |
| Kıyafet hazır seti, katalog, serbest metin | yok | `dress_codes` izni var, ekran ve katalog yok |
| Vardiya: tarih, başlangıç, bitiş, mola, kişi, PO | kısmi | `Shift` CRUD süre ve mola kuralını uygular. PO ve ilan sihirbazındaki vardiya adımı yok |
| İletişim kişisi | kısmi | Contact API profil bazlı. İlana bağlanan kişi seçimi yok |
| İlan adı, açıklama, sağlık ve güvenlik, PDF | kısmi | Job oluşturma ekranı metin alır. PDF ve H&S adımı yok |
| Otomatik işe alım: güvenilen / seçilen | yok | |
| Görünürlük: tüm eşleşenler, favoriler, daha önce çalışanlar | yok | |
| Özet maliyet ve yayınla | kısmi | Shift publish akışı var. £ maliyet özeti yok |
| Yayın sonrası gün çipleri ve doluluk | yok | |
| Menü: ilan detayı, vardiya düzenle, otomatik alımı yönet, işe alımı durdur | kısmi | Detay ve vardiya düzenleme var. Durdurma nedeni listesi yok |
| Aday kartı: fotoğraf, puan, ücret, favori sayısı, yol süresi, CV, iletişim, hakkında | kısmi | `/app/applications` ve `/app/workers` liste. Coople kartı yok |
| Yetenek teyit sayıları, dil seviyeleri, iş geçmişi, DBS, SIA | kısmi | İşçi belgesi ve skill kaydı var. Bu kartın alanları yok |
| Decline ve Hire | kısmi | Başvuru durumu güncellenir. Bu iki eylem ve güvenilen aday kuralı yok |
| Takvim: hafta şeridi, gün noktası, boş gün / saat-ilan-sayı | kısmi | `/app/shifts` liste. Haftalık şerit yok |
| Saatler sekmesinin içi | yok | Ekran görüntülerinde iç sayfa yok |

## İşveren profili

| Fonksiyon | Durum | Kod |
| --- | --- | --- |
| İletişim bilgisi | kısmi | Oturumdaki ad ve e-posta gösterilir. Ayrı iletişim formu yok |
| Şifre değiştir | var | `POST /auth/api/change-password` |
| Koşullar | yok | Coople (UK) Ltd metni bu ürüne ait değil |
| Hesabı sil, `DELETE` yazarak onay | kısmi | Ekran var. Platformda silme ucu yok; istek gönderilmez |
| Yenilikler: görünürlük havuzları | yok | Kabuk seçiminin kendisi profilde anlatılır |

## İşçi — Berry kayıt

| Fonksiyon | Durum | Kod |
| --- | --- | --- |
| Karşılama | yok | |
| E-posta ile şifre sıfırlama | kısmi | Platform auth’ta reset akışı ayrı. Berry adımı yok |
| Tek uzmanlık kategorisi ve örnek roller | kısmi | Industry ve job role kataloğu var. Kayıt sihirbazı yok |
| Deneyim bandı: 1 aydan az, 1–6, 6–12, 12+ | yok | |
| Ad, soyad, posta kodu | kısmi | Kullanıcı kaydı ad taşır. Posta kodu adımı yok |
| Tavsiye kaynağı | yok | |
| Bildirim izni | yok | |

## İşçi — işe girdikten sonra

| Fonksiyon | Durum | Kod |
| --- | --- | --- |
| Açık iş bul ve başvur | kısmi | `/app/jobs` ve başvuru oluşturma izni. Coople işçi ilan akışı yok |
| Kendi takvimi, giriş-çıkış | kısmi | Vardiya okuma var. Clock-in yok |
| Kazanç | yok | Finans ekranı işveren/finans rolüne bağlı |
| Herkese açık profil kartı | kısmi | `/app/profile` işçi kabuğunda mevcut kart. Veri hâlâ örnek |
| İşveren bu kartı kendi profili sanmasın | var | Kabuk `employer` iken profil hesap ekranıdır |

## Admin

| Fonksiyon | Durum | Kod |
| --- | --- | --- |
| Doğrulama kuyruğu | kısmi | `/app/verification` |
| Rol, sektör, yetenek, sertifika, belge tipi | var | İlgili CRUD ekranları ve HRM API |
| Admin profili katalog değil hesap | var | Kabuk `admin` iken profil hesap ve bağlantı listesidir |

## Bilinçli olarak yapılmayanlar

Ücret/maliyet formülü, kıyafet kataloğu, otomatik işe alım, işe alımı durdurma nedenleri, Hire/Decline’ın Coople kuralı, Berry kayıt sihirbazı, clock-in ve kazanç bu haritada **yok** olarak durur. Ekran görüntüsünde olmayan adım eklenmedi.
