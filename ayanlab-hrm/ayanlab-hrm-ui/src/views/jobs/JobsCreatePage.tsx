import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { Input } from "../../ui/components/Input";
import { Button } from "../../ui/components/Button";
import { useTranslations, useLanguage } from "../../state/i18n";
import { TEXTS } from "./i18n";
import { useWorkers, useLocations } from "../../data/hooks";
import { apiSearchAddress, type AddressSuggestion } from "../../api/services/maps";
import { DressModalContent, type DressModalData } from "./DressModalContent";
import { CertificatesDocumentsModalContent } from "./CertificatesDocumentsModalContent";
import { LoadingOverlay } from "../../ui/components/LoadingSpinner";
import { 
  useJobRoles, 
  useSkills, 
  useDressCodes, 
  useContractTypes, 
  useContactTypes, 
  useFreeTimeWindows, 
  useExpertiseLevels, 
  useJobRole, 
  useSkillsByIds, 
  useDressCode,
  useCertificatesByRole,
  useDocumentsByRole,
  useJobTemplates,
  usePredefinedJobTemplates,
  useCreateJobTemplate,
  useJobIndustries,
  useJobRolesByCategory
} from "../../data/jobHooks";

export function JobsCreatePage() {
  const copy = useTranslations(TEXTS);
  const { language } = useLanguage();
  const navigate = useNavigate();
  const ORG_ID = "org-1";
  
  // Form state
  const [formData, setFormData] = useState({
    title: "",
    selectedIndustryId: "",
    selectedRoleId: "",
    description: "",
    location: "",
    address: "",
    workLoad: "",
    selectedContractTypeId: "",
    selectedContactTypeId: "",
    selectedFreeTimeId: "",
    selectedExpertiseId: "",
    selectedDressCodeId: "",
    selectedSkills: [] as string[],
    additionalDressRequirements: "",
    // Language fields
    selectedLanguages: [] as string[],
    // Other instructions
    jobDuties: "",
    otherInstructions: "",
    otherInstructionsFiles: [] as File[],
    // Dress modal data
    selectedDressType: "" as "" | "casual" | "smart-casual" | "business" | "dress-provided" | "custom-dress" | "no-preference",
    selectedDressItems: [] as Array<{ item: string; color: string; id: string }>,
    selectedAttireRestrictions: [] as string[],
    selectedGroomingRestrictions: [] as string[],
    selectedCertificates: [] as string[],
    selectedDocuments: [] as string[],
    selectedTemplateId: "",
    selectedPredefinedTemplateId: "",
    templateName: "",
    saveAsTemplate: false,
    // Work Location fields
    workLocationStreet: "",
    workLocationZipcode: "",
    workLocationName: "",
    parkingType: "", // "free" | "paid" | "none"
    useBackDoor: false,
    hasFreeMeals: false,
    workLocationInstructions: "",
    workLocationPdfs: [] as File[],
    // Build Schedule fields
    recurringSchedule: false,
    scheduleName: "",
    selectedDates: [] as string[], // Array of date strings (YYYY-MM-DD)
    scheduleStartTime: "9:00 AM",
    scheduleEndTime: "5:45 PM",
    totalUnpaidBreak: "30 min",
    preferSamePros: false,
    whenProsUnavailable: "different", // "different" | "unfilled"
    wageType: "hourly", // "hourly" | "daily"
    wageAmount: "",
    employeesNeeded: "",
    // Onsite Contact fields
    onsiteContactId: "",
    onsiteContactName: "",
    onsiteContactPhone: "",
    onsiteContactEmail: "",
    // Selected locations
    selectedLocationIds: [] as string[],
  });
  
  const [selectedClothingItems, setSelectedClothingItems] = useState<Record<string, string>>({});
  const [showCompanyOnAd, setShowCompanyOnAd] = useState(true);
  const [isLocationModalOpen, setIsLocationModalOpen] = useState(false);
  const [isAddContactModalOpen, setIsAddContactModalOpen] = useState(false);
  const [isCalendarOpen, setIsCalendarOpen] = useState(false);
  const [isScheduleModalOpen, setIsScheduleModalOpen] = useState(false);
  const [isWorkLocationModalOpen, setIsWorkLocationModalOpen] = useState(false);
  const [isContactModalOpen, setIsContactModalOpen] = useState(false);
  const [isLanguageModalOpen, setIsLanguageModalOpen] = useState(false);
  const [isExpertiseSkillsModalOpen, setIsExpertiseSkillsModalOpen] = useState(false);
  const [isRequirementsModalOpen, setIsRequirementsModalOpen] = useState(false);
  const [isCertificatesModalOpen, setIsCertificatesModalOpen] = useState(false);
  const [isDressModalOpen, setIsDressModalOpen] = useState(false);
  
  // API queries
  const { data: industries, isLoading: industriesLoading } = useJobIndustries();
  const { data: allJobRoles, isLoading: allRolesLoading } = useJobRoles();
  const { data: jobRolesByCategory, isLoading: categoryRolesLoading } = useJobRolesByCategory(
    formData.selectedIndustryId || null
  );
  
  // Use job roles from category if industry is selected, otherwise use all job roles
  const jobRoles = formData.selectedIndustryId && jobRolesByCategory 
    ? jobRolesByCategory 
    : allJobRoles;
  const rolesLoading = formData.selectedIndustryId 
    ? categoryRolesLoading 
    : allRolesLoading;
  
  const { data: selectedRole } = useJobRole(formData.selectedRoleId);
  const { data: allSkills } = useSkills();
  const { data: roleSkills } = useSkillsByIds(selectedRole?.requiredSkills || []);
  const { data: dressCodes } = useDressCodes();
  const { data: selectedDressCode } = useDressCode(formData.selectedDressCodeId);
  const { data: contractTypes } = useContractTypes();
  const { data: contactTypes } = useContactTypes();
  const { data: freeTimeWindows } = useFreeTimeWindows();
  const { data: expertiseLevels } = useExpertiseLevels();
  const { data: roleCertificates } = useCertificatesByRole(formData.selectedRoleId || null, language);
  const { data: roleDocuments } = useDocumentsByRole(formData.selectedRoleId || null);
  const { data: orgTemplates } = useJobTemplates(ORG_ID);
  const { data: predefinedTemplates } = usePredefinedJobTemplates();
  const createTemplateMutation = useCreateJobTemplate();
  const { data: workers } = useWorkers();
  const { data: locations } = useLocations();
  
  // Reset selected role when industry changes
  useEffect(() => {
    if (formData.selectedIndustryId) {
      setFormData(prev => ({
        ...prev,
        selectedRoleId: "" // Reset role when industry changes
      }));
    }
  }, [formData.selectedIndustryId]);

  // Update skills when role changes
  useEffect(() => {
    if (selectedRole) {
      setFormData(prev => ({
        ...prev,
        selectedSkills: selectedRole.requiredSkills
      }));
    }
  }, [selectedRole]);

  // When role-based certificates/documents load, preselect them
  useEffect(() => {
    if (roleCertificates && Array.isArray(roleCertificates)) {
      setFormData(prev => ({
        ...prev,
        selectedCertificates: roleCertificates.map((c: any) => c.id) || [],
      }));
    }
    if (roleDocuments && Array.isArray(roleDocuments)) {
      setFormData(prev => ({
        ...prev,
        selectedDocuments: roleDocuments.map((d: any) => d.id) || [],
      }));
    }
  }, [roleCertificates, roleDocuments]);
  
  // Reset clothing items when dress code changes
  useEffect(() => {
    setSelectedClothingItems({});
  }, [formData.selectedDressCodeId]);
  
  const handleInputChange = (field: keyof typeof formData, value: string | boolean) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };
  
  const handleSkillsChange = (skillId: string, checked: boolean) => {
    setFormData(prev => ({
      ...prev,
      selectedSkills: checked 
        ? [...prev.selectedSkills, skillId]
        : prev.selectedSkills.filter(id => id !== skillId)
    }));
  };

  const handleCertificateToggle = (id: string, checked: boolean) => {
    setFormData(prev => ({
      ...prev,
      selectedCertificates: checked
        ? [...prev.selectedCertificates, id]
        : prev.selectedCertificates.filter(existingId => existingId !== id),
    }));
  };

  const handleDocumentToggle = (id: string, checked: boolean) => {
    setFormData(prev => ({
      ...prev,
      selectedDocuments: checked
        ? [...prev.selectedDocuments, id]
        : prev.selectedDocuments.filter(existingId => existingId !== id),
    }));
  };
  
  const handleClothingItemToggle = (itemId: string, color: string) => {
    setSelectedClothingItems(prev => {
      const key = `${itemId}-${color}`;
      if (prev[key]) {
        const newItems = { ...prev };
        delete newItems[key];
        return newItems;
      } else {
        return { ...prev, [key]: color };
      }
    });
  };
  
  // Calculate total hours from schedule
  const calculateTotalHours = () => {
    if (formData.selectedDates.length === 0) return 0;
    
    const startTime = parseTime(formData.scheduleStartTime);
    const endTime = parseTime(formData.scheduleEndTime);
    const breakMinutes = parseInt(formData.totalUnpaidBreak.replace(' min', '')) || 0;
    
    const hoursPerDay = (endTime - startTime - breakMinutes / 60);
    return formData.selectedDates.length * hoursPerDay;
  };

  const parseTime = (timeStr: string): number => {
    const [time, period] = timeStr.split(' ');
    const [hours, minutes] = time.split(':').map(Number);
    let hour24 = hours;
    if (period === 'PM' && hours !== 12) hour24 += 12;
    if (period === 'AM' && hours === 12) hour24 = 0;
    return hour24 + minutes / 60;
  };

  // Calculate costs
  const calculateCosts = () => {
    const totalHours = calculateTotalHours();
    const employees = parseInt(formData.employeesNeeded || "0") || 0;
    const wageAmount = parseFloat(formData.wageAmount || "0") || 0;
    
    if (totalHours === 0 || employees === 0 || wageAmount === 0) {
      return {
        totalHours: 0,
        baseCost: 0,
        appRate: 0,
        tax: 0,
        totalCost: 0
      };
    }

    let baseCost = 0;
    if (formData.wageType === "hourly") {
      baseCost = totalHours * wageAmount * employees;
    } else {
      baseCost = formData.selectedDates.length * wageAmount * employees;
    }

    const appRate = baseCost * 0.15; // 15% app rate
    const tax = baseCost * 0.08; // 8% tax (example)
    const totalCost = baseCost + appRate + tax;

    return {
      totalHours: totalHours * employees,
      baseCost,
      appRate,
      tax,
      totalCost
    };
  };

  const costs = calculateCosts();

  const applyTemplateToForm = (template: any) => {
    if (!template) return;
    setFormData(prev => ({
      ...prev,
      title: template.title || prev.title,
      selectedRoleId: template.roleId || prev.selectedRoleId,
      location: template.location || prev.location,
      address: template.address || prev.address,
      workLoad: template.workLoad || prev.workLoad,
      selectedContractTypeId: template.contractTypeId || prev.selectedContractTypeId,
      selectedContactTypeId: template.contactTypeId || prev.selectedContactTypeId,
      selectedDressCodeId: template.dressCodeId || prev.selectedDressCodeId,
      selectedSkills: template.requiredSkillIds || prev.selectedSkills,
      selectedCertificates: template.certificateIds || prev.selectedCertificates,
      selectedDocuments: template.documentIds || prev.selectedDocuments,
    }));
  };
  
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.saveAsTemplate && formData.templateName) {
      createTemplateMutation.mutate({
        orgId: ORG_ID,
        name: formData.templateName,
        description: formData.description,
        title: formData.title,
        roleId: formData.selectedRoleId,
        location: formData.location,
        address: formData.address,
        workLoad: formData.workLoad,
        contractTypeId: formData.selectedContractTypeId,
        contactTypeId: formData.selectedContactTypeId,
        dressCodeId: formData.selectedDressCodeId,
        requiredSkillIds: formData.selectedSkills,
        certificateIds: formData.selectedCertificates,
        documentIds: formData.selectedDocuments,
      });
    }
    // Submit logic here (mock)
    console.log({
      ...formData,
      selectedClothingItems,
      showCompanyOnAd,
      costs,
    });
  };
  
  const isLoading = industriesLoading || (formData.selectedIndustryId ? categoryRolesLoading : allRolesLoading);
  
  return (
    <form onSubmit={handleSubmit} className="space-y-6 relative">
      {isLoading && <LoadingOverlay text="Loading job data..." />}
      {/* Template Selection */}
      <div className="space-y-3">
        <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
          <div className="text-sm font-semibold text-slate-900">
            {copy.templates.sectionTitle}
          </div>
          <label className="flex items-center gap-2 text-xs text-slate-600">
            <input
              type="checkbox"
              className="rounded border-slate-300"
              checked={formData.saveAsTemplate}
              onChange={(e) =>
                setFormData((prev) => ({ ...prev, saveAsTemplate: e.target.checked }))
              }
            />
            {copy.templates.saveAsTemplateLabel}
          </label>
        </div>
        <div className="grid gap-3 md:grid-cols-2">
          <div>
            <div className="text-xs text-slate-500 mb-1">
              {copy.templates.yourTemplatesGroup}
            </div>
            <select
              className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
              value={formData.selectedTemplateId}
              onChange={(e) => {
                const value = e.target.value;
                const template = orgTemplates?.find((t) => t.id === value);
                setFormData((prev) => ({ ...prev, selectedTemplateId: value }));
                if (template) applyTemplateToForm(template);
              }}
            >
              <option value="">{copy.templates.selectPlaceholder}</option>
              {orgTemplates?.map((tpl) => (
                <option key={tpl.id} value={tpl.id}>
                  {tpl.name}
                </option>
              ))}
            </select>
          </div>
          <div>
            <div className="text-xs text-slate-500 mb-1">
              {copy.templates.predefinedTemplatesGroup}
            </div>
            <select
              className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
              value={formData.selectedPredefinedTemplateId}
              onChange={(e) => {
                const value = e.target.value;
                const template = predefinedTemplates?.find((t) => t.id === value);
                setFormData((prev) => ({ ...prev, selectedPredefinedTemplateId: value }));
                if (template) applyTemplateToForm(template);
              }}
            >
              <option value="">{copy.templates.selectPlaceholder}</option>
              {predefinedTemplates?.map((tpl) => (
                <option key={tpl.id} value={tpl.id}>
                  {tpl.name}
                </option>
              ))}
            </select>
          </div>
        </div>
        {formData.saveAsTemplate && (
          <div>
            <div className="text-xs text-slate-500 mb-1">
              {copy.templates.saveAsTemplateLabel}
            </div>
            <Input
              placeholder={copy.templates.templateNamePlaceholder}
              value={formData.templateName}
              onChange={(e) => handleInputChange("templateName", e.target.value)}
            />
          </div>
        )}
      </div>

      <div className="flex items-center justify-between rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3">
        <div>
          <div className="text-xs text-slate-500 uppercase tracking-wide">{copy.hero.title}</div>
          <div className="text-sm font-semibold text-slate-900">{copy.hero.subtitle}</div>
        </div>
        <Button type="submit">{copy.hero.save}</Button>
      </div>

      {/* Job Details - Two Column Layout */}
      <div className="grid gap-6 lg:grid-cols-3">
        {/* Left Column - Job Details */}
        <div className="space-y-4 lg:col-span-2">
          <div className="space-y-3">
            <div>
              <div className="text-xs text-slate-500 mb-1">{copy.basics.titleLabel}</div>
              <Input 
                placeholder={copy.basics.titlePlaceholder}
                value={formData.title}
                onChange={(e) => handleInputChange('title', e.target.value)}
              />
            </div>
            
            <div>
              <div className="text-xs text-slate-500 mb-1">Job Industry</div>
              <select 
                className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
                value={formData.selectedIndustryId}
                onChange={(e) => handleInputChange('selectedIndustryId', e.target.value)}
              >
                <option value="">Select an industry</option>
                {industries?.map((industry) => (
                  <option key={industry.id} value={industry.id}>
                    {industry.name}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <div className="text-xs text-slate-500 mb-1">
                Job Role
                {formData.selectedIndustryId && categoryRolesLoading && (
                  <span className="ml-2 text-slate-400">(Loading...)</span>
                )}
              </div>
              <select 
                className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5 disabled:bg-slate-50 disabled:cursor-not-allowed"
                value={formData.selectedRoleId}
                onChange={(e) => handleInputChange('selectedRoleId', e.target.value)}
                disabled={formData.selectedIndustryId ? categoryRolesLoading : false}
              >
                <option value="">
                  {formData.selectedIndustryId 
                    ? (categoryRolesLoading ? "Loading roles..." : "Select a job role")
                    : "Select an industry first"}
                </option>
                {jobRoles?.map((role) => (
                  <option key={role.id} value={role.id}>
                    {(role as any).name || copy.roles[role.id as keyof typeof copy.roles] || role.id}
                  </option>
                ))}
              </select>
              {formData.selectedIndustryId && jobRolesByCategory && jobRolesByCategory.length === 0 && !categoryRolesLoading && (
                <p className="text-xs text-slate-500 mt-1">No job roles available for this industry</p>
              )}
            </div>

            <div>
              <div className="text-xs text-slate-500 mb-1">Job Description</div>
              <textarea
                className="w-full min-h-[120px] rounded-2xl border border-slate-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
                placeholder="Enter job description"
                value={formData.description}
                onChange={(e) => handleInputChange('description', e.target.value)}
              />
            </div>

            <div>
              <div className="flex items-center justify-between">
                <div className="text-xs text-slate-500 mb-1">{copy.basics.companyLabel}</div>
                <label className="flex items-center gap-2 text-xs text-slate-500">
                  <input 
                    type="checkbox" 
                    className="rounded border-slate-300"
                    checked={showCompanyOnAd}
                    onChange={(e) => setShowCompanyOnAd(e.target.checked)}
                  />
                  Show on advertisement
                </label>
              </div>
              <Input 
                value="Company Name (Read-only)" 
                readOnly 
                className="bg-slate-50"
              />
            </div>
          </div>

          {/* Section Buttons */}
          <div className="space-y-4 pt-6 border-t border-slate-100">
            <div className="text-sm font-semibold text-slate-900 mb-4">Job Configuration</div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
              <Button
                type="button"
                variant="secondary"
                onClick={() => setIsWorkLocationModalOpen(true)}
                className="justify-start h-auto py-3"
              >
                <div className="flex flex-col items-start">
                  <span className="font-medium">Add Location</span>
                  <span className="text-xs text-slate-500 mt-1">
                    {formData.selectedLocationIds.length > 0 
                      ? `${formData.selectedLocationIds.length} location(s) added`
                      : 'No locations added'}
                  </span>
                </div>
              </Button>

              <Button
                type="button"
                variant="secondary"
                onClick={() => setIsContactModalOpen(true)}
                className="justify-start h-auto py-3"
              >
                <div className="flex flex-col items-start">
                  <span className="font-medium">Contact Person</span>
                  <span className="text-xs text-slate-500 mt-1">
                    {formData.onsiteContactName 
                      ? `${formData.onsiteContactName}${formData.onsiteContactPhone ? ` - ${formData.onsiteContactPhone}` : formData.onsiteContactEmail ? ` - ${formData.onsiteContactEmail}` : ''}`
                      : formData.onsiteContactId 
                        ? 'Contact selected'
                        : 'No contact selected'}
                  </span>
                </div>
              </Button>

              <Button
                type="button"
                variant="secondary"
                onClick={() => setIsScheduleModalOpen(true)}
                className="justify-start h-auto py-3"
              >
                <div className="flex flex-col items-start">
                  <span className="font-medium">Build Schedule</span>
                  <span className="text-xs text-slate-500 mt-1">
                    {formData.selectedDates.length > 0 
                      ? `${formData.selectedDates.length} date(s) selected`
                      : 'No schedule defined'}
                  </span>
                </div>
              </Button>

              <Button
                type="button"
                variant="secondary"
                onClick={() => setIsLanguageModalOpen(true)}
                className="justify-start h-auto py-3"
              >
                <div className="flex flex-col items-start">
                  <span className="font-medium">Add Language</span>
                  <span className="text-xs text-slate-500 mt-1">
                    {formData.selectedLanguages.length > 0 
                      ? `${formData.selectedLanguages.length} language(s)`
                      : 'No languages selected'}
                  </span>
                </div>
              </Button>

              <Button
                type="button"
                variant="secondary"
                onClick={() => setIsExpertiseSkillsModalOpen(true)}
                className="justify-start h-auto py-3"
              >
                <div className="flex flex-col items-start">
                  <span className="font-medium">Add Experience & Skills</span>
                  <span className="text-xs text-slate-500 mt-1">
                    {formData.selectedSkills.length > 0 
                      ? `${formData.selectedSkills.length} skill(s) selected`
                      : 'No skills selected'}
                  </span>
                </div>
              </Button>

              <Button
                type="button"
                variant="secondary"
                onClick={() => setIsRequirementsModalOpen(true)}
                className="justify-start h-auto py-3"
              >
                <div className="flex flex-col items-start">
                  <span className="font-medium">Requirements</span>
                  <span className="text-xs text-slate-500 mt-1">Job duties & instructions</span>
                </div>
              </Button>

              <Button
                type="button"
                variant="secondary"
                onClick={() => setIsCertificatesModalOpen(true)}
                className="justify-start h-auto py-3"
              >
                <div className="flex flex-col items-start">
                  <span className="font-medium">Certificates & Documents</span>
                  <span className="text-xs text-slate-500 mt-1">
                    {formData.selectedCertificates.length + formData.selectedDocuments.length} selected
                  </span>
                </div>
              </Button>

              <Button
                type="button"
                variant="secondary"
                onClick={() => setIsDressModalOpen(true)}
                className="justify-start h-auto py-3"
              >
                <div className="flex flex-col items-start">
                  <span className="font-medium">Add Dress Code & Details</span>
                  <span className="text-xs text-slate-500 mt-1">
                    {formData.selectedDressType 
                      ? `${formData.selectedDressType.replace(/-/g, ' ').replace(/\b\w/g, l => l.toUpperCase())}${formData.selectedDressItems.length > 0 ? `, ${formData.selectedDressItems.length} items` : ''}${formData.selectedAttireRestrictions.length > 0 ? `, ${formData.selectedAttireRestrictions.length} restrictions` : ''}`
                      : "No dress code selected"}
                  </span>
                </div>
              </Button>
            </div>
          </div>

            <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
              <div>
                <div className="text-xs text-slate-500 mb-1">Work Load</div>
                <Input 
                  placeholder="e.g., Full-time, Part-time"
                  value={formData.workLoad}
                  onChange={(e) => handleInputChange('workLoad', e.target.value)}
                />
              </div>
              <div>
                <div className="text-xs text-slate-500 mb-1">Contact Type</div>
                <select 
                  className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
                  value={formData.selectedContactTypeId}
                  onChange={(e) => handleInputChange('selectedContactTypeId', e.target.value)}
                >
                  <option value="">Select contact type</option>
                  {contactTypes?.map((type) => (
                    <option key={type.id} value={type.id}>
                      {copy.contact[type.id as keyof typeof copy.contact]}
                    </option>
                  ))}
                </select>
                {formData.selectedContactTypeId && (
                  <div className="mt-2 rounded-xl bg-slate-50 p-2 text-[11px] text-slate-600 space-y-1">
                    <div className="font-semibold">
                      {copy.contact.definition?.title}
                    </div>
                    <div>
                      <span className="font-medium">Tenant: </span>
                      {copy.contact.definition?.tenant}
                    </div>
                    <div>
                      <span className="font-medium">Worker: </span>
                      {copy.contact.definition?.worker}
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>

      {/* Calendar Modal */}
      {isCalendarOpen && (
        <Modal
          title="Select Dates"
          onClose={() => setIsCalendarOpen(false)}
        >
          <CalendarComponent
            selectedDates={formData.selectedDates}
            onDatesChange={(dates) => {
              setFormData(prev => ({ ...prev, selectedDates: dates }));
            }}
            onClose={() => setIsCalendarOpen(false)}
          />
        </Modal>
      )}

      {/* Add Contact Modal */}
      {isAddContactModalOpen && (
        <Modal
          title="Add New Contact"
          onClose={() => {
            setIsAddContactModalOpen(false);
            setIsContactModalOpen(true); // Reopen Contact Modal when closing Add Contact
          }}
        >
          <AddContactForm
            onSave={(contact) => {
              // Store contact information in formData
              setFormData(prev => ({
                ...prev,
                onsiteContactName: contact.name,
                onsiteContactPhone: contact.phone,
                onsiteContactEmail: contact.email,
                // Generate a temporary ID for the new contact
                onsiteContactId: `new-${Date.now()}`,
              }));
              // Close Add Contact modal and reopen Contact Modal
              setIsAddContactModalOpen(false);
              setIsContactModalOpen(true);
            }}
            onCancel={() => {
              setIsAddContactModalOpen(false);
              setIsContactModalOpen(true); // Reopen Contact Modal when canceling
            }}
          />
        </Modal>
      )}

      {/* Work Location Modal */}
      {isWorkLocationModalOpen && (
        <Modal
          title="Add Street Address"
          onClose={() => setIsWorkLocationModalOpen(false)}
          size="large"
        >
          <WorkLocationModalContent
            formData={formData}
            handleInputChange={handleInputChange}
            setFormData={setFormData}
            onClose={() => setIsWorkLocationModalOpen(false)}
          />
        </Modal>
      )}

      {/* Schedule Modal */}
      {isScheduleModalOpen && (
        <Modal
          title="Build your schedule"
          onClose={() => setIsScheduleModalOpen(false)}
          size="large"
        >
          <ScheduleModalContent
            formData={formData}
            handleInputChange={handleInputChange}
            setFormData={setFormData}
            setIsCalendarOpen={setIsCalendarOpen}
            costs={costs}
            onClose={() => setIsScheduleModalOpen(false)}
          />
        </Modal>
      )}

      {/* Contact Modal */}
      {isContactModalOpen && (
        <Modal
          title="Onsite Contact"
          onClose={() => setIsContactModalOpen(false)}
        >
          <ContactModalContent
            formData={formData}
            handleInputChange={handleInputChange}
            workers={workers}
            setIsAddContactModalOpen={setIsAddContactModalOpen}
            setIsContactModalOpen={setIsContactModalOpen}
            onClose={() => setIsContactModalOpen(false)}
          />
        </Modal>
      )}

      {/* Language Modal */}
      {isLanguageModalOpen && (
        <Modal
          title="Add Language"
          onClose={() => setIsLanguageModalOpen(false)}
        >
          <LanguageModalContent
            formData={formData}
            setFormData={setFormData}
            onClose={() => setIsLanguageModalOpen(false)}
          />
        </Modal>
      )}

      {/* Expertise & Skills Modal */}
      {isExpertiseSkillsModalOpen && (
        <Modal
          title="Add Experience & Skills"
          onClose={() => setIsExpertiseSkillsModalOpen(false)}
          size="large"
        >
          <ExpertiseSkillsModalContent
            formData={formData}
            handleInputChange={handleInputChange}
            handleSkillsChange={handleSkillsChange}
            allSkills={allSkills}
            expertiseLevels={expertiseLevels}
            copy={copy}
            onClose={() => setIsExpertiseSkillsModalOpen(false)}
          />
        </Modal>
      )}

      {/* Requirements Modal */}
      {isRequirementsModalOpen && (
        <Modal
          title="Requirements"
          onClose={() => setIsRequirementsModalOpen(false)}
          size="large"
        >
          <RequirementsModalContent
            formData={formData}
            handleInputChange={handleInputChange}
            setFormData={setFormData}
            onClose={() => setIsRequirementsModalOpen(false)}
          />
        </Modal>
      )}

      {/* Certificates & Documents Modal */}
      {isCertificatesModalOpen && (
        <Modal
          title="Certificates & Documents"
          onClose={() => setIsCertificatesModalOpen(false)}
          size="large"
        >
          <CertificatesDocumentsModalContent
            data={{
              selectedCertificates: formData.selectedCertificates,
              selectedDocuments: formData.selectedDocuments,
            }}
            certificates={roleCertificates as any}
            documents={roleDocuments as any}
            onDataChange={(newData) => {
              setFormData(prev => ({
                ...prev,
                selectedCertificates: newData.selectedCertificates,
                selectedDocuments: newData.selectedDocuments,
              }));
            }}
            onClose={() => setIsCertificatesModalOpen(false)}
            isLoading={!roleCertificates && !!formData.selectedRoleId}
          />
        </Modal>
      )}

      {/* Dress Code Modal */}
      {isDressModalOpen && (
        <Modal
          title="Add Dress Code & Details"
          onClose={() => setIsDressModalOpen(false)}
          size="large"
        >
          <DressModalContent
            data={{
              selectedDressType: formData.selectedDressType as any,
              selectedDressItems: formData.selectedDressItems,
              selectedAttireRestrictions: formData.selectedAttireRestrictions,
              selectedGroomingRestrictions: formData.selectedGroomingRestrictions,
            }}
            onDataChange={(newData) => {
              setFormData(prev => ({
                ...prev,
                selectedDressType: newData.selectedDressType,
                selectedDressItems: newData.selectedDressItems,
                selectedAttireRestrictions: newData.selectedAttireRestrictions,
                selectedGroomingRestrictions: newData.selectedGroomingRestrictions,
              }));
            }}
            onClose={() => setIsDressModalOpen(false)}
          />
        </Modal>
      )}
    </form>
  );
}

// Reusable Modal Component
function Modal({ title, children, onClose, size = "medium" }: { 
  title: string; 
  children: React.ReactNode; 
  onClose: () => void;
  size?: "small" | "medium" | "large";
}) {
  const sizeClasses = {
    small: "max-w-md",
    medium: "max-w-2xl",
    large: "max-w-4xl"
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4" onClick={onClose}>
      <div 
        className={`bg-white rounded-lg ${sizeClasses[size]} w-full max-h-[90vh] overflow-y-auto`}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="sticky top-0 bg-white border-b border-slate-200 px-6 py-4 flex items-center justify-between z-10">
          <h3 className="text-lg font-semibold">{title}</h3>
          <button
            type="button"
            onClick={onClose}
            className="text-slate-400 hover:text-slate-600 text-2xl leading-none"
          >
            ×
          </button>
        </div>
        <div className="p-6">
          {children}
        </div>
      </div>
    </div>
  );
}

// Calendar Component
function CalendarComponent({ selectedDates, onDatesChange, onClose }: { selectedDates: string[]; onDatesChange: (dates: string[]) => void; onClose: () => void }) {
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [tempSelected, setTempSelected] = useState<string[]>(selectedDates);

  useEffect(() => {
    setTempSelected(selectedDates);
  }, [selectedDates]);

  const monthStart = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), 1);
  const monthEnd = new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 0);
  const startDate = new Date(monthStart);
  startDate.setDate(startDate.getDate() - startDate.getDay());

  const days: Date[] = [];
  for (let i = 0; i < 42; i++) {
    const date = new Date(startDate);
    date.setDate(startDate.getDate() + i);
    days.push(date);
  }

  const formatDate = (date: Date) => {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  };

  const toggleDate = (date: Date) => {
    const dateStr = formatDate(date);
    setTempSelected(prev =>
      prev.includes(dateStr)
        ? prev.filter(d => d !== dateStr)
        : [...prev, dateStr]
    );
  };

  const handleDone = () => {
    onDatesChange(tempSelected);
  };

  const isSelected = (date: Date) => {
    return tempSelected.includes(formatDate(date));
  };

  const isCurrentMonth = (date: Date) => {
    return date.getMonth() === currentMonth.getMonth();
  };

  const prevMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1, 1));
  };

  const nextMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 1));
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <button type="button" onClick={prevMonth} className="p-1 hover:bg-slate-100 rounded">
          ←
        </button>
        <div className="font-semibold">
          {currentMonth.toLocaleDateString('en-US', { month: 'long', year: 'numeric' })}
        </div>
        <button type="button" onClick={nextMonth} className="p-1 hover:bg-slate-100 rounded">
          →
        </button>
      </div>
      <div className="grid grid-cols-7 gap-1 mb-2">
        {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map(day => (
          <div key={day} className="text-center text-xs text-slate-500 font-medium py-1">
            {day}
          </div>
        ))}
      </div>
      <div className="grid grid-cols-7 gap-1">
        {days.map((date, idx) => (
          <button
            key={idx}
            type="button"
            onClick={() => toggleDate(date)}
            className={`
              aspect-square text-sm rounded
              ${isCurrentMonth(date) ? 'text-slate-900' : 'text-slate-300'}
              ${isSelected(date) ? 'bg-blue-600 text-white' : 'hover:bg-slate-100'}
            `}
          >
            {date.getDate()}
          </button>
        ))}
      </div>
      <div className="mt-4 text-sm text-slate-600">
        {tempSelected.length} day{tempSelected.length !== 1 ? 's' : ''} selected
      </div>
      <div className="mt-4 flex justify-end">
        <Button
          type="button"
          onClick={handleDone}
        >
          Done
        </Button>
      </div>
    </div>
  );
}

