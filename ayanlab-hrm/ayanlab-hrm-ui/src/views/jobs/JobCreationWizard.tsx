import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { Check, Calendar, MapPin, FileText, Shirt, Users, CheckCircle2, ChevronRight, Headphones } from 'lucide-react';
import { Button } from '../../ui/components/Button';
import { Step1JobDefinition } from './wizard/Step1JobDefinition';
import { Step2LocationContact } from './wizard/Step2LocationContact';
import { Step3Schedule } from './wizard/Step3Schedule';
import { Step4Requirements } from './wizard/Step4Requirements';
import { Step5Certificates } from './wizard/Step5Certificates';
import { Step6Dress } from './wizard/Step6Dress';
import { Step7Staff } from './wizard/Step7Staff';
import { Step8Review } from './wizard/Step8Review';
import type { JobFormData } from './types';
import { validateStep, type StepErrors } from './wizard/validation';
import { useJobWizardCopy } from './i18n/wizardTexts';
import { useLanguage } from '../../state/i18n';

const JOB_WIZARD_STORAGE_KEY = 'job-creation-wizard-data';
const ONE_DAY_MS = 24 * 60 * 60 * 1000; // 1 day in milliseconds

type StoredWizardData = {
  data: Partial<JobFormData>;
  timestamp: number;
};

function loadFromStorage(): Partial<JobFormData> | null {
  try {
    const stored = localStorage.getItem(JOB_WIZARD_STORAGE_KEY);
    if (stored) {
      const parsed: StoredWizardData = JSON.parse(stored);
      
      // Check if data has expired (older than 1 day)
      const now = Date.now();
      const age = now - parsed.timestamp;
      
      if (age > ONE_DAY_MS) {
        // Data expired, clear it
        clearStorage();
        return null;
      }
      
      // Filter out File objects (they can't be stored in localStorage)
      const data = parsed.data;
      if (data.otherInstructionsFiles) data.otherInstructionsFiles = [];
      return data;
    }
  } catch {
    // If parsing fails or any error, clear storage
    clearStorage();
  }
  return null;
}

function saveToStorage(data: JobFormData): void {
  try {
    // Clone and remove File objects before saving
    const toSave = { ...data, otherInstructionsFiles: [] };
    const storageData: StoredWizardData = {
      data: toSave,
      timestamp: Date.now(),
    };
    localStorage.setItem(JOB_WIZARD_STORAGE_KEY, JSON.stringify(storageData));
  } catch {
    // ignore
  }
}

function clearStorage(): void {
  try {
    localStorage.removeItem(JOB_WIZARD_STORAGE_KEY);
  } catch {
    // ignore
  }
}

const STEP_ICONS = [
  { id: 1, icon: FileText },
  { id: 2, icon: MapPin },
  { id: 3, icon: Calendar },
  { id: 4, icon: FileText },
  { id: 5, icon: FileText },
  { id: 6, icon: Shirt },
  { id: 7, icon: Users },
  { id: 8, icon: CheckCircle2 },
] as const;

const getDefaultFormData = (): JobFormData => ({
  // Step 1: Job Definition
  title: '',
  selectedIndustryId: '',
  selectedRoleId: '',
  description: '',
  selectedContractTypeId: '',
  selectedContactTypeId: '',
  
  // Step 2: Location and Contact
  selectedAddressId: '',
  workLocationName: '',
  workLocationStreet: '',
  workLocationCity: '',
  workLocationState: '',
  workLocationZipcode: '',
  workLocationCountry: '',
  workLocationNotes: '',
  useBackDoor: false,
  useFrontDoor: false,
  hasFreeBreakfast: false,
  hasFreeLunch: false,
  hasFreeDinner: false,
  selectedContactIds: [],
  
  // Step 3: Schedule
  recurringSchedule: false,
  scheduleName: '',
  selectedDates: [],
  recurringStartDate: '',
  recurringEndDate: '',
  shiftDays: [],
  scheduleStartTime: '09:00',
  scheduleEndTime: '17:45',
  totalUnpaidBreak: '30 min',
  preferSamePros: false,
  whenProsUnavailable: 'different',
  wageType: 'hourly',
  wageAmount: '',
  wageCurrency: 'USD',
  taxPercent: '',
  feesAmount: '',
  tipPercent: '',
  tipAmount: '',
  employeesNeeded: '',
  
  // Step 4: Requirements, Skills, Experience, Language
  selectedSkills: [],
  selectedExpertiseId: '',
  skillRequirements: [],
  selectedLanguages: [],
  languageRequirements: [],
  jobDuties: '',
  otherInstructions: '',
  otherInstructionsFiles: [],
  
  // Step 5: Certificates & Documents
  selectedCertificates: [],
  customCertificates: [],
  noCertificates: false,
  selectedDocuments: [],
  customDocuments: [],
  noDocuments: false,
  selectedEducationLevels: [],
  selectedDepartments: [],
  customDepartments: [],
  selectedDrivingLicences: [],
  noDrivingLicence: false,
  referenceCount: 0,

  // Step 6: Dress Requirements
  selectedDressCodeId: '',
  selectedDressType: '',
  selectedDressItems: [],
  attireRestrictionLevel: '',
  selectedAttireRestrictions: [],
  groomingLevel: '',
  selectedGroomingRestrictions: [],
  customGroomingInstructions: '',
  jobDutiesHtml: '',
  uploadedFileNames: [],
  additionalInstructionsHtml: '',
  additionalDressRequirements: '',
  
  // Step 7: Staff Selection
  staffSelectionType: 'smart-assign',
  
  // Step 8: Review
  saveAsTemplate: false,
  templateName: '',
});

