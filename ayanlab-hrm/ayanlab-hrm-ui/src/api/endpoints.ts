export const endpoints = {
  auth: {
    login: "/auth/api/login",
    me: "/auth/api/me",
    signup: "/auth/api/register",
    validation: "/auth/api/validation"
  },
  orgs: {
    list: "/organizations",
    detail: (id: string) => `/organizations/${id}`
  },
  locations: {
    list: "/locations"
  },
  shifts: {
    list: "/shifts",
    create: "/shifts",
    detail: (id: string) => `/shifts/${id}`
  },
  applications: {
    list: "/applications"
  },
  workers: {
    list: "/workers",
    detail: (id: string) => `/workers/${id}`
  },
  documents: {
    list: "/documents"
  },
  finance: {
    invoices: "/finance-records"
  },
  audit: {
    list: "/user-audits",
    user: "/user-audits",
    system: "/system-audits"
  },
  resources: {
    jobs: "/jobs",
    tasks: "/tasks",
    transactions: "/transactions",
    financeRecords: "/finance-records",
    documentVerifications: "/document-verifications",
    documentVerification: (id: string) => `/document-verifications/${id}`,
    userAudits: "/user-audits",
    systemAudits: "/system-audits",
    departments: "/departments",
    profiles: "/profiles"
  },
  jobs: {
    industries: "/job-role-categories",
    roles: "/job-roles",
    contractTypes: "/contract-types",
    jobDocuments: "/job-documents",
    rolesByCategory: (categoryId: string) => `/job-roles?categoryId=${encodeURIComponent(categoryId)}`
  },
  jobRoles: {
    list: "/job-roles",
    create: "/job-roles",
    update: (id: string) => `/job-roles/${id}`,
    delete: (id: string) => `/job-roles/${id}`
  },
  jobCategories: {
    listAll: "/job-role-categories",
    create: "/job-role-categories",
    update: (id: string) => `/job-role-categories/${id}`,
    delete: (id: string) => `/job-role-categories/${id}`
  },
  maps: {
    search: (address: string) => `/google-maps/search?address=${encodeURIComponent(address)}`
  },
  certificates: {
    list: "/certificates",
    listByJobRole: (jobRoleId: string, language: string) =>
      `/certificates?jobRoleId=${encodeURIComponent(jobRoleId)}&language=${encodeURIComponent(language)}`,
    create: "/certificates",
    update: (id: string) => `/certificates/${id}`,
    delete: (id: string) => `/certificates/${id}`,
    byJobRole: (jobRoleId: string) => `/certificates?jobRoleId=${jobRoleId}`
  },
  skills: {
    list: "/skills",
    listByJobRole: (jobRoleId: string, language: string) =>
      `/skills?jobRoleId=${encodeURIComponent(jobRoleId)}&language=${encodeURIComponent(language)}`,
    create: "/skills",
    update: (id: string) => `/skills/${id}`,
    delete: (id: string) => `/skills/${id}`
  },
  documentTypes: {
    list: "/documents",
    create: "/documents",
    update: (id: string) => `/documents/${id}`,
    delete: (id: string) => `/documents/${id}`
  },
  dashboard: {
    widgets: "/dashboard/widgets",
    defaults: "/dashboard/defaults",
    config: "/dashboard/config"
  }
} as const;