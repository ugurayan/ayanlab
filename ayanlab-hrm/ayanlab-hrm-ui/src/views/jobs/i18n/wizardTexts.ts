import type { UiTranslationLanguage } from '../../../state/i18n';
import { resolveTranslationLanguage, useLanguage } from '../../../state/i18n';

export type JobWizardCopy = {
  shell: {
    kicker: string;
    title: string;
    subtitle: string;
    stepLabel: string;
    exit: string;
    helpTitle: string;
    helpBody: string;
    watchTutorial: string;
    supportTitle: string;
    contactSupport: string;
    chatNow: string;
    or: string;
  };
  nav: Record<`step${1 | 2 | 3 | 4 | 5 | 6 | 7 | 8}`, string>;
  common: {
    back: string;
    next: string;
    edit: string;
    notSet: string;
    none: string;
    loading: string;
    fixErrorsTitle: string;
    selectIndustry: string;
    selectIndustryFirst: string;
    selectRole: string;
    selectContract: string;
    certificationNeeded: string;
    add: string;
    required: string;
    custom: string;
  };
  step1: {
    title: string;
    subtitle: string;
    jobTitle: string;
    jobIndustry: string;
    jobRole: string;
    jobDescription: string;
    contractType: string;
    titlePlaceholder: string;
    descriptionPlaceholder: string;
  };
  step2: {
    title: string;
    subtitle: string;
    selectWorkLocation: string;
    selectLocation: string;
    selectedLocation: string;
    locationNotes: string;
    locationNotesPlaceholder: string;
    entrance: string;
    useBackDoor: string;
    useFrontDoor: string;
    meals: string;
    freeBreakfast: string;
    freeLunch: string;
    freeDinner: string;
    onsiteContacts: string;
    selectContacts: string;
    selectedContacts: string;
    contactsSelected: string;
    onsiteContact: string;
  };
  step3: {
    title: string;
    subtitle: string;
    recurringSchedule: string;
    scheduleName: string;
    scheduleNamePlaceholder: string;
    startDate: string;
    endDate: string;
    shiftDays: string;
    dates: string;
    startTime: string;
    endTime: string;
    unpaidBreak: string;
    preferSamePros: string;
    whenUnavailable: string;
    whenUnavailableDifferent: string;
    whenUnavailableUnfilled: string;
    employeesNeeded: string;
    employeesPlaceholder: string;
    wageAmount: string;
    wagePlaceholder: string;
    tipPercent: string;
    tipPercentPlaceholder: string;
    tipFixed: string;
    tipFixedPlaceholder: string;
    days: Record<string, string>;
    expectedBreaks: string;
    expectedBreaksNone: string;
    breakNoBreak: string;
    dateAndTimes: string;
    addDates: string;
    wageHourly: string;
    wageDaily: string;
  };
  step4: {
    title: string;
    subtitle: string;
    requiredSkills: string;
    requiredSkillsHint: string;
    selectRoleFirst: string;
    skillsLoadWarning: string;
    chooseSkills: string;
    skillsSelected: string;
    searchSkills: string;
    noSkillsMatch: string;
    addOwnSkill: string;
    skillNamePlaceholder: string;
    languages: string;
    languagesHint: string;
    selectLanguage: string;
    jobDuties: string;
    jobDutiesPlaceholder: string;
    additionalInstructions: string;
    additionalInstructionsPlaceholder: string;
    expertiseOptions: Array<{ value: string; label: string }>;
    experienceOptions: Array<{ value: string; label: string }>;
    skillColumn: string;
    expertiseColumn: string;
    experienceColumn: string;
    months: string;
    years: string;
    certificationRequired: string;
    loadingSkills: string;
  };
  step5: {
    title: string;
    subtitle: string;
    certificates: string;
    certificatesHint: string;
    noCertificates: string;
    documents: string;
    documentsHint: string;
    noDocuments: string;
    drivingLicence: string;
    drivingLicenceHint: string;
    noDrivingLicence: string;
    education: string;
    departments: string;
    references: string;
    howManyReferences: string;
    chooseCertificates: string;
    chooseDocuments: string;
    chooseLicence: string;
    chooseEducation: string;
    chooseDepartments: string;
    selectedCount: string;
    selectRoleHint: string;
    certificateNamePlaceholder: string;
    documentNamePlaceholder: string;
    departmentNamePlaceholder: string;
    selectRoleDocsHint: string;
    customRequired: string;
    certificatesLoadFail: string;
    noCertificatesForRole: string;
    documentsLoadFail: string;
    noDocumentsForRole: string;
  };
  step8: {
    title: string;
    subtitle: string;
    sections: {
      jobDefinition: string;
      location: string;
      schedule: string;
      requirements: string;
      certificates: string;
      dress: string;
      staff: string;
    };
    labels: {
      title: string;
      description: string;
      location: string;
      address: string;
      contact: string;
      type: string;
      recurring: string;
      range: string;
      days: string;
      dates: string;
      time: string;
      break: string;
      employees: string;
      wage: string;
      skills: string;
      languages: string;
      selected: string;
      certRequired: string;
    };
    submit: string;
    submitting: string;
  };
  alerts: {
    submitSuccess: string;
    submitFail: string;
  };
  validation: {
    step1: Record<string, string>;
    step2: Record<string, string>;
    step3: Record<string, string>;
    step4: Record<string, string>;
    step6: Record<string, string>;
    step7: Record<string, string>;
  };
};

