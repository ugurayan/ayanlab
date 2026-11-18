export const demoAccounts = [
  {
    email: "next.is.future@icloud.com",
    role: "PLATFORM_ADMIN",
    mainRole: "System Admin"
  },
  {
    email: "admin@acme.test",
    role: "ORG_ADMIN",
    mainRole: "Business Admin"
  },
  {
    email: "manager@acme.test",
    role: "LOCATION_MANAGER",
    mainRole: "Business Member"
  },
  {
    email: "finance@acme.test",
    role: "ORG_FINANCE",
    mainRole: "Business Admin"
  },
  {
    email: "agent@staffing.test",
    role: "AGENT_USER",
    mainRole: "Staff"
  },
  {
    email: "notApproved@platform.test",
    role: "WORKER",
    mainRole: "Worker"
  }
] as const;