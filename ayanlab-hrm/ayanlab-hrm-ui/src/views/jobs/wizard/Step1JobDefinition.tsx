import React from 'react';
import { Input } from '../../../ui/components/Input';
import { useJobIndustries, useJobRolesByCategory, useContractTypesWithLanguage } from '../../../data/jobHooks';
import type { JobFormData } from '../types';
import type { StepErrors } from './validation';
import { Button } from '../../../ui/components/Button';
import { useLanguage } from '../../../state/i18n';
import { getContractTypeName } from './i18n/contractTypes';

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
  const { language } = useLanguage();
  const { data: industries } = useJobIndustries();
  const { data: jobRoles } = useJobRolesByCategory(formData.selectedIndustryId || null);
  const { data: contractTypes } = useContractTypesWithLanguage();

  const hasError = (field: string) => !!errors[field];
  const errorClass = (field: string) => hasError(field) ? 'border-red-400 focus:ring-red-500' : '';

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold text-slate-900 mb-2">Job Definition</h2>
        <p className="text-sm text-slate-600">Define the basic information about your job posting.</p>
      </div>

      <div className="bg-white rounded-2xl border border-slate-200 p-6 space-y-4">
        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Job Title *
          </label>
          <Input
            value={formData.title}
            onChange={(e) => updateFormData({ title: e.target.value })}
            placeholder="e.g. Sales Associate 80-100%"
            className={errorClass('title')}
          />
          <FieldError message={errors.title} />
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Job Industry *
          </label>
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
            <option value="">Select an industry</option>
            {industries?.map((industry) => (
              <option key={industry.id} value={industry.id}>
                {industry.name}
              </option>
            ))}
          </select>
          <FieldError message={errors.selectedIndustryId} />
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Job Role *
          </label>
          <select
            className={`w-full rounded-xl border bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 ${hasError('selectedRoleId') ? 'border-red-400 focus:ring-red-500' : 'border-slate-200 focus:ring-blue-500'}`}
            value={formData.selectedRoleId}
            onChange={(e) => updateFormData({ selectedRoleId: e.target.value })}
            disabled={!formData.selectedIndustryId}
          >
            <option value="">
              {formData.selectedIndustryId ? 'Select a role' : 'Select an industry first'}
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
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Job Description *
          </label>
          <textarea
            className={`w-full rounded-xl border bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 min-h-[120px] ${hasError('description') ? 'border-red-400 focus:ring-red-500' : 'border-slate-200 focus:ring-blue-500'}`}
            value={formData.description}
            onChange={(e) => updateFormData({ description: e.target.value })}
            placeholder="Describe the role, responsibilities and requirements..."
          />
          <FieldError message={errors.description} />
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Contract Type *
          </label>
          <select
            className={`w-full rounded-xl border bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 ${hasError('selectedContractTypeId') ? 'border-red-400 focus:ring-red-500' : 'border-slate-200 focus:ring-blue-500'}`}
            value={formData.selectedContractTypeId}
            onChange={(e) => updateFormData({ selectedContractTypeId: e.target.value })}
          >
            <option value="">Select contract type</option>
            {contractTypes?.map((type) => (
              <option key={type.id} value={type.id}>
                {getContractTypeName(type.id, language)}
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
              Back
            </Button>
            <Button onClick={onNext}>
              Next
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}

