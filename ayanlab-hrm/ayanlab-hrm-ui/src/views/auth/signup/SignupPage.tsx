import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "../../../ui/components/Card";
import { Input } from "../../../ui/components/Input";
import { Button } from "../../../ui/components/Button";
import { useJobRoles } from "../../../data/jobHooks";
import { useJobRoleCategories } from "../../../data/hooks/useJobRoleCategories";
import { useOrgs } from "../../../data/hooks";
import { Building2, Users, Briefcase, Shield, X, Plus, Trash2, Upload, Search, Globe2 } from "lucide-react";
import type { SignupType, BusinessSignupData, EmployerSignupData, WorkerSignupData, AdminSignupData } from "../../../types/signup";
import { useSearchParams } from "react-router-dom";
import { useTranslations, useLanguage, Language } from "../../../state/i18n";
import { TEXTS } from "./i18n";
import { CERTIFICATES } from "../../../mock/jobData";
import { TEXTS as DOCUMENT_TEXTS } from "../../documents/i18n";
import { ToastProvider, useToast } from "../../../ui/components/Toast";
import { 
  validatePhone, 
  validateEmail, 
  validateSSN, 
  validateITIN, 
  validateName, 
  validatePassword,
  formatPhone,
  formatSSN,
  formatITIN,
  formatDate
} from "../../../utils/validation";
import { authHttp } from "../../../api/client";
import { endpoints } from "../../../api/endpoints";
import { cn } from "../../../lib/cn";

export function SignupPage() {
  return (
    <ToastProvider>
      <SignupPageInner />
    </ToastProvider>
  );
}

