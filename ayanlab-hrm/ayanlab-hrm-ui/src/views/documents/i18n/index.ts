export const TEXTS = {
  en: {
    hero: {
      title: "File Manager",
      summary: "{folders} folders, {files} files",
      upload: "+ Upload file",
      loading: "Loading…"
    },
    folders: {
      title: "Folders",
      personal: "Personal",
      photos: "Photos",
      work: "Work",
      count: "{count} files"
    },
    files: {
      title: "Files",
      tooltip: "Type: {type}\nWorker: {worker}\nStatus: {status}\nCreated: {created}\nExpires: {expires}",
      empty: "No documents available."
    },
    defaults: {
      document: "Document",
      unknown: "Unknown",
      none: "—"
    },
    upload: {
      heroTitle: "Upload document",
      heroSubtitle: "Add identity, work eligibility and other documents for review.",
      backToList: "Back to documents",
      sectionTitle: "Document details",
      dropTitle: "Drag & drop files here",
      dropSubtitle: "or click to browse from your device",
      browseButton: "Browse files",
      selectedFiles: "Selected files",
      categoryLabel: "Document category",
      subtypeLabel: "Document type",
      subtypePlaceholder: "Select type",
      customLabel: "Other document name",
      customPlaceholder: "If you selected Other, describe this document",
      customHint: "Used when the document doesn't fit any predefined type.",
      cancel: "Cancel",
      submit: "Upload (mock)",
      categories: {
        identityVerification: "Identity verification",
        proofOfAddress: "Proof of address",
        workEligibility: "Work eligibility",
        certificates: "Certificates",
        photo: "Photo / picture",
        backgroundCheck: "Background check"
      },
      subtypes: {
        identity: {
          items: [
            "drivingLicence",
            "studentId",
            "governmentId",
            "ssnDocument",
            "nationalId",
            "birthCertificate",
            "other"
          ],
          hint: "Typically upload front and back of the selected ID document."
        },
        address: {
          items: ["utilityBill", "bankStatement", "leaseContract", "other"],
          hint: "Documents should be from the last 60–90 days where possible."
        },
        workEligibility: {
          items: ["greenCard", "workPermit", "itin", "taxDocument", "other"],
          hint: "Upload whichever proves your right to work (country specific)."
        },
        certificate: {
          items: ["trainingCertificate", "educationDiploma", "safetyCertificate", "other"],
          hint: "Job related certificates and training proofs."
        },
        photo: {
          items: ["selfie", "facePhoto", "profilePhoto", "other"],
          hint: "Clear photo of your face matching your ID."
        },
        background: {
          items: ["criminalRecord", "referenceLetter", "other"],
          hint: "Only if required by the job or organization."
        }
      },
      subtypesLabels: {
        drivingLicence: "Driving licence",
        studentId: "Student ID",
        governmentId: "Government ID",
        ssnDocument: "SSN document",
        nationalId: "National ID",
        birthCertificate: "Birth certificate",
        utilityBill: "Utility bill",
        bankStatement: "Bank statement",
        leaseContract: "Lease / rental agreement",
        greenCard: "Green card / residence card",
        workPermit: "Work permit",
        itin: "ITIN / tax ID",
        taxDocument: "Tax document",
        trainingCertificate: "Training certificate",
        educationDiploma: "Diploma / degree",
        safetyCertificate: "Safety / compliance certificate",
        selfie: "Selfie",
        facePhoto: "Face photo",
        profilePhoto: "Profile photo",
        criminalRecord: "Criminal record / background check",
        referenceLetter: "Reference letter",
        other: "Other"
      }
    }
  },
  de: {
    hero: {
      title: "Dateimanager",
      summary: "{folders} Ordner, {files} Dateien",
      upload: "+ Datei hochladen",
      loading: "Lädt…"
    },
    folders: {
      title: "Ordner",
      personal: "Persönlich",
      photos: "Fotos",
      work: "Arbeit",
      count: "{count} Dateien"
    },
    files: {
      title: "Dateien",
      tooltip: "Typ: {type}\nWorker: {worker}\nStatus: {status}\nErstellt: {created}\nAblauf: {expires}",
      empty: "Keine Dokumente vorhanden."
    },
    defaults: {
      document: "Dokument",
      unknown: "Unbekannt",
      none: "—"
    },
    upload: {
      heroTitle: "Dokument hochladen",
      heroSubtitle: "Identität, Arbeitserlaubnis und weitere Dokumente hinzufügen.",
      backToList: "Zur Dokumentübersicht",
      sectionTitle: "Dokumentdetails",
      dropTitle: "Dateien hierher ziehen",
      dropSubtitle: "oder klicken, um vom Gerät auszuwählen",
      browseButton: "Dateien auswählen",
      selectedFiles: "Ausgewählte Dateien",
      categoryLabel: "Dokumentkategorie",
      subtypeLabel: "Dokumenttyp",
      subtypePlaceholder: "Typ auswählen",
      customLabel: "Name für \"Sonstiges\"",
      customPlaceholder: "Wenn Sie Sonstiges gewählt haben, Dokument kurz beschreiben",
      customHint: "Wird verwendet, wenn kein vordefinierter Typ passt.",
      cancel: "Abbrechen",
      submit: "Hochladen (Mock)",
      categories: {
        identityVerification: "Identitätsprüfung",
        proofOfAddress: "Adressnachweis",
        workEligibility: "Arbeitserlaubnis",
        certificates: "Zertifikate",
        photo: "Foto / Bild",
        backgroundCheck: "Background-Check"
      },
      subtypes: {
        identity: {
          items: [
            "drivingLicence",
            "studentId",
            "governmentId",
            "ssnDocument",
            "nationalId",
            "birthCertificate",
            "other"
          ],
          hint: "In der Regel Vorder- und Rückseite des gewählten Ausweisdokuments."
        },
        address: {
          items: ["utilityBill", "bankStatement", "leaseContract", "other"],
          hint: "Dokumente sollten idealerweise nicht älter als 60–90 Tage sein."
        },
        workEligibility: {
          items: ["greenCard", "workPermit", "itin", "taxDocument", "other"],
          hint: "Nachweis Ihres Arbeitsrechts (länderabhängig)."
        },
        certificate: {
          items: ["trainingCertificate", "educationDiploma", "safetyCertificate", "other"],
          hint: "Jobbezogene Zertifikate und Schulungsnachweise."
        },
        photo: {
          items: ["selfie", "facePhoto", "profilePhoto", "other"],
          hint: "Klares Foto Ihres Gesichts passend zum Ausweis."
        },
        background: {
          items: ["criminalRecord", "referenceLetter", "other"],
          hint: "Nur falls vom Job oder der Organisation verlangt."
        }
      },
      subtypesLabels: {
        drivingLicence: "Führerausweis",
        studentId: "Studentenausweis",
        governmentId: "Amtlicher Ausweis",
        ssnDocument: "Sozialversicherungsdokument",
        nationalId: "Personalausweis",
        birthCertificate: "Geburtsurkunde",
        utilityBill: "Strom-/Nebenkostenrechnung",
        bankStatement: "Kontoauszug",
        leaseContract: "Mietvertrag",
        greenCard: "Aufenthalts-/Bewilligungskarte",
        workPermit: "Arbeitsbewilligung",
        itin: "Steuer-ID / ITIN",
        taxDocument: "Steuerdokument",
        trainingCertificate: "Schulungszertifikat",
        educationDiploma: "Diplom / Abschluss",
        safetyCertificate: "Sicherheits-/Compliance-Zertifikat",
        selfie: "Selfie",
        facePhoto: "Gesichtsfoto",
        profilePhoto: "Profilfoto",
        criminalRecord: "Strafregisterauszug",
        referenceLetter: "Referenzschreiben",
        other: "Sonstiges"
      }
    }
  },
  tr: {
    hero: {
      title: "Dosya yöneticisi",
      summary: "{folders} klasör, {files} dosya",
      upload: "+ Dosya yükle",
      loading: "Yükleniyor…"
    },
    folders: {
      title: "Klasörler",
      personal: "Kişisel",
      photos: "Fotoğraflar",
      work: "İş",
      count: "{count} dosya"
    },
    files: {
      title: "Dosyalar",
      tooltip: "Tür: {type}\nÇalışan: {worker}\nDurum: {status}\nOluşturma: {created}\nBitiş: {expires}",
      empty: "Belge bulunmuyor."
    },
    defaults: {
      document: "Belge",
      unknown: "Bilinmiyor",
      none: "—"
    },
    upload: {
      heroTitle: "Belge yükle",
      heroSubtitle: "Kimlik, çalışma izni ve diğer belgeleri ekleyin.",
      backToList: "Belge listesine dön",
      sectionTitle: "Belge detayları",
      dropTitle: "Dosyaları buraya sürükleyip bırakın",
      dropSubtitle: "veya cihazınızdan seçmek için tıklayın",
      browseButton: "Dosya seç",
      selectedFiles: "Seçilen dosyalar",
      categoryLabel: "Belge kategorisi",
      subtypeLabel: "Belge türü",
      subtypePlaceholder: "Tür seçin",
      customLabel: "Diğer belge adı",
      customPlaceholder: "Diğer seçtiyseniz belgeyi kısaca açıklayın",
      customHint: "Hiçbir hazır türe uymayan belgeler için kullanılır.",
      cancel: "İptal",
      submit: "Yükle (mock)",
      categories: {
        identityVerification: "Kimlik doğrulama",
        proofOfAddress: "Adres kanıtı",
        workEligibility: "Çalışma uygunluğu",
        certificates: "Sertifikalar",
        photo: "Fotoğraf / resim",
        backgroundCheck: "Geçmiş kontrolü"
      },
      subtypes: {
        identity: {
          items: [
            "drivingLicence",
            "studentId",
            "governmentId",
            "ssnDocument",
            "nationalId",
            "birthCertificate",
            "other"
          ],
          hint: "Genellikle seçilen kimlik belgesinin ön ve arka yüzü yüklenir."
        },
        address: {
          items: ["utilityBill", "bankStatement", "leaseContract", "other"],
          hint: "Tercihen son 60–90 gün içerisinde düzenlenmiş belgeler olmalıdır."
        },
        workEligibility: {
          items: ["greenCard", "workPermit", "itin", "taxDocument", "other"],
          hint: "Çalışma hakkınızı gösteren belge (ülkeye göre değişir)."
        },
        certificate: {
          items: ["trainingCertificate", "educationDiploma", "safetyCertificate", "other"],
          hint: "İşe yönelik sertifikalar ve eğitim belgeleri."
        },
        photo: {
          items: ["selfie", "facePhoto", "profilePhoto", "other"],
          hint: "Kimlik belgenizle eşleşen net yüz fotoğrafı."
        },
        background: {
          items: ["criminalRecord", "referenceLetter", "other"],
          hint: "Sadece iş veya organizasyon tarafından istenirse."
        }
      },
      subtypesLabels: {
        drivingLicence: "Ehliyet",
        studentId: "Öğrenci kartı",
        governmentId: "Resmi kimlik",
        ssnDocument: "Sosyal güvenlik belgesi",
        nationalId: "Kimlik kartı",
        birthCertificate: "Doğum belgesi",
        utilityBill: "Fatura (elektrik, su vb.)",
        bankStatement: "Hesap ekstresi",
        leaseContract: "Kira sözleşmesi",
        greenCard: "Oturum / çalışma kartı",
        workPermit: "Çalışma izni",
        itin: "Vergi numarası / ITIN",
        taxDocument: "Vergi belgesi",
        trainingCertificate: "Eğitim sertifikası",
        educationDiploma: "Diploma",
        safetyCertificate: "Güvenlik / uyum sertifikası",
        selfie: "Selfie",
        facePhoto: "Yüz fotoğrafı",
        profilePhoto: "Profil fotoğrafı",
        criminalRecord: "Adli sicil kaydı",
        referenceLetter: "Referans mektubu",
        other: "Diğer"
      }
    }
  }
} as const;
