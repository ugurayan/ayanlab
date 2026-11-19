import { Link, useLocation, useParams } from "react-router-dom";
import { LEGAL_SLUGS, type LegalSlug } from "../../config/legalPages";
import { LEGAL_DOCUMENTS } from "../../content/legal/documents";
import { useLocale } from "../../state/locale";
import { PageHeader } from "../../ui/components/PageHeader";
import { Card, CardContent } from "../../ui/components/Card";
import { Label } from "../../ui/components/Label";
import { Textarea } from "../../ui/components/Textarea";
import { Button } from "../../ui/components/Button";
import { NotFoundPage } from "../NotFoundPage";

export function LegalDocPage() {
  const { slug } = useParams<{ slug: string }>();
  const { pathname } = useLocation();
  const { locale, t } = useLocale();
  const inApp = pathname.startsWith("/app/");

  if (!slug || !LEGAL_SLUGS.includes(slug as LegalSlug)) {
    return <NotFoundPage />;
  }

  const legalLocale = locale === "tr" ? "tr" : "en";
  const doc = LEGAL_DOCUMENTS[legalLocale][slug as LegalSlug];

  return (
    <div className={inApp ? "mx-auto max-w-3xl space-y-4" : "min-h-screen bg-[rgb(var(--surface-muted))] p-6"}>
      <div className={inApp ? "" : "mx-auto max-w-3xl space-y-4"}>
      {!inApp && (
        <p className="text-sm">
          <Link to="/login" className="text-teal-800 hover:underline">
            ← Login
          </Link>
        </p>
      )}
      <PageHeader title={doc.title} subtitle={`${t("common.legalDisclaimer")} · ${doc.updated}`} />
      <Card>
        <CardContent className="max-w-none space-y-6 text-[rgb(var(--text))]">
          {doc.sections.map((section) => (
            <section key={section.heading} className="mb-6">
              <h2 className="text-base font-semibold text-[rgb(var(--text))]">{section.heading}</h2>
              {section.paragraphs.map((p) => (
                <p key={p.slice(0, 48)} className="mt-2 text-sm leading-relaxed">
                  {p}
                </p>
              ))}
            </section>
          ))}
        </CardContent>
      </Card>
      {slug === "feedback" && <FeedbackMockForm />}
      {slug === "contact" && (
        <p className="text-center text-xs text-[rgb(var(--text-muted))]">
          <Link to={inApp ? "/app/legal/help-faq" : "/legal/help-faq"} className="text-teal-800 hover:underline">
            Help center
          </Link>
        </p>
      )}
      </div>
    </div>
  );
}

function FeedbackMockForm() {
  const { t } = useLocale();
  return (
    <Card>
      <CardContent>
        <Label htmlFor="feedback-message">Message</Label>
        <Textarea id="feedback-message" rows={4} placeholder="Describe the issue…" />
        <Button type="button" className="mt-3" onClick={() => alert(t("appSettings.saved"))}>
          {t("common.save")}
        </Button>
      </CardContent>
    </Card>
  );
}