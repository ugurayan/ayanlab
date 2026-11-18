import type { JobFormData } from '../types';

export type StepErrors = Record<string, string>;

export type ValidationResult = {
  valid: boolean;
  errors: StepErrors;
};

// Step 1: Job Definition
export function validateStep1(data: JobFormData): ValidationResult {
  const errors: StepErrors = {};

  if (!data.title.trim()) {
    errors.title = 'Job title is required';
  }

  if (!data.selectedIndustryId) {
    errors.selectedIndustryId = 'Please select an industry';
  }

  if (!data.selectedRoleId) {
    errors.selectedRoleId = 'Please select a job role';
  }

  if (!data.description.trim()) {
    errors.description = 'Job description is required';
  }

  if (!data.selectedContractTypeId) {
    errors.selectedContractTypeId = 'Please select a contract type';
  }

  return {
    valid: Object.keys(errors).length === 0,
    errors,
  };
}

// Step 2: Location and Contact
export function validateStep2(data: JobFormData): ValidationResult {
  const errors: StepErrors = {};

  if (!data.selectedAddressId && !data.workLocationStreet.trim()) {
    errors.selectedAddressId = 'Please select or add a location';
  }

  if (!data.workLocationName.trim()) {
    errors.workLocationName = 'Work location name is required';
  }

  if (!data.workLocationStreet.trim()) {
    errors.workLocationStreet = 'Street address is required';
  }

  // Zipcode optional: addresses from Google/API often have null zipCode

  if (data.selectedContactIds.length === 0) {
    errors.selectedContactIds = 'Please select at least one onsite contact';
  }

  return {
    valid: Object.keys(errors).length === 0,
    errors,
  };
}

// Step 3: Schedule
const BREAK_OPTIONS = ['0 min', '15 min', '30 min', '45 min', '60 min'];

export function validateStep3(data: JobFormData): ValidationResult {
  const errors: StepErrors = {};

  if (data.recurringSchedule) {
    if (!data.recurringStartDate?.trim()) {
      errors.recurringStartDate = 'Start date is required';
    }
    if (!data.recurringEndDate?.trim()) {
      errors.recurringEndDate = 'End date is required';
    }
    if (data.recurringStartDate && data.recurringEndDate) {
      const start = new Date(data.recurringStartDate).getTime();
      const end = new Date(data.recurringEndDate).getTime();
      if (end < start) {
        errors.recurringEndDate = 'End date must be on or after start date';
      }
    }
    if (!data.shiftDays?.length) {
      errors.shiftDays = 'Select at least one shift day';
    }
  } else {
    if (data.selectedDates.length === 0) {
      errors.selectedDates = 'Please select at least one date';
    }
  }

  if (!data.scheduleStartTime?.trim()) {
    errors.scheduleStartTime = 'Start time is required';
  }
  if (!data.scheduleEndTime?.trim()) {
    errors.scheduleEndTime = 'End time is required';
  }
  if (data.scheduleStartTime && data.scheduleEndTime) {
    const [sh, sm] = data.scheduleStartTime.split(':').map(Number);
    const [eh, em] = data.scheduleEndTime.split(':').map(Number);
    const startMins = (sh ?? 0) * 60 + (sm ?? 0);
    const endMins = (eh ?? 0) * 60 + (em ?? 0);
    if (endMins <= startMins) {
      errors.scheduleEndTime = 'End time must be after start time';
    }
  }

  if (!data.totalUnpaidBreak?.trim() || !BREAK_OPTIONS.includes(data.totalUnpaidBreak)) {
    errors.totalUnpaidBreak = 'Please select a break duration';
  }

  if (!data.employeesNeeded || parseInt(data.employeesNeeded, 10) < 1) {
    errors.employeesNeeded = 'Number of employees is required (min 1)';
  }
  if (!data.wageAmount || parseFloat(data.wageAmount) <= 0) {
    errors.wageAmount = 'Wage amount is required';
  }

  return {
    valid: Object.keys(errors).length === 0,
    errors,
  };
}

// Step 4: Requirements, Skills, Experience, Language
export function validateStep4(data: JobFormData): ValidationResult {
  const errors: StepErrors = {};

  // Step 4: require at least one language (from languageRequirements or legacy selectedLanguages)
  const langCount = (data.languageRequirements?.length ?? 0) || (data.selectedLanguages?.length ?? 0);
  if (langCount === 0) {
    errors.selectedLanguages = 'Please add at least one language';
  }

  return {
    valid: Object.keys(errors).length === 0,
    errors,
  };
}

// Step 5: Certificates & Documents
export function validateStep5(data: JobFormData): ValidationResult {
  // All fields optional in step 5
  return { valid: true, errors: {} };
}

// Step 6: Dress Requirements
export function validateStep6(data: JobFormData): ValidationResult {
  const errors: StepErrors = {};

  if (!data.selectedDressType) {
    errors.selectedDressType = 'Please select a dress code';
  }

  return {
    valid: Object.keys(errors).length === 0,
    errors,
  };
}

// Step 7: Staff Selection
export function validateStep7(data: JobFormData): ValidationResult {
  const errors: StepErrors = {};

  if (!data.staffSelectionType) {
    errors.staffSelectionType = 'Please select a staff selection method';
  }

  return {
    valid: Object.keys(errors).length === 0,
    errors,
  };
}

// Step 8: Review (no validation needed, just review)
export function validateStep8(data: JobFormData): ValidationResult {
  return { valid: true, errors: {} };
}

// Validate a specific step
export function validateStep(step: number, data: JobFormData): ValidationResult {
  switch (step) {
    case 1:
      return validateStep1(data);
    case 2:
      return validateStep2(data);
    case 3:
      return validateStep3(data);
    case 4:
      return validateStep4(data);
    case 5:
      return validateStep5(data);
    case 6:
      return validateStep6(data);
    case 7:
      return validateStep7(data);
    case 8:
      return validateStep8(data);
    default:
      return { valid: true, errors: {} };
  }
}

// Validate all steps up to a given step
export function validateUpToStep(upToStep: number, data: JobFormData): ValidationResult {
  const allErrors: StepErrors = {};

  for (let step = 1; step <= upToStep; step++) {
    const result = validateStep(step, data);
    Object.assign(allErrors, result.errors);
  }

  return {
    valid: Object.keys(allErrors).length === 0,
    errors: allErrors,
  };
}
