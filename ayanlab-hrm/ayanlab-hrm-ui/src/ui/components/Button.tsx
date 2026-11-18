import React from "react";
import { cn } from "../../lib/cn";

type Variant = "primary" | "secondary" | "ghost" | "danger";
type Size = "sm" | "md" | "lg";

const variants: Record<Variant, string> = {
  primary: "bg-slate-900 text-white hover:bg-slate-800",
  secondary: "bg-white text-slate-900 border border-slate-200 hover:bg-slate-50",
  ghost: "bg-transparent text-slate-700 hover:bg-slate-100",
  danger: "bg-rose-600 text-white hover:bg-rose-700"
};

const sizes: Record<Size, string> = {
  sm: "px-3 py-1.5 text-xs",
  md: "px-4 py-2 text-sm",
  lg: "px-6 py-3 text-base"
};

export function Button({
  variant = "primary",
  size = "md",
  className,
  isLoading,
  disabled,
  children,
  ...props
}: React.ButtonHTMLAttributes<HTMLButtonElement> & { variant?: Variant; size?: Size | string; isLoading?: boolean }) {
  const sizeClass = size && size in sizes ? sizes[size as Size] : "px-4 py-2 text-sm";
  const isDisabled = disabled || isLoading;
  
  return (
    <button
      disabled={isDisabled}
      className={cn(
        "inline-flex items-center justify-center gap-2 rounded-2xl font-semibold shadow-sm transition disabled:opacity-60 disabled:cursor-not-allowed",
        sizeClass,
        variants[variant],
        className
      )}
      {...props}
    >
      {isLoading && <span className="animate-spin">⏳</span>}
      {children}
    </button>
  );
}
