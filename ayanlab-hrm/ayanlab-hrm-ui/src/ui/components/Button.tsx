import React from "react";
import { cn } from "../../lib/cn";

type Variant = "primary" | "secondary" | "ghost" | "danger";
type Size = "sm" | "md" | "lg";

const variants: Record<Variant, string> = {
  primary: "bg-brand text-white hover:bg-brand-hover shadow-sm",
  secondary: "bg-canvas text-ink border border-line hover:bg-surface",
  ghost: "bg-transparent text-ink-muted hover:bg-surface hover:text-ink",
  danger: "bg-rose-600 text-white hover:bg-rose-700 shadow-sm"
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
}: React.ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: Variant;
  size?: Size | string;
  isLoading?: boolean;
}) {
  const sizeClass = size && size in sizes ? sizes[size as Size] : "px-4 py-2 text-sm";
  const isDisabled = disabled || isLoading;

  return (
    <button
      disabled={isDisabled}
      className={cn(
        "inline-flex items-center justify-center gap-2 rounded-xl font-semibold transition",
        "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-ring focus-visible:ring-offset-2",
        "disabled:cursor-not-allowed disabled:opacity-50",
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