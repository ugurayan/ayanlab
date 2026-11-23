import React from 'react';
import { useJobWizardCopy } from '../i18n/wizardTexts';

/** Light gray surface for in-card title rows (review blocks, dropdown labels, …). */
export const WIZARD_TITLE_SURFACE =
  'rounded-lg border border-slate-200/60 bg-slate-50/80 px-3 py-2';

/** Main title for each wizard step (above the white card). */
export function WizardStepHeader({ title, subtitle }: { title: string; subtitle?: string }) {
  const { shell } = useJobWizardCopy();
  return (
    <header className="mb-4 rounded-2xl border border-slate-200/80 bg-slate-50/90 px-4 py-4 sm:px-5 sm:py-5">
      <p className="text-xs font-semibold uppercase tracking-wider text-blue-700 mb-1">{shell.kicker}</p>
      <h2 className="text-2xl sm:text-3xl font-bold tracking-tight text-slate-900">{title}</h2>
      {subtitle ? <p className="mt-2 text-sm sm:text-base text-slate-600 max-w-2xl">{subtitle}</p> : null}
    </header>
  );
}

/** Section title inside the form card (Skills, Languages, Certificates, …). */
export function WizardSectionTitle({
  title,
  description,
  required,
}: {
  title: string;
  description?: string;
  required?: boolean;
}) {
  return (
    <div className="mb-3 pt-4 border-t border-slate-100 first:border-t-0 first:pt-0">
      <div className="rounded-xl border border-slate-200/60 bg-slate-50/80 px-3 py-2.5 sm:px-4">
        <h3 className="flex items-center gap-2.5 text-base sm:text-lg font-semibold text-slate-900">
          <span className="inline-block h-6 w-1 shrink-0 rounded-full bg-blue-600" aria-hidden />
          <span>
            {title}
            {required ? <span className="text-red-600 font-bold ml-0.5">*</span> : null}
          </span>
        </h3>
        {description ? <p className="mt-1.5 ml-[1.125rem] text-sm text-slate-500">{description}</p> : null}
      </div>
    </div>
  );
}

/** Label for individual fields — darker and slightly larger than default. */
export function WizardFieldLabel({ children, required }: { children: React.ReactNode; required?: boolean }) {
  return (
    <label className="block mb-2">
      <span className="text-sm font-semibold text-slate-900">{children}</span>
      {required ? <span className="text-red-600 font-bold ml-0.5">*</span> : null}
    </label>
  );
}