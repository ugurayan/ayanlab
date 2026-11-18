export 
const TEXTS = {
  en: {
    hero: {
      title: "Staffing Portal",
      subtitle: "Sign in to manage jobs, shifts, documents, and finance."
    },
    form: {
      title: "Sign in",
      emailLabel: "Email",
      emailPlaceholder: "you@company.com",
      passwordLabel: "Password",
      submit: "Sign in",
      submitting: "Signing in..."
    },
    demo: {
      notePrefix: "Demo accounts (password:",
      noteSuffix: ")"
    },
    tip: "Tip: Connect your Spring Boot backend by setting",
    errors: {
      generic: "Login failed"
    }
  },
  de: {
    hero: {
      title: "Staffing-Portal",
      subtitle: "Melde dich an, um Jobs, Schichten, Dokumente und Finanzen zu verwalten."
    },
    form: {
      title: "Anmelden",
      emailLabel: "E-Mail",
      emailPlaceholder: "du@firma.com",
      passwordLabel: "Passwort",
      submit: "Anmelden",
      submitting: "Anmeldung läuft..."
    },
    demo: {
      notePrefix: "Demo-Konten (Passwort:",
      noteSuffix: ")"
    },
    tip: "Tipp: Verbinde dein Spring-Boot-Backend über",
    errors: {
      generic: "Anmeldung fehlgeschlagen"
    }
  },
  tr: {
    hero: {
      title: "Personel Portalı",
      subtitle: "İşleri, vardiyaları, belgeleri ve finansı yönetmek için giriş yapın."
    },
    form: {
      title: "Giriş yap",
      emailLabel: "E-posta",
      emailPlaceholder: "sen@sirket.com",
      passwordLabel: "Şifre",
      submit: "Giriş yap",
      submitting: "Giriş yapılıyor..."
    },
    demo: {
      notePrefix: "Demo hesaplar (şifre:",
      noteSuffix: ")"
    },
    tip: "İpucu: Spring Boot backend'inizi ayarlamak için",
    errors: {
      generic: "Giriş başarısız"
    }
  }
} as const;
