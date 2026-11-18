export const TEXTS = {
  en: {
    selection: {
      title: "Sign up as a business or a worker",
      business: {
        title: "Business",
        description: "I want to join as a business and hire workers"
      },
      employer: {
        title: "Employer (not a company)",
        description: "I want to join as an employer"
      },
      worker: {
        title: "Worker",
        description: "I want to join as a worker and pick up shifts"
      },
      admin: {
        title: "System Admin",
        description: "I want to register as a system administrator"
      },
      loginInstead: "Log in instead"
    },
    steps: {
      business: {
        1: { title: "Personal Information" },
        2: { title: "Organization Type" },
        3: { title: "Company Information" },
        4: { title: "Industry & Job Roles" }
      },
      employer: {
        1: { title: "Personal Information" },
        2: { title: "Industry & Job Roles" }
      },
      worker: {
        1: { title: "Personal Information" },
        2: { title: "Industry & Job Roles" },
        3: { title: "Driving License" },
        4: { title: "Certificates" },
        5: { title: "Documents" },
        6: { title: "Address" },
        7: { title: "General Availability" },
        8: { title: "Custom Shifts" }
      },
      admin: {
        1: { title: "Personal Information" }
      }
    },
    business: {
      step1: {
        firstName: "First Name",
        firstNamePlaceholder: "Enter first name",
        lastName: "Last Name",
        lastNamePlaceholder: "Enter last name",
        birthday: "Birthday",
        ssn: "SSN",
        ssnPlaceholder: "Enter Social Security Number",
        itin: "ITIN",
        itinPlaceholder: "Enter ITIN if applicable",
        email: "Email",
        emailPlaceholder: "Enter email address",
        phone: "Phone",
        phonePlaceholder: "Enter phone number",
        password: "Password",
        passwordPlaceholder: "Enter password"
      },
      step2: {
        description: "Are you creating a new organization or joining an existing one?",
        newOrganization: {
          title: "New Organization",
          description: "I want to create a new business/organization"
        },
        existingOrganization: {
          title: "Existing Organization",
          description: "I want to join an existing organization"
        }
      },
      step3: {
        searchOrganization: "Search Organization",
        searchPlaceholder: "Search by organization name...",
        noResults: "No organizations found. Try a different search term.",
        noOrganizations: "Start typing to search for organizations...",
        selectedOrganization: "Selected Organization:",
        companyName: "Company Name",
        companyNamePlaceholder: "Enter company name",
        legalEntityType: "Legal Entity Type",
        legalEntityTypePlaceholder: "e.g., AG, GmbH, LLC",
        companyTaxId: "Company Tax ID",
        companyTaxIdPlaceholder: "Enter tax identification number",
        itin: "ITIN",
        itinPlaceholder: "Enter ITIN if applicable",
        officialAddress: "Official Address",
        officialAddressPlaceholder: "Enter registered business address",
        vatNumber: "VAT Number",
        vatNumberPlaceholder: "Enter VAT number",
        registrationNumber: "Registration Number",
        registrationNumberPlaceholder: "Enter business registration number",
        differentBillingAddress: "Use different billing address",
        billingAddress: "Billing Address",
        billingAddressPlaceholder: "Enter billing address if different"
      },
      step4: {
        industry: "Industry",
        selectIndustry: "Select industry",
        jobRoles: "Job Roles",
        hearAboutUs: "Where did you hear about us?",
        hearAboutUsPlaceholder: "e.g., Google, LinkedIn, Friend"
      }
    },
    employer: {
      step1: {
        firstName: "First Name",
        firstNamePlaceholder: "Enter first name",
        lastName: "Last Name",
        lastNamePlaceholder: "Enter last name",
        birthday: "Birthday",
        ssn: "SSN",
        ssnPlaceholder: "Enter Social Security Number",
        itin: "ITIN",
        itinPlaceholder: "Enter ITIN if applicable",
        email: "Email",
        emailPlaceholder: "Enter email address",
        phone: "Phone",
        phonePlaceholder: "Enter phone number",
        password: "Password",
        passwordPlaceholder: "Enter password"
      },
      step2: {
        industry: "Industry",
        selectIndustry: "Select industry",
        jobRoles: "Job Roles",
        hearAboutUs: "Where did you hear about us?",
        hearAboutUsPlaceholder: "e.g., Google, LinkedIn, Friend"
      }
    },
    worker: {
      step1: {
        firstName: "First Name",
        firstNamePlaceholder: "Enter first name",
        lastName: "Last Name",
        lastNamePlaceholder: "Enter last name",
        birthday: "Birthday",
        ssn: "SSN",
        ssnPlaceholder: "Enter Social Security Number",
        itin: "ITIN",
        itinPlaceholder: "Enter ITIN if applicable",
        email: "Email",
        emailPlaceholder: "Enter email address",
        phone: "Phone",
        phonePlaceholder: "Enter phone number",
        password: "Password",
        passwordPlaceholder: "Enter password"
      },
      step2: {
        industry: "Industry (Job Role Category)",
        selectIndustry: "Select industry",
        jobRoles: "Job Roles",
        experiences: "Work Experience",
        addExperience: "Add Experience",
        selectJobRole: "Select job role",
        years: "Years",
        months: "Months",
        days: "Days"
      },
      step3: {
        licenseNumber: "License Number",
        licenseNumberPlaceholder: "Enter driving license number",
        issueDate: "Issue Date",
        expiryDate: "Expiry Date",
        frontImage: "Front Image",
        backImage: "Back Image",
        uploadFront: "Upload front of license",
        uploadBack: "Upload back of license"
      },
      step4: {
        certificates: "Certificates",
        addCertificate: "Add Certificate",
        noCertificates: "No certificates added yet",
        modal: {
          title: "Add Certificate",
          selectCertificate: "Select Certificate",
          selectCertificatePlaceholder: "Choose a certificate type",
          other: "Other",
          customName: "Certificate Name",
          customNamePlaceholder: "Enter certificate name",
          expiryDate: "Expiry Date (Optional)",
          uploadFile: "Upload Certificate File",
          uploadFilePlaceholder: "Click to upload or drag and drop",
          cancel: "Cancel",
          add: "Add"
        }
      },
      step5: {
        documents: "Documents",
        addDocument: "Add Document",
        noDocuments: "No documents added yet",
        modal: {
          title: "Add Document",
          selectDocument: "Select Document Type",
          selectDocumentPlaceholder: "Choose a document type",
          other: "Other",
          customType: "Document Type",
          customTypePlaceholder: "Enter document type",
          frontImage: "Front Image",
          backImage: "Back Image",
          uploadFront: "Upload front image",
          uploadBack: "Upload back image",
          uploadFile: "Upload Document File",
          uploadFilePlaceholder: "Click to upload or drag and drop",
          cancel: "Cancel",
          add: "Add"
        }
      },
      step6: {
        street: "Street Address",
        streetPlaceholder: "Enter street address",
        city: "City",
        cityPlaceholder: "Enter city",
        state: "State/Province",
        statePlaceholder: "Enter state or province",
        zipCode: "ZIP/Postal Code",
        zipCodePlaceholder: "Enter ZIP or postal code",
        country: "Country",
        countryPlaceholder: "Enter country"
      },
      step7: {
        selectDays: "Select Available Days",
        days: {
          monday: "Mon",
          tuesday: "Tue",
          wednesday: "Wed",
          thursday: "Thu",
          friday: "Fri",
          saturday: "Sat",
          sunday: "Sun"
        },
        timeWindows: "Time Windows",
        addTimeWindow: "Add Time Window",
        selectDay: "Select day"
      },
      step8: {
        customShifts: "Custom Shifts",
        addShift: "Add Shift",
        date: "Date",
        startTime: "Start Time",
        endTime: "End Time",
        notes: "Notes (optional)"
      }
    },
    admin: {
      step1: {
        firstName: "First Name",
        firstNamePlaceholder: "Enter first name",
        lastName: "Last Name",
        lastNamePlaceholder: "Enter last name",
        birthday: "Birthday",
        ssn: "SSN",
        ssnPlaceholder: "Enter Social Security Number",
        itin: "ITIN",
        itinPlaceholder: "Enter ITIN if applicable",
        email: "Email",
        emailPlaceholder: "Enter email address",
        phone: "Phone",
        phonePlaceholder: "Enter phone number",
        password: "Password",
        passwordPlaceholder: "Enter password"
      }
    },
    common: {
      back: "Back",
      next: "Next",
      skip: "Skip",
      finish: "Finish & Go to Dashboard",
      finishAndLogin: "Finish & Go to Login",
      stepSaved: "{step} saved successfully",
      stepSkipped: "{step} skipped"
    }
  },
  de: {
    selection: {
      title: "Als Unternehmen oder Mitarbeiter registrieren",
      business: {
        title: "Unternehmen",
        description: "Ich möchte mich als Unternehmen anmelden und Mitarbeiter einstellen"
      },
      employer: {
        title: "Arbeitgeber (kein Unternehmen)",
        description: "Ich möchte mich als Arbeitgeber anmelden"
      },
      worker: {
        title: "Mitarbeiter",
        description: "Ich möchte mich als Mitarbeiter anmelden und Schichten übernehmen"
      },
      loginInstead: "Stattdessen anmelden"
    },
    steps: {
      business: {
        1: { title: "Persönliche Informationen" },
        2: { title: "Organisationstyp" },
        3: { title: "Unternehmensinformationen" },
        4: { title: "Branche & Jobrollen" }
      },
      employer: {
        1: { title: "Persönliche Informationen" },
        2: { title: "Branche & Jobrollen" }
      },
      worker: {
        1: { title: "Persönliche Informationen" },
        2: { title: "Branche & Jobrollen" },
        3: { title: "Führerschein" },
        4: { title: "Zertifikate" },
        5: { title: "Dokumente" },
        6: { title: "Adresse" },
        7: { title: "Allgemeine Verfügbarkeit" },
        8: { title: "Individuelle Schichten" }
      },
      admin: {
        1: { title: "Persönliche Informationen" }
      }
    },
    business: {
      step1: {
        firstName: "Vorname",
        firstNamePlaceholder: "Vorname eingeben",
        lastName: "Nachname",
        lastNamePlaceholder: "Nachname eingeben",
        birthday: "Geburtstag",
        ssn: "Sozialversicherungsnummer",
        ssnPlaceholder: "Sozialversicherungsnummer eingeben",
        itin: "ITIN",
        itinPlaceholder: "ITIN eingeben, falls zutreffend",
        email: "E-Mail",
        emailPlaceholder: "E-Mail-Adresse eingeben",
        phone: "Telefon",
        phonePlaceholder: "Telefonnummer eingeben",
        password: "Passwort",
        passwordPlaceholder: "Passwort eingeben"
      },
      step2: {
        description: "Erstellen Sie eine neue Organisation oder treten Sie einer bestehenden bei?",
        newOrganization: {
          title: "Neue Organisation",
          description: "Ich möchte ein neues Unternehmen/eine neue Organisation erstellen"
        },
        existingOrganization: {
          title: "Bestehende Organisation",
          description: "Ich möchte einer bestehenden Organisation beitreten"
        }
      },
      step3: {
        searchOrganization: "Organisation suchen",
        searchPlaceholder: "Nach Organisationsname suchen...",
        noResults: "Keine Organisationen gefunden. Versuchen Sie einen anderen Suchbegriff.",
        noOrganizations: "Beginnen Sie mit der Eingabe, um nach Organisationen zu suchen...",
        selectedOrganization: "Ausgewählte Organisation:",
        companyName: "Firmenname",
        companyNamePlaceholder: "Firmenname eingeben",
        legalEntityType: "Rechtsform",
        legalEntityTypePlaceholder: "z. B. AG, GmbH, LLC",
        companyTaxId: "Steuernummer",
        companyTaxIdPlaceholder: "Steueridentifikationsnummer eingeben",
        itin: "ITIN",
        itinPlaceholder: "ITIN eingeben, falls zutreffend",
        officialAddress: "Offizielle Adresse",
        officialAddressPlaceholder: "Registrierte Geschäftsadresse eingeben",
        vatNumber: "Umsatzsteuer-ID",
        vatNumberPlaceholder: "Umsatzsteuer-ID eingeben",
        registrationNumber: "Handelsregisternummer",
        registrationNumberPlaceholder: "Handelsregisternummer eingeben",
        differentBillingAddress: "Andere Rechnungsadresse verwenden",
        billingAddress: "Rechnungsadresse",
        billingAddressPlaceholder: "Rechnungsadresse eingeben, falls abweichend"
      },
      step4: {
        industry: "Branche",
        selectIndustry: "Branche auswählen",
        jobRoles: "Jobrollen",
        hearAboutUs: "Wo haben Sie von uns erfahren?",
        hearAboutUsPlaceholder: "z. B. Google, LinkedIn, Freund"
      }
    },
    employer: {
      step1: {
        firstName: "Vorname",
        firstNamePlaceholder: "Vorname eingeben",
        lastName: "Nachname",
        lastNamePlaceholder: "Nachname eingeben",
        birthday: "Geburtstag",
        ssn: "Sozialversicherungsnummer",
        ssnPlaceholder: "Sozialversicherungsnummer eingeben",
        itin: "ITIN",
        itinPlaceholder: "ITIN eingeben, falls zutreffend",
        email: "E-Mail",
        emailPlaceholder: "E-Mail-Adresse eingeben",
        phone: "Telefon",
        phonePlaceholder: "Telefonnummer eingeben",
        password: "Passwort",
        passwordPlaceholder: "Passwort eingeben"
      },
      step2: {
        industry: "Branche",
        selectIndustry: "Branche auswählen",
        jobRoles: "Jobrollen",
        hearAboutUs: "Wo haben Sie von uns erfahren?",
        hearAboutUsPlaceholder: "z. B. Google, LinkedIn, Freund"
      }
    },
    worker: {
      step1: {
        firstName: "Vorname",
        firstNamePlaceholder: "Vorname eingeben",
        lastName: "Nachname",
        lastNamePlaceholder: "Nachname eingeben",
        birthday: "Geburtstag",
        ssn: "Sozialversicherungsnummer",
        ssnPlaceholder: "Sozialversicherungsnummer eingeben",
        itin: "ITIN",
        itinPlaceholder: "ITIN eingeben, falls zutreffend",
        email: "E-Mail",
        emailPlaceholder: "E-Mail-Adresse eingeben",
        phone: "Telefon",
        phonePlaceholder: "Telefonnummer eingeben",
        password: "Passwort",
        passwordPlaceholder: "Passwort eingeben"
      },
      step2: {
        industry: "Branche (Jobrollenkategorie)",
        selectIndustry: "Branche auswählen",
        jobRoles: "Jobrollen",
        experiences: "Berufserfahrung",
        addExperience: "Erfahrung hinzufügen",
        selectJobRole: "Jobrolle auswählen",
        years: "Jahre",
        months: "Monate",
        days: "Tage"
      },
      step3: {
        licenseNumber: "Führerscheinnummer",
        licenseNumberPlaceholder: "Führerscheinnummer eingeben",
        issueDate: "Ausstellungsdatum",
        expiryDate: "Ablaufdatum",
        frontImage: "Vorderseite",
        backImage: "Rückseite",
        uploadFront: "Vorderseite des Führerscheins hochladen",
        uploadBack: "Rückseite des Führerscheins hochladen"
      },
      step4: {
        certificates: "Zertifikate",
        addCertificate: "Zertifikat hinzufügen",
        noCertificates: "Noch keine Zertifikate hinzugefügt",
        modal: {
          title: "Zertifikat hinzufügen",
          selectCertificate: "Zertifikat auswählen",
          selectCertificatePlaceholder: "Zertifikatstyp wählen",
          other: "Sonstiges",
          customName: "Zertifikatsname",
          customNamePlaceholder: "Zertifikatsname eingeben",
          expiryDate: "Ablaufdatum (Optional)",
          uploadFile: "Zertifikatsdatei hochladen",
          uploadFilePlaceholder: "Zum Hochladen klicken oder per Drag & Drop",
          cancel: "Abbrechen",
          add: "Hinzufügen"
        }
      },
      step5: {
        documents: "Dokumente",
        addDocument: "Dokument hinzufügen",
        noDocuments: "Noch keine Dokumente hinzugefügt",
        modal: {
          title: "Dokument hinzufügen",
          selectDocument: "Dokumenttyp auswählen",
          selectDocumentPlaceholder: "Dokumenttyp wählen",
          other: "Sonstiges",
          customType: "Dokumenttyp",
          customTypePlaceholder: "Dokumenttyp eingeben",
          frontImage: "Vorderseite",
          backImage: "Rückseite",
          uploadFront: "Vorderseite hochladen",
          uploadBack: "Rückseite hochladen",
          uploadFile: "Dokumentdatei hochladen",
          uploadFilePlaceholder: "Zum Hochladen klicken oder per Drag & Drop",
          cancel: "Abbrechen",
          add: "Hinzufügen"
        }
      },
      step6: {
        street: "Straßenadresse",
        streetPlaceholder: "Straßenadresse eingeben",
        city: "Stadt",
        cityPlaceholder: "Stadt eingeben",
        state: "Bundesland/Provinz",
        statePlaceholder: "Bundesland oder Provinz eingeben",
        zipCode: "PLZ",
        zipCodePlaceholder: "PLZ eingeben",
        country: "Land",
        countryPlaceholder: "Land eingeben"
      },
      step7: {
        selectDays: "Verfügbare Tage auswählen",
        days: {
          monday: "Mo",
          tuesday: "Di",
          wednesday: "Mi",
          thursday: "Do",
          friday: "Fr",
          saturday: "Sa",
          sunday: "So"
        },
        timeWindows: "Zeitfenster",
        addTimeWindow: "Zeitfenster hinzufügen",
        selectDay: "Tag auswählen"
      },
      step8: {
        customShifts: "Individuelle Schichten",
        addShift: "Schicht hinzufügen",
        date: "Datum",
        startTime: "Startzeit",
        endTime: "Endzeit",
        notes: "Notizen (optional)"
      }
    },
    admin: {
      step1: {
        firstName: "Vorname",
        firstNamePlaceholder: "Vorname eingeben",
        lastName: "Nachname",
        lastNamePlaceholder: "Nachname eingeben",
        birthday: "Geburtstag",
        ssn: "Sozialversicherungsnummer",
        ssnPlaceholder: "Sozialversicherungsnummer eingeben",
        itin: "ITIN",
        itinPlaceholder: "ITIN eingeben, falls zutreffend",
        email: "E-Mail",
        emailPlaceholder: "E-Mail-Adresse eingeben",
        phone: "Telefon",
        phonePlaceholder: "Telefonnummer eingeben"
      }
    },
    common: {
      back: "Zurück",
      next: "Weiter",
      skip: "Überspringen",
      finish: "Abschließen & Zum Dashboard",
      finishAndLogin: "Abschließen & Zum Login",
      stepSaved: "{step} erfolgreich gespeichert",
      stepSkipped: "{step} übersprungen"
    }
  },
  tr: {
    selection: {
      title: "Bir işletme veya çalışan olarak kaydolun",
      business: {
        title: "İşletme",
        description: "Bir işletme olarak katılmak ve çalışan işe almak istiyorum"
      },
      employer: {
        title: "İşveren (şirket değil)",
        description: "Bir işveren olarak katılmak istiyorum"
      },
      worker: {
        title: "Çalışan",
        description: "Bir çalışan olarak katılmak ve vardiya almak istiyorum"
      },
      loginInstead: "Bunun yerine giriş yap"
    },
    steps: {
      business: {
        1: { title: "Kişisel Bilgiler" },
        2: { title: "Organizasyon Türü" },
        3: { title: "Şirket Bilgileri" },
        4: { title: "Sektör & İş Rolleri" }
      },
      employer: {
        1: { title: "Kişisel Bilgiler" },
        2: { title: "Sektör & İş Rolleri" }
      },
      worker: {
        1: { title: "Kişisel Bilgiler" },
        2: { title: "Sektör & İş Rolleri" },
        3: { title: "Ehliyet" },
        4: { title: "Sertifikalar & Belgeler" },
        5: { title: "Adres" },
        6: { title: "Genel Müsaitlik" },
        7: { title: "Özel Vardiyalar" }
      }
    },
    business: {
      step1: {
        firstName: "Ad",
        firstNamePlaceholder: "Adı girin",
        lastName: "Soyad",
        lastNamePlaceholder: "Soyadı girin",
        birthday: "Doğum Tarihi",
        ssn: "SSN",
        ssnPlaceholder: "Sosyal Güvenlik Numarasını girin",
        itin: "ITIN",
        itinPlaceholder: "Varsa ITIN girin",
        email: "E-posta",
        emailPlaceholder: "E-posta adresini girin",
        phone: "Telefon",
        phonePlaceholder: "Telefon numarasını girin",
        password: "Şifre",
        passwordPlaceholder: "Şifre girin"
      },
      step2: {
        description: "Yeni bir organizasyon mu oluşturuyorsunuz yoksa mevcut bir organizasyona mı katılıyorsunuz?",
        newOrganization: {
          title: "Yeni Organizasyon",
          description: "Yeni bir işletme/organizasyon oluşturmak istiyorum"
        },
        existingOrganization: {
          title: "Mevcut Organizasyon",
          description: "Mevcut bir organizasyona katılmak istiyorum"
        }
      },
      step3: {
        searchOrganization: "Organizasyon Ara",
        searchPlaceholder: "Organizasyon adına göre ara...",
        noResults: "Organizasyon bulunamadı. Farklı bir arama terimi deneyin.",
        noOrganizations: "Organizasyonları aramak için yazmaya başlayın...",
        selectedOrganization: "Seçilen Organizasyon:",
        companyName: "Şirket Adı",
        companyNamePlaceholder: "Şirket adını girin",
        legalEntityType: "Yasal Varlık Türü",
        legalEntityTypePlaceholder: "örn. AG, GmbH, LLC",
        companyTaxId: "Şirket Vergi Kimlik No",
        companyTaxIdPlaceholder: "Vergi kimlik numarasını girin",
        itin: "ITIN",
        itinPlaceholder: "Varsa ITIN girin",
        officialAddress: "Resmi Adres",
        officialAddressPlaceholder: "Kayıtlı iş adresini girin",
        vatNumber: "KDV Numarası",
        vatNumberPlaceholder: "KDV numarasını girin",
        registrationNumber: "Kayıt Numarası",
        registrationNumberPlaceholder: "İş kayıt numarasını girin",
        differentBillingAddress: "Farklı fatura adresi kullan",
        billingAddress: "Fatura Adresi",
        billingAddressPlaceholder: "Farklıysa fatura adresini girin"
      },
      step4: {
        industry: "Sektör",
        selectIndustry: "Sektör seçin",
        jobRoles: "İş Rolleri",
        hearAboutUs: "Bizi nereden duydunuz?",
        hearAboutUsPlaceholder: "örn. Google, LinkedIn, Arkadaş"
      }
    },
    employer: {
      step1: {
        firstName: "Ad",
        firstNamePlaceholder: "Adı girin",
        lastName: "Soyad",
        lastNamePlaceholder: "Soyadı girin",
        birthday: "Doğum Tarihi",
        ssn: "SSN",
        ssnPlaceholder: "Sosyal Güvenlik Numarasını girin",
        itin: "ITIN",
        itinPlaceholder: "Varsa ITIN girin",
        email: "E-posta",
        emailPlaceholder: "E-posta adresini girin",
        phone: "Telefon",
        phonePlaceholder: "Telefon numarasını girin",
        password: "Şifre",
        passwordPlaceholder: "Şifre girin"
      },
      step2: {
        industry: "Sektör",
        selectIndustry: "Sektör seçin",
        jobRoles: "İş Rolleri",
        hearAboutUs: "Bizi nereden duydunuz?",
        hearAboutUsPlaceholder: "örn. Google, LinkedIn, Arkadaş"
      }
    },
    worker: {
      step1: {
        firstName: "Ad",
        firstNamePlaceholder: "Adı girin",
        lastName: "Soyad",
        lastNamePlaceholder: "Soyadı girin",
        birthday: "Doğum Tarihi",
        ssn: "SSN",
        ssnPlaceholder: "Sosyal Güvenlik Numarasını girin",
        itin: "ITIN",
        itinPlaceholder: "Varsa ITIN girin",
        email: "E-posta",
        emailPlaceholder: "E-posta adresini girin",
        phone: "Telefon",
        phonePlaceholder: "Telefon numarasını girin",
        password: "Şifre",
        passwordPlaceholder: "Şifre girin"
      },
      step2: {
        industry: "Sektör (İş Rolü Kategorisi)",
        selectIndustry: "Sektör seçin",
        jobRoles: "İş Rolleri",
        experiences: "İş Deneyimi",
        addExperience: "Deneyim Ekle",
        selectJobRole: "İş rolü seçin",
        years: "Yıl",
        months: "Ay",
        days: "Gün"
      },
      step3: {
        licenseNumber: "Ehliyet Numarası",
        licenseNumberPlaceholder: "Ehliyet numarasını girin",
        issueDate: "Veriliş Tarihi",
        expiryDate: "Son Kullanma Tarihi",
        frontImage: "Ön Yüz",
        backImage: "Arka Yüz",
        uploadFront: "Ehliyetin önünü yükle",
        uploadBack: "Ehliyetin arkasını yükle"
      },
      step4: {
        certificates: "Sertifikalar",
        addCertificate: "Sertifika Ekle",
        noCertificates: "Henüz sertifika eklenmedi",
        modal: {
          title: "Sertifika Ekle",
          selectCertificate: "Sertifika Seç",
          selectCertificatePlaceholder: "Bir sertifika türü seçin",
          other: "Diğer",
          customName: "Sertifika Adı",
          customNamePlaceholder: "Sertifika adını girin",
          expiryDate: "Son Kullanma Tarihi (İsteğe Bağlı)",
          uploadFile: "Sertifika Dosyası Yükle",
          uploadFilePlaceholder: "Yüklemek için tıklayın veya sürükleyip bırakın",
          cancel: "İptal",
          add: "Ekle"
        }
      },
      step5: {
        documents: "Belgeler",
        addDocument: "Belge Ekle",
        noDocuments: "Henüz belge eklenmedi",
        modal: {
          title: "Belge Ekle",
          selectDocument: "Belge Türü Seç",
          selectDocumentPlaceholder: "Bir belge türü seçin",
          other: "Diğer",
          customType: "Belge Türü",
          customTypePlaceholder: "Belge türünü girin",
          frontImage: "Ön Yüz",
          backImage: "Arka Yüz",
          uploadFront: "Ön yüzü yükle",
          uploadBack: "Arka yüzü yükle",
          uploadFile: "Belge Dosyası Yükle",
          uploadFilePlaceholder: "Yüklemek için tıklayın veya sürükleyip bırakın",
          cancel: "İptal",
          add: "Ekle"
        }
      },
      step6: {
        street: "Sokak Adresi",
        streetPlaceholder: "Sokak adresini girin",
        city: "Şehir",
        cityPlaceholder: "Şehri girin",
        state: "İl/Eyalet",
        statePlaceholder: "İl veya eyaleti girin",
        zipCode: "Posta Kodu",
        zipCodePlaceholder: "Posta kodunu girin",
        country: "Ülke",
        countryPlaceholder: "Ülkeyi girin"
      },
      step7: {
        selectDays: "Müsait Günleri Seçin",
        days: {
          monday: "Pzt",
          tuesday: "Sal",
          wednesday: "Çar",
          thursday: "Per",
          friday: "Cum",
          saturday: "Cmt",
          sunday: "Paz"
        },
        timeWindows: "Zaman Pencereleri",
        addTimeWindow: "Zaman Penceresi Ekle",
        selectDay: "Gün seçin"
      },
      step8: {
        customShifts: "Özel Vardiyalar",
        addShift: "Vardiya Ekle",
        date: "Tarih",
        startTime: "Başlangıç Saati",
        endTime: "Bitiş Saati",
        notes: "Notlar (isteğe bağlı)"
      }
    },
    admin: {
      step1: {
        firstName: "Ad",
        firstNamePlaceholder: "Adı girin",
        lastName: "Soyad",
        lastNamePlaceholder: "Soyadı girin",
        birthday: "Doğum Tarihi",
        ssn: "SSN",
        ssnPlaceholder: "Sosyal Güvenlik Numarasını girin",
        itin: "ITIN",
        itinPlaceholder: "Varsa ITIN girin",
        email: "E-posta",
        emailPlaceholder: "E-posta adresini girin",
        phone: "Telefon",
        phonePlaceholder: "Telefon numarasını girin"
      }
    },
    common: {
      back: "Geri",
      next: "İleri",
      skip: "Atla",
      finish: "Bitir & Dashboard'a Git",
      finishAndLogin: "Bitir & Giriş'e Git",
      stepSaved: "{step} başarıyla kaydedildi",
      stepSkipped: "{step} atlandı"
    }
  }
} as const;

