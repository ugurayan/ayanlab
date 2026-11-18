export const TEXTS = {
  en: {
    mobile: {
      title: "Verify Mobile Number",
      labels: {
        mobile: "Mobile Number",
        code: "Verification Code"
      },
      placeholders: {
        mobile: "+41 79 123 45 67",
        code: "000000"
      },
      actions: {
        sendCode: "Send Verification Code",
        verify: "Verify",
        changeMobile: "Change Number",
        resendCode: "Resend Code",
        retry: "Try Again"
      },
      hints: {
        codeSent: "We sent a verification code to {mobile}",
        resendIn: "Resend code in {seconds} seconds"
      },
      success: {
        title: "Mobile Verified!",
        message: "Your mobile number has been successfully verified."
      },
      errors: {
        title: "Verification Failed",
        required: "Mobile number is required",
        codeRequired: "Verification code is required",
        sendFailed: "Failed to send verification code. Please try again.",
        verifyFailed: "Failed to verify code. Please try again."
      }
    },
    email: {
      title: "Verify Email Address",
      labels: {
        email: "Email Address",
        code: "Verification Code"
      },
      placeholders: {
        email: "user@example.com",
        code: "000000"
      },
      actions: {
        sendCode: "Send Verification Code",
        verify: "Verify",
        changeEmail: "Change Email",
        resendCode: "Resend Code",
        retry: "Try Again"
      },
      hints: {
        codeSent: "We sent a verification code to {email}",
        resendIn: "Resend code in {seconds} seconds"
      },
      success: {
        title: "Email Verified!",
        message: "Your email address has been successfully verified."
      },
      errors: {
        title: "Verification Failed",
        invalidEmail: "Please enter a valid email address",
        codeRequired: "Verification code is required",
        sendFailed: "Failed to send verification code. Please try again.",
        verifyFailed: "Failed to verify code. Please try again."
      }
    },
    document: {
      title: "Document Verification",
      subtitle: "Review and verify submitted documents",
      empty: "No verification tasks available.",
      table: {
        worker: "Worker",
        type: "Type",
        category: "Category",
        status: "Status",
        priority: "Priority",
        submittedAt: "Submitted",
        actions: "Actions"
      },
      categories: {
        identity: "Identity Verification",
        address: "Proof of Address",
        workEligibility: "Work Eligibility",
        certificate: "Certificates",
        photo: "Photo",
        background: "Background Check"
      },
      types: {
        name_surname: "Name & Surname",
        address: "Address",
        driving_licence: "Driving Licence",
        ssn: "SSN",
        work_permit: "Work Permit",
        itin: "ITIN",
        employer_id: "Employer ID",
        national_id: "National ID",
        birth_certificate: "Birth Certificate",
        utility_bill: "Utility Bill",
        bank_statement: "Bank Statement",
        lease_contract: "Lease Contract",
        green_card: "Green Card",
        tax_document: "Tax Document",
        training_certificate: "Training Certificate",
        education_diploma: "Education Diploma",
        safety_certificate: "Safety Certificate",
        criminal_record: "Criminal Record",
        reference_letter: "Reference Letter",
        photo: "Photo",
        other: "Other"
      },
      statuses: {
        pending: "Pending",
        reviewing: "Reviewing",
        accepted: "Accepted",
        rejected: "Rejected",
        needs_attention: "Needs Attention",
        needs_interview: "Needs Interview"
      },
      priorities: {
        low: "Low",
        medium: "Medium",
        high: "High"
      },
      modal: {
        title: "Review Document",
        worker: "Worker",
        type: "Type",
        category: "Category",
        submittedAt: "Submitted At",
        documents: "Documents",
        notes: "Review Notes",
        notesPlaceholder: "Add any notes about this verification..."
      },
      actions: {
        accept: "Accept",
        reject: "Reject",
        needsAttention: "Needs Attention",
        needsInterview: "Needs Interview"
      }
    }
  },
  de: {
    mobile: {
      title: "Handynummer bestätigen",
      labels: {
        mobile: "Handynummer",
        code: "Bestätigungscode"
      },
      placeholders: {
        mobile: "+41 79 123 45 67",
        code: "000000"
      },
      actions: {
        sendCode: "Bestätigungscode senden",
        verify: "Bestätigen",
        changeMobile: "Nummer ändern",
        resendCode: "Code erneut senden",
        retry: "Erneut versuchen"
      },
      hints: {
        codeSent: "Wir haben einen Bestätigungscode an {mobile} gesendet",
        resendIn: "Code erneut senden in {seconds} Sekunden"
      },
      success: {
        title: "Handynummer bestätigt!",
        message: "Ihre Handynummer wurde erfolgreich bestätigt."
      },
      errors: {
        title: "Bestätigung fehlgeschlagen",
        required: "Handynummer ist erforderlich",
        codeRequired: "Bestätigungscode ist erforderlich",
        sendFailed: "Bestätigungscode konnte nicht gesendet werden. Bitte versuchen Sie es erneut.",
        verifyFailed: "Code konnte nicht bestätigt werden. Bitte versuchen Sie es erneut."
      }
    },
    email: {
      title: "E-Mail-Adresse bestätigen",
      labels: {
        email: "E-Mail-Adresse",
        code: "Bestätigungscode"
      },
      placeholders: {
        email: "user@example.com",
        code: "000000"
      },
      actions: {
        sendCode: "Bestätigungscode senden",
        verify: "Bestätigen",
        changeEmail: "E-Mail ändern",
        resendCode: "Code erneut senden",
        retry: "Erneut versuchen"
      },
      hints: {
        codeSent: "Wir haben einen Bestätigungscode an {email} gesendet",
        resendIn: "Code erneut senden in {seconds} Sekunden"
      },
      success: {
        title: "E-Mail bestätigt!",
        message: "Ihre E-Mail-Adresse wurde erfolgreich bestätigt."
      },
      errors: {
        title: "Bestätigung fehlgeschlagen",
        invalidEmail: "Bitte geben Sie eine gültige E-Mail-Adresse ein",
        codeRequired: "Bestätigungscode ist erforderlich",
        sendFailed: "Bestätigungscode konnte nicht gesendet werden. Bitte versuchen Sie es erneut.",
        verifyFailed: "Code konnte nicht bestätigt werden. Bitte versuchen Sie es erneut."
      }
    },
    document: {
      title: "Dokumentenprüfung",
      subtitle: "Eingereichte Dokumente prüfen und bestätigen",
      empty: "Keine Prüfaufgaben verfügbar.",
      table: {
        worker: "Mitarbeiter",
        type: "Typ",
        category: "Kategorie",
        status: "Status",
        priority: "Priorität",
        submittedAt: "Eingereicht",
        actions: "Aktionen"
      },
      categories: {
        identity: "Identitätsprüfung",
        address: "Adressnachweis",
        workEligibility: "Arbeitserlaubnis",
        certificate: "Zertifikate",
        photo: "Foto",
        background: "Background-Check"
      },
      types: {
        name_surname: "Name & Nachname",
        address: "Adresse",
        driving_licence: "Führerschein",
        ssn: "Sozialversicherungsnummer",
        work_permit: "Arbeitsbewilligung",
        itin: "ITIN",
        employer_id: "Arbeitgeber-ID",
        national_id: "Personalausweis",
        birth_certificate: "Geburtsurkunde",
        utility_bill: "Strom-/Nebenkostenrechnung",
        bank_statement: "Kontoauszug",
        lease_contract: "Mietvertrag",
        green_card: "Aufenthaltskarte",
        tax_document: "Steuerdokument",
        training_certificate: "Schulungszertifikat",
        education_diploma: "Diplom",
        safety_certificate: "Sicherheitszertifikat",
        criminal_record: "Strafregisterauszug",
        reference_letter: "Referenzschreiben",
        photo: "Foto",
        other: "Sonstiges"
      },
      statuses: {
        pending: "Ausstehend",
        reviewing: "In Prüfung",
        accepted: "Akzeptiert",
        rejected: "Abgelehnt",
        needs_attention: "Benötigt Aufmerksamkeit",
        needs_interview: "Benötigt Interview"
      },
      priorities: {
        low: "Niedrig",
        medium: "Mittel",
        high: "Hoch"
      },
      modal: {
        title: "Dokument prüfen",
        worker: "Mitarbeiter",
        type: "Typ",
        category: "Kategorie",
        submittedAt: "Eingereicht am",
        documents: "Dokumente",
        notes: "Prüfnotizen",
        notesPlaceholder: "Notizen zu dieser Prüfung hinzufügen..."
      },
      actions: {
        accept: "Akzeptieren",
        reject: "Ablehnen",
        needsAttention: "Benötigt Aufmerksamkeit",
        needsInterview: "Benötigt Interview"
      }
    }
  },
  tr: {
    mobile: {
      title: "Cep Telefonu Doğrulama",
      labels: {
        mobile: "Cep Telefonu",
        code: "Doğrulama Kodu"
      },
      placeholders: {
        mobile: "+41 79 123 45 67",
        code: "000000"
      },
      actions: {
        sendCode: "Doğrulama Kodu Gönder",
        verify: "Doğrula",
        changeMobile: "Numarayı Değiştir",
        resendCode: "Kodu Tekrar Gönder",
        retry: "Tekrar Dene"
      },
      hints: {
        codeSent: "{mobile} numarasına bir doğrulama kodu gönderdik",
        resendIn: "{seconds} saniye içinde kodu tekrar gönder"
      },
      success: {
        title: "Cep Telefonu Doğrulandı!",
        message: "Cep telefonu numaranız başarıyla doğrulandı."
      },
      errors: {
        title: "Doğrulama Başarısız",
        required: "Cep telefonu numarası gereklidir",
        codeRequired: "Doğrulama kodu gereklidir",
        sendFailed: "Doğrulama kodu gönderilemedi. Lütfen tekrar deneyin.",
        verifyFailed: "Kod doğrulanamadı. Lütfen tekrar deneyin."
      }
    },
    email: {
      title: "E-posta Adresi Doğrulama",
      labels: {
        email: "E-posta Adresi",
        code: "Doğrulama Kodu"
      },
      placeholders: {
        email: "user@example.com",
        code: "000000"
      },
      actions: {
        sendCode: "Doğrulama Kodu Gönder",
        verify: "Doğrula",
        changeEmail: "E-postayı Değiştir",
        resendCode: "Kodu Tekrar Gönder",
        retry: "Tekrar Dene"
      },
      hints: {
        codeSent: "{email} adresine bir doğrulama kodu gönderdik",
        resendIn: "{seconds} saniye içinde kodu tekrar gönder"
      },
      success: {
        title: "E-posta Doğrulandı!",
        message: "E-posta adresiniz başarıyla doğrulandı."
      },
      errors: {
        title: "Doğrulama Başarısız",
        invalidEmail: "Lütfen geçerli bir e-posta adresi girin",
        codeRequired: "Doğrulama kodu gereklidir",
        sendFailed: "Doğrulama kodu gönderilemedi. Lütfen tekrar deneyin.",
        verifyFailed: "Kod doğrulanamadı. Lütfen tekrar deneyin."
      }
    },
    document: {
      title: "Belge Doğrulama",
      subtitle: "Gönderilen belgeleri incele ve doğrula",
      empty: "Doğrulama görevi bulunmuyor.",
      table: {
        worker: "Çalışan",
        type: "Tür",
        category: "Kategori",
        status: "Durum",
        priority: "Öncelik",
        submittedAt: "Gönderildi",
        actions: "İşlemler"
      },
      categories: {
        identity: "Kimlik Doğrulama",
        address: "Adres Kanıtı",
        workEligibility: "Çalışma Uygunluğu",
        certificate: "Sertifikalar",
        photo: "Fotoğraf",
        background: "Geçmiş Kontrolü"
      },
      types: {
        name_surname: "Ad & Soyad",
        address: "Adres",
        driving_licence: "Ehliyet",
        ssn: "Sosyal Güvenlik Numarası",
        work_permit: "Çalışma İzni",
        itin: "ITIN",
        employer_id: "İşveren ID",
        national_id: "Kimlik Kartı",
        birth_certificate: "Doğum Belgesi",
        utility_bill: "Fatura",
        bank_statement: "Hesap Ekstresi",
        lease_contract: "Kira Sözleşmesi",
        green_card: "Oturum Kartı",
        tax_document: "Vergi Belgesi",
        training_certificate: "Eğitim Sertifikası",
        education_diploma: "Diploma",
        safety_certificate: "Güvenlik Sertifikası",
        criminal_record: "Adli Sicil Kaydı",
        reference_letter: "Referans Mektubu",
        photo: "Fotoğraf",
        other: "Diğer"
      },
      statuses: {
        pending: "Beklemede",
        reviewing: "İnceleniyor",
        accepted: "Kabul Edildi",
        rejected: "Reddedildi",
        needs_attention: "Dikkat Gerekli",
        needs_interview: "Mülakat Gerekli"
      },
      priorities: {
        low: "Düşük",
        medium: "Orta",
        high: "Yüksek"
      },
      modal: {
        title: "Belge İncele",
        worker: "Çalışan",
        type: "Tür",
        category: "Kategori",
        submittedAt: "Gönderilme Tarihi",
        documents: "Belgeler",
        notes: "İnceleme Notları",
        notesPlaceholder: "Bu doğrulama hakkında notlar ekleyin..."
      },
      actions: {
        accept: "Kabul Et",
        reject: "Reddet",
        needsAttention: "Dikkat Gerekli",
        needsInterview: "Mülakat Gerekli"
      }
    }
  }
} as const;

