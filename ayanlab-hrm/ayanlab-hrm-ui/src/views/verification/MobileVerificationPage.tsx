import React, { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import { useTranslations } from "../../state/i18n";
import { TEXTS } from "./i18n";
import { sendVerificationCode, verifyCode, getVerificationRequest } from "../../mock/verificationApi";
import { CheckCircle2, XCircle, Loader2 } from "lucide-react";

export function MobileVerificationPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const copy = useTranslations(TEXTS);
  const [mobile, setMobile] = useState("");
  const [code, setCode] = useState("");
  const [step, setStep] = useState<"enter-mobile" | "enter-code" | "verified" | "error">("enter-mobile");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [countdown, setCountdown] = useState(0);

  // Check query params on mount
  useEffect(() => {
    const mobileParam = searchParams.get("mobile");
    const codeParam = searchParams.get("code");

    if (mobileParam) {
      setMobile(mobileParam);
      if (codeParam) {
        setCode(codeParam);
        setStep("enter-code");
        handleVerify(mobileParam, codeParam);
      } else {
        setStep("enter-code");
      }
    } else {
      // Try to get saved mobile from localStorage
      const savedMobile = localStorage.getItem("user_mobile");
      if (savedMobile) {
        setMobile(savedMobile);
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
    if (!mobile.trim()) {
      setError(copy.mobile.errors.required);
      return;
    }

    setLoading(true);
    setError("");

    try {
      await sendVerificationCode("mobile", mobile);
      localStorage.setItem("user_mobile", mobile);
      setStep("enter-code");
      setCountdown(60); // 60 seconds countdown
    } catch (err) {
      setError(copy.mobile.errors.sendFailed);
    } finally {
      setLoading(false);
    }
  };

  const handleVerify = async (mobileNum?: string, codeNum?: string) => {
    const mobileToUse = mobileNum || mobile;
    const codeToUse = codeNum || code;

    if (!codeToUse.trim()) {
      setError(copy.mobile.errors.codeRequired);
      return;
    }

    setLoading(true);
    setError("");

    try {
      const result = await verifyCode("mobile", mobileToUse, codeToUse);
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
      setError(copy.mobile.errors.verifyFailed);
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
          <CardTitle className="text-xl">{copy.mobile.title}</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {step === "enter-mobile" && (
            <>
              <div className="space-y-2">
                <label className="text-sm font-semibold text-slate-700">{copy.mobile.labels.mobile}</label>
                <Input
                  type="tel"
                  placeholder={copy.mobile.placeholders.mobile}
                  value={mobile}
                  onChange={(e) => setMobile(e.target.value)}
                  disabled={loading}
                />
              </div>
              {error && <div className="text-sm text-rose-600">{error}</div>}
              <Button onClick={handleSendCode} disabled={loading || !mobile.trim()} className="w-full">
                {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : copy.mobile.actions.sendCode}
              </Button>
            </>
          )}

          {step === "enter-code" && (
            <>
              <div className="space-y-2">
                <label className="text-sm font-semibold text-slate-700">{copy.mobile.labels.code}</label>
                <Input
                  type="text"
                  placeholder={copy.mobile.placeholders.code}
                  value={code}
                  onChange={(e) => setCode(e.target.value.replace(/\D/g, "").slice(0, 6))}
                  maxLength={6}
                  disabled={loading}
                  className="text-center text-2xl tracking-widest"
                />
                <p className="text-xs text-slate-500">{copy.mobile.hints.codeSent.replace("{mobile}", mobile)}</p>
              </div>
              {error && <div className="text-sm text-rose-600">{error}</div>}
              <div className="flex gap-2">
                <Button onClick={() => handleVerify()} disabled={loading || !code.trim()} className="flex-1">
                  {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : copy.mobile.actions.verify}
                </Button>
                <Button
                  variant="secondary"
                  onClick={() => setStep("enter-mobile")}
                  disabled={loading}
                >
                  {copy.mobile.actions.changeMobile}
                </Button>
              </div>
              {countdown > 0 ? (
                <p className="text-xs text-center text-slate-500">
                  {copy.mobile.hints.resendIn.replace("{seconds}", countdown.toString())}
                </p>
              ) : (
                <Button variant="ghost" onClick={handleResend} disabled={loading} className="w-full text-sm">
                  {copy.mobile.actions.resendCode}
                </Button>
              )}
            </>
          )}

          {step === "verified" && (
            <div className="text-center space-y-4 py-4">
              <CheckCircle2 className="w-16 h-16 text-green-600 mx-auto" />
              <p className="text-lg font-semibold text-green-600">{copy.mobile.success.title}</p>
              <p className="text-sm text-slate-600">{copy.mobile.success.message}</p>
            </div>
          )}

          {step === "error" && (
            <div className="text-center space-y-4 py-4">
              <XCircle className="w-16 h-16 text-rose-600 mx-auto" />
              <p className="text-lg font-semibold text-rose-600">{copy.mobile.errors.title}</p>
              <p className="text-sm text-slate-600">{error}</p>
              <Button onClick={() => { setStep("enter-code"); setError(""); }} className="w-full">
                {copy.mobile.actions.retry}
              </Button>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