function SignupPageInner() {
  const [searchParams] = useSearchParams();
  const isAdminSignup = searchParams.get("type") === "admin";
  const [signupType, setSignupType] = useState<SignupType | null>(isAdminSignup ? "admin" : null);
  const [step, setStep] = useState(isAdminSignup ? 1 : 0); // 0 = selection, then step numbers start from 1
  const nav = useNavigate();
  const copy = useTranslations(TEXTS);
  const { showToast } = useToast();
  const { language, setLanguage } = useLanguage();
  
  // Form data states
  const [businessData, setBusinessData] = useState<Partial<BusinessSignupData>>({});
  const [employerData, setemployerData] = useState<Partial<EmployerSignupData>>({});
  const [workerData, setWorkerData] = useState<Partial<WorkerSignupData>>({
    experiences: [],
    jobRoles: []
  });
  const [adminData, setAdminData] = useState<Partial<AdminSignupData>>({});

  const { data: jobRoles = [] } = useJobRoles();
  const { data: jobRoleCategories = [] } = useJobRoleCategories();
  
  // Validation state
  const [isStepValid, setIsStepValid] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Update state when query parameter changes
  useEffect(() => {
    if (isAdminSignup && signupType !== "admin") {
      setSignupType("admin");
      setStep(1);
    }
  }, [isAdminSignup, signupType]);

  // Generate correlation ID
  const generateCorrelationId = () => {
    return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  };

  // Signup API call for employer
  const handleEmployerSignup = async () => {
    if (!employerData.firstName || !employerData.lastName || !employerData.email || 
        !employerData.phone || !employerData.ssn || !employerData.password || !employerData.birthday) {
      showToast("Please fill in all required fields");
      return;
    }

    setIsSubmitting(true);
    try {
      const correlationId = generateCorrelationId();
      const formattedDate = formatDate(employerData.birthday);
      const formattedPhone = formatPhone(employerData.phone || '');
      const formattedSSN = formatSSN(employerData.ssn || '');
      
      const signupPayload = {
        firstName: employerData.firstName,
        lastName: employerData.lastName,
        birthDate: formattedDate,
        email: employerData.email,
        mobile: formattedPhone,
        ssn: formattedSSN,
        password: employerData.password,
        defaultProfile: "EMPLOYER" as const
      };

      if (employerData.itin) {
        (signupPayload as any).itin = formatITIN(employerData.itin);
      }

      const response = await authHttp.post(
        `${endpoints.auth.signup}?lang=${language}`,
        signupPayload,
        {
          headers: {
            'X-Correlation-ID': correlationId,
            'Content-Type': 'application/json'
          }
        }
      );

      if (response.data?.success && response.data?.data?.token) {
        // Store tokens
        localStorage.setItem('token', response.data.data.token);
        localStorage.setItem('refreshToken', response.data.data.refreshToken);
        
        showToast("Signup successful! Redirecting...");
        setTimeout(() => {
          nav("/app/dashboard");
        }, 1000);
      } else {
        showToast(response.data?.message || "Signup failed. Please try again.");
      }
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || "Signup failed. Please try again.";
      showToast(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleTypeSelect = (type: SignupType) => {
    setSignupType(type);
    setStep(1);
  };

  const handleFinish = () => {
    // Mock: Save data and navigate
    console.log("Signup data:", { signupType, businessData, employerData, workerData, adminData });
    if (signupType === "admin") {
      nav("/login");
    } else {
      nav("/app/dashboard");
    }
  };

  const maxSteps = signupType === "worker" ? 8 : signupType === "business" ? 4 : signupType === "employer" ? 2 : signupType === "admin" ? 1 : 0;

  return (
    <div className="min-h-screen grid place-items-center p-6 bg-slate-50 relative">
      {/* Language Selector - Top Right */}
      <div className="absolute top-4 right-4 md:top-6 md:right-6">
        <div className="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 shadow-sm">
          <Globe2 size={16} className="text-slate-500" />
          <select
            value={language}
            onChange={(e) => setLanguage(e.target.value as Language)}
            className="bg-transparent text-sm font-medium text-slate-700 outline-none border-none focus:ring-0 cursor-pointer"
          >
            <option value="en">EN</option>
            <option value="de">DE</option>
            <option value="tr">TR</option>
          </select>
        </div>
      </div>

      <div className="w-full max-w-4xl space-y-4">
        {step === 0 && !isAdminSignup && (
          <>
            <div className="text-center">
              <h1 className="text-3xl font-bold text-slate-900 mb-2">
                {copy.selection.title}
              </h1>
            </div>
            <div className="grid gap-4 md:grid-cols-3">
              <SignupTypeCard
                icon={<Building2 size={32} />}
                title={copy.selection.business.title}
                description={copy.selection.business.description}
                selected={signupType === "business"}
                onClick={() => handleTypeSelect("business")}
              />
              <SignupTypeCard
                icon={<Users size={32} />}
                title={copy.selection.employer.title}
                description={copy.selection.employer.description}
                selected={signupType === "employer"}
                onClick={() => handleTypeSelect("employer")}
              />
              <SignupTypeCard
                icon={<Briefcase size={32} />}
                title={copy.selection.worker.title}
                description={copy.selection.worker.description}
                selected={signupType === "worker"}
                onClick={() => handleTypeSelect("worker")}
              />
            </div>
            <div className="text-center">
              <Link to="/login" className="text-sm text-slate-600 hover:text-slate-900">
                {copy.selection.loginInstead}
              </Link>
            </div>
          </>
        )}

        {step > 0 && signupType && (
          <Card>
            <CardHeader>
              <CardTitle>
                {copy.steps[signupType][step]?.title || `Step ${step} of ${maxSteps}`}
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {signupType === "business" && (
                <>
                  {step === 1 && (
                    <BusinessStep1
                      data={businessData}
                      onChange={setBusinessData}
                      copy={copy.business.step1}
                    />
                  )}
                  {step === 2 && (
                    <BusinessStep2
                      data={businessData}
                      onChange={setBusinessData}
                      copy={copy.business.step2}
                    />
                  )}
                  {step === 3 && (
                    <BusinessStep3
                      data={businessData}
                      onChange={setBusinessData}
                      copy={copy.business.step3}
                    />
                  )}
                  {step === 4 && (
                    <BusinessStep4
                      data={businessData}
                      onChange={setBusinessData}
                      jobRoles={jobRoles}
                      jobRoleCategories={jobRoleCategories}
                      copy={copy.business.step4}
                    />
                  )}
                </>
              )}

              {signupType === "employer" && (
                <>
                  {step === 1 && (
                    <EmployerStep1
                      data={employerData}
                      onChange={setemployerData}
                      copy={copy.employer.step1}
                      language={language}
                      onValidationChange={setIsStepValid}
                    />
                  )}
                  {step === 2 && (
                    <EmployerStep2
                      data={employerData}
                      onChange={setemployerData}
                      jobRoles={jobRoles}
                      jobRoleCategories={jobRoleCategories}
                      copy={copy.employer.step2}
                    />
                  )}
                </>
              )}

              {signupType === "admin" && (
                <>
                  {step === 1 && (
                    <AdminStep1
                      data={adminData}
                      onChange={setAdminData}
                      copy={copy.admin.step1}
                    />
                  )}
                </>
              )}

              {signupType === "worker" && (
                <>
                  {step === 1 && (
                    <WorkerStep1
                      data={workerData}
                      onChange={setWorkerData}
                      copy={copy.worker.step1}
                    />
                  )}
                  {step === 2 && (
                    <WorkerStep2
                      data={workerData}
                      onChange={setWorkerData}
                      jobRoles={jobRoles}
                      jobRoleCategories={jobRoleCategories}
                      copy={copy.worker.step2}
                    />
                  )}
                  {step === 3 && (
                    <WorkerStep3
                      data={workerData}
                      onChange={setWorkerData}
                      copy={copy.worker.step3}
                    />
                  )}
                  {step === 4 && (
                    <WorkerStep4
                      data={workerData}
                      onChange={setWorkerData}
                      copy={copy.worker.step4}
                    />
                  )}
                  {step === 5 && (
                    <WorkerStep5
                      data={workerData}
                      onChange={setWorkerData}
                      copy={copy.worker.step5}
                    />
                  )}
                  {step === 6 && (
                    <WorkerStep6Address
                      data={workerData}
                      onChange={setWorkerData}
                      copy={copy.worker.step6}
                    />
                  )}
                  {step === 7 && (
                    <WorkerStep7
                      data={workerData}
                      onChange={setWorkerData}
                      copy={copy.worker.step7}
                    />
                  )}
                  {step === 8 && (
                    <WorkerStep8
                      data={workerData}
                      onChange={setWorkerData}
                      copy={copy.worker.step8}
                    />
                  )}
                  {step === 8 && (
                    <WorkerStep8
                      data={workerData}
                      onChange={setWorkerData}
                      copy={copy.worker.step8}
                    />
                  )}
                </>
              )}

              <div className="flex justify-between pt-4 border-t">
                <Button
                  type="button"
                  variant="secondary"
                  onClick={() => {
                    if (signupType === "admin") {
                      nav("/signup");
                    } else {
                      step === 1 ? setStep(0) : setStep(step - 1);
                    }
                  }}
                >
                  {copy.common.back}
                </Button>
                <div className="flex gap-2">
                  {step < maxSteps && step > 1 && (
                    <Button
                      type="button"
                      variant="ghost"
                      onClick={() => {
                        const stepTitle = copy.steps[signupType]?.[step]?.title || `Step ${step}`;
                        showToast(copy.common.stepSkipped.replace("{step}", stepTitle));
                        setStep(step + 1);
                      }}
                    >
                      {copy.common.skip}
                    </Button>
                  )}
                  {step < maxSteps ? (
                    <Button
                      type="button"
                      onClick={async () => {
                        // For employer step 1, validate and call signup API
                        if (signupType === "employer" && step === 1) {
                          await handleEmployerSignup();
                        } else {
                          const stepTitle = copy.steps[signupType]?.[step]?.title || `Step ${step}`;
                          showToast(copy.common.stepSaved.replace("{step}", stepTitle));
                          setStep(step + 1);
                        }
                      }}
                      disabled={isSubmitting || (signupType === "employer" && step === 1 && !isStepValid)}
                    >
                      {isSubmitting ? "Submitting..." : copy.common.next}
                    </Button>
                  ) : (
                    <Button type="button" onClick={handleFinish}>
                      {signupType === "admin" ? copy.common.finishAndLogin : copy.common.finish}
                    </Button>
                  )}
                </div>
              </div>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
}

// Selection Card Component
type SignupTypeCardProps = {
  icon: React.ReactNode;
  title: string;
  description: string;
  selected: boolean;
  onClick: () => void;
};

function SignupTypeCard({ icon, title, description, selected, onClick }: SignupTypeCardProps) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={[
        "flex flex-col items-center rounded-2xl border-2 p-6 text-center transition",
        selected
          ? "border-slate-900 bg-slate-900 text-white"
          : "border-slate-200 bg-white hover:border-slate-300"
      ].join(" ")}
    >
      <div className={selected ? "text-white" : "text-slate-600 mb-4"}>{icon}</div>
      <h3 className="text-xl font-bold mb-2">{title}</h3>
      <p className="text-sm opacity-80">{description}</p>
    </button>
  );
}

// Business Step 1: Company Details
// Business Step 1: User Details
function BusinessStep1({
  data,
  onChange,
  copy
}: {
  data: Partial<BusinessSignupData>;
  onChange: (data: Partial<BusinessSignupData>) => void;
  copy: any;
}) {
  return (
    <div className="space-y-4">
      <div className="grid gap-4 md:grid-cols-2">
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.firstName}</label>
          <Input
            value={data.firstName || ""}
            onChange={(e) => onChange({ ...data, firstName: e.target.value })}
            placeholder={copy.firstNamePlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.lastName}</label>
          <Input
            value={data.lastName || ""}
            onChange={(e) => onChange({ ...data, lastName: e.target.value })}
            placeholder={copy.lastNamePlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.birthday}</label>
          <Input
            type="date"
            value={data.birthday || ""}
            onChange={(e) => onChange({ ...data, birthday: e.target.value })}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.ssn}</label>
          <Input
            value={data.ssn || ""}
            onChange={(e) => onChange({ ...data, ssn: e.target.value })}
            placeholder={copy.ssnPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.itin}</label>
          <Input
            value={data.itin || ""}
            onChange={(e) => onChange({ ...data, itin: e.target.value })}
            placeholder={copy.itinPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.email}</label>
          <Input
            type="email"
            value={data.email || ""}
            onChange={(e) => onChange({ ...data, email: e.target.value })}
            placeholder={copy.emailPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.phone}</label>
          <Input
            type="tel"
            value={data.phone || ""}
            onChange={(e) => onChange({ ...data, phone: e.target.value })}
            placeholder={copy.phonePlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.password}</label>
          <Input
            type="password"
            value={data.password || ""}
            onChange={(e) => onChange({ ...data, password: e.target.value })}
            placeholder={copy.passwordPlaceholder}
          />
        </div>
      </div>
    </div>
  );
}

// Business Step 2: Organization Type Selection
function BusinessStep2({
  data,
  onChange,
  copy
}: {
  data: Partial<BusinessSignupData>;
  onChange: (data: Partial<BusinessSignupData>) => void;
  copy: any;
}) {
  return (
    <div className="space-y-4">
      <div className="text-sm text-slate-600 mb-4">{copy.description}</div>
      <div className="grid gap-4 md:grid-cols-2">
        <button
          type="button"
          onClick={() => onChange({ ...data, organizationType: "new", existingOrgId: undefined })}
          className={[
            "p-6 rounded-xl border-2 text-left transition-all",
            data.organizationType === "new"
              ? "border-slate-900 bg-slate-50"
              : "border-slate-200 hover:border-slate-300"
          ].join(" ")}
        >
          <div className="font-semibold text-slate-900 mb-1">{copy.newOrganization.title}</div>
          <div className="text-xs text-slate-600">{copy.newOrganization.description}</div>
        </button>
        <button
          type="button"
          onClick={() => onChange({ ...data, organizationType: "existing", companyName: undefined })}
          className={[
            "p-6 rounded-xl border-2 text-left transition-all",
            data.organizationType === "existing"
              ? "border-slate-900 bg-slate-50"
              : "border-slate-200 hover:border-slate-300"
          ].join(" ")}
        >
          <div className="font-semibold text-slate-900 mb-1">{copy.existingOrganization.title}</div>
          <div className="text-xs text-slate-600">{copy.existingOrganization.description}</div>
        </button>
      </div>
    </div>
  );
}

// Business Step 3: Company Information (if new) or Organization Search (if existing)
function BusinessStep3({
  data,
  onChange,
  copy
}: {
  data: Partial<BusinessSignupData>;
  onChange: (data: Partial<BusinessSignupData>) => void;
  copy: any;
}) {
  const { data: orgs = [] } = useOrgs();
  const [searchQuery, setSearchQuery] = useState("");
  const [useDifferentBilling, setUseDifferentBilling] = useState(false);

  const filteredOrgs = orgs.filter((org: any) =>
    org.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  if (data.organizationType === "existing") {
    return (
      <div className="space-y-4">
        <div className="space-y-2">
          <label className="text-xs font-semibold text-slate-600">{copy.searchOrganization}</label>
          <div className="relative">
            <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <Input
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder={copy.searchPlaceholder}
              className="pl-10"
            />
          </div>
        </div>
        <div className="space-y-2 max-h-96 overflow-y-auto">
          {filteredOrgs.length === 0 ? (
            <div className="text-sm text-slate-500 text-center py-8">
              {searchQuery ? copy.noResults : copy.noOrganizations}
            </div>
          ) : (
            filteredOrgs.map((org: any) => (
              <button
                key={org.id}
                type="button"
                onClick={() => onChange({ ...data, existingOrgId: org.id })}
                className={[
                  "w-full p-4 rounded-xl border-2 text-left transition-all",
                  data.existingOrgId === org.id
                    ? "border-slate-900 bg-slate-50"
                    : "border-slate-200 hover:border-slate-300"
                ].join(" ")}
              >
                <div className="font-semibold text-slate-900">{org.name}</div>
                <div className="text-xs text-slate-500 mt-1">{org.country}</div>
              </button>
            ))
          )}
        </div>
        {data.existingOrgId && (
          <div className="p-4 bg-green-50 border border-green-200 rounded-xl">
            <div className="text-sm font-semibold text-green-900">{copy.selectedOrganization}</div>
            <div className="text-xs text-green-700 mt-1">
              {filteredOrgs.find((o: any) => o.id === data.existingOrgId)?.name}
            </div>
          </div>
        )}
      </div>
    );
  }

  // New organization form
  return (
    <div className="space-y-4">
      <div className="grid gap-4 md:grid-cols-2">
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.companyName}</label>
          <Input
            value={data.companyName || ""}
            onChange={(e) => onChange({ ...data, companyName: e.target.value })}
            placeholder={copy.companyNamePlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.legalEntityType}</label>
          <Input
            value={data.legalEntityType || ""}
            onChange={(e) => onChange({ ...data, legalEntityType: e.target.value })}
            placeholder={copy.legalEntityTypePlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.companyTaxId}</label>
          <Input
            value={data.companyTaxId || ""}
            onChange={(e) => onChange({ ...data, companyTaxId: e.target.value })}
            placeholder={copy.companyTaxIdPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.itin}</label>
          <Input
            value={data.itin || ""}
            onChange={(e) => onChange({ ...data, itin: e.target.value })}
            placeholder={copy.itinPlaceholder}
          />
        </div>
        <div className="space-y-1 md:col-span-2">
          <label className="text-xs font-semibold text-slate-600">{copy.officialAddress}</label>
          <Input
            value={data.officialAddress || ""}
            onChange={(e) => onChange({ ...data, officialAddress: e.target.value })}
            placeholder={copy.officialAddressPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.vatNumber}</label>
          <Input
            value={data.vatNumber || ""}
            onChange={(e) => onChange({ ...data, vatNumber: e.target.value })}
            placeholder={copy.vatNumberPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.registrationNumber}</label>
          <Input
            value={data.registrationNumber || ""}
            onChange={(e) => onChange({ ...data, registrationNumber: e.target.value })}
            placeholder={copy.registrationNumberPlaceholder}
          />
        </div>
        <div className="md:col-span-2">
          <label className="flex items-center gap-2 text-xs font-semibold text-slate-600">
            <input
              type="checkbox"
              checked={useDifferentBilling}
              onChange={(e) => setUseDifferentBilling(e.target.checked)}
              className="rounded border-slate-300"
            />
            {copy.differentBillingAddress}
          </label>
        </div>
        {useDifferentBilling && (
          <div className="space-y-1 md:col-span-2">
            <label className="text-xs font-semibold text-slate-600">{copy.billingAddress}</label>
            <Input
              value={data.billingAddress || ""}
              onChange={(e) => onChange({ ...data, billingAddress: e.target.value })}
              placeholder={copy.billingAddressPlaceholder}
            />
          </div>
        )}
      </div>
    </div>
  );
}

// Business Step 4: Industry & Job Roles
function BusinessStep4({
  data,
  onChange,
  jobRoles,
  jobRoleCategories,
  copy
}: {
  data: Partial<BusinessSignupData>;
  onChange: (data: Partial<BusinessSignupData>) => void;
  jobRoles: any[];
  jobRoleCategories: any[];
  copy: any;
}) {
  return (
    <div className="space-y-4">
      <div className="space-y-1">
        <label className="text-xs font-semibold text-slate-600">{copy.industry}</label>
        <select
          className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm"
          value={data.industry || ""}
          onChange={(e) => onChange({ ...data, industry: e.target.value })}
        >
          <option value="">{copy.selectIndustry}</option>
          {jobRoleCategories.map((cat) => (
            <option key={cat.id} value={cat.id}>
              {cat.name}
            </option>
          ))}
        </select>
      </div>
      <div className="space-y-2">
        <label className="text-xs font-semibold text-slate-600">{copy.jobRoles}</label>
        <div className="grid gap-2 md:grid-cols-3">
          {jobRoles.map((role) => (
            <label
              key={role.id}
              className="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs cursor-pointer hover:bg-slate-50"
            >
              <input
                type="checkbox"
                checked={data.jobRoles?.includes(role.id) || false}
                onChange={(e) => {
                  const current = data.jobRoles || [];
                  onChange({
                    ...data,
                    jobRoles: e.target.checked
                      ? [...current, role.id]
                      : current.filter((id) => id !== role.id)
                  });
                }}
                className="rounded border-slate-300"
              />
              <span>{role.nameKey}</span>
            </label>
          ))}
        </div>
      </div>
      <div className="space-y-1">
        <label className="text-xs font-semibold text-slate-600">{copy.hearAboutUs}</label>
        <Input
          value={data.hearAboutUs || ""}
          onChange={(e) => onChange({ ...data, hearAboutUs: e.target.value })}
          placeholder={copy.hearAboutUsPlaceholder}
        />
      </div>
    </div>
  );
}

// Admin Step 1: Personal Details
function AdminStep1({
  data,
  onChange,
  copy
}: {
  data: Partial<AdminSignupData>;
  onChange: (data: Partial<AdminSignupData>) => void;
  copy: any;
}) {
  return (
    <div className="space-y-4">
      <div className="grid gap-4 md:grid-cols-2">
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.firstName}</label>
          <Input
            value={data.firstName || ""}
            onChange={(e) => onChange({ ...data, firstName: e.target.value })}
            placeholder={copy.firstNamePlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.lastName}</label>
          <Input
            value={data.lastName || ""}
            onChange={(e) => onChange({ ...data, lastName: e.target.value })}
            placeholder={copy.lastNamePlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.birthday}</label>
          <Input
            type="date"
            value={data.birthday || ""}
            onChange={(e) => onChange({ ...data, birthday: e.target.value })}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.ssn}</label>
          <Input
            value={data.ssn || ""}
            onChange={(e) => onChange({ ...data, ssn: e.target.value })}
            placeholder={copy.ssnPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.itin}</label>
          <Input
            value={data.itin || ""}
            onChange={(e) => onChange({ ...data, itin: e.target.value })}
            placeholder={copy.itinPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.email}</label>
          <Input
            type="email"
            value={data.email || ""}
            onChange={(e) => onChange({ ...data, email: e.target.value })}
            placeholder={copy.emailPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.phone}</label>
          <Input
            type="tel"
            value={data.phone || ""}
            onChange={(e) => onChange({ ...data, phone: e.target.value })}
            placeholder={copy.phonePlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.password}</label>
          <Input
            type="password"
            value={data.password || ""}
            onChange={(e) => onChange({ ...data, password: e.target.value })}
            placeholder={copy.passwordPlaceholder}
          />
        </div>
      </div>
    </div>
  );
}