const EN: JobWizardCopy = {
  shell: {
    kicker: 'Create job',
    title: 'Create Job',
    subtitle: 'Step-by-step wizard',
    stepLabel: 'Step',
    exit: 'Exit',
    helpTitle: 'How to book?',
    helpBody: 'Learn how to create a booking with easy to follow steps.',
    watchTutorial: 'Watch tutorial',
    supportTitle: 'Questions about booking?',
    contactSupport: 'Contact support',
    chatNow: 'Chat now',
    or: 'or',
  },
  nav: {
    step1: 'Job definition',
    step2: 'Location and contact',
    step3: 'Schedule',
    step4: 'Requirements, skills & language',
    step5: 'Certificates & documents',
    step6: 'Dress requirements',
    step7: 'Staff selection',
    step8: 'Review and confirm',
  },
  common: {
    back: 'Back',
    next: 'Next',
    edit: 'Edit',
    notSet: 'Not set',
    none: 'None',
    loading: 'Loading...',
    fixErrorsTitle: 'Please fix the following before continuing:',
    selectIndustry: 'Select an industry',
    selectIndustryFirst: 'Select an industry first',
    selectRole: 'Select a role',
    selectContract: 'Select contract type',
    certificationNeeded: 'Certification needed',
    add: 'Add',
    required: 'Required',
    custom: 'Custom',
  },
  step1: {
    title: 'Job definition',
    subtitle: 'Define the basic information about your job posting.',
    jobTitle: 'Job title',
    jobIndustry: 'Job industry',
    jobRole: 'Job role',
    jobDescription: 'Job description',
    contractType: 'Contract type',
    titlePlaceholder: 'e.g. Sales Associate 80-100%',
    descriptionPlaceholder: 'Describe the role, responsibilities and requirements...',
  },
  step2: {
    title: 'Location & contact',
    subtitle: 'Set the work location and onsite contact information.',
    selectWorkLocation: 'Select work location',
    selectLocation: 'Select a location',
    selectedLocation: 'Selected location',
    locationNotes: 'Location notes / directions',
    locationNotesPlaceholder: 'Add clear directions or important notes about the location...',
    entrance: 'Entrance',
    useBackDoor: 'Use back door entrance',
    useFrontDoor: 'Use front door entrance',
    meals: 'Meals',
    freeBreakfast: 'Free breakfast',
    freeLunch: 'Free lunch',
    freeDinner: 'Free dinner',
    onsiteContacts: 'Onsite contacts',
    selectContacts: 'Select contacts...',
    selectedContacts: 'selected',
    contactsSelected: '{count} contact(s) selected',
    onsiteContact: 'Onsite contact',
  },
  step3: {
    title: 'Schedule',
    subtitle: 'Define when and how long the work will take place.',
    recurringSchedule: 'Recurring schedule',
    scheduleName: 'Schedule name (optional)',
    scheduleNamePlaceholder: 'e.g. Weekly morning shift',
    startDate: 'Start date',
    endDate: 'End date',
    shiftDays: 'Shift days',
    dates: 'Dates',
    startTime: 'Start time',
    endTime: 'End time',
    unpaidBreak: 'Total unpaid break',
    preferSamePros: 'Prefer same professionals when possible',
    whenUnavailable: 'When preferred pros are unavailable',
    whenUnavailableDifferent: 'Assign different professionals',
    whenUnavailableUnfilled: 'Leave shift unfilled',
    employeesNeeded: 'Employees needed',
    employeesPlaceholder: 'Number of workers',
    wageAmount: 'Wage amount',
    wagePlaceholder: 'Amount',
    tipPercent: 'Tip (optional) % of base',
    tipPercentPlaceholder: 'e.g. 10, 15, 20',
    tipFixed: 'Tip (optional) fixed amount',
    tipFixedPlaceholder: '0',
    days: { sun: 'Sun', mon: 'Mon', tue: 'Tue', wed: 'Wed', thu: 'Thu', fri: 'Fri', sat: 'Sat' },
    expectedBreaks: 'Expected breaks:',
    expectedBreaksNone: 'None',
    breakNoBreak: 'No break',
    dateAndTimes: 'Date & times',
    addDates: 'Add dates',
    wageHourly: 'Hourly',
    wageDaily: 'Daily',
  },
  step4: {
    title: 'Requirements, skills & language',
    subtitle: 'Specify what skills, experience, and languages are required for this job.',
    requiredSkills: 'Required skills',
    requiredSkillsHint: 'Suggested for the job role you chose in step 1. You can add your own skills.',
    selectRoleFirst: 'Select a job role in Step 1 to see available skills for this role.',
    skillsLoadWarning: 'Skills could not be loaded from the server. Try again or add skills manually below.',
    chooseSkills: 'Choose skills (multi-select)',
    skillsSelected: '{count} skill(s) selected',
    searchSkills: 'Search skills...',
    noSkillsMatch: 'No skills match your search.',
    addOwnSkill: 'Add your own skill',
    skillNamePlaceholder: 'Type skill name...',
    languages: 'Languages',
    languagesHint: 'Languages workers must speak on this job.',
    selectLanguage: 'Select language',
    jobDuties: 'Job duties',
    jobDutiesPlaceholder: 'List specific tasks and responsibilities...',
    additionalInstructions: 'Additional instructions',
    additionalInstructionsPlaceholder: 'Any additional instructions for workers...',
    expertiseOptions: [
      { value: 'same-job-title', label: 'Worked before with same job title' },
      { value: 'similar-role', label: 'Worked in similar role' },
      { value: 'formally-trained', label: 'Formally trained' },
      { value: 'some-experience', label: 'Some experience' },
      { value: 'no-experience', label: 'No experience / Willing to learn' },
    ],
    experienceOptions: [
      { value: 'none', label: 'No experience' },
      { value: '3m', label: '3 months' },
      { value: '6m', label: '6 months' },
      { value: '1yr', label: '1 year' },
      { value: '2yr', label: '2 years' },
      { value: '3yr', label: '3 years' },
      { value: '4yr', label: '4 years' },
      { value: '5yr', label: '5 years' },
      { value: 'custom', label: 'Custom' },
    ],
    skillColumn: 'Skill',
    expertiseColumn: 'Expertise level',
    experienceColumn: 'Experience',
    months: 'months',
    years: 'years',
    certificationRequired: '(Certification required)',
    loadingSkills: 'Loading skills...',
  },
  step5: {
    title: 'Certificates & documents',
    subtitle: 'Select required certificates and documents for this job.',
    certificates: 'Certificates',
    certificatesHint: 'Proof or training workers may need before starting.',
    noCertificates: 'No certificates',
    documents: 'Required documents',
    documentsHint: 'Identity, work permit, and other paperwork.',
    noDocuments: 'No documents',
    drivingLicence: 'Driving licence',
    drivingLicenceHint: 'Only if the job requires a vehicle.',
    noDrivingLicence: 'No driving licence needed',
    education: 'Education',
    departments: 'Departments',
    references: 'References needed',
    howManyReferences: 'How many references',
    chooseCertificates: 'Choose certificates (multi-select)',
    chooseDocuments: 'Choose documents (multi-select)',
    chooseLicence: 'Choose driving licence types (multi-select)',
    chooseEducation: 'Choose education levels',
    chooseDepartments: 'Choose departments',
    selectedCount: '{count} selected',
    selectRoleHint: 'Select a job role in Step 1 to load certificates.',
    certificateNamePlaceholder: 'Certificate name',
    documentNamePlaceholder: 'Document name',
    departmentNamePlaceholder: 'Department name',
    selectRoleDocsHint: 'Select a job role in Step 1 to see documents.',
    customRequired: 'Required',
    certificatesLoadFail: 'Failed to load certificates. Check that you are signed in and the API is available.',
    noCertificatesForRole: 'No certificates returned for this job role. You can add a custom certificate below.',
    documentsLoadFail: 'Failed to load documents. Check that you are signed in and the API is available.',
    noDocumentsForRole: 'No documents returned for this job role.',
  },
  step8: {
    title: 'Review & confirm',
    subtitle: 'Review all details before confirming your job posting.',
    sections: {
      jobDefinition: 'Job definition',
      location: 'Location',
      schedule: 'Schedule & positions',
      requirements: 'Requirements',
      certificates: 'Certificates & documents',
      dress: 'Dress requirements',
      staff: 'Staff selection',
    },
    labels: {
      title: 'Title',
      description: 'Description',
      location: 'Location',
      address: 'Address',
      contact: 'Contact',
      type: 'Type',
      recurring: 'Recurring',
      range: 'Range',
      days: 'Days',
      dates: 'Dates',
      time: 'Time',
      break: 'Break',
      employees: 'Employees needed',
      wage: 'Wage',
      skills: 'Skills',
      languages: 'Languages',
      selected: '{count} selected',
      certRequired: 'cert. required',
    },
    submit: 'Confirm job',
    submitting: 'Creating...',
  },
  alerts: {
    submitSuccess: 'Job created successfully!',
    submitFail: 'Failed to create job. Please try again.',
  },
  validation: {
    step1: {
      title: 'Job title is required',
      selectedIndustryId: 'Please select an industry',
      selectedRoleId: 'Please select a job role',
      description: 'Job description is required',
      selectedContractTypeId: 'Please select a contract type',
    },
    step2: {
      selectedAddressId: 'Please select or add a location',
      workLocationName: 'Work location name is required',
      workLocationStreet: 'Street address is required',
      selectedContactIds: 'Please select at least one onsite contact',
    },
    step3: {
      recurringStartDate: 'Start date is required',
      recurringEndDate: 'End date is required',
      recurringEndBeforeStart: 'End date must be on or after start date',
      shiftDays: 'Select at least one shift day',
      selectedDates: 'Please select at least one date',
      scheduleStartTime: 'Start time is required',
      scheduleEndTime: 'End time is required',
      scheduleEndBeforeStart: 'End time must be after start time',
      totalUnpaidBreak: 'Please select a break duration',
      employeesNeeded: 'Number of employees is required (min 1)',
      wageAmount: 'Wage amount is required',
    },
    step4: {
      selectedLanguages: 'Please add at least one language',
    },
    step6: {
      selectedDressType: 'Please select a dress code',
    },
    step7: {
      staffSelectionType: 'Please select a staff selection method',
    },
  },
};