// Add Contact Form Component
function AddContactForm({ onSave, onCancel }: { onSave: (contact: any) => void; onCancel: () => void }) {
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');

  return (
    <div className="space-y-4">
      <div>
        <label className="text-xs text-slate-500 mb-1 block">Name*</label>
        <Input
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="Contact name"
        />
      </div>
      <div>
        <label className="text-xs text-slate-500 mb-1 block">Phone</label>
        <Input
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
          placeholder="Phone number"
        />
      </div>
      <div>
        <label className="text-xs text-slate-500 mb-1 block">Email</label>
        <Input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="Email address"
        />
      </div>
      <div className="flex justify-end gap-2 pt-4">
        <Button type="button" variant="secondary" onClick={onCancel}>
          Cancel
        </Button>
        <Button
          type="button"
          onClick={() => onSave({ name, phone, email })}
          disabled={!name}
        >
          Save
        </Button>
      </div>
    </div>
  );
}

// Modal Content Components - These will contain the section content
// Placeholder implementations - full content from removed inline sections should be moved here

function WorkLocationModalContent({ formData, handleInputChange, setFormData, onClose }: any) {
  const [addressSearchQuery, setAddressSearchQuery] = useState(formData.workLocationStreet || "");
  const [debouncedQuery, setDebouncedQuery] = useState("");
  const [addressSuggestions, setAddressSuggestions] = useState<AddressSuggestion[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const searchRef = useRef<HTMLDivElement>(null);

  // Sync addressSearchQuery with formData when formData changes externally
  useEffect(() => {
    if (formData.workLocationStreet && formData.workLocationStreet !== addressSearchQuery) {
      setAddressSearchQuery(formData.workLocationStreet);
    }
  }, [formData.workLocationStreet]);

  // Debounce the search query
  useEffect(() => {
    if (addressSearchQuery.length < 5) {
      setDebouncedQuery("");
      setAddressSuggestions([]);
      return;
    }
    const handle = setTimeout(() => setDebouncedQuery(addressSearchQuery), 1000);
    return () => clearTimeout(handle);
  }, [addressSearchQuery]);

  // Search addresses when debounced query changes
  useEffect(() => {
    if (!debouncedQuery || debouncedQuery.length < 5) {
      setAddressSuggestions([]);
      setIsSearching(false);
      return;
    }

    setIsSearching(true);
    apiSearchAddress(debouncedQuery)
      .then((results) => {
        setAddressSuggestions(results);
        setIsSearching(false);
        setShowSuggestions(true);
      })
      .catch((error) => {
        console.error("Error searching addresses:", error);
        setAddressSuggestions([]);
        setIsSearching(false);
      });
  }, [debouncedQuery]);

  // Close suggestions when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (searchRef.current && !searchRef.current.contains(event.target as Node)) {
        setShowSuggestions(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleAddressSelect = (suggestion: AddressSuggestion) => {
    setAddressSearchQuery(suggestion.formattedAddress);
    handleInputChange('workLocationStreet', suggestion.formattedAddress);
    // Extract zipcode from formatted address if possible
    const zipMatch = suggestion.formattedAddress.match(/\b\d{5}(-\d{4})?\b/);
    if (zipMatch) {
      handleInputChange('workLocationZipcode', zipMatch[0]);
    }
    setAddressSuggestions([]);
    setShowSuggestions(false);
  };

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
        <div className="relative" ref={searchRef}>
          <div className="text-xs text-slate-500 mb-1">Street address*</div>
          <Input 
            placeholder="Address"
            value={addressSearchQuery}
            onChange={(e) => {
              setAddressSearchQuery(e.target.value);
              handleInputChange('workLocationStreet', e.target.value);
              if (e.target.value.length >= 5) {
                setShowSuggestions(true);
              } else {
                setShowSuggestions(false);
                setAddressSuggestions([]);
              }
            }}
            onFocus={() => {
              if (addressSuggestions.length > 0) {
                setShowSuggestions(true);
              }
            }}
          />
          {isSearching && (
            <div className="absolute right-3 top-[2.25rem] text-xs text-slate-400 flex items-center gap-1">
              <svg className="animate-spin h-3 w-3" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              Searching...
            </div>
          )}
          {showSuggestions && addressSuggestions.length > 0 && (
            <div className="absolute z-10 w-full mt-1 bg-white border border-slate-200 rounded-lg shadow-lg max-h-60 overflow-y-auto">
              {addressSuggestions.map((suggestion, index) => (
                <button
                  key={suggestion.placeId || index}
                  type="button"
                  onClick={() => handleAddressSelect(suggestion)}
                  className="w-full text-left px-4 py-3 hover:bg-slate-50 border-b border-slate-100 last:border-b-0 transition-colors"
                >
                  <div className="font-medium text-sm text-slate-900">{suggestion.name}</div>
                  <div className="text-xs text-slate-500 mt-0.5">{suggestion.formattedAddress}</div>
                </button>
              ))}
            </div>
          )}
        </div>
        <div>
          <div className="text-xs text-slate-500 mb-1">Zipcode*</div>
          <Input 
            placeholder="Zip code"
            value={formData.workLocationZipcode}
            onChange={(e) => handleInputChange('workLocationZipcode', e.target.value)}
          />
        </div>
      </div>
      <div>
        <div className="text-xs text-slate-500 mb-1">Work location name*</div>
        <Input 
          placeholder="Location Name"
          value={formData.workLocationName}
          onChange={(e) => handleInputChange('workLocationName', e.target.value)}
        />
      </div>
      <div>
        <div className="text-xs text-slate-500 mb-1">Parking*</div>
        <div className="flex gap-3">
          {['free', 'paid', 'none'].map((type) => (
            <label key={type} className="flex items-center gap-2">
              <input
                type="radio"
                name="parking"
                value={type}
                checked={formData.parkingType === type}
                onChange={(e) => handleInputChange('parkingType', e.target.value)}
                className="text-slate-700 focus:ring-slate-500"
              />
              <span className="text-sm text-slate-700 capitalize">
                {type === 'free' ? 'Free parking' : type === 'paid' ? 'Paid parking' : 'No parking'}
              </span>
            </label>
          ))}
        </div>
      </div>
      <div>
        <div className="text-xs text-slate-500 mb-1">Specifics</div>
        <div className="flex gap-4">
          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              checked={formData.useBackDoor}
              onChange={(e) => handleInputChange('useBackDoor', e.target.checked)}
              className="rounded border-slate-300"
            />
            <span className="text-sm text-slate-700">Use back door</span>
          </label>
          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              checked={formData.hasFreeMeals}
              onChange={(e) => handleInputChange('hasFreeMeals', e.target.checked)}
              className="rounded border-slate-300"
            />
            <span className="text-sm text-slate-700">Has free meals</span>
          </label>
        </div>
      </div>
      <div>
        <div className="text-xs text-slate-500 mb-1">Instructions</div>
        <p className="text-xs text-slate-600 mb-2">
          Add details to help workers locate and prepare for your work location.
        </p>
        <div className="border border-slate-200 rounded-lg">
          <div className="flex items-center gap-2 p-2 border-b border-slate-200">
            <button type="button" className="p-1 hover:bg-slate-100 rounded" title="Bold">
              <span className="text-sm font-bold">B</span>
            </button>
            <button type="button" className="p-1 hover:bg-slate-100 rounded" title="Italic">
              <span className="text-sm italic">I</span>
            </button>
            <button type="button" className="p-1 hover:bg-slate-100 rounded text-xs" title="Add link">
              Add link
            </button>
          </div>
          <textarea
            className="w-full min-h-[120px] px-3 py-2 text-sm focus:outline-none"
            placeholder="Add instructions..."
            value={formData.workLocationInstructions}
            onChange={(e) => handleInputChange('workLocationInstructions', e.target.value)}
          />
        </div>
      </div>
      <div>
        <div className="flex items-center gap-2 mb-1">
          <div className="text-xs text-slate-500">PDF Files</div>
          <span className="text-[10px] bg-blue-100 text-blue-700 px-1.5 py-0.5 rounded">NEW</span>
        </div>
        <p className="text-xs text-slate-600 mb-2">
          Please upload files for Pros review.
        </p>
        <div
          className="border-2 border-dashed border-slate-300 rounded-lg p-6 text-center cursor-pointer hover:border-slate-400 transition-colors"
          onClick={() => {
            const input = document.createElement('input');
            input.type = 'file';
            input.accept = 'application/pdf';
            input.multiple = true;
            input.onchange = (e: any) => {
              const files = Array.from(e.target.files).filter((f: any) => f.type === 'application/pdf');
              if (files.length > 0) {
                setFormData((prev: any) => ({
                  ...prev,
                  workLocationPdfs: [...prev.workLocationPdfs, ...files as File[]]
                }));
              }
            };
            input.click();
          }}
        >
          <p className="text-sm text-slate-600">Drag & drop, or click to select files</p>
        </div>
        {formData.workLocationPdfs.length > 0 && (
          <div className="mt-2 space-y-1">
            {formData.workLocationPdfs.map((file: File, idx: number) => (
              <div key={idx} className="flex items-center justify-between text-xs text-slate-600 bg-slate-50 px-2 py-1 rounded">
                <span>{file.name}</span>
                <button
                  type="button"
                  onClick={() => {
                    setFormData((prev: any) => ({
                      ...prev,
                      workLocationPdfs: prev.workLocationPdfs.filter((_: any, i: number) => i !== idx)
                    }));
                  }}
                  className="text-slate-400 hover:text-slate-600"
                >
                  ×
                </button>
              </div>
            ))}
          </div>
        )}
      </div>
      <div className="flex justify-end gap-2 pt-4 border-t">
        <Button type="button" variant="secondary" onClick={onClose}>
          Back
        </Button>
        <Button type="button" onClick={onClose}>
          Next
        </Button>
      </div>
    </div>
  );
}

function ScheduleModalContent({ formData, handleInputChange, setFormData, setIsCalendarOpen, costs, onClose }: any) {
  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <label className="flex items-center gap-2">
          <span className="text-xs text-slate-600">Recurring schedule</span>
          <input
            type="checkbox"
            checked={formData.recurringSchedule}
            onChange={(e) => handleInputChange('recurringSchedule', e.target.checked)}
            className="rounded border-slate-300"
          />
        </label>
      </div>
      <div>
        <div className="text-xs text-slate-500 mb-1">Date & Times</div>
        <Input
          placeholder="Schedule name (optional)"
          value={formData.scheduleName}
          onChange={(e) => handleInputChange('scheduleName', e.target.value)}
          className="mb-3"
        />
        <div className="mb-3">
          <div className="text-xs text-slate-500 mb-1">Dates</div>
          <div className="flex flex-wrap gap-2 mb-2">
            {formData.selectedDates.map((date: string) => {
              const d = new Date(date);
              const dayNames = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
              return (
                <div key={date} className="flex items-center gap-1 rounded-full bg-slate-100 px-3 py-1 text-sm">
                  <span>{d.getDate()} {d.toLocaleDateString('en-US', { month: 'short' })} {dayNames[d.getDay()]}</span>
                  <button
                    type="button"
                    onClick={() => {
                      setFormData((prev: any) => ({
                        ...prev,
                        selectedDates: prev.selectedDates.filter((d: string) => d !== date)
                      }));
                    }}
                    className="text-slate-500 hover:text-slate-700"
                  >
                    ×
                  </button>
                </div>
              );
            })}
          </div>
          <Button
            type="button"
            variant="secondary"
            onClick={() => setIsCalendarOpen(true)}
          >
            + Add dates
          </Button>
        </div>
      </div>
      <div className="grid grid-cols-3 gap-3">
        <div>
          <div className="text-xs text-slate-500 mb-1">Start time</div>
          <select
            className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
            value={formData.scheduleStartTime}
            onChange={(e) => handleInputChange('scheduleStartTime', e.target.value)}
          >
            {Array.from({ length: 24 * 2 }, (_, i) => {
              const hour = Math.floor(i / 2);
              const minute = i % 2 === 0 ? '00' : '30';
              const time12 = hour === 0 ? '12' : hour > 12 ? String(hour - 12) : String(hour);
              const ampm = hour >= 12 ? 'PM' : 'AM';
              const value = `${time12}:${minute} ${ampm}`;
              return <option key={i} value={value}>{value}</option>;
            })}
          </select>
        </div>
        <div>
          <div className="text-xs text-slate-500 mb-1">End time</div>
          <select
            className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
            value={formData.scheduleEndTime}
            onChange={(e) => handleInputChange('scheduleEndTime', e.target.value)}
          >
            {Array.from({ length: 24 * 2 }, (_, i) => {
              const hour = Math.floor(i / 2);
              const minute = i % 2 === 0 ? '00' : '30';
              const time12 = hour === 0 ? '12' : hour > 12 ? String(hour - 12) : String(hour);
              const ampm = hour >= 12 ? 'PM' : 'AM';
              const value = `${time12}:${minute} ${ampm}`;
              return <option key={i} value={value}>{value}</option>;
            })}
          </select>
        </div>
        <div>
          <div className="text-xs text-slate-500 mb-1">Total unpaid break</div>
          <select
            className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
            value={formData.totalUnpaidBreak}
            onChange={(e) => handleInputChange('totalUnpaidBreak', e.target.value)}
          >
            {[0, 15, 30, 45, 60, 90, 120].map((min) => (
              <option key={min} value={`${min} min`}>{min} min</option>
            ))}
          </select>
        </div>
      </div>
      <div className="grid grid-cols-2 gap-3">
        <div>
          <div className="text-xs text-slate-500 mb-1">Wage Type*</div>
          <select
            className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
            value={formData.wageType}
            onChange={(e) => handleInputChange('wageType', e.target.value)}
          >
            <option value="hourly">Hourly</option>
            <option value="daily">Daily</option>
          </select>
        </div>
        <div>
          <div className="text-xs text-slate-500 mb-1">Wage Amount*</div>
          <div className="relative">
            <Input
              type="number"
              placeholder={formData.wageType === "hourly" ? "e.g. 30" : "e.g. 240"}
              value={formData.wageAmount}
              onChange={(e) => handleInputChange('wageAmount', e.target.value)}
              className="pl-7"
            />
            <span className="absolute left-3 top-2.5 text-sm text-slate-500">$</span>
          </div>
        </div>
      </div>
      <div>
        <div className="text-xs text-slate-500 mb-1">How many employees needed*</div>
        <Input
          type="number"
          min="1"
          placeholder="Enter number of employees"
          value={formData.employeesNeeded}
          onChange={(e) => handleInputChange('employeesNeeded', e.target.value)}
        />
      </div>
      {formData.selectedDates.length > 0 && formData.wageAmount && formData.employeesNeeded && (
        <div className="bg-slate-50 rounded-lg p-4 space-y-2">
          <div className="text-sm font-semibold text-slate-900 mb-3">Cost Summary</div>
          <div className="grid grid-cols-2 gap-2 text-sm">
            <div className="text-slate-600">Total Hours:</div>
            <div className="text-slate-900 font-medium">{costs.totalHours.toFixed(2)} hrs</div>
            <div className="text-slate-600">Base Cost:</div>
            <div className="text-slate-900 font-medium">${costs.baseCost.toFixed(2)}</div>
            <div className="text-slate-600">App Rate (15%):</div>
            <div className="text-slate-900 font-medium">${costs.appRate.toFixed(2)}</div>
            <div className="text-slate-600">Tax (8%):</div>
            <div className="text-slate-900 font-medium">${costs.tax.toFixed(2)}</div>
            <div className="text-slate-900 font-semibold border-t border-slate-200 pt-2">Total Cost:</div>
            <div className="text-slate-900 font-semibold border-t border-slate-200 pt-2">${costs.totalCost.toFixed(2)}</div>
          </div>
        </div>
      )}
      <label className="flex items-center gap-2">
        <input
          type="checkbox"
          checked={formData.preferSamePros}
          onChange={(e) => handleInputChange('preferSamePros', e.target.checked)}
          className="rounded border-slate-300"
        />
        <span className="text-sm text-slate-700">I prefer the same Pros for all dates</span>
      </label>
      {formData.preferSamePros && (
        <div>
          <div className="text-sm font-semibold text-slate-900 mb-2">When same Pros are unavailable for all shifts</div>
          <div className="space-y-2">
            <label className="flex items-center gap-2">
              <input
                type="radio"
                name="whenProsUnavailable"
                value="different"
                checked={formData.whenProsUnavailable === 'different'}
                onChange={(e) => handleInputChange('whenProsUnavailable', e.target.value)}
                className="text-slate-700 focus:ring-slate-500"
              />
              <span className="text-sm text-slate-700">Let different Pros book the remaining shifts</span>
            </label>
            <label className="flex items-center gap-2">
              <input
                type="radio"
                name="whenProsUnavailable"
                value="unfilled"
                checked={formData.whenProsUnavailable === 'unfilled'}
                onChange={(e) => handleInputChange('whenProsUnavailable', e.target.value)}
                className="text-slate-700 focus:ring-slate-500"
              />
              <span className="text-sm text-slate-700">Let the shifts go unfilled</span>
            </label>
          </div>
        </div>
      )}
      <div className="flex justify-end gap-2 pt-4 border-t">
        <Button type="button" variant="secondary" onClick={onClose}>
          Back
        </Button>
        <Button type="button" onClick={onClose}>
          Next
        </Button>
      </div>
    </div>
  );
}

