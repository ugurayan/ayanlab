import React, { useState, Suspense } from "react";
import { useNavigate } from "react-router-dom";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Card, CardContent, CardHeader, CardTitle } from "../../../ui/components/Card";
import { Input } from "../../../ui/components/Input";
import { Button } from "../../../ui/components/Button";
import { Github, Facebook, Linkedin } from "lucide-react";
import { useAuth } from "../../../state/auth";
import { useTranslations } from "../../../state/i18n";
import { TEXTS } from "./i18n";
import { demoAccounts } from "../../../data/constants/demoAccounts";
import { KEYS } from "../../../data/constants/storageKeys";

// Lazy load icons to reduce initial bundle size
const FaGoogle = React.lazy(() => import("react-icons/fa").then(m => ({ default: m.FaGoogle })));
const FaFacebook = React.lazy(() => import("react-icons/fa").then(m => ({ default: m.FaFacebook })));
const FaGithub = React.lazy(() => import("react-icons/fa").then(m => ({ default: m.FaGithub })));
const FaLinkedin = React.lazy(() => import("react-icons/fa").then(m => ({ default: m.FaLinkedin })));

// Icon wrapper with Suspense fallback
const IconWrapper = ({ children }: { children: React.ReactNode }) => (
  <Suspense fallback={<span className="w-3.5 h-3.5" />}>{children}</Suspense>
);

const schema = z.object({
  email: z.string().email(),
  password: z.string().min(6)
});
type Form = z.infer<typeof schema>;


export function LoginPage() {
  const { login } = useAuth();
  const nav = useNavigate();
  const [err, setErr] = useState<string | null>(null);
  const copy = useTranslations(TEXTS);

  const { register, handleSubmit, formState: { isSubmitting } , setValue } = useForm<Form>({
    resolver: zodResolver(schema),
    defaultValues: { email: "", password: "Password123@" }
  });

  async function onSubmit(values: Form) {
    setErr(null);
    try {
      // Call login api (this stores token and user data internally)
      await login(values.email, values.password);
      
      // Get the stored user data to extract additional fields for localStorage
      const storedUser = localStorage.getItem("ui");
      const token = localStorage.getItem(KEYS.TOKEN);
      const refreshToken = localStorage.getItem(KEYS.REFRESH_TOKEN);
      
      if (storedUser && token) {
        try {
          const user = JSON.parse(storedUser);
          
          // Store individual fields to localStorage for backward compatibility
          if (token) localStorage.setItem(KEYS.TOKEN, token);
          if (refreshToken) localStorage.setItem(KEYS.REFRESH_TOKEN, refreshToken);
          if (user.id) localStorage.setItem(KEYS.USER_ID, user.id);
          if (user.scope?.orgId) localStorage.setItem(KEYS.ORG_ID, user.scope.orgId);
          if (user.profileId) localStorage.setItem(KEYS.PROFILE_ID, user.profileId);
          if (user.profileApproved !== undefined) {
            localStorage.setItem(KEYS.PROFILE_STATUS, String(user.profileApproved));
          }
          if (user.permissions) localStorage.setItem(KEYS.PERMISSIONS, JSON.stringify(user.permissions));
          if (user.roles) localStorage.setItem(KEYS.ROLES, JSON.stringify(user.roles));
          if (user.displayName) localStorage.setItem(KEYS.DISPLAY_NAME, user.displayName);
          if (user.email) localStorage.setItem(KEYS.EMAIL, user.email);
        } catch (parseError) {
          console.error("Failed to parse user data:", parseError);
          // Continue anyway, login() already stored the data
        }
      }
      
      nav("/app/dashboard", { replace: true });
    } catch (e: any) {
      setErr(e?.message ?? copy.errors.generic);
    }
  }

  return (
    <div className="min-h-screen grid place-items-center p-6">
      <div className="w-full max-w-md">
        <div className="mb-6">
          <div className="text-2xl font-bold text-slate-900">{copy.hero.title}</div>
          <div className="text-slate-500">{copy.hero.subtitle}</div>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>{copy.form.title}</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
              <div>
                <label className="text-sm font-semibold text-slate-700">{copy.form.emailLabel}</label>
                <Input placeholder={copy.form.emailPlaceholder} {...register("email")} />
              </div>
              <div>
                <label className="text-sm font-semibold text-slate-700">{copy.form.passwordLabel}</label>
                <Input type="password" {...register("password")} />
              </div>

              {err && <div className="text-sm font-semibold text-rose-700 bg-rose-50 border border-rose-100 rounded-2xl p-3">{err}</div>}

              <Button className="w-full" disabled={isSubmitting} type="submit">
                {isSubmitting ? copy.form.submitting : copy.form.submit}
              </Button>
            </form>

            <div className="mt-4 flex items-center gap-2 text-[11px] text-slate-400">
              <span className="h-px flex-1 bg-slate-200" />
              <span>Or continue with</span>
              <span className="h-px flex-1 bg-slate-200" />
            </div>
            <div className="mt-2 grid grid-cols-3 gap-2">
              <Button
                type="button"
                variant="secondary"
                className="w-full justify-center text-xs"
                onClick={() => nav("/app/dashboard")}
              >
                <IconWrapper><FaGoogle size={14} className="mr-1" /></IconWrapper> Google
              </Button>
              <Button
                type="button"
                variant="secondary"
                className="w-full justify-center text-xs"
                onClick={() => nav("/app/dashboard")}
              >
                <IconWrapper><FaFacebook size={14} className="mr-1" /></IconWrapper> Facebook
              </Button>
              <Button
                type="button"
                variant="secondary"
                className="w-full justify-center text-xs"
                onClick={() => nav("/app/dashboard")}
              >
                <IconWrapper><FaGithub size={14} className="mr-1" /></IconWrapper> Github
              </Button>
              <Button
                type="button"
                variant="secondary"
                className="w-full justify-center text-xs"
                onClick={() => nav("/app/dashboard")}
              >
                <IconWrapper><FaLinkedin size={14} className="mr-1" /></IconWrapper> LinkedIn
              </Button>
            </div>

            <div className="mt-4 text-xs text-slate-500">
              Don't have an account?{" "}
              <button
                type="button"
                className="text-slate-900 underline"
                onClick={() => nav("/signup")}
              >
                Sign up
              </button>
            </div>

            <div className="mt-6">
              <div className="text-xs font-semibold text-slate-500 mb-2">
                {copy.demo.notePrefix} <span className="font-mono">password</span>{copy.demo.noteSuffix}
              </div>
              <div className="flex flex-col gap-2">
                {demoAccounts.map((a) => (
                  <button
                    key={a.email}
                    className="w-full text-left rounded-2xl border border-slate-100 bg-slate-50 hover:bg-slate-100 px-3 py-2"
                    onClick={() => {
                      setValue("email", a.email);
                      setValue("password", "Password123@");
                    }}
                  >
                    <div className="text-sm font-semibold">{a.email}</div>
                    <div className="text-xs text-slate-500">{a.role}</div>
                  </button>
                ))}
              </div>
            </div>
          </CardContent>
        </Card>

        <div className="mt-4 text-xs text-slate-500">
          {copy.tip} <span className="font-mono">VITE_USE_MOCKS=false</span>.
        </div>
      </div>
    </div>
  );
}
