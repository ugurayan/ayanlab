import React from 'react';
import { Button } from '../../../ui/components/Button';
import { MapPin, Calendar, FileText, Shirt, Users } from 'lucide-react';
import { useTranslations } from '../../../state/i18n';
import { TEXTS } from '../i18n';
import type { JobFormData } from '../types';
import { getProfileTaxAndFee } from '../../../data/profileUiData';

const STAFF_SELECTION_TITLE_KEYS: Record<JobFormData['staffSelectionType'], keyof typeof TEXTS.en.staff> = {
  'open-to-all': 'openToAllTitle',
  'open-to-new': 'openToNewTitle',
  'smart-assign': 'smartAssignTitle',
  'request-approval': 'requestApprovalTitle',
  'top-pros-only': 'topProsOnlyTitle',
};

interface Step8ReviewProps {
  formData: JobFormData;
  onSubmit: () => void;
  onBack?: () => void;
  canGoBack?: boolean;
  isSubmitting?: boolean;
}

const DAY_IDS = ['sun', 'mon', 'tue', 'wed', 'thu', 'fri', 'sat'];

function countRecurringDates(start: string, end: string, shiftDays: string[]): number {
  if (!start || !end || shiftDays.length === 0) return 0;
  const startDate = new Date(start);
  const endDate = new Date(end);
  let count = 0;
  const d = new Date(startDate);
  while (d <= endDate) {
    const dayId = DAY_IDS[d.getDay()];
    if (shiftDays.includes(dayId)) count++;
    d.setDate(d.getDate() + 1);
  }
  return count;
}