function ContactModalContent({ formData, handleInputChange, workers, setIsAddContactModalOpen, setIsContactModalOpen, onClose }: any) {
  const handleAddNewContact = () => {
    setIsContactModalOpen(false); // Close Contact Modal
    setIsAddContactModalOpen(true); // Open Add Contact Modal
  };

  return (
    <div className="space-y-4">
      <div>
        <div className="text-xs text-slate-500 mb-1">Onsite Contact</div>
        <div className="relative">
          <select
            className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5 pr-10"
            value={formData.onsiteContactId}
            onChange={(e) => {
              handleInputChange('onsiteContactId', e.target.value);
              // If selecting an existing worker, update contact info
              const selectedWorker = workers?.find((w: any) => w.id === e.target.value);
              if (selectedWorker) {
                handleInputChange('onsiteContactName', selectedWorker.displayName || '');
                handleInputChange('onsiteContactPhone', selectedWorker.phone || '');
                handleInputChange('onsiteContactEmail', selectedWorker.email || '');
              }
            }}
          >
            <option value="">Select a contact</option>
            {workers?.map((worker: any) => (
              <option key={worker.id} value={worker.id}>
                {worker.displayName} - {worker.phone || worker.email || ''}
              </option>
            ))}
            {/* Show newly added contact if exists */}
            {formData.onsiteContactName && formData.onsiteContactId?.startsWith('new-') && (
              <option value={formData.onsiteContactId}>
                {formData.onsiteContactName} - {formData.onsiteContactPhone || formData.onsiteContactEmail || ''}
              </option>
            )}
          </select>
          <button
            type="button"
            onClick={handleAddNewContact}
            className="absolute right-2 top-1/2 -translate-y-1/2 h-8 w-8 rounded-full bg-slate-100 hover:bg-slate-200 flex items-center justify-center text-slate-600"
          >
            +
          </button>
        </div>
        <button
          type="button"
          onClick={handleAddNewContact}
          className="mt-2 text-xs text-slate-600 hover:text-slate-900 flex items-center gap-1"
        >
          <span>+</span> Add new contact
        </button>
      </div>
      <div className="flex justify-end gap-2 pt-4 border-t">
        <Button type="button" variant="secondary" onClick={onClose}>
          Back
        </Button>
        <Button type="button" onClick={onClose}>
          Save
        </Button>
      </div>
    </div>
  );
}