// Employer Step 1: Personal Details (same as Worker Step 1)
function EmployerStep1({
  data,
  onChange,
  copy,
  onValidationChange,
  language
}: {
  data: Partial<EmployerSignupData>;
  onChange: (data: Partial<EmployerSignupData>) => void;
  copy: any;
  onValidationChange?: (isValid: boolean) => void;
  language: Language;
}) {
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [validating, setValidating] = useState<{ email?: boolean; phone?: boolean }>({});

  // Generate correlation ID
  const generateCorrelationId = () => {
    return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  };

  // Validate email/phone existence API call
  const checkEmailExists = async (email: string) => {
    if (!email || !validateEmail(email)) return;
    
    setValidating(prev => ({ ...prev, email: true }));
    try {
      const correlationId = generateCorrelationId();
      const response = await authHttp.get(endpoints.auth.validation, {
        params: { email },
        headers: {
          'X-Correlation-ID': correlationId
        }
      });
      
      // API returns true if email is valid (available), false if it exists
      if (response.data?.data === false) {
        setErrors(prev => ({ ...prev, email: "Email already exists" }));
      } else {
        setErrors(prev => {
          const newErrors = { ...prev };
          delete newErrors.email;
          return newErrors;
        });
      }
    } catch (error: any) {
      // If API returns error or false, email might exist
      if (error.response?.status === 400 || error.response?.data?.data === false) {
        setErrors(prev => ({ ...prev, email: "Email already exists" }));
      } else {
        // If API fails, don't block user (optional validation)
        setErrors(prev => {
          const newErrors = { ...prev };
          delete newErrors.email;
          return newErrors;
        });
      }
    } finally {
      setValidating(prev => ({ ...prev, email: false }));
    }
  };

  const checkPhoneExists = async (phone: string) => {
    const formattedPhone = formatPhone(phone);
    if (!formattedPhone || !validatePhone(phone)) return;
    
    setValidating(prev => ({ ...prev, phone: true }));
    try {
      const correlationId = generateCorrelationId();
      const response = await authHttp.get(endpoints.auth.validation, {
        params: { mobile: formattedPhone },
        headers: {
          'X-Correlation-ID': correlationId
        }
      });
      
      // API returns true if phone is valid (available), false if it exists
      if (response.data?.data === false) {
        setErrors(prev => ({ ...prev, phone: "Phone number already exists" }));
      } else {
        setErrors(prev => {
          const newErrors = { ...prev };
          delete newErrors.phone;
          return newErrors;
        });
      }
    } catch (error: any) {
      // If API returns error or false, phone might exist
      if (error.response?.status === 400 || error.response?.data?.data === false) {
        setErrors(prev => ({ ...prev, phone: "Phone number already exists" }));
      } else {
        // If API fails, don't block user (optional validation)
        setErrors(prev => {
          const newErrors = { ...prev };
          delete newErrors.phone;
          return newErrors;
        });
      }
    } finally {
      setValidating(prev => ({ ...prev, phone: false }));
    }
  };

  // Validate individual fields
  const validateField = (field: string, value: string) => {
    const newErrors = { ...errors };
    
    switch (field) {
      case 'firstName':
        if (!value) {
          newErrors.firstName = "First name is required";
        } else if (!validateName(value)) {
          newErrors.firstName = "First name must be at least 2 characters";
        } else {
          delete newErrors.firstName;
        }
        break;
      case 'lastName':
        if (!value) {
          newErrors.lastName = "Last name is required";
        } else if (!validateName(value)) {
          newErrors.lastName = "Last name must be at least 2 characters";
        } else {
          delete newErrors.lastName;
        }
        break;
      case 'email':
        if (!value) {
          newErrors.email = "Email is required";
        } else if (!validateEmail(value)) {
          newErrors.email = "Invalid email format";
        } else {
          delete newErrors.email;
        }
        break;
      case 'phone':
        if (!value) {
          newErrors.phone = "Phone is required";
        } else if (!validatePhone(value)) {
          newErrors.phone = "Invalid phone number";
        } else {
          delete newErrors.phone;
        }
        break;
      case 'ssn':
        if (!value) {
          newErrors.ssn = "SSN is required";
        } else if (!validateSSN(value)) {
          newErrors.ssn = "SSN must be 9 digits";
        } else {
          delete newErrors.ssn;
        }
        break;
      case 'itin':
        if (value && !validateITIN(value)) {
          newErrors.itin = "ITIN must be 9 digits starting with 9";
        } else {
          delete newErrors.itin;
        }
        break;
      case 'password':
        if (!value) {
          newErrors.password = "Password is required";
        } else if (!validatePassword(value)) {
          newErrors.password = "Password must contain uppercase, lowercase, number, and special character";
        } else {
          delete newErrors.password;
        }
        break;
      case 'birthday':
        if (!value) {
          newErrors.birthday = "Birthday is required";
        } else {
          delete newErrors.birthday;
        }
        break;
    }
    
    setErrors(newErrors);
    
    // Notify parent of validation status
    if (onValidationChange) {
      const isValid = Object.keys(newErrors).length === 0;
      onValidationChange(isValid);
    }
  };

  // Check if form is valid
  const isFormValid = (): boolean => {
    return !!(
      data.firstName &&
      data.lastName &&
      data.email &&
      data.phone &&
      data.ssn &&
      data.password &&
      data.birthday &&
      validateName(data.firstName) &&
      validateName(data.lastName) &&
      validateEmail(data.email) &&
      validatePhone(data.phone) &&
      validateSSN(data.ssn) &&
      validatePassword(data.password) &&
      (!data.itin || validateITIN(data.itin)) &&
      Object.keys(errors).length === 0
    );
  };

  useEffect(() => {
    if (onValidationChange) {
      onValidationChange(isFormValid());
    }
  }, [data, errors]);

  return (
    <div className="space-y-4">
      <div className="grid gap-4 md:grid-cols-2">
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.firstName}</label>
          <Input
            value={data.firstName || ""}
            onChange={(e) => {
              onChange({ ...data, firstName: e.target.value });
              validateField('firstName', e.target.value);
            }}
            onBlur={() => validateField('firstName', data.firstName || '')}
            placeholder={copy.firstNamePlaceholder}
            className={cn(errors.firstName && "border-red-500")}
          />
          {errors.firstName && (
            <p className="text-xs text-red-500">{errors.firstName}</p>
          )}
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.lastName}</label>
          <Input
            value={data.lastName || ""}
            onChange={(e) => {
              onChange({ ...data, lastName: e.target.value });
              validateField('lastName', e.target.value);
            }}
            onBlur={() => validateField('lastName', data.lastName || '')}
            placeholder={copy.lastNamePlaceholder}
            className={cn(errors.lastName && "border-red-500")}
          />
          {errors.lastName && (
            <p className="text-xs text-red-500">{errors.lastName}</p>
          )}
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.birthday}</label>
          <Input
            type="date"
            value={data.birthday || ""}
            onChange={(e) => {
              onChange({ ...data, birthday: e.target.value });
              validateField('birthday', e.target.value);
            }}
            onBlur={() => validateField('birthday', data.birthday || '')}
            className={cn(errors.birthday && "border-red-500")}
          />
          {errors.birthday && (
            <p className="text-xs text-red-500">{errors.birthday}</p>
          )}
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.ssn}</label>
          <Input
            value={data.ssn || ""}
            onChange={(e) => {
              const formatted = formatSSN(e.target.value);
              onChange({ ...data, ssn: formatted });
              validateField('ssn', formatted);
            }}
            onBlur={() => validateField('ssn', data.ssn || '')}
            placeholder={copy.ssnPlaceholder}
            maxLength={9}
            className={cn(errors.ssn && "border-red-500")}
          />
          {errors.ssn && (
            <p className="text-xs text-red-500">{errors.ssn}</p>
          )}
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.itin}</label>
          <Input
            value={data.itin || ""}
            onChange={(e) => {
              const formatted = formatITIN(e.target.value);
              onChange({ ...data, itin: formatted });
              validateField('itin', formatted);
            }}
            onBlur={() => validateField('itin', data.itin || '')}
            placeholder={copy.itinPlaceholder}
            maxLength={9}
            className={cn(errors.itin && "border-red-500")}
          />
          {errors.itin && (
            <p className="text-xs text-red-500">{errors.itin}</p>
          )}
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.email}</label>
          <Input
            type="email"
            value={data.email || ""}
            onChange={(e) => {
              onChange({ ...data, email: e.target.value });
              validateField('email', e.target.value);
            }}
            onBlur={() => {
              validateField('email', data.email || '');
              if (data.email && validateEmail(data.email)) {
                checkEmailExists(data.email);
              }
            }}
            placeholder={copy.emailPlaceholder}
            className={cn(errors.email && "border-red-500")}
          />
          {validating.email && (
            <p className="text-xs text-slate-500">Checking email...</p>
          )}
          {errors.email && !validating.email && (
            <p className="text-xs text-red-500">{errors.email}</p>
          )}
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.phone}</label>
          <Input
            type="tel"
            value={data.phone || ""}
            onChange={(e) => {
              onChange({ ...data, phone: e.target.value });
              validateField('phone', e.target.value);
            }}
            onBlur={() => {
              validateField('phone', data.phone || '');
              if (data.phone && validatePhone(data.phone)) {
                checkPhoneExists(data.phone);
              }
            }}
            placeholder={copy.phonePlaceholder}
            className={cn(errors.phone && "border-red-500")}
          />
          {validating.phone && (
            <p className="text-xs text-slate-500">Checking phone...</p>
          )}
          {errors.phone && !validating.phone && (
            <p className="text-xs text-red-500">{errors.phone}</p>
          )}
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.password}</label>
          <Input
            type="password"
            value={data.password || ""}
            onChange={(e) => {
              onChange({ ...data, password: e.target.value });
              validateField('password', e.target.value);
            }}
            onBlur={() => validateField('password', data.password || '')}
            placeholder={copy.passwordPlaceholder}
            className={cn(errors.password && "border-red-500")}
          />
          {errors.password && (
            <p className="text-xs text-red-500">{errors.password}</p>
          )}
        </div>
      </div>
    </div>
  );
}