export function Step8Review({ formData, onSubmit, onBack, canGoBack = false, isSubmitting = false }: Step8ReviewProps) {
  const copy = useTranslations(TEXTS);
  const staff = copy.staff;
  const scheduleDateCount = formData.recurringSchedule
    ? countRecurringDates(
        formData.recurringStartDate,
        formData.recurringEndDate,
        formData.shiftDays ?? []
      )
    : formData.selectedDates.length;

  const calculateTotalHours = () => {
    if (scheduleDateCount === 0) return 0;
    const [sh, sm] = (formData.scheduleStartTime || '0:0').split(':').map(Number);
    const [eh, em] = (formData.scheduleEndTime || '0:0').split(':').map(Number);
    const minsPerDay = (eh - sh) * 60 + (em - sm);
    return (scheduleDateCount * minsPerDay) / 60;
  };

  const baseCost = () => {
    const hours = calculateTotalHours();
    const employees = parseInt(formData.employeesNeeded || '0');
    const wage = parseFloat(formData.wageAmount || '0');
    if (formData.wageType === 'hourly') {
      return hours * employees * wage;
    }
    return scheduleDateCount * employees * wage;
  };

  const { taxPercent, feePercent } = getProfileTaxAndFee();
  const tipPct = Math.max(0, parseFloat(formData.tipPercent || '0'));
  const tipFixed = Math.max(0, parseFloat(formData.tipAmount || '0'));
  const base = baseCost();
  const taxAmount = base * (taxPercent / 100);
  const feesAmount = base * (feePercent / 100);
  const tipAmount = base * (tipPct / 100) + tipFixed;
  const estimatedTotal = base + taxAmount + feesAmount + tipAmount;
  const currency = formData.wageCurrency || 'USD';
  const formatCurrency = (amount: number) => (currency === 'USD' ? `$${amount.toFixed(2)}` : `${currency} ${amount.toFixed(2)}`);

  return (
    <div className="space-y-6 pb-8">
      <div>
        <h2 className="text-2xl font-bold text-slate-900 mb-2">Review your booking</h2>
        <p className="text-sm text-slate-600">Review all details before confirming your job posting.</p>
      </div>

      <div className="space-y-4">
        {/* Job Definition */}
        <div className="bg-white rounded-2xl border border-slate-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-slate-900 flex items-center gap-2">
              <FileText size={18} />
              Job Definition
            </h3>
            <Button variant="secondary" size="sm">Edit</Button>
          </div>
          <div className="space-y-2 text-sm">
            <div><span className="text-slate-500">Title:</span> <span className="font-medium">{formData.title || 'Not set'}</span></div>
            <div><span className="text-slate-500">Description:</span> <span className="font-medium">{formData.description || 'Not set'}</span></div>
          </div>
        </div>

        {/* Location */}
        <div className="bg-white rounded-2xl border border-slate-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-slate-900 flex items-center gap-2">
              <MapPin size={18} />
              Location
            </h3>
            <Button variant="secondary" size="sm">Edit</Button>
          </div>
          <div className="space-y-2 text-sm">
            <div><span className="text-slate-500">Location:</span> <span className="font-medium">{formData.workLocationName || 'Not set'}</span></div>
            <div><span className="text-slate-500">Address:</span> <span className="font-medium">{formData.workLocationStreet || 'Not set'}</span></div>
            <div><span className="text-slate-500">Contact:</span> <span className="font-medium">{formData.onsiteContactName || 'Not set'}</span></div>
          </div>
        </div>

        {/* Schedule */}
        <div className="bg-white rounded-2xl border border-slate-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-slate-900 flex items-center gap-2">
              <Calendar size={18} />
              Schedule & Positions
            </h3>
            <Button variant="secondary" size="sm">Edit</Button>
          </div>
          <div className="space-y-2 text-sm">
            {formData.recurringSchedule ? (
              <>
                <div><span className="text-slate-500">Type:</span> <span className="font-medium">Recurring</span></div>
                <div><span className="text-slate-500">Range:</span> <span className="font-medium">{formData.recurringStartDate} to {formData.recurringEndDate}</span></div>
                <div><span className="text-slate-500">Days:</span> <span className="font-medium">{(formData.shiftDays ?? []).map(d => d.charAt(0).toUpperCase() + d.slice(1)).join(', ') || 'None'}</span></div>
              </>
            ) : (
              <div><span className="text-slate-500">Dates:</span> <span className="font-medium">{formData.selectedDates.length} date(s) selected</span></div>
            )}
            <div><span className="text-slate-500">Time:</span> <span className="font-medium">{formData.scheduleStartTime} - {formData.scheduleEndTime}</span></div>
            <div><span className="text-slate-500">Break:</span> <span className="font-medium">{formData.totalUnpaidBreak}</span></div>
            <div><span className="text-slate-500">Employees needed:</span> <span className="font-medium">{formData.employeesNeeded || 'Not set'}</span></div>
            <div><span className="text-slate-500">Wage:</span> <span className="font-medium">{formData.wageAmount} {formData.wageType}</span></div>
          </div>
        </div>

        {/* Requirements */}
        <div className="bg-white rounded-2xl border border-slate-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-slate-900 flex items-center gap-2">
              <FileText size={18} />
              Requirements
            </h3>
            <Button variant="secondary" size="sm">Edit</Button>
          </div>
          <div className="space-y-2 text-sm">
            <div><span className="text-slate-500">Skills:</span> <span className="font-medium">{(formData.skillRequirements?.length ?? formData.selectedSkills.length)} selected</span></div>
            <div><span className="text-slate-500">Languages:</span> <span className="font-medium">{formData.languageRequirements?.length ? formData.languageRequirements.map((l) => `${l.language} (${l.level}${l.certificationNeeded ? ', cert. required' : ''})`).join(', ') : (formData.selectedLanguages?.join(', ') || 'None')}</span></div>
          </div>
        </div>

        {/* Certificates & Documents */}
        <div className="bg-white rounded-2xl border border-slate-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-slate-900 flex items-center gap-2">
              <FileText size={18} />
              Certificates & Documents
            </h3>
            <Button variant="secondary" size="sm">Edit</Button>
          </div>
          <div className="space-y-2 text-sm">
            <div><span className="text-slate-500">Certificates:</span> <span className="font-medium">{formData.noCertificates ? 'None' : `${(formData.selectedCertificates?.length ?? 0) + (formData.customCertificates?.length ?? 0)} selected`}</span></div>
            <div><span className="text-slate-500">Documents:</span> <span className="font-medium">{formData.noDocuments ? 'None' : `${(formData.selectedDocuments?.length ?? 0) + (formData.customDocuments?.length ?? 0)} selected`}</span></div>
            <div><span className="text-slate-500">Education:</span> <span className="font-medium">{(formData.selectedEducationLevels?.length ?? 0) ? formData.selectedEducationLevels.join(', ') : 'None'}</span></div>
            <div><span className="text-slate-500">Departments:</span> <span className="font-medium">{((formData.selectedDepartments?.length ?? 0) + (formData.customDepartments?.length ?? 0)) ? [...(formData.selectedDepartments ?? []), ...(formData.customDepartments ?? [])].join(', ') : 'None'}</span></div>
            <div><span className="text-slate-500">Driving licence:</span> <span className="font-medium">{formData.noDrivingLicence ? 'None needed' : (formData.selectedDrivingLicences?.length ? formData.selectedDrivingLicences.join(', ') : 'None')}</span></div>
            <div><span className="text-slate-500">References needed:</span> <span className="font-medium">{(formData.referenceCount ?? 0) || 'None'}</span></div>
          </div>
        </div>

        {/* Dress Requirements */}
        <div className="bg-white rounded-2xl border border-slate-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-slate-900 flex items-center gap-2">
              <Shirt size={18} />
              {copy.step6?.title ?? 'Dress Requirements'}
            </h3>
            <Button variant="secondary" size="sm">Edit</Button>
          </div>
          <div className="text-sm space-y-2">
            <div><span className="text-slate-500">{copy.step6?.dressCode ?? 'Dress Code'}:</span> <span className="font-medium">{formData.selectedDressType ? (copy.step6 as Record<string, string>)?.[formData.selectedDressType === 'casual' ? 'dressCasual' : formData.selectedDressType === 'smart-casual' ? 'dressSmartCasual' : formData.selectedDressType === 'business' ? 'dressOffice' : formData.selectedDressType === 'dress-provided' ? 'dressProvided' : formData.selectedDressType === 'custom-uniform' ? 'dressCreateUniform' : 'dressNoPreference'] ?? formData.selectedDressType : 'Not set'}</span></div>
            {(formData.selectedDressItems?.length ?? 0) > 0 && (
              <div><span className="text-slate-500">{copy.step6?.dressItems ?? 'Dress Items'}:</span> <span className="font-medium">{formData.selectedDressItems?.map((i) => `${i.item} (${i.color})`).join(', ')}</span></div>
            )}
            {(formData.attireRestrictionLevel && formData.attireRestrictionLevel !== '') && (
              <div><span className="text-slate-500">{copy.step6?.attireRestrictions ?? 'Attire'}:</span> <span className="font-medium">{formData.attireRestrictionLevel === 'no-preference' ? copy.step6?.noPreference : formData.attireRestrictionLevel === 'basic' ? copy.step6?.attireBasic : formData.attireRestrictionLevel === 'strict' ? copy.step6?.attireStrict : formData.attireRestrictionLevel === 'custom' ? `${formData.selectedAttireRestrictions?.length ?? 0} custom` : formData.attireRestrictionLevel}</span></div>
            )}
            {(formData.groomingLevel && formData.groomingLevel !== '') && (
              <div><span className="text-slate-500">{copy.step6?.groomingInstructions ?? 'Grooming'}:</span> <span className="font-medium">{formData.groomingLevel === 'no-preference' ? copy.step6?.noPreference : formData.groomingLevel === 'elevated' ? copy.step6?.groomingElevated : formData.groomingLevel === 'professional' ? copy.step6?.groomingProfessional : formData.groomingLevel === 'standard' ? copy.step6?.groomingStandard : formData.groomingLevel === 'custom' ? 'Custom' : formData.groomingLevel}</span></div>
            )}
            {formData.jobDutiesHtml && (
              <div><span className="text-slate-500">{copy.step6?.jobDuties ?? 'Job Duties'}:</span> <span className="font-medium line-clamp-2">{formData.jobDutiesHtml.replace(/<[^>]+>/g, ' ').trim().slice(0, 120)}{formData.jobDutiesHtml.length > 120 ? '…' : ''}</span></div>
            )}
            {(formData.uploadedFileNames?.length ?? 0) > 0 && (
              <div><span className="text-slate-500">{copy.step6?.uploadFiles ?? 'Files'}:</span> <span className="font-medium">{formData.uploadedFileNames?.join(', ')}</span></div>
            )}
            {formData.additionalInstructionsHtml && (
              <div><span className="text-slate-500">{copy.step6?.additionalInstructions ?? 'Additional'}:</span> <span className="font-medium line-clamp-2">{formData.additionalInstructionsHtml.replace(/<[^>]+>/g, ' ').slice(0, 80)}…</span></div>
            )}
          </div>
        </div>

        {/* Staff Selection */}
        <div className="bg-white rounded-2xl border border-slate-200 p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold text-slate-900 flex items-center gap-2">
              <Users size={18} />
              {staff.reviewSectionTitle}
            </h3>
            <Button variant="secondary" size="sm">Edit</Button>
          </div>
          <div className="text-sm">
            <span className="text-slate-500">{staff.reviewSelectionType}:</span>{' '}
            <span className="font-medium">
              {formData.staffSelectionType ? staff[STAFF_SELECTION_TITLE_KEYS[formData.staffSelectionType]] : ''}
            </span>
          </div>
        </div>

        {/* Billing Information */}
        <div className="bg-white rounded-2xl border border-slate-200 p-6">
          <h3 className="font-semibold text-slate-900 mb-4">Billing Information</h3>
          <div className="space-y-1 text-sm mb-3">
            <div className="flex justify-between">
              <span className="text-slate-600">Base (persons × wage × {formData.wageType === 'hourly' ? 'hours' : 'dates'})</span>
              <span>{formatCurrency(base)}</span>
            </div>
            {taxPercent > 0 && (
              <div className="flex justify-between">
                <span className="text-slate-600">Tax ({taxPercent}%)</span>
                <span>{formatCurrency(taxAmount)}</span>
              </div>
            )}
            {feesAmount > 0 && (
              <div className="flex justify-between">
                <span className="text-slate-600">Fees ({feePercent}%)</span>
                <span>{formatCurrency(feesAmount)}</span>
              </div>
            )}
            {tipAmount > 0 && (
              <div className="flex justify-between">
                <span className="text-slate-600">
                  Tip{tipPct > 0 && tipFixed > 0 ? ' (% + fixed)' : tipPct > 0 ? ' (%)' : ' (fixed)'}
                </span>
                <span>{formatCurrency(tipAmount)}</span>
              </div>
            )}
          </div>
          <div className="flex items-center justify-between pt-2 border-t border-slate-200">
            <span className="text-sm font-medium text-slate-700">Estimated total</span>
            <span className="text-2xl font-bold text-slate-900">{formatCurrency(estimatedTotal)}</span>
          </div>
          <p className="text-xs text-slate-500 mt-2">
            *The final bill will be calculated after shift completion depending on the number of hours worked.
          </p>
        </div>

        {/* Save as Template */}
        <div className="bg-white rounded-2xl border border-slate-200 p-6">
          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              checked={formData.saveAsTemplate}
              onChange={(e) => {/* Handle template save */}}
              className="rounded border-slate-300"
            />
            <span className="text-sm text-slate-700">Save this booking as a template to book your next shift even faster</span>
          </label>
        </div>
      </div>

      {/* Navigation Buttons - Always visible */}
      <div className="bg-white rounded-2xl border border-slate-200 p-6 mt-6">
        <div className="flex justify-between items-center">
          <Button
            variant="secondary"
            onClick={onBack}
            disabled={!canGoBack || !onBack || isSubmitting}
          >
            Back
          </Button>
          <Button onClick={onSubmit} className="px-8" disabled={isSubmitting}>
            {isSubmitting ? 'Creating Job...' : 'Confirm & Create Job'}
          </Button>
        </div>
      </div>
    </div>
  );
}

