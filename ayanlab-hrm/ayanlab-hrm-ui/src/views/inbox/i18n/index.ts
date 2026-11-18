export const TEXTS = {
  en: {
    sidebar: {
      title: "Mailbox",
      compose: "+ Compose",
      mailboxes: "MAILBOXES",
      filtersTitle: "FILTERS",
      filters: {
        starred: "Starred",
        important: "Important"
      },
      folderNames: {
        inbox: "Messages",
        sent: "Sent",
        drafts: "Drafts",
        spam: "Spam",
        trash: "Trash"
      }
    },
    list: {
      searchPlaceholder: "Search for an email or task",
      empty: "No messages in this folder."
    },
    detail: {
      attachments: "{count} Attachments",
      reply: "Reply",
      replyAll: "Reply all",
      forward: "Forward",
      empty: "Select a message to view its details.",
      showHistory: "Show conversation history",
      hideHistory: "Hide conversation history",
      conversationHistory: "Conversation History",
      previousMessage: "Previous message",
      originalMessage: "Original message"
    },
    compose: {
      title: "New message",
      close: "Close",
      to: "To",
      recipient: "Recipient",
      subject: "Subject",
      message: "Message",
      messagePlaceholder: "Add your message details here...",
      cancel: "Cancel",
      send: "Send",
      you: "You",
      now: "Now",
      selectDepartment: "Select a department",
      selectIssue: "Select an issue",
      selectDepartmentFirst: "Please select a department first",
      jobId: "Job ID",
      selectJobId: "Select a job (optional)",
      jobIdHint: "Optional: Select a job ID to associate this message with a specific job",
      attachments: "Attachments",
      attachFiles: "Attach files"
    }
  },
  de: {
    sidebar: {
      title: "Mailbox",
      compose: "+ Schreiben",
      mailboxes: "POSTFÄCHER",
      filtersTitle: "FILTER",
      filters: {
        starred: "Markiert",
        important: "Wichtig"
      },
      folderNames: {
        inbox: "Nachrichten",
        sent: "Gesendet",
        drafts: "Entwürfe",
        spam: "Spam",
        trash: "Papierkorb"
      }
    },
    list: {
      searchPlaceholder: "E-Mail oder Aufgabe suchen",
      empty: "Keine Nachrichten in diesem Ordner."
    },
    detail: {
      attachments: "{count} Anhänge",
      reply: "Antworten",
      replyAll: "Allen antworten",
      forward: "Weiterleiten",
      empty: "Wähle eine Nachricht aus, um Details zu sehen.",
      showHistory: "Konversationsverlauf anzeigen",
      hideHistory: "Konversationsverlauf ausblenden",
      conversationHistory: "Konversationsverlauf",
      previousMessage: "Vorherige Nachricht",
      originalMessage: "Ursprüngliche Nachricht"
    },
    compose: {
      title: "Neue Nachricht",
      close: "Schliessen",
      to: "An",
      recipient: "Empfänger",
      subject: "Betreff",
      message: "Nachricht",
      messagePlaceholder: "Fügen Sie hier Ihre Nachrichtendetails ein...",
      cancel: "Abbrechen",
      send: "Senden",
      you: "Du",
      now: "Jetzt",
      selectDepartment: "Abteilung auswählen",
      selectIssue: "Problem auswählen",
      selectDepartmentFirst: "Bitte wählen Sie zuerst eine Abteilung aus",
      jobId: "Job-ID",
      selectJobId: "Job auswählen (optional)",
      jobIdHint: "Optional: Wählen Sie eine Job-ID, um diese Nachricht einem bestimmten Job zuzuordnen",
      attachments: "Anhänge",
      attachFiles: "Dateien anhängen"
    }
  },
  tr: {
    sidebar: {
      title: "Posta kutusu",
      compose: "+ Yaz",
      mailboxes: "KUTULAR",
      filtersTitle: "FİLTRELER",
      filters: {
        starred: "Yıldızlı",
        important: "Önemli"
      },
      folderNames: {
        inbox: "Mesajlar",
        sent: "Gönderilenler",
        drafts: "Taslaklar",
        spam: "Spam",
        trash: "Çöp"
      }
    },
    list: {
      searchPlaceholder: "E-posta veya görev ara",
      empty: "Bu klasörde mesaj yok."
    },
    detail: {
      attachments: "{count} Ek",
      reply: "Yanıtla",
      replyAll: "Tümünü yanıtla",
      forward: "Yönlendir",
      empty: "Detayları görmek için bir mesaj seçin.",
      showHistory: "Konuşma geçmişini göster",
      hideHistory: "Konuşma geçmişini gizle",
      conversationHistory: "Konuşma Geçmişi",
      previousMessage: "Önceki mesaj",
      originalMessage: "Orijinal mesaj"
    },
    compose: {
      title: "Yeni mesaj",
      close: "Kapat",
      to: "Kime",
      recipient: "Alıcı",
      subject: "Konu",
      message: "Mesaj",
      messagePlaceholder: "Mesaj detaylarınızı buraya ekleyin...",
      cancel: "Vazgeç",
      send: "Gönder",
      you: "Sen",
      now: "Şimdi",
      selectDepartment: "Bir departman seçin",
      selectIssue: "Bir sorun seçin",
      selectDepartmentFirst: "Lütfen önce bir departman seçin",
      jobId: "İş ID",
      selectJobId: "Bir iş seçin (isteğe bağlı)",
      jobIdHint: "İsteğe bağlı: Bu mesajı belirli bir işle ilişkilendirmek için bir iş ID seçin",
      attachments: "Ekler",
      attachFiles: "Dosya ekle"
    }
  }
} as const;