export function JobCreationWizard() {
  const navigate = useNavigate();
  const w = useJobWizardCopy();
  const { language } = useLanguage();
  const steps = STEP_ICONS.map((step) => ({
    ...step,
    title: w.nav[`step${step.id}` as keyof typeof w.nav],
  }));
  const [currentStep, setCurrentStep] = useState(1);
  const [completedSteps, setCompletedSteps] = useState<Set<number>>(new Set());
  const [stepErrors, setStepErrors] = useState<StepErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState<JobFormData>(() => {
    const defaults = getDefaultFormData();
    const stored = loadFromStorage();
    if (!stored) return defaults;
    const merged = { ...defaults, ...stored };
    // Migrate legacy selectedSkills → skillRequirements
    if (!merged.skillRequirements?.length && merged.selectedSkills?.length) {
      merged.skillRequirements = merged.selectedSkills.map((skillId) => ({
        skillId,
        expertiseLevel: 'same-job-title',
        experienceDuration: 'none',
      }));
    }
    // Migrate legacy selectedLanguages → languageRequirements
    if (!merged.languageRequirements?.length && merged.selectedLanguages?.length) {
      merged.languageRequirements = merged.selectedLanguages.map((language) => ({
        language,
        level: 'B1',
        certificationNeeded: false,
      }));
    }
    // Migrate legacy staff selection values
    const validStaffTypes = ['open-to-all', 'open-to-new', 'smart-assign', 'request-approval', 'top-pros-only'];
    if (merged.staffSelectionType && !validStaffTypes.includes(merged.staffSelectionType)) {
      const staffType = merged.staffSelectionType as string;
      merged.staffSelectionType = staffType === 'new-pros' ? 'open-to-new' : staffType === 'roster-only' ? 'request-approval' : 'smart-assign';
    }
    // Migrate legacy dress type
    if ((merged.selectedDressType as string) === 'custom-dress') merged.selectedDressType = 'custom-uniform';
    // Ensure Step 6 extended fields exist
    if (merged.attireRestrictionLevel == null) merged.attireRestrictionLevel = '';
    if (merged.groomingLevel == null) merged.groomingLevel = '';
    if (merged.customGroomingInstructions == null) merged.customGroomingInstructions = '';
    if (merged.jobDutiesHtml == null) merged.jobDutiesHtml = '';
    if (merged.uploadedFileNames == null) merged.uploadedFileNames = [];
    if (merged.additionalInstructionsHtml == null) merged.additionalInstructionsHtml = '';
    return merged;
  });

  // Persist formData to localStorage on every change
  useEffect(() => {
    saveToStorage(formData);
  }, [formData]);

  const handleNext = useCallback(() => {
    // Validate current step before proceeding
    const result = validateStep(currentStep, formData, language);
    if (!result.valid) {
      setStepErrors(result.errors);
      return;
    }

    // Clear errors and proceed
    setStepErrors({});
    if (currentStep < steps.length) {
      setCompletedSteps(prev => new Set(prev).add(currentStep));
      setCurrentStep(prev => prev + 1);
    }
  }, [currentStep, formData, language, steps.length]);

  const handleBack = useCallback(() => {
    // Clear errors when going back
    setStepErrors({});
    if (currentStep > 1) {
      setCurrentStep(prev => prev - 1);
    }
  }, [currentStep]);

  const handleStepClick = useCallback((stepId: number) => {
    // Allow clicking on completed steps or current step
    if (completedSteps.has(stepId) || stepId === currentStep) {
      setStepErrors({});
      setCurrentStep(stepId);
    }
  }, [completedSteps, currentStep]);

  const handleSubmit = useCallback(async () => {
    setIsSubmitting(true);
    try {
      // TODO: Replace with actual API call when endpoint is available
      // Example: await apiCreateJob(formData);
      console.log('Submitting job:', formData);
      
      // Simulate API delay
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Clear storage after successful submission
      clearStorage();
      
      // Navigate to jobs list
      alert(w.alerts.submitSuccess);
      navigate('/app/jobs');
    } catch (error) {
      console.error('Failed to create job:', error);
      alert(w.alerts.submitFail);
    } finally {
      setIsSubmitting(false);
    }
  }, [formData, navigate, w.alerts]);

  const updateFormData = useCallback((updates: Partial<JobFormData>) => {
    setFormData(prev => ({ ...prev, ...updates }));
    // Clear errors for updated fields
    const updatedKeys = Object.keys(updates);
    if (updatedKeys.length > 0) {
      setStepErrors(prev => {
        const next = { ...prev };
        updatedKeys.forEach(key => {
          delete next[key];
        });
        return next;
      });
    }
  }, []);

  const renderStepContent = () => {
    const commonProps = {
      formData,
      updateFormData,
      onNext: handleNext,
      onBack: handleBack,
      canGoBack: currentStep > 1,
      errors: stepErrors,
    };

    switch (currentStep) {
      case 1:
        return <Step1JobDefinition {...commonProps} />;
      case 2:
        return <Step2LocationContact {...commonProps} />;
      case 3:
        return <Step3Schedule {...commonProps} />;
      case 4:
        return <Step4Requirements {...commonProps} />;
      case 5:
        return <Step5Certificates {...commonProps} />;
      case 6:
        return <Step6Dress {...commonProps} />;
      case 7:
        return <Step7Staff {...commonProps} />;
      case 8:
        return <Step8Review formData={formData} onSubmit={handleSubmit} onBack={handleBack} canGoBack={currentStep > 1} isSubmitting={isSubmitting} />;
      default:
        return null;
    }
  };

  return (
    <div className="h-screen bg-slate-50 flex overflow-hidden">
      {/* Left Sidebar - Step Navigation */}
      <aside className="w-64 bg-white border-r border-slate-200 p-6 flex flex-col h-full overflow-hidden">
        <div className="mb-8">
          <h2 className="text-lg font-semibold text-slate-900">{w.shell.title}</h2>
          <p className="text-sm text-slate-500 mt-1">{w.shell.subtitle}</p>
        </div>
        
        <nav className="flex-1 space-y-1 overflow-y-auto min-h-0">
          {steps.map((step) => {
            const Icon = step.icon;
            const isCompleted = completedSteps.has(step.id);
            const isActive = currentStep === step.id;
            const isAccessible = isCompleted || step.id === currentStep || step.id === currentStep + 1;
            
            return (
              <button
                key={step.id}
                onClick={() => isAccessible && handleStepClick(step.id)}
                disabled={!isAccessible}
                className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-left transition-colors ${
                  isActive
                    ? 'bg-blue-50 text-blue-700 border border-blue-200'
                    : isCompleted
                    ? 'text-slate-700 hover:bg-slate-50'
                    : 'text-slate-400 cursor-not-allowed'
                }`}
              >
                <div className={`flex-shrink-0 w-8 h-8 rounded-lg flex items-center justify-center ${
                  isActive
                    ? 'bg-blue-100 text-blue-700'
                    : isCompleted
                    ? 'bg-green-100 text-green-700'
                    : 'bg-slate-100 text-slate-400'
                }`}>
                  {isCompleted ? (
                    <Check size={16} />
                  ) : (
                    <Icon size={16} />
                  )}
                </div>
                <div className="flex-1 min-w-0">
                  <div className="text-xs font-medium">{w.shell.stepLabel} {step.id}</div>
                  <div className="text-xs text-slate-600 mt-0.5">{step.title}</div>
                </div>
                {isActive && <ChevronRight size={16} className="text-blue-700" />}
              </button>
            );
          })}
        </nav>

        <div className="mt-6 pt-6 border-t border-slate-200">
          <Button
            variant="secondary"
            onClick={() => navigate('/app/jobs')}
            className="w-full"
          >
            {w.shell.exit}
          </Button>
        </div>
      </aside>

      {/* Main Content Area */}
      <main className="flex-1 flex flex-col h-full min-h-0 overflow-hidden">
        <div className="flex-1 overflow-y-auto min-h-0">
          <div className="p-8 pb-24">
            <div className="max-w-4xl mx-auto">
              {renderStepContent()}
            </div>
          </div>
        </div>
      </main>

      {/* Right Sidebar - Help & Support */}
      <aside className="w-80 bg-white border-l border-slate-200 p-6 hidden xl:block h-full overflow-y-auto">
        <div className="space-y-6">
          <div>
            <h3 className="text-sm font-semibold text-slate-900 mb-2">{w.shell.helpTitle}</h3>
            <p className="text-xs text-slate-600 mb-3">{w.shell.helpBody}</p>
            <Button variant="secondary" className="w-full">
              {w.shell.watchTutorial}
            </Button>
          </div>

          <div className="pt-6 border-t border-slate-200">
            <h3 className="text-sm font-semibold text-slate-900 mb-2">{w.shell.supportTitle}</h3>
            <div className="flex items-center gap-3 mb-3">
              <div className="w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center">
                <Headphones size={20} className="text-slate-600" />
              </div>
              <div>
                <p className="text-xs text-slate-600">{w.shell.contactSupport}</p>
                <a href="#" className="text-xs text-blue-600 hover:underline">{w.shell.chatNow}</a>
                <span className="text-xs text-slate-400 mx-1">{w.shell.or}</span>
                <a href="mailto:support@example.com" className="text-xs text-blue-600 hover:underline">
                  support@example.com
                </a>
              </div>
            </div>
          </div>
        </div>
      </aside>
    </div>
  );
}

