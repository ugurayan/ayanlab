import { useState } from "react";
import { Link } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Input } from "../../ui/components/Input";
import { Button } from "../../ui/components/Button";
import { Badge } from "../../ui/components/Badge";
import { useAuth } from "../../state/auth";
import { DEMO_LOGIN_ACCOUNTS } from "../../config/demoAccounts";
import { MOCK_ONLY } from "../../env";
import { useLocale } from "../../state/locale";
import { ThemeToggle } from "../../ui/components/ThemeToggle";
import { Label } from "../../ui/components/Label";
import { Select } from "../../ui/components/Select";
import { cn } from "../../lib/cn";
import type { Locale } from "../../i18n/types";
import { LOCALE_OPTIONS, localeOptionLabel } from "../../i18n/localeMeta";

const schema = z.object({
  email: z.string().email(),
  password: z.string().min(4)
});

type Form = z.infer<typeof schema>;

export function LoginPage() {
  const { login } = useAuth();
  const { locale, setLocale, t } = useLocale();
  const navigate = useNavigate();
  const [err, setErr] = useState<string | null>(null);
  const [quickLoading, setQuickLoading] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    setValue,
    formState: { isSubmitting }
  } = useForm<Form>({
    resolver: zodResolver(schema),
    defaultValues: { email: "pm@demo.ayanlab", password: "password" }
  });

  async function doLogin(email: string, password: string) {
    setErr(null);
    await login(email, password);
    navigate("/app/dashboard", { replace: true });
  }

  async function onSubmit(values: Form) {
    try {
      await doLogin(values.email, values.password);
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Login failed");
    }
  }

  async function quickSignIn(email: string, password: string) {
    setQuickLoading(email);
    setValue("email", email);
    setValue("password", password);
    try {
      await doLogin(email, password);
    } catch (e) {
      setErr(e instanceof Error ? e.message : "Login failed");
    } finally {
      setQuickLoading(null);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-[rgb(var(--surface-muted))] via-[rgb(var(--surface))] to-teal-50 p-6 dark:to-teal-950/40">
      <Card className="w-full max-w-lg ring-1 ring-[rgb(var(--border))]">
        <CardHeader>
          <div className="flex items-start justify-between gap-2">
            <CardTitle className="text-xl">{t("login.title")}</CardTitle>
            <div className="flex items-center gap-1">
              <ThemeToggle />
              <Select
                density="sm"
                className="max-w-[11rem]"
                value={locale}
                onChange={(e) => setLocale(e.target.value as Locale)}
                aria-label={t("common.language")}
              >
                {LOCALE_OPTIONS.map((o) => (
                  <option key={o.code} value={o.code}>
                    {localeOptionLabel(o)}
                  </option>
                ))}
              </Select>
            </div>
          </div>
          <p className="mt-1 text-xs text-[rgb(var(--text-muted))]">
            {MOCK_ONLY ? (
              <>
                {t("login.demoHint")} <code className="font-mono">password</code>)
              </>
            ) : (
              <>
                Platform JWT / demo (<code className="font-mono">password</code>)
              </>
            )}
          </p>
        </CardHeader>
        <CardContent className="space-y-5">
          <section aria-labelledby="demo-accounts-heading">
            <div className="mb-2 flex items-center justify-between gap-2">
              <h2 id="demo-accounts-heading" className="text-sm font-semibold text-[rgb(var(--text))]">
                {t("login.demoAccounts")}
              </h2>
              <Badge tone="ok">{t("login.oneClick")}</Badge>
            </div>
            <ul className="space-y-2">
              {DEMO_LOGIN_ACCOUNTS.map((a) => (
                <li key={a.email}>
                  <button
                    type="button"
                    disabled={!!quickLoading || isSubmitting}
                    className={cn(
                      "flex w-full min-h-[44px] flex-col gap-0.5 rounded-xl border px-3 py-2.5 text-left transition",
                      "border-teal-200/80 bg-teal-500/5 hover:border-teal-400/50 hover:bg-teal-500/10",
                      "dark:border-teal-500/25 dark:bg-teal-400/5 dark:hover:bg-teal-400/10",
                      "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-teal-600/40 focus-visible:ring-offset-2 focus-visible:ring-offset-[rgb(var(--surface-elevated))]",
                      "disabled:pointer-events-none disabled:opacity-50"
                    )}
                    onClick={() => quickSignIn(a.email, a.password)}
                  >
                    <span className="flex items-center justify-between gap-2">
                      <span className="text-sm font-semibold text-[rgb(var(--text))]">{a.label}</span>
                      <span className="font-mono text-[11px] text-teal-800 dark:text-teal-300">{a.email}</span>
                    </span>
                    <span className="text-xs text-[rgb(var(--text-muted))]">{a.hint}</span>
                    {quickLoading === a.email && (
                      <span className="text-xs text-teal-700">{t("common.loading")}</span>
                    )}
                  </button>
                </li>
              ))}
            </ul>
          </section>

          <div className="border-t border-[rgb(var(--border))] pt-4">
            <p className="mb-3 text-xs font-semibold uppercase tracking-wide text-[rgb(var(--text-muted))]">
              {t("login.manual")}
            </p>
            <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
              <div>
                <Label htmlFor="login-email">{t("login.email")}</Label>
                <Input id="login-email" type="email" autoComplete="username" {...register("email")} />
              </div>
              <div>
                <Label htmlFor="login-password">{t("login.password")}</Label>
                <Input id="login-password" type="password" autoComplete="current-password" {...register("password")} />
              </div>
              {err && <p className="text-sm text-rose-600">{err}</p>}
              <Button type="submit" className="w-full" variant="secondary" isLoading={isSubmitting}>
                {t("login.signIn")}
              </Button>
            </form>
          </div>
          <p className="text-center text-[11px] text-[rgb(var(--text-muted))]">
            <Link to="/legal/privacy" className="font-medium text-teal-800 hover:underline dark:text-teal-300">
              {t("nav.privacy")}
            </Link>
            {" · "}
            <Link to="/legal/terms" className="font-medium text-teal-800 hover:underline dark:text-teal-300">
              {t("nav.terms")}
            </Link>
          </p>
        </CardContent>
      </Card>
    </div>
  );
}