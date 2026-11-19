/** Shared Tailwind classes for production UI (light + dark via CSS vars). */

export const labelClass = "mb-1.5 block text-xs font-medium text-[rgb(var(--text-muted))]";

export const fieldSelectClass =
  "w-full appearance-none rounded-xl border border-[rgb(var(--border))] bg-[rgb(var(--surface))] px-3 py-2.5 text-sm text-[rgb(var(--text))] shadow-sm outline-none transition focus-visible:ring-2 focus-visible:ring-teal-600/35 focus-visible:ring-offset-2 focus-visible:ring-offset-[rgb(var(--surface-muted))] disabled:cursor-not-allowed disabled:opacity-50 dark:focus-visible:ring-teal-400/40";

export const fieldSelectSmClass =
  "max-w-full appearance-none rounded-lg border border-[rgb(var(--border))] bg-[rgb(var(--surface))] px-2.5 py-1.5 text-xs text-[rgb(var(--text))] shadow-sm outline-none transition focus-visible:ring-2 focus-visible:ring-teal-600/35 focus-visible:ring-offset-1 focus-visible:ring-offset-[rgb(var(--surface-muted))]";

export const surfacePanelClass =
  "rounded-xl border border-[rgb(var(--border))] bg-[rgb(var(--surface-elevated))] shadow-sm dark:shadow-none";

export const interactiveRowClass =
  "rounded-lg border border-[rgb(var(--border))] bg-[rgb(var(--surface))] px-3 py-2.5 transition-colors hover:bg-[rgb(var(--surface-muted))]";

export const textMutedClass = "text-[rgb(var(--text-muted))]";

export const divideListClass = "divide-y divide-[rgb(var(--border))]";

export const loadingLineClass = "text-sm text-[rgb(var(--text-muted))]";

export const tableHeadClass = "text-xs text-[rgb(var(--text-muted))]";

export const tableRowBorderClass = "border-b border-[rgb(var(--border))]";

export const listRowHoverClass = "transition-colors hover:bg-[rgb(var(--surface-muted))]";

export const borderedCardClass = "rounded-xl border border-[rgb(var(--border))] p-3";