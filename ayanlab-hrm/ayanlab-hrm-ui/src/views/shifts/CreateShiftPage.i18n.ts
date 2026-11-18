export const TEXTS = {
  en: {
    hero: {
      title: "Create shift",
      subtitle: "Publish a new shift and notify matching workers."
    },
    form: {
      title: "Shift details",
      location: "Location",
      select: "Select…",
      role: "Role",
      headcount: "Headcount",
      start: "Start",
      end: "End",
      cancel: "Cancel",
      publishing: "Publishing...",
      publish: "Publish shift"
    },
    errors: {
      selectLocation: "Select a location",
      roleRequired: "Role is required",
      endAfterStart: "End must be after start",
      creation: "Failed to create shift",
      default: "Check this field"
    }
  },
  de: {
    hero: {
      title: "Schicht erstellen",
      subtitle: "Neue Schicht veröffentlichen und passende Worker informieren."
    },
    form: {
      title: "Schichtdetails",
      location: "Standort",
      select: "Auswählen…",
      role: "Rolle",
      headcount: "Kopfzahl",
      start: "Start",
      end: "Ende",
      cancel: "Abbrechen",
      publishing: "Wird veröffentlicht...",
      publish: "Schicht veröffentlichen"
    },
    errors: {
      selectLocation: "Standort auswählen",
      roleRequired: "Rolle ist erforderlich",
      endAfterStart: "Ende muss nach dem Start liegen",
      creation: "Schicht konnte nicht erstellt werden",
      default: "Bitte Feld prüfen"
    }
  },
  tr: {
    hero: {
      title: "Vardiya oluştur",
      subtitle: "Yeni vardiya yayınlayın ve eşleşen çalışanları bilgilendirin."
    },
    form: {
      title: "Vardiya detayları",
      location: "Lokasyon",
      select: "Seç…",
      role: "Rol",
      headcount: "Kişi sayısı",
      start: "Başlangıç",
      end: "Bitiş",
      cancel: "Vazgeç",
      publishing: "Yayınlanıyor...",
      publish: "Vardiyayı yayınla"
    },
    errors: {
      selectLocation: "Bir lokasyon seçin",
      roleRequired: "Rol zorunludur",
      endAfterStart: "Bitiş baştan sonra olmalı",
      creation: "Vardiya oluşturulamadı",
      default: "Lütfen alanı kontrol edin"
    }
  }
} as const;