// Employer Step 2: Industry & Job Roles (same as Business Step 2)
function EmployerStep2({
  data,
  onChange,
  jobRoles,
  jobRoleCategories,
  copy
}: {
  data: Partial<EmployerSignupData>;
  onChange: (data: Partial<EmployerSignupData>) => void;
  jobRoles: any[];
  jobRoleCategories: any[];
  copy: any;
}) {
  return (
    <div className="space-y-4">
      <div className="space-y-1">
        <label className="text-xs font-semibold text-slate-600">{copy.industry}</label>
        <select
          className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm"
          value={data.industry || ""}
          onChange={(e) => onChange({ ...data, industry: e.target.value })}
        >
          <option value="">{copy.selectIndustry}</option>
          {jobRoleCategories.map((cat) => (
            <option key={cat.id} value={cat.id}>
              {cat.name}
            </option>
          ))}
        </select>
      </div>
      <div className="space-y-2">
        <label className="text-xs font-semibold text-slate-600">{copy.jobRoles}</label>
        <div className="grid gap-2 md:grid-cols-3">
          {jobRoles.map((role) => (
            <label
              key={role.id}
              className="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs cursor-pointer hover:bg-slate-50"
            >
              <input
                type="checkbox"
                checked={data.jobRoles?.includes(role.id) || false}
                onChange={(e) => {
                  const current = data.jobRoles || [];
                  onChange({
                    ...data,
                    jobRoles: e.target.checked
                      ? [...current, role.id]
                      : current.filter((id) => id !== role.id)
                  });
                }}
                className="rounded border-slate-300"
              />
              <span>{role.nameKey}</span>
            </label>
          ))}
        </div>
      </div>
      <div className="space-y-1">
        <label className="text-xs font-semibold text-slate-600">{copy.hearAboutUs}</label>
        <Input
          value={data.hearAboutUs || ""}
          onChange={(e) => onChange({ ...data, hearAboutUs: e.target.value })}
          placeholder={copy.hearAboutUsPlaceholder}
        />
      </div>
    </div>
  );
}

