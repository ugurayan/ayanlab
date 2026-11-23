import type { Language } from '../../../state/i18n';
import type { JobFormData } from '../types';
import { WIZARD_TEXTS } from '../i18n/wizardTexts';

export type StepErrors = Record<string, string>;

export type ValidationResult = {
  valid: boolean;
  errors: StepErrors;
};

function messages(lang: Language) {
  return WIZARD_TEXTS[lang]?.validation ?? WIZARD_TEXTS.en.validation;
}

// Step 1: Job Definition
export function validateStep1(data: JobFormData, lang: Language = 'en'): ValidationResult {
  const m = messages(lang).step1;
  const errors: StepErrors = {};

  if (!data.title.trim()) {
    errors.title = m.title;
  }

  if (!data.selectedIndustryId) {
    errors.selectedIndustryId = m.selectedIndustryId;
  }

  if (!data.selectedRoleId) {
    errors.selectedRoleId = m.selectedRoleId;
  }

  if (!data.description.trim()) {
    errors.description = m.description;
  }

  if (!data.selectedContractTypeId) {
    errors.selectedContractTypeId = m.selectedContractTypeId;
  }

  return {
    valid: Object.keys(errors).length === 0,
    errors,
  };
}

// Step 2: Location and Contact
export function validateStep2(data: JobFormData, lang: Language = 'en'): ValidationResult {
  const m = messages(lang).step2;
  const errors: StepErrors = {};

  if (!data.selectedAddressId && !data.workLocationStreet.trim()) {
    errors.selectedAddressId = m.selectedAddressId;
  }

  if (!data.workLocationName.trim()) {
    errors.workLocationName = m.workLocationName;
  }

  if (!data.workLocationStreet.trim()) {
    errors.workLocationStreet = m.workLocationStreet;
  }

  if (data.selectedContactIds.length === 0) {
    errors.selectedContactIds = m.selectedContactIds;
  }

  return {
    valid: Object.keys(errors).length === 0,
    errors,
  };
}

// Step 3: Schedule
const BREAK_OPTIONS = ['0 min', '15 min', '30 min', '45 min', '60 min'];

export function validateStep3(data: JobFormData, lang: Language = 'en'): ValidationResult {
  const m = messages(lang).step3;
  const errors: StepErrors = {};

  if (data.recurringSchedule) {
    if (!data.recurringStartDate?.trim()) {
      errors.recurringStartDate = m.recurringStartDate;
    }
    if (!data.recurringEndDate?.trim()) {
      errors.recurringEndDate = m.recurringEndDate;
    }
    if (data.recurringStartDate && data.recurringEndDate) {
      const start = new Date(data.recurringStartDate).getTime();
      const end = new Date(data.recurringEndDate).getTime();
      if (end < start) {
        errors.recurringEndDate = m.recurringEndBeforeStart;
      }
    }
    if (!data.shiftDays?.length) {
      errors.shiftDays = m.shiftDays;
    }
  } else if (data.selectedDates.length === 0) {
    errors.selectedDates = m.selectedDates;
  }

  if (!data.scheduleStartTime?.trim()) {
    errors.scheduleStartTime = m.scheduleStartTime;
  }
  if (!data.scheduleEndTime?.trim()) {
    errors.scheduleEndTime = m.scheduleEndTime;
  }
  if (data.scheduleStartTime && data.scheduleEndTime) {
    const [sh, sm] = data.scheduleStartTime.split(':').map(Number);
    const [eh, em] = data.scheduleEndTime.split(':').map(Number);
    const startMins = (sh ?? 0) * 60 + (sm ?? 0);
    const endMins = (eh ?? 0) * 60 + (em ?? 0);
    if (endMins <= startMins) {
      errors.scheduleEndTime = m.scheduleEndBeforeStart;
    }
  }

  if (!data.totalUnpaidBreak?.trim() || !BREAK_OPTIONS.includes(data.totalUnpaidBreak)) {
    errors.totalUnpaidBreak = m.totalUnpaidBreak;
  }

  if (!data.employeesNeeded || parseInt(data.employeesNeeded, 10) < 1) {
    errors.employeesNeeded = m.employeesNeeded;
  }
  if (!data.wageAmount || parseFloat(data.wageAmount) <= 0) {
    errors.wageAmount = m.wageAmount;
  }

  return {
    valid: Object.keys(errors).length === 0,
    errors,
  };
}

// Step 4: Requirements, Skills, Experience, Language
export function validateStep4(data: JobFormData, lang: Language = 'en'): ValidationResult {
  const m = messages(lang).step4;
  const errors: StepErrors = {};

  const langCount = (data.languageRequirements?.length ?? 0) || (data.selectedLanguages?.length ?? 0);
  if (langCount === 0) {
    errors.selectedLanguages = m.selectedLanguages;
  }

  return {
    valid: Object.keys(errors).length === 0,
    errors,
  };
}

export function validateStep5(_data: JobFormData, _lang: Language = 'en'): ValidationResult {
  return { valid: true, errors: {} };
}

export function validateStep6(data: JobFormData, lang: Language = 'en'): ValidationResult {
  const m = messages(lang).step6;
  const errors: StepErrors = {};

  if (!data.selectedDressType) {
    errors.selectedDressType = m.selectedDressType;
  }

  return {
    valid: Object.keys(errors).length === 0,
    errors,
  };
}

export function validateStep7(data: JobFormData, lang: Language = 'en'): ValidationResult {
  const m = messages(lang).step7;
  const errors: StepErrors = {};

  if (!data.staffSelectionType) {
    errors.staffSelectionType = m.staffSelectionType;
  }

  return {
    valid: Object.keys(errors).length === 0,
    errors,
  };
}

export function validateStep8(_data: JobFormData, _lang: Language = 'en'): ValidationResult {
  return { valid: true, errors: {} };
}

export function validateStep(step: number, data: JobFormData, lang: Language = 'en'): ValidationResult {
  switch (step) {
    case 1:
      return validateStep1(data, lang);
    case 2:
      return validateStep2(data, lang);
    case 3:
      return validateStep3(data, lang);
    case 4:
      return validateStep4(data, lang);
    case 5:
      return validateStep5(data, lang);
    case 6:
      return validateStep6(data, lang);
    case 7:
      return validateStep7(data, lang);
    case 8:
      return validateStep8(data, lang);
    default:
      return { valid: true, errors: {} };
  }
}

export function validateUpToStep(upToStep: number, data: JobFormData, lang: Language = 'en'): ValidationResult {
  const allErrors: StepErrors = {};

  for (let step = 1; step <= upToStep; step++) {
    const result = validateStep(step, data, lang);
    Object.assign(allErrors, result.errors);
  }

  return {
    valid: Object.keys(allErrors).length === 0,
    errors: allErrors,
  };
}