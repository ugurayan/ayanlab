import React from "react";
import { RequirePermission } from "../../router/guards";
import { useUserAudit } from "../../data/hooks";
import { useTranslations } from "../../state/i18n";
import { AuditLogTable } from "./AuditLogTable";

const TEXTS = {
  en: {
    hero: {
      title: "User audit",
      subtitle: "Logins, profile changes, role assignments and other user-driven actions."
    },
    table: {
      title: "User events",
      loading: "Loading…",
      time: "Time",
      actor: "Actor",
      action: "Action",
      target: "Target"
    }
  },
  de: {
    hero: {
      title: "Benutzer-Audit",
      subtitle: "Anmeldungen, Profiländerungen, Rollenzuweisungen und andere benutzergetriebene Aktionen."
    },
    table: {
      title: "Benutzerereignisse",
      loading: "Lädt…",
      time: "Zeit",
      actor: "Akteur",
      action: "Aktion",
      target: "Ziel"
    }
  },
  tr: {
    hero: {
      title: "Kullanıcı denetimi",
      subtitle: "Girişler, profil değişiklikleri, rol atamaları ve diğer kullanıcı işlemleri."
    },
    table: {
      title: "Kullanıcı olayları",
      loading: "Yükleniyor…",
      time: "Zaman",
      actor: "Kullanıcı",
      action: "İşlem",
      target: "Hedef"
    }
  }
} as const;

export function UserAuditPage() {
  return (
    <RequirePermission anyOf={["eecbbe51-6997-4b24-bdac-94a39928a9f9"]}>
      <UserAuditInner />
    </RequirePermission>
  );
}

function UserAuditInner() {
  const q = useUserAudit();
  const copy = useTranslations(TEXTS);
  const rows = (q.data ?? []) as { id: string; actorEmail: string; action: string; target: string; createdAt: string }[];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">{copy.hero.title}</h1>
        <p className="text-slate-500 text-sm">{copy.hero.subtitle}</p>
      </div>
      <AuditLogTable
        rows={rows}
        copy={copy.table}
        loading={q.isLoading}
        emptyMessage="No user audit events found."
      />
    </div>
  );
}