function LanguageModalContent({ formData, setFormData, onClose }: any) {
  const languages = ['English', 'Spanish', 'French', 'German', 'Italian', 'Portuguese'];
  
  return (
    <div className="space-y-4">
      <div>
        <div className="text-xs text-slate-500 mb-2">Select Languages</div>
        <div className="space-y-2">
          {languages.map((lang) => (
            <label key={lang} className="flex items-center gap-2">
              <input
                type="checkbox"
                checked={formData.selectedLanguages.includes(lang)}
                onChange={(e) => {
                  if (e.target.checked) {
                    setFormData((prev: any) => ({
                      ...prev,
                      selectedLanguages: [...prev.selectedLanguages, lang]
                    }));
                  } else {
                    setFormData((prev: any) => ({
                      ...prev,
                      selectedLanguages: prev.selectedLanguages.filter((l: string) => l !== lang)
                    }));
                  }
                }}
                className="rounded border-slate-300"
              />
              <span className="text-sm text-slate-700">{lang}</span>
            </label>
          ))}
        </div>
      </div>
      <div className="flex justify-end gap-2 pt-4 border-t">
        <Button type="button" variant="secondary" onClick={onClose}>
          Back
        </Button>
        <Button type="button" onClick={onClose}>
          Next
        </Button>
      </div>
    </div>
  );
}