const DE: JobWizardCopy = {
  ...EN,
  shell: {
    ...EN.shell,
    kicker: 'Stelle erstellen',
    title: 'Stelle erstellen',
    subtitle: 'Schritt-für-Schritt-Assistent',
    stepLabel: 'Schritt',
    exit: 'Beenden',
    helpTitle: 'Wie buchen?',
    helpBody: 'Lernen Sie in einfachen Schritten, eine Buchung zu erstellen.',
    watchTutorial: 'Tutorial ansehen',
    supportTitle: 'Fragen zur Buchung?',
    contactSupport: 'Support kontaktieren',
    chatNow: 'Jetzt chatten',
    or: 'oder',
  },
  nav: {
    step1: 'Stellenbeschreibung',
    step2: 'Standort & Kontakt',
    step3: 'Zeitplan',
    step4: 'Anforderungen, Skills & Sprachen',
    step5: 'Zertifikate & Dokumente',
    step6: 'Kleiderordnung',
    step7: 'Personalauswahl',
    step8: 'Prüfen & bestätigen',
  },
  common: {
    ...EN.common,
    back: 'Zurück',
    next: 'Weiter',
    edit: 'Bearbeiten',
    notSet: 'Nicht gesetzt',
    none: 'Keine',
    loading: 'Wird geladen...',
    fixErrorsTitle: 'Bitte beheben Sie Folgendes, bevor Sie fortfahren:',
    selectIndustry: 'Branche auswählen',
    selectIndustryFirst: 'Zuerst Branche auswählen',
    selectRole: 'Rolle auswählen',
    selectContract: 'Vertragsart auswählen',
    certificationNeeded: 'Zertifikat erforderlich',
    add: 'Hinzufügen',
    required: 'Pflicht',
    custom: 'Benutzerdefiniert',
  },
  step1: {
    title: 'Stellenbeschreibung',
    subtitle: 'Geben Sie die Grundinformationen zu Ihrer Stelle ein.',
    jobTitle: 'Stellentitel',
    jobIndustry: 'Branche',
    jobRole: 'Berufsrolle',
    jobDescription: 'Stellenbeschreibung',
    contractType: 'Vertragsart',
    titlePlaceholder: 'z. B. Verkäufer:in 80–100 %',
    descriptionPlaceholder: 'Beschreiben Sie Rolle, Aufgaben und Anforderungen...',
  },
  step2: {
    title: 'Standort & Kontakt',
    subtitle: 'Arbeitsort und Ansprechperson vor Ort festlegen.',
    selectWorkLocation: 'Arbeitsort auswählen',
    selectLocation: 'Standort auswählen',
    selectedLocation: 'Ausgewählter Standort',
    locationNotes: 'Hinweise / Wegbeschreibung',
    locationNotesPlaceholder: 'Wegbeschreibung oder wichtige Hinweise zum Standort...',
    entrance: 'Eingang',
    useBackDoor: 'Hintereingang nutzen',
    useFrontDoor: 'Haupteingang nutzen',
    meals: 'Mahlzeiten',
    freeBreakfast: 'Gratis Frühstück',
    freeLunch: 'Gratis Mittagessen',
    freeDinner: 'Gratis Abendessen',
    onsiteContacts: 'Kontakte vor Ort',
    selectContacts: 'Kontakte auswählen...',
    selectedContacts: 'ausgewählt',
    contactsSelected: '{count} Kontakt(e) ausgewählt',
    onsiteContact: 'Kontakt vor Ort',
  },
  step3: {
    ...EN.step3,
    title: 'Zeitplan',
    subtitle: 'Legen Sie fest, wann und wie lange gearbeitet wird.',
    recurringSchedule: 'Wiederkehrender Plan',
    scheduleName: 'Planname (optional)',
    scheduleNamePlaceholder: 'z. B. Wöchentliche Frühschicht',
    startDate: 'Startdatum',
    endDate: 'Enddatum',
    shiftDays: 'Schichttage',
    dates: 'Daten',
    startTime: 'Startzeit',
    endTime: 'Endzeit',
    unpaidBreak: 'Unbezahlte Pause',
    preferSamePros: 'Nach Möglichkeit gleiche Fachkräfte',
    whenUnavailable: 'Wenn bevorzugte Fachkräfte nicht verfügbar',
    whenUnavailableDifferent: 'Andere Fachkräfte zuweisen',
    whenUnavailableUnfilled: 'Schicht unbesetzt lassen',
    employeesNeeded: 'Benötigte Mitarbeitende',
    employeesPlaceholder: 'Anzahl',
    wageAmount: 'Lohnbetrag',
    wagePlaceholder: 'Betrag',
    tipPercent: 'Trinkgeld (optional) %',
    tipPercentPlaceholder: 'z. B. 10, 15, 20',
    tipFixed: 'Trinkgeld (optional) fester Betrag',
    tipFixedPlaceholder: '0',
    days: { sun: 'So', mon: 'Mo', tue: 'Di', wed: 'Mi', thu: 'Do', fri: 'Fr', sat: 'Sa' },
    expectedBreaks: 'Erwartete Pausen:',
    expectedBreaksNone: 'Keine',
    breakNoBreak: 'Keine Pause',
    dateAndTimes: 'Datum & Zeiten',
    addDates: 'Daten hinzufügen',
    wageHourly: 'Stündlich',
    wageDaily: 'Täglich',
  },
  step4: {
    title: 'Anforderungen, Skills & Sprachen',
    subtitle: 'Erforderliche Fähigkeiten, Erfahrung und Sprachen festlegen.',
    requiredSkills: 'Erforderliche Skills',
    requiredSkillsHint: 'Vorschläge basierend auf der Rolle in Schritt 1. Eigene Skills möglich.',
    selectRoleFirst: 'Wählen Sie in Schritt 1 eine Rolle, um Skills zu sehen.',
    skillsLoadWarning: 'Skills konnten nicht geladen werden. Manuell hinzufügen oder erneut versuchen.',
    chooseSkills: 'Skills auswählen (Mehrfach)',
    skillsSelected: '{count} Skill(s) ausgewählt',
    searchSkills: 'Skills suchen...',
    noSkillsMatch: 'Keine passenden Skills.',
    addOwnSkill: 'Eigenen Skill hinzufügen',
    skillNamePlaceholder: 'Skill-Name eingeben...',
    languages: 'Sprachen',
    languagesHint: 'Sprachen, die für diese Stelle erforderlich sind.',
    selectLanguage: 'Sprache auswählen',
    jobDuties: 'Aufgaben',
    jobDutiesPlaceholder: 'Konkrete Aufgaben und Verantwortlichkeiten...',
    additionalInstructions: 'Zusätzliche Hinweise',
    additionalInstructionsPlaceholder: 'Weitere Anweisungen für Mitarbeitende...',
    expertiseOptions: [
      { value: 'same-job-title', label: 'Bereits in gleicher Position gearbeitet' },
      { value: 'similar-role', label: 'Ähnliche Rolle ausgeübt' },
      { value: 'formally-trained', label: 'Formal ausgebildet' },
      { value: 'some-experience', label: 'Etwas Erfahrung' },
      { value: 'no-experience', label: 'Keine Erfahrung / Lernbereitschaft' },
    ],
    experienceOptions: [
      { value: 'none', label: 'Keine Erfahrung' },
      { value: '3m', label: '3 Monate' },
      { value: '6m', label: '6 Monate' },
      { value: '1yr', label: '1 Jahr' },
      { value: '2yr', label: '2 Jahre' },
      { value: '3yr', label: '3 Jahre' },
      { value: '4yr', label: '4 Jahre' },
      { value: '5yr', label: '5 Jahre' },
      { value: 'custom', label: 'Benutzerdefiniert' },
    ],
    skillColumn: 'Skill',
    expertiseColumn: 'Expertise-Niveau',
    experienceColumn: 'Erfahrung',
    months: 'Monate',
    years: 'Jahre',
    certificationRequired: '(Zertifikat erforderlich)',
    loadingSkills: 'Skills werden geladen...',
  },
  step5: {
    title: 'Zertifikate & Dokumente',
    subtitle: 'Erforderliche Nachweise und Dokumente auswählen.',
    certificates: 'Zertifikate',
    certificatesHint: 'Nachweise oder Schulungen vor Arbeitsbeginn.',
    noCertificates: 'Keine Zertifikate',
    documents: 'Erforderliche Dokumente',
    documentsHint: 'Ausweis, Arbeitserlaubnis usw.',
    noDocuments: 'Keine Dokumente',
    drivingLicence: 'Führerschein',
    drivingLicenceHint: 'Nur wenn ein Fahrzeug benötigt wird.',
    noDrivingLicence: 'Kein Führerschein nötig',
    education: 'Ausbildung',
    departments: 'Abteilungen',
    references: 'Referenzen',
    howManyReferences: 'Anzahl Referenzen',
    chooseCertificates: 'Zertifikate auswählen (Mehrfach)',
    chooseDocuments: 'Dokumente auswählen (Mehrfach)',
    chooseLicence: 'Führerscheinklassen (Mehrfach)',
    chooseEducation: 'Bildungsniveau wählen',
    chooseDepartments: 'Abteilungen wählen',
    selectedCount: '{count} ausgewählt',
    selectRoleHint: 'Rolle in Schritt 1 wählen, um Zertifikate zu laden.',
    certificateNamePlaceholder: 'Zertifikatsname',
    documentNamePlaceholder: 'Dokumentname',
    departmentNamePlaceholder: 'Abteilungsname',
    selectRoleDocsHint: 'Rolle in Schritt 1 wählen, um Dokumente zu sehen.',
    customRequired: 'Pflicht',
    certificatesLoadFail: 'Zertifikate konnten nicht geladen werden. Prüfen Sie Anmeldung und API.',
    noCertificatesForRole: 'Keine Zertifikate für diese Rolle. Sie können unten ein eigenes hinzufügen.',
    documentsLoadFail: 'Dokumente konnten nicht geladen werden. Prüfen Sie Anmeldung und API.',
    noDocumentsForRole: 'Keine Dokumente für diese Rolle zurückgegeben.',
  },
  step8: {
    title: 'Prüfen & bestätigen',
    subtitle: 'Alle Angaben prüfen, bevor Sie die Stelle veröffentlichen.',
    sections: {
      jobDefinition: 'Stellenbeschreibung',
      location: 'Standort',
      schedule: 'Zeitplan & Positionen',
      requirements: 'Anforderungen',
      certificates: 'Zertifikate & Dokumente',
      dress: 'Kleiderordnung',
      staff: 'Personalauswahl',
    },
    labels: {
      title: 'Titel',
      description: 'Beschreibung',
      location: 'Standort',
      address: 'Adresse',
      contact: 'Kontakt',
      type: 'Typ',
      recurring: 'Wiederkehrend',
      range: 'Zeitraum',
      days: 'Tage',
      dates: 'Daten',
      time: 'Zeit',
      break: 'Pause',
      employees: 'Mitarbeitende',
      wage: 'Lohn',
      skills: 'Skills',
      languages: 'Sprachen',
      selected: '{count} ausgewählt',
      certRequired: 'Zert. erforderlich',
    },
    submit: 'Stelle bestätigen',
    submitting: 'Wird erstellt...',
  },
  alerts: {
    submitSuccess: 'Stelle erfolgreich erstellt!',
    submitFail: 'Erstellen fehlgeschlagen. Bitte erneut versuchen.',
  },
  validation: {
    step1: {
      title: 'Stellentitel ist erforderlich',
      selectedIndustryId: 'Bitte Branche auswählen',
      selectedRoleId: 'Bitte Berufsrolle auswählen',
      description: 'Stellenbeschreibung ist erforderlich',
      selectedContractTypeId: 'Bitte Vertragsart auswählen',
    },
    step2: {
      selectedAddressId: 'Bitte Standort auswählen oder hinzufügen',
      workLocationName: 'Name des Arbeitsorts ist erforderlich',
      workLocationStreet: 'Straße ist erforderlich',
      selectedContactIds: 'Mindestens einen Kontakt auswählen',
    },
    step3: {
      recurringStartDate: 'Startdatum ist erforderlich',
      recurringEndDate: 'Enddatum ist erforderlich',
      recurringEndBeforeStart: 'Enddatum muss nach Startdatum liegen',
      shiftDays: 'Mindestens einen Schichttag wählen',
      selectedDates: 'Mindestens ein Datum wählen',
      scheduleStartTime: 'Startzeit ist erforderlich',
      scheduleEndTime: 'Endzeit ist erforderlich',
      scheduleEndBeforeStart: 'Endzeit muss nach Startzeit liegen',
      totalUnpaidBreak: 'Pausendauer auswählen',
      employeesNeeded: 'Anzahl Mitarbeitende erforderlich (mind. 1)',
      wageAmount: 'Lohnbetrag ist erforderlich',
    },
    step4: { selectedLanguages: 'Mindestens eine Sprache hinzufügen' },
    step6: { selectedDressType: 'Bitte Kleiderordnung wählen' },
    step7: { staffSelectionType: 'Bitte Auswahlmethode wählen' },
  },
};

