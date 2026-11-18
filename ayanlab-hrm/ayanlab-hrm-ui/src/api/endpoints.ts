export const endpoints = {
  auth: {
    login: "/auth/login",
    me: "/me",
    signup: "/auth/signup",
    validation: "/auth/validation"
  },
  orgs: {
    list: "/orgs",
    detail: (id: string) => `/orgs/${id}`
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
    list: "/workers"
  },
  documents: {
    list: "/documents",
    reviews: "/admin/document-reviews"
  },
  finance: {
    invoices: "/finance/invoices",
    payouts: "/finance/payouts"
  },
  audit: {
    list: "/admin/audit",
    user: "/admin/audit/user",
    // System audit is served by the auth service (authHttp) at /system-audit
    system: "/system-audit"
  },
  jobs: {
    industries: "/job-category/list/id-name",
    rolesByCategory: (categoryId: string) => `/job-role/category/${categoryId}/active`
  },
  jobRoles: {
    list: "/job-role",
    create: "/job-role",
    update: (id: string) => `/job-role/${id}`,
    delete: (id: string) => `/job-role/${id}`
  },
  jobCategories: {
    listAll: "/job-category/list-all",
    create: "/job-category",
    update: (id: string) => `/job-category/${id}`,
    delete: (id: string) => `/job-category/${id}`
  },
  maps: {
    search: (address: string) => `/google-maps/search?address=${encodeURIComponent(address)}`
  },
  certificates: {
    list: "/certificate/list",
    listByJobRole: (jobRoleId: string, language: string) =>
      `/certificate/list/by-job-role?jobRoleId=${encodeURIComponent(jobRoleId)}&language=${encodeURIComponent(language)}`,
    create: "/certificate",
    update: (id: string) => `/certificate/${id}`,
    delete: (id: string) => `/certificate/${id}`,
    byJobRole: (jobRoleId: string) => `/certificate/by-job-role?jobRoleId=${jobRoleId}`
  },
  skills: {
    list: "/skill/list",
    listByJobRole: (jobRoleId: string, language: string) =>
      `/skill/list/by-job-role?jobRoleId=${encodeURIComponent(jobRoleId)}&language=${encodeURIComponent(language)}`,
    create: "/skill",
    update: (id: string) => `/skill/${id}`,
    delete: (id: string) => `/skill/${id}`
  },
  documentTypes: {
    list: "/document-type/list",
    create: "/document-type",
    update: (id: string) => `/document-type/${id}`,
    delete: (id: string) => `/document-type/${id}`
  },
  dashboard: {
    widgets: "/dashboard/widgets",
    defaults: "/dashboard/defaults",
    config: "/dashboard/config"
  }
} as const;