function ExpertiseSkillsModalContent({ formData, handleInputChange, handleSkillsChange, allSkills, expertiseLevels, copy, onClose }: any) {
  return (
    <div className="space-y-4">
      <div>
        <div className="text-xs text-slate-500 mb-1">Required Skills</div>
        <div className="min-h-[100px] rounded-2xl border border-slate-200 bg-white p-3 text-sm">
          {allSkills?.map((skill: any) => (
            <label key={skill.id} className="inline-flex items-center mr-3 mb-2">
              <input 
                type="checkbox" 
                className="rounded border-slate-300 text-slate-700 focus:ring-slate-500"
                checked={formData.selectedSkills.includes(skill.id)}
                onChange={(e) => handleSkillsChange(skill.id, e.target.checked)}
              />
              <span className="ml-2 text-sm">{copy.skills[skill.id as keyof typeof copy.skills]}</span>
            </label>
          ))}
        </div>
      </div>
      <div>
        <div className="text-xs text-slate-500 mb-1">Expertise Level</div>
        <select
          className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-900/5"
          value={formData.selectedExpertiseId}
          onChange={(e) => handleInputChange('selectedExpertiseId', e.target.value)}
        >
          <option value="">Select expertise level</option>
          {expertiseLevels?.map((level: any) => (
            <option key={level.value} value={level.value}>
              {copy.expertise[level.value as keyof typeof copy.expertise]}
            </option>
          ))}
        </select>
      </div>
      <div className="flex justify-end gap-2 pt-4 border-t">
        <Button type="button" variant="secondary" onClick={onClose}>
          Back
        </Button>
        <Button type="button" onClick={onClose}>
          Next
        </Button>
      </div>
    </div>
  );
}