const TR: JobWizardCopy = {
  ...EN,
  shell: {
    ...EN.shell,
    kicker: 'İş ilanı oluştur',
    title: 'İş ilanı oluştur',
    subtitle: 'Adım adım sihirbaz',
    stepLabel: 'Adım',
    exit: 'Çıkış',
    helpTitle: 'Nasıl rezervasyon yapılır?',
    helpBody: 'Kolay adımlarla iş ilanı oluşturmayı öğrenin.',
    watchTutorial: 'Eğitimi izle',
    supportTitle: 'Rezervasyon hakkında sorular?',
    contactSupport: 'Destek ile iletişim',
    chatNow: 'Şimdi sohbet',
    or: 'veya',
  },
  nav: {
    step1: 'İş tanımı',
    step2: 'Konum ve iletişim',
    step3: 'Program',
    step4: 'Gereksinimler, beceriler ve dil',
    step5: 'Sertifikalar ve belgeler',
    step6: 'Kıyafet kuralları',
    step7: 'Personel seçimi',
    step8: 'İnceleme ve onay',
  },
  common: {
    ...EN.common,
    back: 'Geri',
    next: 'İleri',
    edit: 'Düzenle',
    notSet: 'Belirtilmedi',
    none: 'Yok',
    loading: 'Yükleniyor...',
    fixErrorsTitle: 'Devam etmeden önce şunları düzeltin:',
    selectIndustry: 'Sektör seçin',
    selectIndustryFirst: 'Önce sektör seçin',
    selectRole: 'Rol seçin',
    selectContract: 'Sözleşme tipi seçin',
    certificationNeeded: 'Sertifika gerekli',
    add: 'Ekle',
    required: 'Zorunlu',
    custom: 'Özel',
  },
  step1: {
    title: 'İş tanımı',
    subtitle: 'İlanınızın temel bilgilerini girin.',
    jobTitle: 'İş unvanı',
    jobIndustry: 'Sektör',
    jobRole: 'İş rolü',
    jobDescription: 'İş açıklaması',
    contractType: 'Sözleşme tipi',
    titlePlaceholder: 'ör. Satış Danışmanı %80–100',
    descriptionPlaceholder: 'Rolü, sorumlulukları ve gereksinimleri açıklayın...',
  },
  step2: {
    title: 'Konum ve iletişim',
    subtitle: 'Çalışma yeri ve sahada iletişim kişisini belirleyin.',
    selectWorkLocation: 'Çalışma konumu seçin',
    selectLocation: 'Konum seçin',
    selectedLocation: 'Seçilen konum',
    locationNotes: 'Konum notları / yol tarifi',
    locationNotesPlaceholder: 'Yol tarifi veya önemli konum notları...',
    entrance: 'Giriş',
    useBackDoor: 'Arka kapı girişi',
    useFrontDoor: 'Ön kapı girişi',
    meals: 'Yemekler',
    freeBreakfast: 'Ücretsiz kahvaltı',
    freeLunch: 'Ücretsiz öğle yemeği',
    freeDinner: 'Ücretsiz akşam yemeği',
    onsiteContacts: 'Saha iletişim kişileri',
    selectContacts: 'Kişi seçin...',
    selectedContacts: 'seçildi',
    contactsSelected: '{count} kişi seçildi',
    onsiteContact: 'Saha iletişim kişisi',
  },
  step3: {
    ...EN.step3,
    title: 'Program',
    subtitle: 'İşin ne zaman ve ne kadar süreceğini belirleyin.',
    recurringSchedule: 'Tekrarlayan program',
    scheduleName: 'Program adı (isteğe bağlı)',
    scheduleNamePlaceholder: 'ör. Haftalık sabah vardiyası',
    startDate: 'Başlangıç tarihi',
    endDate: 'Bitiş tarihi',
    shiftDays: 'Vardiya günleri',
    dates: 'Tarihler',
    startTime: 'Başlangıç saati',
    endTime: 'Bitiş saati',
    unpaidBreak: 'Ücretsiz mola',
    preferSamePros: 'Mümkünse aynı profesyoneller',
    whenUnavailable: 'Tercih edilenler müsait değilse',
    whenUnavailableDifferent: 'Farklı profesyonel ata',
    whenUnavailableUnfilled: 'Vardiyayı boş bırak',
    employeesNeeded: 'Gerekli çalışan sayısı',
    employeesPlaceholder: 'Kişi sayısı',
    wageAmount: 'Ücret tutarı',
    wagePlaceholder: 'Tutar',
    tipPercent: 'Bahşiş (isteğe bağlı) %',
    tipPercentPlaceholder: 'ör. 10, 15, 20',
    tipFixed: 'Bahşiş (isteğe bağlı) sabit',
    tipFixedPlaceholder: '0',
    days: { sun: 'Paz', mon: 'Pzt', tue: 'Sal', wed: 'Çar', thu: 'Per', fri: 'Cum', sat: 'Cmt' },
    expectedBreaks: 'Beklenen molalar:',
    expectedBreaksNone: 'Yok',
    breakNoBreak: 'Mola yok',
    dateAndTimes: 'Tarih ve saatler',
    addDates: 'Tarih ekle',
    wageHourly: 'Saatlik',
    wageDaily: 'Günlük',
  },
  step4: {
    title: 'Gereksinimler, beceriler ve dil',
    subtitle: 'Gerekli beceri, deneyim ve dilleri belirleyin.',
    requiredSkills: 'Gerekli beceriler',
    requiredSkillsHint: '1. adımda seçtiğiniz role göre önerilir. Kendi becerinizi ekleyebilirsiniz.',
    selectRoleFirst: 'Becerileri görmek için 1. adımda rol seçin.',
    skillsLoadWarning: 'Beceriler sunucudan yüklenemedi. Tekrar deneyin veya elle ekleyin.',
    chooseSkills: 'Beceri seçin (çoklu)',
    skillsSelected: '{count} beceri seçildi',
    searchSkills: 'Beceri ara...',
    noSkillsMatch: 'Aramanızla eşleşen beceri yok.',
    addOwnSkill: 'Kendi becerinizi ekleyin',
    skillNamePlaceholder: 'Beceri adı yazın...',
    languages: 'Diller',
    languagesHint: 'Bu iş için gereken diller.',
    selectLanguage: 'Dil seçin',
    jobDuties: 'İş görevleri',
    jobDutiesPlaceholder: 'Görevleri ve sorumlulukları listeleyin...',
    additionalInstructions: 'Ek talimatlar',
    additionalInstructionsPlaceholder: 'Çalışanlar için ek notlar...',
    expertiseOptions: [
      { value: 'same-job-title', label: 'Aynı unvanla daha önce çalıştı' },
      { value: 'similar-role', label: 'Benzer rolde deneyim' },
      { value: 'formally-trained', label: 'Resmi eğitim aldı' },
      { value: 'some-experience', label: 'Biraz deneyim' },
      { value: 'no-experience', label: 'Deneyim yok / öğrenmeye istekli' },
    ],
    experienceOptions: [
      { value: 'none', label: 'Deneyim yok' },
      { value: '3m', label: '3 ay' },
      { value: '6m', label: '6 ay' },
      { value: '1yr', label: '1 yıl' },
      { value: '2yr', label: '2 yıl' },
      { value: '3yr', label: '3 yıl' },
      { value: '4yr', label: '4 yıl' },
      { value: '5yr', label: '5 yıl' },
      { value: 'custom', label: 'Özel' },
    ],
    skillColumn: 'Beceri',
    expertiseColumn: 'Uzmanlık düzeyi',
    experienceColumn: 'Deneyim',
    months: 'ay',
    years: 'yıl',
    certificationRequired: '(Sertifika gerekli)',
    loadingSkills: 'Beceriler yükleniyor...',
  },
  step5: {
    title: 'Sertifikalar ve belgeler',
    subtitle: 'Gerekli sertifika ve belgeleri seçin.',
    certificates: 'Sertifikalar',
    certificatesHint: 'İşe başlamadan önce gerekebilecek belgeler.',
    noCertificates: 'Sertifika yok',
    documents: 'Gerekli belgeler',
    documentsHint: 'Kimlik, çalışma izni vb.',
    noDocuments: 'Belge yok',
    drivingLicence: 'Ehliyet',
    drivingLicenceHint: 'Yalnızca araç gerekiyorsa.',
    noDrivingLicence: 'Ehliyet gerekmez',
    education: 'Eğitim',
    departments: 'Departmanlar',
    references: 'Referans sayısı',
    howManyReferences: 'Kaç referans',
    chooseCertificates: 'Sertifika seçin (çoklu)',
    chooseDocuments: 'Belge seçin (çoklu)',
    chooseLicence: 'Ehliyet sınıfı seçin (çoklu)',
    chooseEducation: 'Eğitim seviyesi',
    chooseDepartments: 'Departman seçin',
    selectedCount: '{count} seçildi',
    selectRoleHint: 'Sertifikalar için 1. adımda rol seçin.',
    certificateNamePlaceholder: 'Sertifika adı',
    documentNamePlaceholder: 'Belge adı',
    departmentNamePlaceholder: 'Departman adı',
    selectRoleDocsHint: 'Belgeler için 1. adımda rol seçin.',
    customRequired: 'Zorunlu',
    certificatesLoadFail: 'Sertifikalar yüklenemedi. Giriş ve API’yi kontrol edin.',
    noCertificatesForRole: 'Bu rol için sertifika yok. Aşağıdan özel ekleyebilirsiniz.',
    documentsLoadFail: 'Belgeler yüklenemedi. Giriş ve API’yi kontrol edin.',
    noDocumentsForRole: 'Bu rol için belge döndürülmedi.',
  },
  step8: {
    title: 'İnceleme ve onay',
    subtitle: 'Yayınlamadan önce tüm bilgileri kontrol edin.',
    sections: {
      jobDefinition: 'İş tanımı',
      location: 'Konum',
      schedule: 'Program ve pozisyonlar',
      requirements: 'Gereksinimler',
      certificates: 'Sertifikalar ve belgeler',
      dress: 'Kıyafet kuralları',
      staff: 'Personel seçimi',
    },
    labels: {
      title: 'Unvan',
      description: 'Açıklama',
      location: 'Konum',
      address: 'Adres',
      contact: 'İletişim',
      type: 'Tür',
      recurring: 'Tekrarlayan',
      range: 'Aralık',
      days: 'Günler',
      dates: 'Tarihler',
      time: 'Saat',
      break: 'Mola',
      employees: 'Çalışan sayısı',
      wage: 'Ücret',
      skills: 'Beceriler',
      languages: 'Diller',
      selected: '{count} seçildi',
      certRequired: 'sert. gerekli',
    },
    submit: 'İlanı onayla',
    submitting: 'Oluşturuluyor...',
  },
  alerts: {
    submitSuccess: 'İş ilanı başarıyla oluşturuldu!',
    submitFail: 'Oluşturma başarısız. Lütfen tekrar deneyin.',
  },
  validation: {
    step1: {
      title: 'İş unvanı gerekli',
      selectedIndustryId: 'Lütfen sektör seçin',
      selectedRoleId: 'Lütfen iş rolü seçin',
      description: 'İş açıklaması gerekli',
      selectedContractTypeId: 'Lütfen sözleşme tipi seçin',
    },
    step2: {
      selectedAddressId: 'Konum seçin veya ekleyin',
      workLocationName: 'Çalışma yeri adı gerekli',
      workLocationStreet: 'Sokak adresi gerekli',
      selectedContactIds: 'En az bir iletişim kişisi seçin',
    },
    step3: {
      recurringStartDate: 'Başlangıç tarihi gerekli',
      recurringEndDate: 'Bitiş tarihi gerekli',
      recurringEndBeforeStart: 'Bitiş, başlangıçtan sonra olmalı',
      shiftDays: 'En az bir vardiya günü seçin',
      selectedDates: 'En az bir tarih seçin',
      scheduleStartTime: 'Başlangıç saati gerekli',
      scheduleEndTime: 'Bitiş saati gerekli',
      scheduleEndBeforeStart: 'Bitiş saati başlangıçtan sonra olmalı',
      totalUnpaidBreak: 'Mola süresi seçin',
      employeesNeeded: 'Çalışan sayısı gerekli (en az 1)',
      wageAmount: 'Ücret tutarı gerekli',
    },
    step4: { selectedLanguages: 'En az bir dil ekleyin' },
    step6: { selectedDressType: 'Lütfen kıyafet kodu seçin' },
    step7: { staffSelectionType: 'Lütfen personel seçim yöntemi seçin' },
  },
};

export const WIZARD_TEXTS: Record<UiTranslationLanguage, JobWizardCopy> = { en: EN, de: DE, tr: TR };

export function useJobWizardCopy(): JobWizardCopy {
  const { language } = useLanguage();
  const key = resolveTranslationLanguage(language);
  return WIZARD_TEXTS[key] ?? WIZARD_TEXTS.en;
}

export function formatWizard(copy: string, vars: Record<string, string | number>): string {
  return Object.entries(vars).reduce(
    (text, [key, value]) => text.replace(`{${key}}`, String(value)),
    copy
  );
}