// Worker Step 1: Personal Details
function WorkerStep1({
  data,
  onChange,
  copy
}: {
  data: Partial<WorkerSignupData>;
  onChange: (data: Partial<WorkerSignupData>) => void;
  copy: any;
}) {
  return (
    <div className="space-y-4">
      <div className="grid gap-4 md:grid-cols-2">
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.firstName}</label>
          <Input
            value={data.firstName || ""}
            onChange={(e) => onChange({ ...data, firstName: e.target.value })}
            placeholder={copy.firstNamePlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.lastName}</label>
          <Input
            value={data.lastName || ""}
            onChange={(e) => onChange({ ...data, lastName: e.target.value })}
            placeholder={copy.lastNamePlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.birthday}</label>
          <Input
            type="date"
            value={data.birthday || ""}
            onChange={(e) => onChange({ ...data, birthday: e.target.value })}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.ssn}</label>
          <Input
            value={data.ssn || ""}
            onChange={(e) => onChange({ ...data, ssn: e.target.value })}
            placeholder={copy.ssnPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.itin}</label>
          <Input
            value={data.itin || ""}
            onChange={(e) => onChange({ ...data, itin: e.target.value })}
            placeholder={copy.itinPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.email}</label>
          <Input
            type="email"
            value={data.email || ""}
            onChange={(e) => onChange({ ...data, email: e.target.value })}
            placeholder={copy.emailPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.phone}</label>
          <Input
            type="tel"
            value={data.phone || ""}
            onChange={(e) => onChange({ ...data, phone: e.target.value })}
            placeholder={copy.phonePlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.password}</label>
          <Input
            type="password"
            value={data.password || ""}
            onChange={(e) => onChange({ ...data, password: e.target.value })}
            placeholder={copy.passwordPlaceholder}
          />
        </div>
      </div>
    </div>
  );
}

// Worker Step 2: Industry & Job Roles with Experience
function WorkerStep2({
  data,
  onChange,
  jobRoles,
  jobRoleCategories,
  copy
}: {
  data: Partial<WorkerSignupData>;
  onChange: (data: Partial<WorkerSignupData>) => void;
  jobRoles: any[];
  jobRoleCategories: any[];
  copy: any;
}) {
  const addExperience = () => {
    const experiences = data.experiences || [];
    onChange({
      ...data,
      experiences: [...experiences, { jobRoleId: "", years: 0, months: 0, days: 0 }]
    });
  };

  const updateExperience = (index: number, field: string, value: any) => {
    const experiences = [...(data.experiences || [])];
    experiences[index] = { ...experiences[index], [field]: value };
    onChange({ ...data, experiences });
  };

  const removeExperience = (index: number) => {
    const experiences = data.experiences || [];
    onChange({ ...data, experiences: experiences.filter((_, i) => i !== index) });
  };

  return (
    <div className="space-y-4">
      <div className="space-y-1">
        <label className="text-xs font-semibold text-slate-600">{copy.industry}</label>
        <select
          className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm"
          value={data.industry || ""}
          onChange={(e) => onChange({ ...data, industry: e.target.value })}
        >
          <option value="">{copy.selectIndustry}</option>
          {jobRoleCategories.map((cat) => (
            <option key={cat.id} value={cat.id}>
              {cat.name}
            </option>
          ))}
        </select>
      </div>
      <div className="space-y-2">
        <label className="text-xs font-semibold text-slate-600">{copy.jobRoles}</label>
        <div className="grid gap-2 md:grid-cols-3">
          {jobRoles.map((role) => (
            <label
              key={role.id}
              className="flex items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2 text-xs cursor-pointer hover:bg-slate-50"
            >
              <input
                type="checkbox"
                checked={data.jobRoles?.includes(role.id) || false}
                onChange={(e) => {
                  const current = data.jobRoles || [];
                  onChange({
                    ...data,
                    jobRoles: e.target.checked
                      ? [...current, role.id]
                      : current.filter((id) => id !== role.id)
                  });
                }}
                className="rounded border-slate-300"
              />
              <span>{role.nameKey}</span>
            </label>
          ))}
        </div>
      </div>
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <label className="text-xs font-semibold text-slate-600">{copy.experiences}</label>
          <Button type="button" variant="secondary" onClick={addExperience} className="text-xs px-2 py-1">
            <Plus size={14} className="mr-1" /> {copy.addExperience}
          </Button>
        </div>
        {(data.experiences || []).map((exp, idx) => (
          <div key={idx} className="grid gap-3 md:grid-cols-5 p-3 border border-slate-200 rounded-xl">
            <select
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm"
              value={exp.jobRoleId}
              onChange={(e) => updateExperience(idx, "jobRoleId", e.target.value)}
            >
              <option value="">{copy.selectJobRole}</option>
              {jobRoles.map((role) => (
                <option key={role.id} value={role.id}>
                  {role.nameKey}
                </option>
              ))}
            </select>
            <Input
              type="number"
              placeholder={copy.years}
              value={exp.years || ""}
              onChange={(e) => updateExperience(idx, "years", parseInt(e.target.value) || 0)}
            />
            <Input
              type="number"
              placeholder={copy.months}
              value={exp.months || ""}
              onChange={(e) => updateExperience(idx, "months", parseInt(e.target.value) || 0)}
            />
            <Input
              type="number"
              placeholder={copy.days}
              value={exp.days || ""}
              onChange={(e) => updateExperience(idx, "days", parseInt(e.target.value) || 0)}
            />
            <Button
              type="button"
              variant="ghost"
              onClick={() => removeExperience(idx)}
              className="px-2 py-1"
            >
              <Trash2 size={14} />
            </Button>
          </div>
        ))}
      </div>
    </div>
  );
}

