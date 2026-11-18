import React from "react";
import { ClipLoader, PulseLoader, RingLoader, ScaleLoader, SyncLoader } from "react-spinners";
import { cn } from "../../lib/cn";

type SpinnerType = "clip" | "pulse" | "ring" | "scale" | "sync";

interface LoadingSpinnerProps {
  type?: SpinnerType;
  size?: number;
  color?: string;
  overlay?: boolean;
  fullScreen?: boolean;
  className?: string;
  text?: string;
}

const spinnerComponents = {
  clip: ClipLoader,
  pulse: PulseLoader,
  ring: RingLoader,
  scale: ScaleLoader,
  sync: SyncLoader,
};

export function LoadingSpinner({
  type = "clip",
  size = 40,
  color = "#0f172a", // slate-900
  overlay = false,
  fullScreen = false,
  className,
  text,
}: LoadingSpinnerProps) {
  const SpinnerComponent = spinnerComponents[type];

  const spinner = (
    <div className={cn("flex flex-col items-center justify-center gap-3", className)}>
      <SpinnerComponent color={color} size={size} />
      {text && <p className="text-sm text-slate-600">{text}</p>}
    </div>
  );

  if (overlay) {
    return (
      <div
        className={cn(
          "absolute inset-0 z-50 flex items-center justify-center bg-white/80 backdrop-blur-sm",
          fullScreen && "fixed inset-0"
        )}
      >
        {spinner}
      </div>
    );
  }

  if (fullScreen) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-white/80 backdrop-blur-sm">
        {spinner}
      </div>
    );
  }

  return spinner;
}

// Convenience components for common use cases
export function LoadingOverlay({ text, className }: { text?: string; className?: string }) {
  return (
    <LoadingSpinner
      type="clip"
      overlay
      text={text}
      className={className}
    />
  );
}

export function LoadingInline({ text, size = 20 }: { text?: string; size?: number }) {
  return <LoadingSpinner type="pulse" size={size} text={text} />;
}

