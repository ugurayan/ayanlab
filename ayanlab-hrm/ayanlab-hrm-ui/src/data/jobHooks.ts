import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { jobApi } from "../mock/jobApi";
import type { JobRole, Skill, DressCode, ContractType, ContactType, FreeTimeWindow, ExpertiseLevel, Certificate, JobDocument, JobTemplate } from "../types/jobs";
import { apiGetIndustries, apiGetJobRolesByCategory, apiGetCertificatesByJobRole, apiGetSkillsByJobRole } from "../api/services/jobs";
import { apiGetJobRoleWages } from "../api/services/jobRoleWages";
import { useLanguage } from "../state/i18n";

const USE_MOCKS = (import.meta.env.VITE_USE_MOCKS ?? "false") === "true";

export const useJobRoles = () => {
  return useQuery({
    queryKey: ["jobRoles"],
    queryFn: () => jobApi.getJobRoles(),
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

export const useJobRole = (id: string) => {
  return useQuery({
    queryKey: ["jobRole", id],
    queryFn: () => jobApi.getJobRoleById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000,
  });
};

export const useSkills = () => {
  return useQuery({
    queryKey: ["skills"],
    queryFn: () => jobApi.getSkills(),
    staleTime: 5 * 60 * 1000,
  });
};

/** GET /api/v1/skill/list/by-job-role?jobRoleId=...&language=... - skills for job role (multi-select options). Always uses real API when jobRoleId is set. */
export const useSkillsByJobRole = (jobRoleId: string | null, language: string) => {
  return useQuery({
    queryKey: ["skills", "byJobRole", jobRoleId, language],
    queryFn: () => {
      if (!jobRoleId) return Promise.resolve([]);
      // Always call real API so required skills list comes from backend
      return apiGetSkillsByJobRole(jobRoleId, language);
    },
    enabled: !!jobRoleId,
    staleTime: 5 * 60 * 1000,
  });
};

export const useSkillsByCategory = (category: string) => {
  return useQuery({
    queryKey: ["skills", "category", category],
    queryFn: () => jobApi.getSkillsByCategory(category),
    enabled: !!category,
    staleTime: 5 * 60 * 1000,
  });
};

export const useSkillsByIds = (ids: string[]) => {
  return useQuery({
    queryKey: ["skills", "ids", ids],
    queryFn: () => jobApi.getSkillsByIds(ids),
    enabled: ids.length > 0,
    staleTime: 5 * 60 * 1000,
  });
};

export const useDressCodes = () => {
  return useQuery({
    queryKey: ["dressCodes"],
    queryFn: () => jobApi.getDressCodes(),
    staleTime: 5 * 60 * 1000,
  });
};

export const useDressCode = (id: string) => {
  return useQuery({
    queryKey: ["dressCode", id],
    queryFn: () => jobApi.getDressCodeById(id),
    enabled: !!id,
    staleTime: 5 * 60 * 1000,
  });
};

export const useContractTypes = () => {
  return useQuery({
    queryKey: ["contractTypes"],
    queryFn: () => jobApi.getContractTypes(),
    staleTime: 5 * 60 * 1000,
  });
};

export const useContractTypesWithLanguage = () => {
  const { language } = useLanguage();
  return useQuery({
    queryKey: ["contractTypes", language],
    queryFn: () => jobApi.getContractTypes(),
    staleTime: ONE_DAY_MS,
    gcTime: ONE_DAY_MS,
  });
};

export const useContactTypes = () => {
  return useQuery({
    queryKey: ["contactTypes"],
    queryFn: () => jobApi.getContactTypes(),
    staleTime: 5 * 60 * 1000,
  });
};

export const useFreeTimeWindows = () => {
  return useQuery({
    queryKey: ["freeTimeWindows"],
    queryFn: () => jobApi.getFreeTimeWindows(),
    staleTime: 5 * 60 * 1000,
  });
};

export const useExpertiseLevels = () => {
  return useQuery({
    queryKey: ["expertiseLevels"],
    queryFn: () => jobApi.getExpertiseLevels(),
    staleTime: 5 * 60 * 1000,
  });
};

/** GET /api/v1/certificate/list/by-job-role?jobRoleId=...&language=... - uses real API when roleId set; when USE_MOCKS uses mock with sample data. */
export const useCertificatesByRole = (roleId: string | null, language: string) => {
  return useQuery({
    queryKey: ["certificates", "role", roleId, language],
    queryFn: () => {
      if (!roleId) return Promise.resolve([]);
      if (USE_MOCKS) return jobApi.getCertificatesByRole(roleId);
      return apiGetCertificatesByJobRole(roleId, language);
    },
    enabled: !!roleId,
    staleTime: 5 * 60 * 1000,
  });
};

/** Documents by job role – mock API returns { id, name }[] with sample data; real API can be wired later. */
export const useDocumentsByRole = (roleId: string | null) => {
  return useQuery({
    queryKey: ["documents", "role", roleId],
    queryFn: () => {
      if (!roleId) return Promise.resolve([]);
      return jobApi.getDocumentsByRole(roleId);
    },
    enabled: !!roleId,
    staleTime: 5 * 60 * 1000,
  });
};

export const useJobTemplates = (orgId: string) => {
  return useQuery({
    queryKey: ["jobTemplates", "org", orgId],
    queryFn: () => jobApi.getJobTemplatesByOrg(orgId),
    enabled: !!orgId,
    staleTime: 5 * 60 * 1000,
  });
};

export const usePredefinedJobTemplates = () => {
  return useQuery({
    queryKey: ["jobTemplates", "predefined"],
    queryFn: () => jobApi.getPredefinedJobTemplates(),
    staleTime: 5 * 60 * 1000,
  });
};

export const useCreateJobTemplate = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (template: Omit<JobTemplate, "id">) => jobApi.createJobTemplate(template),
    onSuccess: (newTemplate) => {
      if (newTemplate.orgId) {
        queryClient.invalidateQueries({
          queryKey: ["jobTemplates", "org", newTemplate.orgId],
        });
      } else {
        queryClient.invalidateQueries({
          queryKey: ["jobTemplates", "predefined"],
        });
      }
    },
  });
};

const ONE_DAY_MS = 24 * 60 * 60 * 1000;

export const useJobIndustries = () => {
  const { language } = useLanguage();
  return useQuery({
    queryKey: ["jobIndustries", language],
    queryFn: () => apiGetIndustries(language),
    staleTime: ONE_DAY_MS,
    gcTime: ONE_DAY_MS,
  });
};

export const useJobRolesByCategory = (categoryId: string | null) => {
  return useQuery({
    queryKey: ["jobRolesByCategory", categoryId],
    queryFn: () => {
      if (!categoryId) {
        throw new Error("Category ID is required");
      }
      return apiGetJobRolesByCategory(categoryId);
    },
    enabled: !!categoryId, // Only fetch when categoryId is provided
    staleTime: ONE_DAY_MS,
    gcTime: ONE_DAY_MS,
  });
};

/** GET /api/v1/job-roles/wages/{zipcode} - min, max, avg hourly for job profile in area */
export const useJobRoleWages = (zipcode: string) => {
  return useQuery({
    queryKey: ["jobRoleWages", zipcode],
    queryFn: () => apiGetJobRoleWages(zipcode),
    enabled: !!zipcode?.trim(),
    staleTime: 10 * 60 * 1000,
  });
};
