export const SETTINGS_TEXTS = {
  en: {
    hero: {
      title: "Settings",
      subtitle: "Configure your account and workspace."
    },
    sections: {
      account: { label: "Account", description: "Manage your public profile and private information." },
      security: { label: "Security", description: "Manage your password and 2-step verification preferences." },
      plan: { label: "Plan & Billing", description: "Manage your subscription plan, payment methods, and invoices." },
      notifications: { label: "Notifications", description: "Manage when you'll be notified on which channels." },
      team: { label: "Team", description: "Manage your existing team and change roles/permissions." },
      app: { label: "Application Settings", description: "Global workspace and application settings." },
      bank: { label: "Bank accounts", description: "Add and manage payout bank accounts (mocked only)." },
      cards: { label: "Credit cards", description: "Add and manage payment cards (mocked only)." },
      org: { label: "Organization", description: "Organization profile and verification details." }
    }
  },
  de: {
    hero: {
      title: "Einstellungen",
      subtitle: "Account und Workspace konfigurieren."
    },
    sections: {
      account: { label: "Account", description: "Öffentliches Profil und private Daten verwalten." },
      security: { label: "Sicherheit", description: "Passwort und 2-Faktor-Präferenzen verwalten." },
      plan: { label: "Plan & Abrechnung", description: "Abonnement, Zahlungsmittel und Rechnungen verwalten." },
      notifications: { label: "Benachrichtigungen", description: "Wann und auf welchen Kanälen informiert wird." },
      team: { label: "Team", description: "Team verwalten und Rollen/Rechte ändern." },
      app: { label: "Applikations-Einstellungen", description: "Globale Workspace-Optionen." },
      bank: { label: "Bankkonten", description: "Auszahlungs-Bankkonten verwalten (nur Mock)." },
      cards: { label: "Kreditkarten", description: "Zahlungskarten verwalten (nur Mock)." },
      org: { label: "Organisation", description: "Organisationsprofil und Verifizierungsdetails." }
    }
  },
  tr: {
    hero: {
      title: "Ayarlar",
      subtitle: "Hesabınızı ve çalışma alanınızı yapılandırın."
    },
    sections: {
      account: { label: "Hesap", description: "Genel profilinizi ve özel bilgileri yönetin." },
      security: { label: "Güvenlik", description: "Şifre ve 2 adımlı doğrulama tercihlerini yönetin." },
      plan: { label: "Plan & Faturalama", description: "Abonelik, ödeme yöntemleri ve faturalar." },
      notifications: { label: "Bildirimler", description: "Hangi kanalda ne zaman bilgilendirileceğinizi yönetin." },
      team: { label: "Ekip", description: "Mevcut ekibi yönetin ve roller/izinleri değiştirin." },
      app: { label: "Uygulama ayarları", description: "Global çalışma alanı ayarları." },
      bank: { label: "Banka hesapları", description: "Ödeme alınacak banka hesaplarını yönetin (sadece mock)." },
      cards: { label: "Kredi kartları", description: "Ödeme kartlarını yönetin (sadece mock)." },
      org: { label: "Organizasyon", description: "Organizasyon profili ve doğrulama detayları." }
    }
  }
} as const;

export const FOOTER_TEXTS = {
  en: { cancel: "Cancel", save: "Save" },
  de: { cancel: "Abbrechen", save: "Speichern" },
  tr: { cancel: "Vazgeç", save: "Kaydet" }
} as const;

