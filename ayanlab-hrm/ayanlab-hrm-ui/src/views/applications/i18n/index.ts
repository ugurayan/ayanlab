export const TEXTS = {
  en: {
    hero: {
      title: "Applications",
      subtitle: "Review applicants and manage the hiring pipeline."
    },
    table: {
      title: "Application list",
      loading: "Loading…",
      columns: {
        id: "ID",
        shift: "Shift",
        worker: "Worker",
        status: "Status"
      }
    }
  },
  de: {
    hero: {
      title: "Bewerbungen",
      subtitle: "Bewerbende prüfen und den Hiring-Prozess steuern."
    },
    table: {
      title: "Bewerbungsliste",
      loading: "Lädt…",
      columns: {
        id: "ID",
        shift: "Schicht",
        worker: "Worker",
        status: "Status"
      }
    }
  },
  tr: {
    hero: {
      title: "Başvurular",
      subtitle: "Adayları inceleyin ve işe alım sürecini yönetin."
    },
    table: {
      title: "Başvuru listesi",
      loading: "Yükleniyor…",
      columns: {
        id: "ID",
        shift: "Vardiya",
        worker: "Çalışan",
        status: "Durum"
      }
    }
  }
} as const;
