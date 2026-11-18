export interface JobFormData {
  // Step 1: Job Definition
  title: string;
  selectedIndustryId: string;
  selectedRoleId: string;
  description: string;
  selectedContractTypeId: string;
  selectedContactTypeId: string;
  
  // Step 2: Location and Contact
  selectedAddressId: string;
  workLocationName: string;
  workLocationStreet: string;
  workLocationCity: string;
  workLocationState: string;
  workLocationZipcode: string;
  workLocationCountry: string;
  workLocationNotes: string;
  useBackDoor: boolean;
  useFrontDoor: boolean;
  hasFreeBreakfast: boolean;
  hasFreeLunch: boolean;
  hasFreeDinner: boolean;
  selectedContactIds: string[];
  
  // Step 3: Schedule
  recurringSchedule: boolean;
  scheduleName: string;
  /** Non-recurring: explicit list of dates (ISO date strings) */
  selectedDates: string[];
  /** Recurring: range start (YYYY-MM-DD) */
  recurringStartDate: string;
  /** Recurring: range end (YYYY-MM-DD) */
  recurringEndDate: string;
  /** Recurring: weekdays on which shift runs: 'sun' | 'mon' | 'tue' | 'wed' | 'thu' | 'fri' | 'sat' */
  shiftDays: string[];
  scheduleStartTime: string;
  scheduleEndTime: string;
  totalUnpaidBreak: string;
  preferSamePros: boolean;
  whenProsUnavailable: 'different' | 'unfilled';
  wageType: 'hourly' | 'daily';
  wageAmount: string;
  /** Currency code for display (e.g. USD) */
  wageCurrency: string;
  /** Tax as percentage of base (e.g. "10" for 10%) */
  taxPercent: string;
  /** Fixed fees amount (e.g. "50") */
  feesAmount: string;
  /** Tip as percentage of base (e.g. "10" for 10%) */
  tipPercent: string;
  /** Tip as fixed amount (e.g. "25") */
  tipAmount: string;
  employeesNeeded: string;
  
  // Step 4: Requirements, Skills, Experience, Language
  /** @deprecated Derived from skillRequirements; kept for compatibility */
  selectedSkills: string[];
  selectedExpertiseId: string;
  /** Per-skill: expertise level + experience duration (from API list). */
  skillRequirements: Array<{
    skillId: string;
    /** Display name for custom (user-added) skills; undefined for API skills */
    skillName?: string;
    expertiseLevel: string;
    experienceDuration: string;
    customValue?: number;
    customUnit?: 'months' | 'years';
  }>;
  /** @deprecated Derived from languageRequirements; kept for compatibility */
  selectedLanguages: string[];
  /** Language + proficiency level (A1–C2, Native) + certification needed. */
  languageRequirements: Array<{
    language: string;
    level: string;
    certificationNeeded?: boolean;
  }>;
  jobDuties: string;
  otherInstructions: string;
  otherInstructionsFiles: File[];
  
  // Step 5: Certificates & Documents
  selectedCertificates: string[];
  /** Custom certificate entries (name + required flag). */
  customCertificates: Array<{ name: string; required?: boolean }>;
  noCertificates: boolean;
  selectedDocuments: string[];
  /** Custom document names when user selects Custom. */
  customDocuments: Array<{ name: string }>;
  noDocuments: boolean;
  /** Education levels (e.g. no-education, high-school, bachelor, master). Multi-select. */
  selectedEducationLevels: string[];
  /** Department ids or names; multi-language labels stored for display. */
  selectedDepartments: string[];
  /** Custom department names when user selects Custom. */
  customDepartments: string[];
  /** Driving licence types (e.g. B, C, CE). Multi-select. */
  selectedDrivingLicences: string[];
  /** No driving licence required. */
  noDrivingLicence: boolean;
  /** Number of references required (1–5). */
  referenceCount: number;

  // Step 6: Dress Requirements
  selectedDressCodeId: string;
  selectedDressType: '' | 'casual' | 'smart-casual' | 'business' | 'dress-provided' | 'custom-uniform' | 'no-preference';
  selectedDressItems: Array<{ item: string; color: string; id: string }>;
  /** Attire restriction level: basic preset, strict preset, custom list, or no preference */
  attireRestrictionLevel: '' | 'basic' | 'strict' | 'custom' | 'no-preference';
  selectedAttireRestrictions: string[];
  /** Grooming level: elevated, professional, standard, custom, or no preference */
  groomingLevel: '' | 'elevated' | 'professional' | 'standard' | 'custom' | 'no-preference';
  selectedGroomingRestrictions: string[];
  customGroomingInstructions: string;
  /** Rich text / plain HTML for job duties */
  jobDutiesHtml: string;
  /** Uploaded file names (or IDs); actual upload is mock/API later */
  uploadedFileNames: string[];
  /** Rich text for additional instructions */
  additionalInstructionsHtml: string;
  additionalDressRequirements: string;
  
  // Step 7: Staff Selection
  staffSelectionType: 'open-to-all' | 'open-to-new' | 'smart-assign' | 'request-approval' | 'top-pros-only';
  
  // Step 8: Review
  saveAsTemplate: boolean;
  templateName: string;
}

