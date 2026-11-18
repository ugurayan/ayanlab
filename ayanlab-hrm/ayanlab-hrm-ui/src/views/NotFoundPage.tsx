import React from "react";
import { Link } from "react-router-dom";
import { Button } from "../ui/components/Button";
import { useTranslations } from "../state/i18n";

export function NotFoundPage() {
  const copy = useTranslations(TEXTS);
  return (
    <div className="min-h-screen grid place-items-center p-6">
      <div className="max-w-md text-center">
        <div className="text-2xl font-bold">{copy.title}</div>
        <div className="text-slate-500 mt-2">{copy.subtitle}</div>
        <div className="mt-6">
          <Link to="/app/dashboard">
            <Button>{copy.cta}</Button>
          </Link>
        </div>
      </div>
    </div>
  );
}

const TEXTS = {
  en: {
    title: "Not Found",
    subtitle: "The page you’re looking for doesn’t exist.",
    cta: "Go to dashboard"
  },
  de: {
    title: "Nicht gefunden",
    subtitle: "Die gesuchte Seite existiert nicht.",
    cta: "Zur Übersicht"
  },
  tr: {
    title: "Sayfa bulunamadı",
    subtitle: "Aradığınız sayfa mevcut değil.",
    cta: "Panele git"
  }
} as const;
