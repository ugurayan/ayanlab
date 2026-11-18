import React, { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import { useTranslations } from "../../state/i18n";
import { TEXTS } from "./i18n";
import { sendVerificationCode, verifyCode } from "../../mock/verificationApi";
import { CheckCircle2, XCircle, Loader2 } from "lucide-react";

export function EmailVerificationPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const copy = useTranslations(TEXTS);
  const [email, setEmail] = useState("");
  const [code, setCode] = useState("");
  const [step, setStep] = useState<"enter-email" | "enter-code" | "verified" | "error">("enter-email");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [countdown, setCountdown] = useState(0);

  // Check query params on mount
  useEffect(() => {
    const emailParam = searchParams.get("email");
    const codeParam = searchParams.get("code");

    if (emailParam) {
      setEmail(emailParam);
      if (codeParam) {
        setCode(codeParam);
        setStep("enter-code");
        handleVerify(emailParam, codeParam);
      } else {
        setStep("enter-code");
      }
    } else {
      // Try to get saved email from localStorage
      const savedEmail = localStorage.getItem("user_email");
      if (savedEmail) {
        setEmail(savedEmail);
      }
    }
  }, [searchParams]);

  // Countdown timer
  useEffect(() => {
    if (countdown > 0) {
      const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
      return () => clearTimeout(timer);
    }
  }, [countdown]);

  const handleSendCode = async () => {
    if (!email.trim() || !email.includes("@")) {
      setError(copy.email.errors.invalidEmail);
      return;
    }

    setLoading(true);
    setError("");

    try {
      await sendVerificationCode("email", email);
      localStorage.setItem("user_email", email);
      setStep("enter-code");
      setCountdown(60); // 60 seconds countdown
    } catch (err) {
      setError(copy.email.errors.sendFailed);
    } finally {
      setLoading(false);
    }
  };

  const handleVerify = async (emailAddr?: string, codeNum?: string) => {
    const emailToUse = emailAddr || email;
    const codeToUse = codeNum || code;

    if (!codeToUse.trim()) {
      setError(copy.email.errors.codeRequired);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const result = await verifyCode("email", emailToUse, codeToUse);
      if (result.success) {
        setStep("verified");
        setTimeout(() => {
          navigate("/login");
        }, 2000);
      } else {
        setError(result.message);
        setStep("error");
      }
    } catch (err) {
      setError(copy.email.errors.verifyFailed);
      setStep("error");
    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    await handleSendCode();
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 p-4">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle className="text-xl">{copy.email.title}</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {step === "enter-email" && (
            <>
              <div className="space-y-2">
                <label className="text-sm font-semibold text-slate-700">{copy.email.labels.email}</label>
                <Input
                  type="email"
                  placeholder={copy.email.placeholders.email}
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  disabled={loading}
                />
              </div>
              {error && <div className="text-sm text-rose-600">{error}</div>}
              <Button onClick={handleSendCode} disabled={loading || !email.trim()} className="w-full">
                {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : copy.email.actions.sendCode}
              </Button>
            </>
          )}

          {step === "enter-code" && (
            <>
              <div className="space-y-2">
                <label className="text-sm font-semibold text-slate-700">{copy.email.labels.code}</label>
                <Input
                  type="text"
                  placeholder={copy.email.placeholders.code}
                  value={code}
                  onChange={(e) => setCode(e.target.value.replace(/\D/g, "").slice(0, 6))}
                  maxLength={6}
                  disabled={loading}
                  className="text-center text-2xl tracking-widest"
                />
                <p className="text-xs text-slate-500">{copy.email.hints.codeSent.replace("{email}", email)}</p>
              </div>
              {error && <div className="text-sm text-rose-600">{error}</div>}
              <div className="flex gap-2">
                <Button onClick={() => handleVerify()} disabled={loading || !code.trim()} className="flex-1">
                  {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : copy.email.actions.verify}
                </Button>
                <Button
                  variant="secondary"
                  onClick={() => setStep("enter-email")}
                  disabled={loading}
                >
                  {copy.email.actions.changeEmail}
                </Button>
              </div>
              {countdown > 0 ? (
                <p className="text-xs text-center text-slate-500">
                  {copy.email.hints.resendIn.replace("{seconds}", countdown.toString())}
                </p>
              ) : (
                <Button variant="ghost" onClick={handleResend} disabled={loading} className="w-full text-sm">
                  {copy.email.actions.resendCode}
                </Button>
              )}
            </>
          )}

          {step === "verified" && (
            <div className="text-center space-y-4 py-4">
              <CheckCircle2 className="w-16 h-16 text-green-600 mx-auto" />
              <p className="text-lg font-semibold text-green-600">{copy.email.success.title}</p>
              <p className="text-sm text-slate-600">{copy.email.success.message}</p>
            </div>
          )}

          {step === "error" && (
            <div className="text-center space-y-4 py-4">
              <XCircle className="w-16 h-16 text-rose-600 mx-auto" />
              <p className="text-lg font-semibold text-rose-600">{copy.email.errors.title}</p>
              <p className="text-sm text-slate-600">{error}</p>
              <Button onClick={() => { setStep("enter-code"); setError(""); }} className="w-full">
                {copy.email.actions.retry}
              </Button>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

