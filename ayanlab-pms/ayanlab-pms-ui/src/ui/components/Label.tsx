import type { LabelHTMLAttributes } from "react";
import { cn } from "../../lib/cn";
import { labelClass } from "../../lib/uiClasses";

export function Label({ className, ...props }: LabelHTMLAttributes<HTMLLabelElement>) {
  return <label className={cn(labelClass, className)} {...props} />;
}