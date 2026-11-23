import React from 'react';
import { Button } from '../../../ui/components/Button';
import { useTranslations } from '../../../state/i18n';
import { TEXTS } from '../i18n';
import type { JobFormData } from '../types';
import type { StepErrors } from './validation';
import { WizardStepHeader } from './WizardSectionHeading';

const STAFF_OPTION_IDS: Array<JobFormData['staffSelectionType']> = [
  'open-to-all',
  'open-to-new',
  'smart-assign',
  'request-approval',
  'top-pros-only',
];

interface Step7StaffProps {
  formData: JobFormData;
  updateFormData: (updates: Partial<JobFormData>) => void;
  onNext?: () => void;
  onBack?: () => void;
  canGoBack?: boolean;
  errors?: StepErrors;
}

function FieldError({ message }: { message?: string }) {
  if (!message) return null;
  return <p className="text-xs text-red-600 mt-1">{message}</p>;
}

const TITLE_KEYS = {
  'open-to-all': 'openToAllTitle',
  'open-to-new': 'openToNewTitle',
  'smart-assign': 'smartAssignTitle',
  'request-approval': 'requestApprovalTitle',
  'top-pros-only': 'topProsOnlyTitle',
} as const;
const DESC_KEYS = {
  'open-to-all': 'openToAllDescription',
  'open-to-new': 'openToNewDescription',
  'smart-assign': 'smartAssignDescription',
  'request-approval': 'requestApprovalDescription',
  'top-pros-only': 'topProsOnlyDescription',
} as const;

export function Step7Staff({ formData, updateFormData, onNext, onBack, canGoBack = false, errors = {} }: Step7StaffProps) {
  const copy = useTranslations(TEXTS);
  const staff = copy.staff;
  const selected = formData.staffSelectionType || 'smart-assign';

  return (
    <div className="space-y-6">
      <WizardStepHeader title={staff.step7Title} subtitle={staff.step7Subtitle} />

      <div className="space-y-3">
        {STAFF_OPTION_IDS.map((id) => (
          <div
            key={id}
            className={`p-5 rounded-2xl border-2 cursor-pointer transition-all ${
              selected === id ? 'border-blue-500 bg-blue-50' : 'border-slate-200 bg-white hover:border-slate-300'
            }`}
            onClick={() => updateFormData({ staffSelectionType: id })}
          >
            <div className="flex items-start gap-3">
              <input
                type="radio"
                checked={selected === id}
                onChange={() => updateFormData({ staffSelectionType: id })}
                className="mt-1 shrink-0"
              />
              <div className="min-w-0 flex-1">
                <h3 className="font-semibold text-slate-900 mb-1">{staff[TITLE_KEYS[id]]}</h3>
                <p className="text-sm text-slate-600">{staff[DESC_KEYS[id]]}</p>
                {id === 'top-pros-only' && (
                  <div className="mt-3 pl-4 border-l-2 border-slate-200">
                    <p className="text-xs font-medium text-slate-700 mb-1.5">{staff.topProsWhoTitle}</p>
                    <ul className="text-xs text-slate-600 space-y-0.5 list-disc list-inside">
                      <li>{staff.topProsBullet1}</li>
                      <li>{staff.topProsBullet2}</li>
                      <li>{staff.topProsBullet3}</li>
                      <li>{staff.topProsBullet4}</li>
                    </ul>
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>

      {errors.staffSelectionType && (
        <div className="bg-red-50 border border-red-200 rounded-xl p-3">
          <FieldError message={staff.step7Error} />
        </div>
      )}

      {/* Navigation Buttons */}
      {(onNext || onBack) && (
        <div className="bg-white rounded-2xl border border-slate-200 p-6">
          <div className="flex justify-between items-center">
            <Button
              variant="secondary"
              onClick={onBack}
              disabled={!canGoBack}
            >
              {staff.back}
            </Button>
            <Button onClick={onNext}>
              {staff.next}
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}

