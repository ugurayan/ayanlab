import type { Department } from "../types/departments";

export const departments: Department[] = [
  {
    department_id: "live_operations_shift_support",
    title: {
      en: "Live Operations (Shift Support)",
      de: "Live-Betrieb (Schicht-Support)",
      tr: "Canlı Operasyon (Vardiya Desteği)"
    },
    issues: {
      en: [
        "Worker is late / cannot arrive",
        "Cannot find location / wrong address",
        "Client contact not reachable",
        "Job instructions unclear / missing",
        "Replacement needed urgently",
        "On-site access problem (badge, gate, security)",
        "Emergency escalation during shift"
      ],
      de: [
        "Worker verspätet / kann nicht kommen",
        "Ort nicht gefunden / falsche Adresse",
        "Ansprechpartner beim Kunden nicht erreichbar",
        "Auftragsanweisungen unklar / fehlen",
        "Dringend Ersatz erforderlich",
        "Zutrittsproblem vor Ort (Badge, Tor, Security)",
        "Notfall-Eskalation während der Schicht"
      ],
      tr: [
        "Çalışan geç kaldı / gelemedi",
        "Konum bulunamıyor / adres yanlış",
        "Müşteri iletişim kişisine ulaşılamıyor",
        "İş talimatları belirsiz / eksik",
        "Acil yedek çalışan gerekiyor",
        "Saha giriş problemi (kart, kapı, güvenlik)",
        "Vardiya sırasında acil durum eskalasyonu"
      ]
    }
  },
  {
    department_id: "time_and_attendance_timesheets",
    title: {
      en: "Time & Attendance (Timesheets)",
      de: "Zeit & Anwesenheit (Stundenzettel)",
      tr: "Zaman & Devam (Puantaj)"
    },
    issues: {
      en: [
        "Cannot clock in / clock out",
        "GPS/geofence check-in failed",
        "QR/check-in code not working",
        "Missing or incorrect breaks",
        "Timesheet correction request",
        "Client approval pending too long",
        "Dispute over recorded hours"
      ],
      de: [
        "Ein-/Ausstempeln nicht möglich",
        "GPS/Geofence-Check-in fehlgeschlagen",
        "QR-/Check-in-Code funktioniert nicht",
        "Pausen fehlen oder sind falsch",
        "Anfrage zur Korrektur des Stundenzettels",
        "Kundenfreigabe dauert zu lange",
        "Streit über erfasste Stunden"
      ],
      tr: [
        "Giriş/çıkış yapılamıyor",
        "GPS/geofence check-in başarısız",
        "QR/check-in kodu çalışmıyor",
        "Mola kaydı eksik veya yanlış",
        "Puantaj düzeltme talebi",
        "Müşteri onayı çok uzun sürüyor",
        "Kaydedilen saatler hakkında anlaşmazlık"
      ]
    }
  },
  {
    department_id: "worker_pay_and_payouts",
    title: {
      en: "Worker Pay & Payouts",
      de: "Auszahlung & Vergütung (Worker)",
      tr: "Çalışan Ücreti & Ödeme"
    },
    issues: {
      en: [
        "Missing earnings / underpaid",
        "Payout failed / delayed",
        "Bank account setup issues",
        "Instant payout not received",
        "Pay rate mismatch",
        "Deductions explanation request",
        "Payment schedule questions"
      ],
      de: [
        "Verdienst fehlt / zu wenig bezahlt",
        "Auszahlung fehlgeschlagen / verspätet",
        "Probleme bei der Bankkonto-Einrichtung",
        "Sofortauszahlung nicht angekommen",
        "Stundenlohn stimmt nicht",
        "Erklärung zu Abzügen",
        "Fragen zum Auszahlungsplan"
      ],
      tr: [
        "Kazanç eksik / düşük ödeme",
        "Ödeme başarısız / gecikmiş",
        "Banka hesabı kurulum sorunları",
        "Anında ödeme ulaşmadı",
        "Ücret oranı uyuşmuyor",
        "Kesintilerle ilgili açıklama talebi",
        "Ödeme takvimi soruları"
      ]
    }
  },
  {
    department_id: "client_billing_and_invoicing",
    title: {
      en: "Client Billing & Invoicing",
      de: "Abrechnung & Rechnungen (Kunde)",
      tr: "Müşteri Faturalandırma & Tahsilat"
    },
    issues: {
      en: [
        "Invoice questions / invoice copy",
        "Payment failed",
        "Billing method update",
        "Unexpected charges",
        "Tax/VAT invoice details",
        "Monthly statements",
        "Credit notes / adjustments"
      ],
      de: [
        "Rechnungsfragen / Rechnungsduplikat",
        "Zahlung fehlgeschlagen",
        "Zahlungsmethode aktualisieren",
        "Unerwartete Gebühren",
        "Steuer-/MwSt.-Details auf Rechnung",
        "Monatsabrechnungen",
        "Gutschriften / Anpassungen"
      ],
      tr: [
        "Fatura soruları / fatura kopyası",
        "Ödeme başarısız",
        "Ödeme yöntemini güncelleme",
        "Beklenmeyen ücretler",
        "Vergi/KDV fatura detayları",
        "Aylık hesap dökümleri",
        "Alacak dekontu / düzeltmeler"
      ]
    }
  },
  {
    department_id: "disputes_and_adjustments",
    title: {
      en: "Disputes & Adjustments",
      de: "Streitfälle & Korrekturen",
      tr: "Anlaşmazlıklar & Düzeltmeler"
    },
    issues: {
      en: [
        "Timesheet dispute escalation",
        "Charge dispute / billing dispute",
        "Refund request review",
        "Cancellation fee dispute",
        "No-show fee dispute",
        "Client-worker conflict resolution",
        "Evidence review (photos, logs, messages)"
      ],
      de: [
        "Eskalation bei Stundenzettel-Streit",
        "Gebühren-/Abrechnungsstreit",
        "Prüfung von Rückerstattungsanfragen",
        "Streit über Stornogebühr",
        "Streit über No-Show-Gebühr",
        "Konfliktlösung Kunde–Worker",
        "Prüfung von Belegen (Fotos, Logs, Nachrichten)"
      ],
      tr: [
        "Puantaj anlaşmazlığı eskalasyonu",
        "Ücret/faturalandırma itirazı",
        "İade talebi incelemesi",
        "İptal ücreti itirazı",
        "No-show ücreti itirazı",
        "Müşteri-çalışan anlaşmazlığı çözümü",
        "Kanıt inceleme (foto, log, mesaj)"
      ]
    }
  },
  {
    department_id: "trust_and_safety",
    title: {
      en: "Trust & Safety",
      de: "Vertrauen & Sicherheit",
      tr: "Güven & Emniyet"
    },
    issues: {
      en: [
        "Harassment or discrimination report",
        "Threats or violence",
        "Unsafe workplace conditions",
        "Injury incident report",
        "Substance abuse / impairment concerns",
        "Policy violations (conduct)",
        "Emergency support routing"
      ],
      de: [
        "Meldung von Belästigung oder Diskriminierung",
        "Drohungen oder Gewalt",
        "Unsichere Arbeitsbedingungen",
        "Meldung von Verletzungen/Unfällen",
        "Verdacht auf Alkohol/Drogen/Beinträchtigung",
        "Richtlinienverstöße (Verhalten)",
        "Notfall-Support/Weiterleitung"
      ],
      tr: [
        "Taciz veya ayrımcılık bildirimi",
        "Tehdit veya şiddet",
        "Güvensiz çalışma koşulları",
        "Yaralanma/olay bildirimi",
        "Alkol/uyuşturucu/uygunsuzluk şüphesi",
        "Politika ihlalleri (davranış)",
        "Acil destek yönlendirmesi"
      ]
    }
  },
  {
    department_id: "identity_and_verification_kyc",
    title: {
      en: "Identity & Verification (KYC)",
      de: "Identität & Verifizierung (KYC)",
      tr: "Kimlik & Doğrulama (KYC)"
    },
    issues: {
      en: [
        "ID upload rejected",
        "Selfie/face match failed",
        "Name or DOB mismatch",
        "Work authorization verification issue",
        "Background check status questions",
        "Re-verification needed",
        "Verification taking too long"
      ],
      de: [
        "Ausweis-Upload abgelehnt",
        "Selfie-/Gesichtsabgleich fehlgeschlagen",
        "Name- oder Geburtsdatum stimmt nicht überein",
        "Problem bei der Arbeitserlaubnis-Verifizierung",
        "Fragen zum Status der Background-Checks",
        "Erneute Verifizierung erforderlich",
        "Verifizierung dauert zu lange"
      ],
      tr: [
        "Kimlik yükleme reddedildi",
        "Selfie/yüz eşleştirme başarısız",
        "İsim veya doğum tarihi uyuşmuyor",
        "Çalışma izni doğrulama sorunu",
        "Arka plan kontrolü durum soruları",
        "Yeniden doğrulama gerekiyor",
        "Doğrulama çok uzun sürüyor"
      ]
    }
  },
  {
    department_id: "compliance_and_documents",
    title: {
      en: "Compliance & Documents",
      de: "Compliance & Dokumente",
      tr: "Uyumluluk & Belgeler"
    },
    issues: {
      en: [
        "Certificate upload rejected (e.g., food handler)",
        "Document expired / expiring soon",
        "Missing required certification for a job",
        "Incorrect document type uploaded",
        "Edit document details (issuer/expiry)",
        "Client site-specific compliance requirements",
        "Policy questions about required documents"
      ],
      de: [
        "Zertifikat-Upload abgelehnt (z. B. Hygienepass)",
        "Dokument abgelaufen / läuft bald ab",
        "Erforderliche Qualifikation für Job fehlt",
        "Falscher Dokumenttyp hochgeladen",
        "Dokumentdetails bearbeiten (Aussteller/Ablaufdatum)",
        "Standortspezifische Compliance-Anforderungen des Kunden",
        "Fragen zu erforderlichen Dokumenten"
      ],
      tr: [
        "Sertifika yükleme reddedildi (örn. hijyen belgesi)",
        "Belge süresi doldu / yakında dolacak",
        "İş için gerekli sertifika eksik",
        "Yanlış belge türü yüklendi",
        "Belge detaylarını düzenleme (veren/son tarih)",
        "Müşteriye özel saha uyumluluk gereksinimleri",
        "Gerekli belgelerle ilgili politika soruları"
      ]
    }
  },
  {
    department_id: "account_and_security",
    title: {
      en: "Account & Security",
      de: "Konto & Sicherheit",
      tr: "Hesap & Güvenlik"
    },
    issues: {
      en: [
        "Cannot log in / account locked",
        "2FA/OTP not received",
        "Change phone/email request",
        "Suspicious login / account takeover",
        "Password reset issues",
        "Close or delete account",
        "Device change verification"
      ],
      de: [
        "Login nicht möglich / Konto gesperrt",
        "2FA/OTP nicht erhalten",
        "Telefon/E-Mail ändern",
        "Verdächtiger Login / Kontoübernahme",
        "Probleme beim Zurücksetzen des Passworts",
        "Konto schließen oder löschen",
        "Verifizierung bei Gerätewechsel"
      ],
      tr: [
        "Giriş yapılamıyor / hesap kilitli",
        "2FA/OTP gelmiyor",
        "Telefon/e-posta değişikliği talebi",
        "Şüpheli giriş / hesap ele geçirilmesi",
        "Şifre sıfırlama sorunları",
        "Hesabı kapatma veya silme",
        "Cihaz değişimi doğrulaması"
      ]
    }
  },
  {
    department_id: "technical_support_app_platform",
    title: {
      en: "Technical Support (App & Platform)",
      de: "Technischer Support (App & Plattform)",
      tr: "Teknik Destek (Uygulama & Platform)"
    },
    issues: {
      en: [
        "App crashes or freezes",
        "Notifications not working",
        "GPS/location inaccurate",
        "Upload fails (photos/PDF)",
        "Messaging not working",
        "Payment page errors",
        "Performance/slow loading"
      ],
      de: [
        "App stürzt ab oder hängt",
        "Benachrichtigungen funktionieren nicht",
        "GPS/Standort ungenau",
        "Upload schlägt fehl (Fotos/PDF)",
        "Nachrichten funktionieren nicht",
        "Fehler auf der Zahlungsseite",
        "Leistung/zu langsames Laden"
      ],
      tr: [
        "Uygulama çöküyor veya donuyor",
        "Bildirimler çalışmıyor",
        "GPS/konum hatalı",
        "Yükleme başarısız (foto/PDF)",
        "Mesajlaşma çalışmıyor",
        "Ödeme sayfası hataları",
        "Performans/yavaş yükleme"
      ]
    }
  },
  {
    department_id: "marketplace_and_bookings",
    title: {
      en: "Marketplace & Bookings",
      de: "Marktplatz & Buchungen",
      tr: "Pazar Yeri & Rezervasyonlar"
    },
    issues: {
      en: [
        "Cannot see available jobs",
        "Not eligible for a job",
        "Booking failed",
        "Schedule conflict",
        "Cancel a booking",
        "No-show policy questions",
        "Job details clarification"
      ],
      de: [
        "Keine verfügbaren Jobs sichtbar",
        "Nicht berechtigt für einen Job",
        "Buchung fehlgeschlagen",
        "Terminkonflikt",
        "Buchung stornieren",
        "Fragen zur No-Show-Regel",
        "Klärung von Jobdetails"
      ],
      tr: [
        "Uygun işler görünmüyor",
        "İş için uygun değilim",
        "Rezervasyon başarısız",
        "Takvim çakışması",
        "Rezervasyon iptali",
        "No-show politikası soruları",
        "İş detayları açıklaması"
      ]
    }
  },
  {
    department_id: "quality_and_performance",
    title: {
      en: "Quality & Performance",
      de: "Qualität & Performance",
      tr: "Kalite & Performans"
    },
    issues: {
      en: [
        "Rating or review dispute",
        "Client complaint about worker",
        "Worker complaint about client",
        "Performance warning explanation",
        "Account restriction / deactivation appeal",
        "Service quality feedback",
        "Repeat issue monitoring"
      ],
      de: [
        "Streit über Bewertung/Review",
        "Kundenbeschwerde über Worker",
        "Worker-Beschwerde über Kunde",
        "Erklärung zu Performance-Warnungen",
        "Einschränkung/Deaktivierung – Einspruch",
        "Feedback zur Servicequalität",
        "Überwachung wiederholter Probleme"
      ],
      tr: [
        "Puan/yorum itirazı",
        "Müşterinin çalışan şikayeti",
        "Çalışanın müşteri şikayeti",
        "Performans uyarısı açıklaması",
        "Kısıtlama/hesap kapatma itirazı",
        "Hizmet kalitesi geri bildirimi",
        "Tekrarlayan sorun takibi"
      ]
    }
  },
  {
    department_id: "onboarding_and_training",
    title: {
      en: "Onboarding & Training",
      de: "Onboarding & Schulung",
      tr: "Onboarding & Eğitim"
    },
    issues: {
      en: [
        "How to start working (first shift help)",
        "How to check in/out",
        "Workplace conduct guidelines",
        "Client site rules onboarding",
        "Troubleshooting common onboarding steps",
        "Profile completion help",
        "Getting verified faster tips"
      ],
      de: [
        "Erste Schritte (erste Schicht Hilfe)",
        "Wie stempelt man ein/aus",
        "Verhaltensrichtlinien am Arbeitsplatz",
        "Einführung in Kunden-/Standortregeln",
        "Hilfe bei typischen Onboarding-Schritten",
        "Hilfe beim Profilabschluss",
        "Tipps für schnellere Verifizierung"
      ],
      tr: [
        "Başlama rehberi (ilk vardiya desteği)",
        "Giriş/çıkış nasıl yapılır",
        "İş yeri davranış kuralları",
        "Müşteri saha kuralları onboarding",
        "Onboarding adımlarında sorun giderme",
        "Profil tamamlama desteği",
        "Daha hızlı doğrulanma ipuçları"
      ]
    }
  },
  {
    department_id: "sales_and_account_management_clients",
    title: {
      en: "Sales & Account Management (Clients)",
      de: "Vertrieb & Kundenbetreuung (Kunden)",
      tr: "Satış & Hesap Yönetimi (Müşteriler)"
    },
    issues: {
      en: [
        "Pricing and plan questions",
        "Contract setup / onboarding new location",
        "Add/remove admins and permissions",
        "Reporting and analytics requests",
        "Service level / support escalation",
        "Volume discounts",
        "Integration discussion (API, payroll, ATS)"
      ],
      de: [
        "Fragen zu Preisen und Tarifen",
        "Vertragseinrichtung / neuen Standort onboarden",
        "Admins und Berechtigungen hinzufügen/entfernen",
        "Anfragen zu Reports und Analysen",
        "Service-Level / Support-Eskalation",
        "Mengenrabatte",
        "Integrationsgespräch (API, Payroll, ATS)"
      ],
      tr: [
        "Fiyatlandırma ve plan soruları",
        "Sözleşme kurulumu / yeni lokasyon onboarding",
        "Admin ve yetkileri ekleme/çıkarma",
        "Raporlama ve analiz talepleri",
        "Servis seviyesi / destek eskalasyonu",
        "Hacim indirimleri",
        "Entegrasyon görüşmesi (API, payroll, ATS)"
      ]
    }
  }
];