function RequirementsModalContent({ formData, handleInputChange, setFormData, onClose }: any) {
  return (
    <div className="space-y-4">
      <div>
        <div className="text-xs text-slate-500 mb-1">Job duties</div>
        <p className="text-xs text-slate-600 mb-2">List out specific tasks and responsibilities. Add relevant links.</p>
        <div className="border border-slate-200 rounded-lg">
          <div className="flex items-center gap-2 p-2 border-b border-slate-200">
            <button type="button" className="p-1 hover:bg-slate-100 rounded" title="Bold">B</button>
            <button type="button" className="p-1 hover:bg-slate-100 rounded" title="Italic">I</button>
            <button type="button" className="p-1 hover:bg-slate-100 rounded text-xs" title="Add link">Add link</button>
          </div>
          <textarea
            className="w-full min-h-[120px] px-3 py-2 text-sm focus:outline-none"
            placeholder="Enter job duties..."
            value={formData.jobDuties}
            onChange={(e) => handleInputChange('jobDuties', e.target.value)}
          />
        </div>
      </div>
      <div>
        <div className="flex items-center gap-2 mb-1">
          <div className="text-xs text-slate-500">Upload important files</div>
          <span className="text-[10px] bg-purple-100 text-purple-700 px-1.5 py-0.5 rounded">NEW</span>
        </div>
        <p className="text-xs text-slate-600 mb-2">
          Upload relevant files for Pros to review, like safety guidelines, event details, etc.
        </p>
        <div
          className="border-2 border-dashed border-slate-300 rounded-lg p-6 text-center cursor-pointer hover:border-slate-400 transition-colors"
          onClick={() => {
            const input = document.createElement('input');
            input.type = 'file';
            input.accept = 'application/pdf';
            input.multiple = true;
            input.onchange = (e: any) => {
              const files = Array.from(e.target.files).filter((f: any) => f.type === 'application/pdf');
              if (files.length > 0) {
                setFormData((prev: any) => ({
                  ...prev,
                  otherInstructionsFiles: [...prev.otherInstructionsFiles, ...files as File[]]
                }));
              }
            };
            input.click();
          }}
        >
          <p className="text-sm text-slate-600">Drag & drop, or click to select files</p>
        </div>
      </div>
      <div>
        <div className="text-xs text-slate-500 mb-1">Additional instructions</div>
        <div className="border border-slate-200 rounded-lg">
          <div className="flex items-center gap-2 p-2 border-b border-slate-200">
            <button type="button" className="p-1 hover:bg-slate-100 rounded" title="Bold">B</button>
            <button type="button" className="p-1 hover:bg-slate-100 rounded" title="Italic">I</button>
            <button type="button" className="p-1 hover:bg-slate-100 rounded text-xs" title="Add link">Add link</button>
          </div>
          <textarea
            className="w-full min-h-[120px] px-3 py-2 text-sm focus:outline-none"
            placeholder="Enter additional instructions..."
            value={formData.otherInstructions}
            onChange={(e) => handleInputChange('otherInstructions', e.target.value)}
          />
        </div>
      </div>
      <div className="flex justify-end gap-2 pt-4 border-t">
        <Button type="button" variant="secondary" onClick={onClose}>
          Back
        </Button>
        <Button type="button" onClick={onClose}>
          Next
        </Button>
      </div>
    </div>
  );
}