// Worker Step 3: Driving License
function WorkerStep3({
  data,
  onChange,
  copy
}: {
  data: Partial<WorkerSignupData>;
  onChange: (data: Partial<WorkerSignupData>) => void;
  copy: any;
}) {
  const handleFileChange = (field: "frontImage" | "backImage", file: File | null) => {
    onChange({
      ...data,
      drivingLicense: {
        ...data.drivingLicense,
        [field]: file || undefined
      } as any
    });
  };

  return (
    <div className="space-y-4">
      <div className="grid gap-4 md:grid-cols-3">
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.licenseNumber}</label>
          <Input
            value={data.drivingLicense?.number || ""}
            onChange={(e) =>
              onChange({
                ...data,
                drivingLicense: { ...data.drivingLicense, number: e.target.value } as any
              })
            }
            placeholder={copy.licenseNumberPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.issueDate}</label>
          <Input
            type="date"
            value={data.drivingLicense?.issueDate || ""}
            onChange={(e) =>
              onChange({
                ...data,
                drivingLicense: { ...data.drivingLicense, issueDate: e.target.value } as any
              })
            }
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.expiryDate}</label>
          <Input
            type="date"
            value={data.drivingLicense?.expiryDate || ""}
            onChange={(e) =>
              onChange({
                ...data,
                drivingLicense: { ...data.drivingLicense, expiryDate: e.target.value } as any
              })
            }
          />
        </div>
      </div>
      <div className="grid gap-4 md:grid-cols-2">
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.frontImage}</label>
          <label className="flex flex-col items-center justify-center rounded-xl border-2 border-dashed border-slate-300 p-6 cursor-pointer hover:bg-slate-50">
            <Upload size={24} className="text-slate-400 mb-2" />
            <span className="text-xs text-slate-600">{copy.uploadFront}</span>
            <input
              type="file"
              accept="image/*"
              className="hidden"
              onChange={(e) => handleFileChange("frontImage", e.target.files?.[0] || null)}
            />
            {data.drivingLicense?.frontImage && (
              <span className="text-xs text-green-600 mt-1">{(data.drivingLicense.frontImage as File).name}</span>
            )}
          </label>
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.backImage}</label>
          <label className="flex flex-col items-center justify-center rounded-xl border-2 border-dashed border-slate-300 p-6 cursor-pointer hover:bg-slate-50">
            <Upload size={24} className="text-slate-400 mb-2" />
            <span className="text-xs text-slate-600">{copy.uploadBack}</span>
            <input
              type="file"
              accept="image/*"
              className="hidden"
              onChange={(e) => handleFileChange("backImage", e.target.files?.[0] || null)}
            />
            {data.drivingLicense?.backImage && (
              <span className="text-xs text-green-600 mt-1">{(data.drivingLicense.backImage as File).name}</span>
            )}
          </label>
        </div>
      </div>
    </div>
  );
}