export const ACCOUNT_TEXTS = {
  en: {
    title: "Account",
    pending: "Your changes are pending admin approval",
    chip: "Pending",
    profile: {
      title: "Profile Information",
      name: "Full Name",
      namePlaceholder: "Enter your full name",
      company: "Company",
      companyPlaceholder: "Enter your company name",
      titleLabel: "Job Title",
      titlePlaceholder: "Enter your job title",
      notes: "Bio",
      notesHint: "Tell us about yourself",
      notesPlaceholder: "Enter your bio"
    },
    personal: {
      title: "Personal Information",
      email: "Email",
      emailPlaceholder: "Enter your email",
      phone: "Phone",
      phonePlaceholder: "Enter your phone number",
      country: "Country",
      countryPlaceholder: "Enter your country",
      language: "Language",
      languagePlaceholder: "Select language"
    },
    actions: {
      edit: "Edit",
      cancel: "Cancel",
      save: "Save"
    },
    modal: {
      title: "Confirm Changes",
      body: "Your changes will be submitted for admin approval. Are you sure you want to continue?",
      submit: "Submit for Approval"
    }
  },
  de: {
    title: "Account",
    pending: "Ihre Änderungen warten auf Admin-Genehmigung",
    chip: "Ausstehend",
    profile: {
      title: "Profilinformationen",
      name: "Vollständiger Name",
      namePlaceholder: "Vollständigen Namen eingeben",
      company: "Unternehmen",
      companyPlaceholder: "Unternehmensname eingeben",
      titleLabel: "Jobtitel",
      titlePlaceholder: "Jobtitel eingeben",
      notes: "Bio",
      notesHint: "Erzählen Sie uns über sich",
      notesPlaceholder: "Bio eingeben"
    },
    personal: {
      title: "Persönliche Informationen",
      email: "E-Mail",
      emailPlaceholder: "E-Mail eingeben",
      phone: "Telefon",
      phonePlaceholder: "Telefonnummer eingeben",
      country: "Land",
      countryPlaceholder: "Land eingeben",
      language: "Sprache",
      languagePlaceholder: "Sprache auswählen"
    },
    actions: {
      edit: "Bearbeiten",
      cancel: "Abbrechen",
      save: "Speichern"
    },
    modal: {
      title: "Änderungen bestätigen",
      body: "Ihre Änderungen werden zur Admin-Genehmigung eingereicht. Sind Sie sicher, dass Sie fortfahren möchten?",
      submit: "Zur Genehmigung einreichen"
    }
  },
  tr: {
    title: "Hesap",
    pending: "Değişiklikleriniz admin onayı bekliyor",
    chip: "Beklemede",
    profile: {
      title: "Profil Bilgileri",
      name: "Tam Ad",
      namePlaceholder: "Tam adınızı girin",
      company: "Şirket",
      companyPlaceholder: "Şirket adınızı girin",
      titleLabel: "İş Unvanı",
      titlePlaceholder: "İş unvanınızı girin",
      notes: "Biyografi",
      notesHint: "Kendinizden bahsedin",
      notesPlaceholder: "Biyografinizi girin"
    },
    personal: {
      title: "Kişisel Bilgiler",
      email: "E-posta",
      emailPlaceholder: "E-postanızı girin",
      phone: "Telefon",
      phonePlaceholder: "Telefon numaranızı girin",
      country: "Ülke",
      countryPlaceholder: "Ülkenizi girin",
      language: "Dil",
      languagePlaceholder: "Dil seçin"
    },
    actions: {
      edit: "Düzenle",
      cancel: "İptal",
      save: "Kaydet"
    },
    modal: {
      title: "Değişiklikleri Onayla",
      body: "Değişiklikleriniz admin onayı için gönderilecektir. Devam etmek istediğinizden emin misiniz?",
      submit: "Onay İçin Gönder"
    }
  }
} as const;

export const SECURITY_TEXTS = {
  en: {
    title: "Security",
    password: {
      title: "Password",
      hint: "Change your password to keep your account secure",
      current: "Current Password",
      new: "New Password"
    },
    preferences: {
      title: "Security Preferences",
      twoStep: {
        label: "Two-Factor Authentication",
        description: "Add an extra layer of security to your account"
      },
      rotate: {
        label: "Rotate Password",
        description: "Automatically rotate password every 90 days"
      }
    }
  },
  de: {
    title: "Sicherheit",
    password: {
      title: "Passwort",
      hint: "Ändern Sie Ihr Passwort, um Ihr Konto sicher zu halten",
      current: "Aktuelles Passwort",
      new: "Neues Passwort"
    },
    preferences: {
      title: "Sicherheitseinstellungen",
      twoStep: {
        label: "Zwei-Faktor-Authentifizierung",
        description: "Extra Sicherheitsebene für Ihr Konto"
      },
      rotate: {
        label: "Passwort rotieren",
        description: "Passwort automatisch alle 90 Tage rotieren"
      }
    }
  },
  tr: {
    title: "Güvenlik",
    password: {
      title: "Şifre",
      hint: "Hesabınızı güvende tutmak için şifrenizi değiştirin",
      current: "Mevcut Şifre",
      new: "Yeni Şifre"
    },
    preferences: {
      title: "Güvenlik Tercihleri",
      twoStep: {
        label: "İki Faktörlü Kimlik Doğrulama",
        description: "Hesabınıza ek güvenlik katmanı ekleyin"
      },
      rotate: {
        label: "Şifre Döndür",
        description: "Şifreyi otomatik olarak her 90 günde bir değiştir"
      }
    }
  }
} as const;
