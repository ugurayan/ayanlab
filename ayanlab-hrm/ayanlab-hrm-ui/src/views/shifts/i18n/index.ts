export const TEXTS = {
  en: {
    hero: {
      title: "Shifts",
      subtitle: "Publish jobs/shifts, manage headcount, and track fulfillment.",
      create: "Create shift"
    },
    table: {
      title: "Shift list",
      loading: "Loading…",
      columns: {
        role: "Role",
        location: "Location",
        start: "Start",
        end: "End",
        headcount: "Headcount",
        status: "Status"
      }
    }
  },
  de: {
    hero: {
      title: "Schichten",
      subtitle: "Jobs/Schichten veröffentlichen, Kapazität steuern und Besetzung verfolgen.",
      create: "Schicht erstellen"
    },
    table: {
      title: "Schichtenliste",
      loading: "Lädt…",
      columns: {
        role: "Rolle",
        location: "Standort",
        start: "Start",
        end: "Ende",
        headcount: "Besetzung",
        status: "Status"
      }
    }
  },
  tr: {
    hero: {
      title: "Vardiyalar",
      subtitle: "İşleri/vardiyaları yayınlayın, kadroyu yönetin ve doluluğu takip edin.",
      create: "Vardiya oluştur"
    },
    table: {
      title: "Vardiya listesi",
      loading: "Yükleniyor…",
      columns: {
        role: "Rol",
        location: "Lokasyon",
        start: "Başlangıç",
        end: "Bitiş",
        headcount: "Kişi sayısı",
        status: "Durum"
      }
    }
  }
} as const;
