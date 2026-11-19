import type { LegalByLocale } from "./types";

const companyEn = "AyanLab Yazılım A.Ş. (demo)";
const companyTr = "AyanLab Yazılım A.Ş. (demo)";

export const LEGAL_DOCUMENTS: LegalByLocale = {
  en: {
    about: {
      title: "About us",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Who we are",
          paragraphs: [
            `${companyEn} builds cloud property management software for residential, commercial, and association portfolios in Türkiye and international markets.`,
            "AyanLab PMS (IPMS) helps property managers, accountants, leasing teams, and site managers run rentals, HOA operations, accounting, and Turkey-specific compliance workflows."
          ]
        },
        {
          heading: "Mission",
          paragraphs: [
            "We aim to combine Buildium-class operations with Apsiyon-grade Turkey localization in one auditable platform integrated with AyanLab Platform identity and billing."
          ]
        }
      ]
    },
    "help-faq": {
      title: "Help center & FAQ",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Getting started",
          paragraphs: [
            "Use demo accounts on the login screen (password: password). Property Manager sees the full menu; other personas see role-appropriate areas.",
            "Toggle Apsis lite mode in the sidebar to hide advanced Pro modules."
          ]
        },
        {
          heading: "FAQ",
          paragraphs: [
            "Q: Is this production data? A: No — default mode uses in-browser mock data.",
            "Q: How do I switch language? A: Application settings or the header language control.",
            "Q: Where is live API documentation? A: PMS backend OpenAPI when the server is running on port 8105."
          ]
        }
      ]
    },
    contact: {
      title: "Contact & support",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Commercial contact",
          paragraphs: [
            "Company: AyanLab Yazılım A.Ş. (demo)",
            "Address: Barbaros Mah. Begonya Sok. No:1, Ataşehir / İstanbul (placeholder)",
            "MERSIS / Tax ID: placeholder — replace before go-live",
            "Email: support@ayanlab.com · Phone: +90 (212) 000 00 00"
          ]
        },
        {
          heading: "Support hours",
          paragraphs: ["Business days 09:00–18:00 TRT for Professional plans (demo SLA)."]
        }
      ]
    },
    feedback: {
      title: "Feedback & report a bug",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Send feedback",
          paragraphs: [
            "Describe steps to reproduce, expected vs actual behavior, and attach screenshots where possible.",
            "Demo form: this screen records feedback locally only until a ticketing API is connected."
          ]
        }
      ]
    },
    terms: {
      title: "Terms of service",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Acceptance",
          paragraphs: [
            "By accessing AyanLab PMS you agree to these terms and the Privacy Policy. If you use the service on behalf of an organization, you represent that you have authority to bind that organization."
          ]
        },
        {
          heading: "Permitted use",
          paragraphs: [
            "You may use the service for lawful property-management operations. You must not reverse engineer, abuse APIs, or upload unlawful content.",
            "We may suspend access for security, non-payment, or material breach."
          ]
        },
        {
          heading: "Intellectual property",
          paragraphs: ["Software, branding, and documentation remain AyanLab property. You retain ownership of data you upload."]
        }
      ]
    },
    privacy: {
      title: "Privacy policy",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Data we process",
          paragraphs: [
            "We process account data (name, email), organization membership, property and tenant records you enter, usage logs, and payment metadata via the platform billing bridge.",
            "Legal bases include contract performance, legitimate interest, and consent where required (e.g. marketing cookies)."
          ]
        },
        {
          heading: "Retention & security",
          paragraphs: [
            "Data is stored in EU/TR capable regions per deployment profile. Encryption in transit (TLS) and at rest are applied in production stacks.",
            "See KVKK notice (TR) for Turkey-specific disclosures."
          ]
        }
      ]
    },
    cookies: {
      title: "Cookie policy",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Cookies we use",
          paragraphs: [
            "Strictly necessary: session / JWT storage, locale preference (localStorage), lite mode toggle.",
            "Analytics (optional): anonymized product analytics when enabled in application settings."
          ]
        },
        {
          heading: "Managing preferences",
          paragraphs: ["You can clear site data in the browser or use in-app locale controls. A full consent banner will be wired before production marketing sites."]
        }
      ]
    },
    licenses: {
      title: "Open source attributions",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Web UI",
          paragraphs: [
            "React, React Router, TanStack Query, Vite, Tailwind CSS, Lucide icons, axios, react-hook-form, zod — each subject to their respective licenses (MIT and similar)."
          ]
        },
        {
          heading: "Notice",
          paragraphs: ["Full SPDX bill of materials is generated in CI for release builds (placeholder in demo)."]
        }
      ]
    },
    kvkk: {
      title: "KVKK notice (summary EN)",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Data controller",
          paragraphs: [`${companyEn} as data controller under Turkish Law No. 6698 (KVKK). Full Turkish text is shown when locale is TR.`]
        },
        {
          heading: "Your rights",
          paragraphs: [
            "You may request access, rectification, deletion, or objection regarding personal data processed in the service. Contact DPO at privacy@ayanlab.com (placeholder)."
          ]
        }
      ]
    },
    "distance-sales": {
      title: "Distance sales agreement (summary)",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Scope",
          paragraphs: [
            "Applies when subscriptions or add-ons are purchased online via AyanLab Platform billing. Full Turkish contract text is mandatory for TR consumers (see TR locale)."
          ]
        }
      ]
    },
    "pre-information": {
      title: "Pre-information form (summary)",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Before purchase",
          paragraphs: [
            "Checkout displays service description, price, billing period, payment method, and withdrawal rights for eligible consumer transactions."
          ]
        }
      ]
    },
    "cancellation-refund": {
      title: "Cancellation & refund",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Subscription cancellation",
          paragraphs: [
            "Organization admins may cancel at period end via Subscriptions. Refunds follow platform policy and applicable consumer law for TR buyers."
          ]
        },
        {
          heading: "Withdrawal",
          paragraphs: ["14-day withdrawal may apply to certain distance contracts for consumers unless digital service exceptions apply."]
        }
      ]
    },
    "account-deletion": {
      title: "Account & data deletion",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Deleting your account",
          paragraphs: [
            "Users can request account closure via Contact / Support. Organization owners may export data before offboarding.",
            "Mobile app: Settings → Privacy → Delete account (mock) will call platform API when live."
          ]
        },
        {
          heading: "App store requirement",
          paragraphs: ["Apple/Google require a visible path to delete accounts and associated personal data — this page satisfies that policy placeholder."]
        }
      ]
    }
  },
  tr: {
    about: {
      title: "Hakkımızda",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Biz kimiz",
          paragraphs: [
            `${companyTr}, Türkiye ve uluslararası pazarlarda konut, ticari ve site yönetimi portföyleri için bulut tabanlı mülk yönetimi yazılımı geliştirir.`,
            "AyanLab PMS (IPMS); kiralama, site/HOA operasyonları, muhasebe ve Türkiye’ye özgü uyum süreçlerini tek platformda toplar."
          ]
        },
        {
          heading: "Misyon",
          paragraphs: ["Buildium sınıfı operasyon ile Apsiyon sınıfı yerel uyumu, AyanLab Platform kimlik ve faturalama ile bütünleşik sunmak."]
        }
      ]
    },
    "help-faq": {
      title: "Yardım merkezi ve SSS",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Başlangıç",
          paragraphs: [
            "Giriş ekranındaki demo hesapları kullanın (şifre: password). Property Manager tüm menüyü görür.",
            "Kenar çubuğundan Apsis lite modu ile gelişmiş modülleri gizleyebilirsiniz."
          ]
        },
        {
          heading: "Sık sorulanlar",
          paragraphs: [
            "S: Canlı veri mi? C: Hayır, varsayılan mod tarayıcı mock verisidir.",
            "S: Dili nasıl değiştiririm? C: Uygulama ayarları veya üst çubuk dil seçici.",
            "S: API dokümantasyonu? C: PMS backend OpenAPI (8105)."
          ]
        }
      ]
    },
    contact: {
      title: "İletişim ve destek",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Kurumsal iletişim",
          paragraphs: [
            "Ticari unvan: AyanLab Yazılım A.Ş. (demo)",
            "Adres: Barbaros Mah. Begonya Sok. No:1, Ataşehir / İstanbul (örnek)",
            "MERSIS / Vergi no: yayına almadan güncelleyin",
            "E-posta: support@ayanlab.com · Tel: +90 (212) 000 00 00"
          ]
        }
      ]
    },
    feedback: {
      title: "Geri bildirim ve hata bildirimi",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Bize yazın",
          paragraphs: ["Hatayı adımlarıyla anlatın; mümkünse ekran görüntüsü ekleyin. Demo ortamında kayıt yalnızca yerel mock’tur."]
        }
      ]
    },
    terms: {
      title: "Kullanım koşulları",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Sözleşmenin kabulü",
          paragraphs: [
            "AyanLab PMS’e erişerek bu koşulları ve Gizlilik Politikasını kabul etmiş sayılırsınız. Kurum adına işlem yapıyorsanız buna yetkili olduğunuzu beyan edersiniz."
          ]
        },
        {
          heading: "Kullanım kuralları",
          paragraphs: [
            "Hizmet yalnızca hukuka uygun mülk yönetimi faaliyetleri için kullanılabilir. Kötüye kullanım, yetkisiz API erişimi veya hukuka aykırı içerik yasaktır."
          ]
        },
        {
          heading: "Fikri mülkiyet",
          paragraphs: ["Yazılım ve marka AyanLab’e aittir; yüklediğiniz verinin mülkiyeti size aittir."]
        }
      ]
    },
    privacy: {
      title: "Gizlilik politikası",
      updated: "2026-03-01",
      sections: [
        {
          heading: "İşlenen veriler",
          paragraphs: [
            "Hesap bilgileri, kuruluş üyeliği, girdiğiniz mülk/kiracı kayıtları, kullanım logları ve platform üzerinden ödeme meta verileri işlenebilir.",
            "Hukuki sebepler: sözleşmenin ifası, meşru menfaat ve gerektiğinde açık rıza (ör. pazarlama çerezleri)."
          ]
        },
        {
          heading: "Saklama ve güvenlik",
          paragraphs: ["Üretim ortamında TLS ve saklama şifrelemesi uygulanır. Türkiye için ayrıntılar KVKK aydınlatma metninde yer alır."]
        }
      ]
    },
    cookies: {
      title: "Çerez politikası",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Kullanılan çerezler",
          paragraphs: [
            "Zorunlu: oturum/JWT, dil tercihi (localStorage), lite mod.",
            "İsteğe bağlı: anonim ürün analitiği (uygulama ayarlarından)."
          ]
        },
        {
          heading: "Tercih yönetimi",
          paragraphs: ["Tarayıcıdan site verisini silebilir veya uygulama içi dil ayarını kullanabilirsiniz. Pazarlama sitesi için çerez banner’ı yayın öncesi eklenecektir."]
        }
      ]
    },
    licenses: {
      title: "Lisanslar ve açık kaynak",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Web arayüzü",
          paragraphs: ["React, Vite, Tailwind, TanStack Query ve diğer bağımlılıklar kendi lisansları (çoğunlukla MIT) altındadır."]
        }
      ]
    },
    kvkk: {
      title: "KVKK aydınlatma metni",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Veri sorumlusu",
          paragraphs: [
            `${companyTr}, 6698 sayılı Kişisel Verilerin Korunması Kanunu (“KVKK”) kapsamında veri sorumlusudur.`,
            "İletişim: privacy@ayanlab.com (KVYK başvuruları için örnek adres)."
          ]
        },
        {
          heading: "İşleme amaçları ve hukuki sebepler",
          paragraphs: [
            "Üyelik ve sözleşme ilişkisinin kurulması, hizmetin sunulması, faturalama, destek, güvenlik ve yasal yükümlülüklerin yerine getirilmesi.",
            "Hukuki sebepler: KVKK m.5/2 (c) sözleşme, (f) meşru menfaat, (a) kanunlarda açıkça öngörülme; özel nitelikli veri işlenmez (demo kapsamı)."
          ]
        },
        {
          heading: "Aktarım ve saklama",
          paragraphs: [
            "Altyapı sağlayıcıları ve ödeme kuruluşlarıyla sınırlı aktarım yapılabilir (yurt içi / yurt dışı hosting politikasına göre).",
            "Saklama süresi: sözleşme süresi + zamanaşımı ve mevzuat süreleri."
          ]
        },
        {
          heading: "İlgili kişi hakları",
          paragraphs: [
            "KVKK m.11 kapsamında; öğrenme, düzeltme, silme, itiraz ve zararın giderilmesini talep edebilirsiniz. Başvurular en geç 30 gün içinde yanıtlanır (üretim süreci)."
          ]
        }
      ]
    },
    "distance-sales": {
      title: "Mesafeli satış sözleşmesi",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Taraflar",
          paragraphs: [
            "Satıcı: AyanLab Yazılım A.Ş. (demo) — Alıcı: platformda abonelik satın alan kullanıcı/kuruluş.",
            "6502 sayılı Tüketicinin Korunması Hakkında Kanun ve Mesafeli Sözleşmeler Yönetmeliği çerçevesinde düzenlenir."
          ]
        },
        {
          heading: "Konu ve bedel",
          paragraphs: [
            "SaaS abonelik / eklenti hizmeti; bedel, KDV dahil/hariç gösterimi ve ödeme yöntemi ödeme adımında onaylanır.",
            "Elektronik ortamda anında ifa başlayabilir; tüketici onayı ile cayma istisnaları uygulanabilir."
          ]
        }
      ]
    },
    "pre-information": {
      title: "Ön bilgilendirme formu",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Satın alma öncesi bilgiler",
          paragraphs: [
            "Hizmetin temel nitelikleri, toplam fiyat, ödeme ve teslim/ifa şekli, satıcı iletişim bilgileri ve cayma hakkı ödeme öncesi ekranda sunulur.",
            "Kurumsal alıcılarda tüketici hükümleri uygulanmayabilir."
          ]
        }
      ]
    },
    "cancellation-refund": {
      title: "İptal, iade ve cayma koşulları",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Cayma hakkı",
          paragraphs: [
            "Tüketici işlemlerinde 14 gün içinde cayma hakkı; dijital içerik ve anında başlayan SaaS hizmetlerinde mevzuattaki istisnalar geçerli olabilir.",
            "Cayma bildirimi yazılı veya kalıcı veri saklayıcısı ile yapılır."
          ]
        },
        {
          heading: "İade süreci",
          paragraphs: ["Onaylanan iadeler 14 iş günü içinde ödeme yöntemine iade edilir (üretim SLA hedefi, demo metin)."]
        }
      ]
    },
    "account-deletion": {
      title: "Hesap ve veri silme",
      updated: "2026-03-01",
      sections: [
        {
          heading: "Hesabın silinmesi",
          paragraphs: [
            "Kullanıcılar İletişim / Destek üzerinden veya Profil → Hesabı kapat (yayın) ile talep oluşturabilir.",
            "Kuruluş sahibi veri dışa aktarımı sonrası hizmeti sonlandırabilir."
          ]
        },
        {
          heading: "Mobil uygulama",
          paragraphs: [
            "Ayarlar → Gizlilik → Hesabı sil yolu App Store / Google Play hesap silme politikası için görünür tutulmalıdır.",
            "Silme işlemi kişisel verileri KVKK/GDPR saklama süreleri çerçevesinde anonimleştirir veya siler."
          ]
        }
      ]
    }
  }
};