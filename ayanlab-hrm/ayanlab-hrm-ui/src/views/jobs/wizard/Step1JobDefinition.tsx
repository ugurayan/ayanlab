import React from 'react';
import { Input } from '../../../ui/components/Input';
import { useJobIndustries, useJobRolesByCategory, useContractTypesWithLanguage } from '../../../data/jobHooks';
import type { JobFormData } from '../types';
import type { StepErrors } from './validation';
import { Button } from '../../../ui/components/Button';
import { resolveTranslationLanguage, useLanguage } from '../../../state/i18n';
import { getContractTypeName } from './i18n/contractTypes';
import { WizardStepHeader, WizardFieldLabel } from './WizardSectionHeading';
import { useJobWizardCopy } from '../i18n/wizardTexts';

interface Step1JobDefinitionProps {
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

export function Step1JobDefinition({ formData, updateFormData, onNext, onBack, canGoBack = false, errors = {} }: Step1JobDefinitionProps) {
  const w = useJobWizardCopy();
  const c = w.common;
  const s = w.step1;
  const { language } = useLanguage();
  const { data: industries } = useJobIndustries();
  const { data: jobRoles } = useJobRolesByCategory(formData.selectedIndustryId || null);
  const { data: contractTypes } = useContractTypesWithLanguage();

  const hasError = (field: string) => !!errors[field];
  const errorClass = (field: string) => hasError(field) ? 'border-red-400 focus:ring-red-500' : '';

  return (
    <div className="space-y-6">
      <WizardStepHeader title={s.title} subtitle={s.subtitle} />

      <div className="bg-white rounded-2xl border border-slate-200 p-6 space-y-4">
        <div>
          <WizardFieldLabel required>{s.jobTitle}</WizardFieldLabel>
          <Input
            value={formData.title}
            onChange={(e) => updateFormData({ title: e.target.value })}
            placeholder={s.titlePlaceholder}
            className={errorClass('title')}
          />
          <FieldError message={errors.title} />
        </div>

        <div>
          <WizardFieldLabel required>{s.jobIndustry}</WizardFieldLabel>
          <select
            className={`w-full rounded-xl border bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 ${hasError('selectedIndustryId') ? 'border-red-400 focus:ring-red-500' : 'border-slate-200 focus:ring-blue-500'}`}
            value={formData.selectedIndustryId}
            onChange={(e) => {
              updateFormData({ 
                selectedIndustryId: e.target.value,
                selectedRoleId: '' // Reset role when industry changes
              });
            }}
          >
            <option value="">{c.selectIndustry}</option>
            {industries?.map((industry) => (
              <option key={industry.id} value={industry.id}>
                {industry.name}
              </option>
            ))}
          </select>
          <FieldError message={errors.selectedIndustryId} />
        </div>

        <div>
          <WizardFieldLabel required>{s.jobRole}</WizardFieldLabel>
          <select
            className={`w-full rounded-xl border bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 ${hasError('selectedRoleId') ? 'border-red-400 focus:ring-red-500' : 'border-slate-200 focus:ring-blue-500'}`}
            value={formData.selectedRoleId}
            onChange={(e) => updateFormData({ selectedRoleId: e.target.value })}
            disabled={!formData.selectedIndustryId}
          >
            <option value="">
              {formData.selectedIndustryId ? c.selectRole : c.selectIndustryFirst}
            </option>
            {jobRoles?.map((role) => (
              <option key={role.id} value={role.id}>
                {role.name}
              </option>
            ))}
          </select>
          <FieldError message={errors.selectedRoleId} />
        </div>

        <div>
          <WizardFieldLabel required>{s.jobDescription}</WizardFieldLabel>
          <textarea
            className={`w-full rounded-xl border bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 min-h-[120px] ${hasError('description') ? 'border-red-400 focus:ring-red-500' : 'border-slate-200 focus:ring-blue-500'}`}
            value={formData.description}
            onChange={(e) => updateFormData({ description: e.target.value })}
            placeholder={s.descriptionPlaceholder}
          />
          <FieldError message={errors.description} />
        </div>

        <div>
          <WizardFieldLabel required>{s.contractType}</WizardFieldLabel>
          <select
            className={`w-full rounded-xl border bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 ${hasError('selectedContractTypeId') ? 'border-red-400 focus:ring-red-500' : 'border-slate-200 focus:ring-blue-500'}`}
            value={formData.selectedContractTypeId}
            onChange={(e) => updateFormData({ selectedContractTypeId: e.target.value })}
          >
            <option value="">{c.selectContract}</option>
            {contractTypes?.map((type) => (
              <option key={type.id} value={type.id}>
                {getContractTypeName(type.id, resolveTranslationLanguage(language))}
              </option>
            ))}
          </select>
          <FieldError message={errors.selectedContractTypeId} />
        </div>

        {/* Navigation Buttons */}
        {(onNext || onBack) && (
          <div className="flex justify-between items-center gap-4 pt-6 mt-6 pb-2 border-t border-slate-200">
            <Button
              variant="secondary"
              onClick={onBack}
              disabled={!canGoBack}
            >
              {c.back}
            </Button>
            <Button onClick={onNext}>
              {c.next}
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}

