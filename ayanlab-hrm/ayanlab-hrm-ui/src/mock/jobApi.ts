import { JOB_ROLES, SKILLS, DRESS_CODES, CONTRACT_TYPES, CONTACT_TYPES, FREE_TIME_WINDOWS, EXPERTISE_LEVELS, CERTIFICATES, DOCUMENTS, JOB_TEMPLATES, MOCK_CERTIFICATES_LIST, MOCK_DOCUMENTS_LIST } from "./jobData";
import type { JobRole, Skill, DressCode, ContractType, ContactType, FreeTimeWindow, ExpertiseLevel, Certificate, JobDocument, JobTemplate } from "../types/jobs";

// Mock API delay
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export const jobApi = {
  // Job Roles
  async getJobRoles(): Promise<JobRole[]> {
    await delay(300);
    return JOB_ROLES;
  },

  async getJobRoleById(id: string): Promise<JobRole | null> {
    await delay(200);
    return JOB_ROLES.find(role => role.id === id) || null;
  },

  // Skills
  async getSkills(): Promise<Skill[]> {
    await delay(300);
    return SKILLS;
  },

  async getSkillsByCategory(category: string): Promise<Skill[]> {
    await delay(250);
    return SKILLS.filter(skill => skill.category === category);
  },

  async getSkillsByIds(ids: string[]): Promise<Skill[]> {
    await delay(200);
    return SKILLS.filter(skill => ids.includes(skill.id));
  },

  /** Skills for job role (id + name). Mock: returns skills linked to role's requiredSkills. */
  async getSkillsByJobRole(roleId: string): Promise<{ id: string; name: string }[]> {
    await delay(250);
    const role = JOB_ROLES.find(r => r.id === roleId);
    if (!role) return [];
    return SKILLS.filter(s => role.requiredSkills.includes(s.id))
      .map(s => ({ id: s.id, name: s.nameKey }));
  },

  // Dress Codes
  async getDressCodes(): Promise<DressCode[]> {
    await delay(300);
    return DRESS_CODES;
  },

  async getDressCodeById(id: string): Promise<DressCode | null> {
    await delay(200);
    return DRESS_CODES.find(dress => dress.id === id) || null;
  },

  // Contract Types
  async getContractTypes(): Promise<ContractType[]> {
    await delay(200);
    return CONTRACT_TYPES;
  },

  // Contact Types
  async getContactTypes(): Promise<ContactType[]> {
    await delay(200);
    return CONTACT_TYPES;
  },

  // Free Time Windows
  async getFreeTimeWindows(): Promise<FreeTimeWindow[]> {
    await delay(200);
    return FREE_TIME_WINDOWS;
  },

  // Expertise Levels
  async getExpertiseLevels(): Promise<ExpertiseLevel[]> {
    await delay(200);
    return EXPERTISE_LEVELS;
  },

  // Certificates – mock API returns same shape as real API { id, name } for any job role (so dropdown is always populated)
  async getCertificatesByRole(roleId: string): Promise<{ id: string; name: string }[]> {
    await delay(200);
    return MOCK_CERTIFICATES_LIST;
  },

  async getAllCertificates(): Promise<Certificate[]> {
    await delay(250);
    return CERTIFICATES;
  },

  // Documents – mock API returns same shape { id, name } for any job role (so dropdown is always populated)
  async getDocumentsByRole(roleId: string): Promise<{ id: string; name: string }[]> {
    await delay(200);
    return MOCK_DOCUMENTS_LIST;
  },

  async getAllDocuments(): Promise<JobDocument[]> {
    await delay(250);
    return DOCUMENTS;
  },

  // Job Templates
  async getJobTemplatesByOrg(orgId: string): Promise<JobTemplate[]> {
    await delay(250);
    return JOB_TEMPLATES.filter(template => template.orgId === orgId);
  },

  async getPredefinedJobTemplates(): Promise<JobTemplate[]> {
    await delay(250);
    return JOB_TEMPLATES.filter(template => !template.orgId);
  },

  async createJobTemplate(template: Omit<JobTemplate, "id">): Promise<JobTemplate> {
    await delay(300);
    const newTemplate: JobTemplate = {
      id: `tpl_${Date.now()}`,
      ...template,
    };
    JOB_TEMPLATES.push(newTemplate);
    return newTemplate;
  }
};
