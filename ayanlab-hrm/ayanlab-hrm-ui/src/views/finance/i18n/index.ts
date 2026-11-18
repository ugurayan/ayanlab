export const TEXTS = {
  en: {
    hero: {
      title: "Transactions",
      subtitle: "Payouts and charges across workers and organizations."
    },
    filters: {
      all: "All statuses",
      completed: "Completed",
      pending: "Pending",
      failed: "Failed",
      refunded: "Refunded"
    },
    actions: {
      create: "Create Transaction"
    },
    table: {
      title: "Transaction history",
      loading: "Loading…",
      empty: "No transactions found.",
      columns: {
        date: "Date",
        from: "From",
        to: "To",
        job: "Job / Shift",
        description: "Description",
        amount: "Amount",
        fees: "Fees",
        paymentType: "Payment Type",
        paymentMethod: "Payment Method",
        reference: "Reference",
        status: "Status",
        actions: "Actions",
        audit: "Audit Info"
      }
    },
    modal: {
      createTitle: "Create Transaction",
      editTitle: "Edit Transaction",
      direction: "Direction",
      status: "Status",
      worker: "Worker",
      orgId: "Organization ID",
      amount: "Amount",
      fees: "Fees",
      jobTitle: "Job Title",
      shiftId: "Shift ID",
      paymentType: "Payment Type",
      bankName: "Bank Name",
      bankAccountLast4: "Account Last 4",
      cardBrand: "Card Brand",
      cardLast4: "Card Last 4",
      description: "Description",
      externalReference: "External Reference",
      invoiceId: "Invoice ID",
      cancel: "Cancel",
      save: "Save"
    }
  },
  de: {
    hero: {
      title: "Transaktionen",
      subtitle: "Auszahlungen und Belastungen über Worker und Organisationen."
    },
    filters: {
      all: "Alle Stati",
      completed: "Abgeschlossen",
      pending: "Ausstehend",
      failed: "Fehlgeschlagen",
      refunded: "Erstattet"
    },
    actions: {
      create: "Transaktion erstellen"
    },
    table: {
      title: "Transaktionshistorie",
      loading: "Lädt…",
      empty: "Keine Transaktionen gefunden.",
      columns: {
        date: "Datum",
        from: "Von",
        to: "An",
        job: "Job / Schicht",
        description: "Beschreibung",
        amount: "Betrag",
        fees: "Gebühren",
        paymentType: "Zahlungsart",
        paymentMethod: "Zahlungsmethode",
        reference: "Referenz",
        status: "Status",
        actions: "Aktionen",
        audit: "Audit-Info"
      }
    },
    modal: {
      createTitle: "Transaktion erstellen",
      editTitle: "Transaktion bearbeiten",
      direction: "Richtung",
      status: "Status",
      worker: "Worker",
      orgId: "Organisations-ID",
      amount: "Betrag",
      fees: "Gebühren",
      jobTitle: "Jobtitel",
      shiftId: "Schicht-ID",
      paymentType: "Zahlungsart",
      bankName: "Bankname",
      bankAccountLast4: "Konto letzten 4",
      cardBrand: "Kartenmarke",
      cardLast4: "Karte letzten 4",
      description: "Beschreibung",
      externalReference: "Externe Referenz",
      invoiceId: "Rechnungs-ID",
      cancel: "Abbrechen",
      save: "Speichern"
    }
  },
  tr: {
    hero: {
      title: "İşlemler",
      subtitle: "Çalışanlar ve organizasyonlar için ödemeler ve ücretler."
    },
    filters: {
      all: "Tüm durumlar",
      completed: "Tamamlandı",
      pending: "Beklemede",
      failed: "Başarısız",
      refunded: "İade edildi"
    },
    actions: {
      create: "İşlem Oluştur"
    },
    table: {
      title: "İşlem geçmişi",
      loading: "Yükleniyor…",
      empty: "İşlem bulunamadı.",
      columns: {
        date: "Tarih",
        from: "Gönderen",
        to: "Alıcı",
        job: "İş / Vardiya",
        description: "Açıklama",
        amount: "Tutar",
        fees: "Ücretler",
        paymentType: "Ödeme Türü",
        paymentMethod: "Ödeme Yöntemi",
        reference: "Referans",
        status: "Durum",
        actions: "İşlemler",
        audit: "Denetim Bilgisi"
      }
    },
    modal: {
      createTitle: "İşlem Oluştur",
      editTitle: "İşlemi Düzenle",
      direction: "Yön",
      status: "Durum",
      worker: "Çalışan",
      orgId: "Organizasyon ID",
      amount: "Tutar",
      fees: "Ücretler",
      jobTitle: "İş Unvanı",
      shiftId: "Vardiya ID",
      paymentType: "Ödeme Türü",
      bankName: "Banka Adı",
      bankAccountLast4: "Hesap Son 4",
      cardBrand: "Kart Markası",
      cardLast4: "Kart Son 4",
      description: "Açıklama",
      externalReference: "Harici Referans",
      invoiceId: "Fatura ID",
      cancel: "İptal",
      save: "Kaydet"
    }
  }
} as const;