// Worker Step 4: Certificates
function WorkerStep4({
  data,
  onChange,
  copy
}: {
  data: Partial<WorkerSignupData>;
  onChange: (data: Partial<WorkerSignupData>) => void;
  copy: any;
}) {
  const [showAddModal, setShowAddModal] = useState(false);
  const [selectedCertId, setSelectedCertId] = useState<string>("");
  const [customCertName, setCustomCertName] = useState("");
  const [expiryDate, setExpiryDate] = useState("");
  const [certFile, setCertFile] = useState<File | null>(null);
  const docCopy = useTranslations(DOCUMENT_TEXTS);

  const handleAddCertificate = () => {
    if (!certFile) return;
    
    let certName: string;
    if (selectedCertId === "other") {
      certName = customCertName;
    } else {
      const cert = CERTIFICATES.find(c => c.id === selectedCertId);
      certName = cert ? (docCopy.upload.subtypesLabels[cert.nameKey as keyof typeof docCopy.upload.subtypesLabels] || cert.nameKey) : customCertName;
    }
    
    const certificates = data.certificates || [];
    onChange({
      ...data,
      certificates: [...certificates, {
        id: selectedCertId !== "other" ? selectedCertId : undefined,
        name: certName,
        file: certFile,
        expiryDate: expiryDate || undefined
      }]
    });
    
    // Reset modal
    setShowAddModal(false);
    setSelectedCertId("");
    setCustomCertName("");
    setExpiryDate("");
    setCertFile(null);
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <label className="text-xs font-semibold text-slate-600">{copy.certificates}</label>
        <Button type="button" variant="secondary" onClick={() => setShowAddModal(true)} className="text-xs px-2 py-1">
          <Plus size={14} className="mr-1" /> {copy.addCertificate}
        </Button>
      </div>
      
      {/* List of added certificates (read-only) */}
      <div className="space-y-2">
        {(data.certificates || []).map((cert, idx) => (
          <div key={idx} className="flex items-center justify-between p-3 border border-slate-200 rounded-xl bg-slate-50">
            <div className="flex-1">
              <div className="text-sm font-semibold text-slate-900">{cert.name}</div>
              {cert.expiryDate && (
                <div className="text-xs text-slate-500">Expires: {new Date(cert.expiryDate).toLocaleDateString()}</div>
              )}
              {cert.file && (
                <div className="text-xs text-slate-500">File: {(cert.file as File).name}</div>
              )}
            </div>
            <Button
              type="button"
              variant="ghost"
              onClick={() => {
                onChange({
                  ...data,
                  certificates: (data.certificates || []).filter((_, i) => i !== idx)
                });
              }}
              className="px-2 py-1"
            >
              <Trash2 size={14} />
            </Button>
          </div>
        ))}
        {(!data.certificates || data.certificates.length === 0) && (
          <div className="text-sm text-slate-500 text-center py-4">{copy.noCertificates}</div>
        )}
      </div>

      {/* Add Certificate Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4">
          <Card className="w-full max-w-md">
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>{copy.modal.title}</CardTitle>
              <Button variant="ghost" onClick={() => setShowAddModal(false)} className="px-2 py-1">
                <X size={16} />
              </Button>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <label className="text-xs font-semibold text-slate-600">{copy.modal.selectCertificate}</label>
                <select
                  className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm"
                  value={selectedCertId}
                  onChange={(e) => {
                    setSelectedCertId(e.target.value);
                    setCustomCertName("");
                  }}
                >
                  <option value="">{copy.modal.selectCertificatePlaceholder}</option>
                  {CERTIFICATES.map((cert) => (
                    <option key={cert.id} value={cert.id}>
                      {docCopy.upload.subtypesLabels[cert.nameKey as keyof typeof docCopy.upload.subtypesLabels] || cert.nameKey}
                    </option>
                  ))}
                  <option value="other">{copy.modal.other}</option>
                </select>
              </div>
              
              {selectedCertId === "other" && (
                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-600">{copy.modal.customName}</label>
                  <Input
                    value={customCertName}
                    onChange={(e) => setCustomCertName(e.target.value)}
                    placeholder={copy.modal.customNamePlaceholder}
                  />
                </div>
              )}
              
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-600">{copy.modal.expiryDate}</label>
                <Input
                  type="date"
                  value={expiryDate}
                  onChange={(e) => setExpiryDate(e.target.value)}
                />
              </div>
              
              <div className="space-y-1">
                <label className="text-xs font-semibold text-slate-600">{copy.modal.uploadFile}</label>
                <label className="flex flex-col items-center justify-center rounded-xl border-2 border-dashed border-slate-300 p-4 cursor-pointer hover:bg-slate-50">
                  <Upload size={24} className="text-slate-400 mb-2" />
                  <span className="text-xs text-slate-600">{copy.modal.uploadFilePlaceholder}</span>
                  <input
                    type="file"
                    accept=".pdf,.jpg,.jpeg,.png"
                    className="hidden"
                    onChange={(e) => setCertFile(e.target.files?.[0] || null)}
                  />
                  {certFile && (
                    <span className="text-xs text-green-600 mt-1">{certFile.name}</span>
                  )}
                </label>
              </div>
              
              <div className="flex justify-end gap-2 pt-2">
                <Button variant="secondary" onClick={() => setShowAddModal(false)}>
                  {copy.modal.cancel}
                </Button>
                <Button onClick={handleAddCertificate} disabled={!certFile || (!selectedCertId || (selectedCertId === "other" && !customCertName))}>
                  {copy.modal.add}
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}

// Worker Step 5: Documents
function WorkerStep5({
  data,
  onChange,
  copy
}: {
  data: Partial<WorkerSignupData>;
  onChange: (data: Partial<WorkerSignupData>) => void;
  copy: any;
}) {
  const [showAddModal, setShowAddModal] = useState(false);
  const [selectedDocId, setSelectedDocId] = useState<string>("");
  const [customDocType, setCustomDocType] = useState("");
  const [frontImage, setFrontImage] = useState<File | null>(null);
  const [backImage, setBackImage] = useState<File | null>(null);
  const [docFile, setDocFile] = useState<File | null>(null);
  const docCopy = useTranslations(DOCUMENT_TEXTS);

  // Documents that require front/back images (ID documents)
  const idDocuments = ["drivingLicence", "studentId", "governmentId", "nationalId", "ssnDocument", "birthCertificate"];
  const requiresFrontBack = selectedDocId && idDocuments.includes(selectedDocId);

  // Get all document types from document upload page
  const documentTypes = [
    ...docCopy.upload.subtypes.identity.items.filter((item: string) => item !== "other"),
    ...docCopy.upload.subtypes.address.items.filter((item: string) => item !== "other"),
    ...docCopy.upload.subtypes.workEligibility.items.filter((item: string) => item !== "other"),
    ...docCopy.upload.subtypes.background.items.filter((item: string) => item !== "other")
  ];

  const handleAddDocument = () => {
    if (requiresFrontBack && (!frontImage || !backImage)) return;
    if (!requiresFrontBack && !docFile) return;
    
    const docType = selectedDocId === "other" 
      ? customDocType 
      : docCopy.upload.subtypesLabels[selectedDocId as keyof typeof docCopy.upload.subtypesLabels] || customDocType;
    
    const documents = data.documents || [];
    onChange({
      ...data,
      documents: [...documents, {
        id: selectedDocId !== "other" ? selectedDocId : undefined,
        type: docType,
        frontImage: requiresFrontBack ? frontImage || undefined : undefined,
        backImage: requiresFrontBack ? backImage || undefined : undefined,
        file: !requiresFrontBack ? docFile || undefined : undefined
      }]
    });
    
    // Reset modal
    setShowAddModal(false);
    setSelectedDocId("");
    setCustomDocType("");
    setFrontImage(null);
    setBackImage(null);
    setDocFile(null);
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <label className="text-xs font-semibold text-slate-600">{copy.documents}</label>
        <Button type="button" variant="secondary" onClick={() => setShowAddModal(true)} className="text-xs px-2 py-1">
          <Plus size={14} className="mr-1" /> {copy.addDocument}
        </Button>
      </div>
      
      {/* List of added documents (read-only) */}
      <div className="space-y-2">
        {(data.documents || []).map((doc, idx) => (
          <div key={idx} className="flex items-center justify-between p-3 border border-slate-200 rounded-xl bg-slate-50">
            <div className="flex-1">
              <div className="text-sm font-semibold text-slate-900">{doc.type}</div>
              {doc.frontImage && (
                <div className="text-xs text-slate-500">Front: {(doc.frontImage as File).name}</div>
              )}
              {doc.backImage && (
                <div className="text-xs text-slate-500">Back: {(doc.backImage as File).name}</div>
              )}
              {doc.file && (
                <div className="text-xs text-slate-500">File: {(doc.file as File).name}</div>
              )}
            </div>
            <Button
              type="button"
              variant="ghost"
              onClick={() => {
                onChange({
                  ...data,
                  documents: (data.documents || []).filter((_, i) => i !== idx)
                });
              }}
              className="px-2 py-1"
            >
              <Trash2 size={14} />
            </Button>
          </div>
        ))}
        {(!data.documents || data.documents.length === 0) && (
          <div className="text-sm text-slate-500 text-center py-4">{copy.noDocuments}</div>
        )}
      </div>

      {/* Add Document Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4">
          <Card className="w-full max-w-md max-h-[90vh] overflow-y-auto">
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>{copy.modal.title}</CardTitle>
              <Button variant="ghost" onClick={() => setShowAddModal(false)} className="px-2 py-1">
                <X size={16} />
              </Button>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <label className="text-xs font-semibold text-slate-600">{copy.modal.selectDocument}</label>
                <select
                  className="w-full rounded-2xl border border-slate-200 bg-white px-3 py-2 text-sm"
                  value={selectedDocId}
                  onChange={(e) => {
                    setSelectedDocId(e.target.value);
                    setCustomDocType("");
                    setFrontImage(null);
                    setBackImage(null);
                    setDocFile(null);
                  }}
                >
                  <option value="">{copy.modal.selectDocumentPlaceholder}</option>
                  {documentTypes.map((docType: string) => (
                    <option key={docType} value={docType}>
                      {docCopy.upload.subtypesLabels[docType as keyof typeof docCopy.upload.subtypesLabels] || docType}
                    </option>
                  ))}
                  <option value="other">{copy.modal.other}</option>
                </select>
              </div>
              
              {selectedDocId === "other" && (
                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-600">{copy.modal.customType}</label>
                  <Input
                    value={customDocType}
                    onChange={(e) => setCustomDocType(e.target.value)}
                    placeholder={copy.modal.customTypePlaceholder}
                  />
                </div>
              )}
              
              {requiresFrontBack ? (
                <>
                  <div className="space-y-1">
                    <label className="text-xs font-semibold text-slate-600">{copy.modal.frontImage}</label>
                    <label className="flex flex-col items-center justify-center rounded-xl border-2 border-dashed border-slate-300 p-4 cursor-pointer hover:bg-slate-50">
                      <Upload size={24} className="text-slate-400 mb-2" />
                      <span className="text-xs text-slate-600">{copy.modal.uploadFront}</span>
                      <input
                        type="file"
                        accept="image/*"
                        className="hidden"
                        onChange={(e) => setFrontImage(e.target.files?.[0] || null)}
                      />
                      {frontImage && (
                        <span className="text-xs text-green-600 mt-1">{frontImage.name}</span>
                      )}
                    </label>
                  </div>
                  <div className="space-y-1">
                    <label className="text-xs font-semibold text-slate-600">{copy.modal.backImage}</label>
                    <label className="flex flex-col items-center justify-center rounded-xl border-2 border-dashed border-slate-300 p-4 cursor-pointer hover:bg-slate-50">
                      <Upload size={24} className="text-slate-400 mb-2" />
                      <span className="text-xs text-slate-600">{copy.modal.uploadBack}</span>
                      <input
                        type="file"
                        accept="image/*"
                        className="hidden"
                        onChange={(e) => setBackImage(e.target.files?.[0] || null)}
                      />
                      {backImage && (
                        <span className="text-xs text-green-600 mt-1">{backImage.name}</span>
                      )}
                    </label>
                  </div>
                </>
              ) : selectedDocId && (
                <div className="space-y-1">
                  <label className="text-xs font-semibold text-slate-600">{copy.modal.uploadFile}</label>
                  <label className="flex flex-col items-center justify-center rounded-xl border-2 border-dashed border-slate-300 p-4 cursor-pointer hover:bg-slate-50">
                    <Upload size={24} className="text-slate-400 mb-2" />
                    <span className="text-xs text-slate-600">{copy.modal.uploadFilePlaceholder}</span>
                    <input
                      type="file"
                      accept=".pdf,.jpg,.jpeg,.png"
                      className="hidden"
                      onChange={(e) => setDocFile(e.target.files?.[0] || null)}
                    />
                    {docFile && (
                      <span className="text-xs text-green-600 mt-1">{docFile.name}</span>
                    )}
                  </label>
                </div>
              )}
              
              <div className="flex justify-end gap-2 pt-2">
                <Button variant="secondary" onClick={() => setShowAddModal(false)}>
                  {copy.modal.cancel}
                </Button>
                <Button 
                  onClick={handleAddDocument} 
                  disabled={
                    !selectedDocId || 
                    (selectedDocId === "other" && !customDocType) ||
                    (requiresFrontBack && (!frontImage || !backImage)) ||
                    (!requiresFrontBack && !docFile)
                  }
                >
                  {copy.modal.add}
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}

// Worker Step 6: Address
function WorkerStep6Address({
  data,
  onChange,
  copy
}: {
  data: Partial<WorkerSignupData>;
  onChange: (data: Partial<WorkerSignupData>) => void;
  copy: any;
}) {
  return (
    <div className="space-y-4">
      <div className="grid gap-4 md:grid-cols-2">
        <div className="space-y-1 md:col-span-2">
          <label className="text-xs font-semibold text-slate-600">{copy.street}</label>
          <Input
            value={data.address?.street || ""}
            onChange={(e) =>
              onChange({
                ...data,
                address: { ...data.address, street: e.target.value } as any
              })
            }
            placeholder={copy.streetPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.city}</label>
          <Input
            value={data.address?.city || ""}
            onChange={(e) =>
              onChange({
                ...data,
                address: { ...data.address, city: e.target.value } as any
              })
            }
            placeholder={copy.cityPlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.state}</label>
          <Input
            value={data.address?.state || ""}
            onChange={(e) =>
              onChange({
                ...data,
                address: { ...data.address, state: e.target.value } as any
              })
            }
            placeholder={copy.statePlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.zipCode}</label>
          <Input
            value={data.address?.zipCode || ""}
            onChange={(e) =>
              onChange({
                ...data,
                address: { ...data.address, zipCode: e.target.value } as any
              })
            }
            placeholder={copy.zipCodePlaceholder}
          />
        </div>
        <div className="space-y-1">
          <label className="text-xs font-semibold text-slate-600">{copy.country}</label>
          <Input
            value={data.address?.country || ""}
            onChange={(e) =>
              onChange({
                ...data,
                address: { ...data.address, country: e.target.value } as any
              })
            }
            placeholder={copy.countryPlaceholder}
          />
        </div>
      </div>
    </div>
  );
}

// Worker Step 7: General Availability
function WorkerStep7({
  data,
  onChange,
  copy
}: {
  data: Partial<WorkerSignupData>;
  onChange: (data: Partial<WorkerSignupData>) => void;
  copy: any;
}) {
  const days = ["monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"];

  const toggleDay = (day: string) => {
    const current = data.generalAvailability?.days || [];
    const newDays = current.includes(day)
      ? current.filter((d) => d !== day)
      : [...current, day];
    onChange({
      ...data,
      generalAvailability: { ...data.generalAvailability, days: newDays } as any
    });
  };

  const addTimeWindow = () => {
    const windows = data.generalAvailability?.timeWindows || [];
    onChange({
      ...data,
      generalAvailability: {
        ...data.generalAvailability,
        timeWindows: [...windows, { day: "", startTime: "", endTime: "" }]
      } as any
    });
  };

  return (
    <div className="space-y-4">
      <div className="space-y-2">
        <label className="text-xs font-semibold text-slate-600">{copy.selectDays}</label>
        <div className="grid gap-2 md:grid-cols-7">
          {days.map((day) => (
            <label
              key={day}
              className={[
                "flex items-center justify-center rounded-xl border px-3 py-2 text-xs cursor-pointer",
                (data.generalAvailability?.days || []).includes(day)
                  ? "border-slate-900 bg-slate-900 text-white"
                  : "border-slate-200 bg-white hover:bg-slate-50"
              ].join(" ")}
            >
              <input
                type="checkbox"
                checked={(data.generalAvailability?.days || []).includes(day)}
                onChange={() => toggleDay(day)}
                className="hidden"
              />
              {copy.days[day]}
            </label>
          ))}
        </div>
      </div>
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <label className="text-xs font-semibold text-slate-600">{copy.timeWindows}</label>
          <Button type="button" variant="secondary" onClick={addTimeWindow} className="text-xs px-2 py-1">
            <Plus size={14} className="mr-1" /> {copy.addTimeWindow}
          </Button>
        </div>
        {(data.generalAvailability?.timeWindows || []).map((window, idx) => (
          <div key={idx} className="grid gap-3 md:grid-cols-4 p-3 border border-slate-200 rounded-xl">
            <select
              className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm"
              value={window.day}
              onChange={(e) => {
                const windows = [...(data.generalAvailability?.timeWindows || [])];
                windows[idx] = { ...window, day: e.target.value };
                onChange({
                  ...data,
                  generalAvailability: { ...data.generalAvailability, timeWindows: windows } as any
                });
              }}
            >
              <option value="">{copy.selectDay}</option>
              {days.map((day) => (
                <option key={day} value={day}>
                  {copy.days[day]}
                </option>
              ))}
            </select>
            <Input
              type="time"
              value={window.startTime}
              onChange={(e) => {
                const windows = [...(data.generalAvailability?.timeWindows || [])];
                windows[idx] = { ...window, startTime: e.target.value };
                onChange({
                  ...data,
                  generalAvailability: { ...data.generalAvailability, timeWindows: windows } as any
                });
              }}
            />
            <Input
              type="time"
              value={window.endTime}
              onChange={(e) => {
                const windows = [...(data.generalAvailability?.timeWindows || [])];
                windows[idx] = { ...window, endTime: e.target.value };
                onChange({
                  ...data,
                  generalAvailability: { ...data.generalAvailability, timeWindows: windows } as any
                });
              }}
            />
            <Button
              type="button"
              variant="ghost"
              onClick={() => {
                const windows = (data.generalAvailability?.timeWindows || []).filter((_, i) => i !== idx);
                onChange({
                  ...data,
                  generalAvailability: { ...data.generalAvailability, timeWindows: windows } as any
                });
              }}
              className="px-2 py-1"
            >
              <Trash2 size={14} />
            </Button>
          </div>
        ))}
      </div>
    </div>
  );
}

// Worker Step 8: Custom Shifts
function WorkerStep8({
  data,
  onChange,
  copy
}: {
  data: Partial<WorkerSignupData>;
  onChange: (data: Partial<WorkerSignupData>) => void;
  copy: any;
}) {
  const addShift = () => {
    const shifts = data.customShifts || [];
    onChange({
      ...data,
      customShifts: [...shifts, { date: "", startTime: "", endTime: "", notes: "" }]
    });
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <label className="text-xs font-semibold text-slate-600">{copy.customShifts}</label>
        <Button type="button" variant="secondary" onClick={addShift} className="text-xs px-2 py-1">
          <Plus size={14} className="mr-1" /> {copy.addShift}
        </Button>
      </div>
      {(data.customShifts || []).map((shift, idx) => (
        <div key={idx} className="grid gap-3 md:grid-cols-4 p-3 border border-slate-200 rounded-xl">
          <Input
            type="date"
            value={shift.date}
            onChange={(e) => {
              const shifts = [...(data.customShifts || [])];
              shifts[idx] = { ...shift, date: e.target.value };
              onChange({ ...data, customShifts: shifts });
            }}
          />
          <Input
            type="time"
            placeholder={copy.startTime}
            value={shift.startTime}
            onChange={(e) => {
              const shifts = [...(data.customShifts || [])];
              shifts[idx] = { ...shift, startTime: e.target.value };
              onChange({ ...data, customShifts: shifts });
            }}
          />
          <Input
            type="time"
            placeholder={copy.endTime}
            value={shift.endTime}
            onChange={(e) => {
              const shifts = [...(data.customShifts || [])];
              shifts[idx] = { ...shift, endTime: e.target.value };
              onChange({ ...data, customShifts: shifts });
            }}
          />
          <div className="flex gap-2">
            <Input
              placeholder={copy.notes}
              value={shift.notes || ""}
              onChange={(e) => {
                const shifts = [...(data.customShifts || [])];
                shifts[idx] = { ...shift, notes: e.target.value };
                onChange({ ...data, customShifts: shifts });
              }}
              className="flex-1"
            />
            <Button
              type="button"
              variant="ghost"
              onClick={() => {
                onChange({
                  ...data,
                  customShifts: (data.customShifts || []).filter((_, i) => i !== idx)
                });
              }}
              className="px-2 py-1"
            >
              <Trash2 size={14} />
            </Button>
          </div>
        </div>
      ))}
    </div>
  );
}
