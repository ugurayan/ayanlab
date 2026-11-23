/**
 * Export UI mock stores → backend JSON seed.
 * Run from repo root:
 *   cd ayanlab-hrm/ayanlab-hrm-ui && npx --yes tsx ../scripts/export-staffiq-mock-data.mts
 */
import { mkdirSync, writeFileSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

import { db } from "../ayanlab-hrm-ui/src/mock/mockDb.ts";
import {
  JOB_ROLES,
  SKILLS,
  DRESS_CODES,
  CONTRACT_TYPES,
  CONTACT_TYPES,
  FREE_TIME_WINDOWS,
  EXPERTISE_LEVELS,
  MOCK_CERTIFICATES_LIST,
  MOCK_DOCUMENTS_LIST,
  CERTIFICATES,
  DOCUMENTS,
  JOB_TEMPLATES,
} from "../ayanlab-hrm-ui/src/mock/jobData.ts";
import { departments } from "../ayanlab-hrm-ui/src/mock/departmentData.ts";

const here = dirname(fileURLToPath(import.meta.url));
const out = resolve(here, "../ayanlab-hrm-backend/src/main/resources/db/staffiq-mock-data.json");

const bundle = {
  version: 1,
  orgs: db.orgs,
  locations: db.locations,
  shifts: db.shifts,
  applications: db.applications,
  workers: db.workers,
  jobProfiles: db.jobProfiles,
  documents: db.documents,
  invoices: db.invoices,
  audit: db.audit,
  auditUser: db.auditUser,
  auditSystem: db.auditSystem,
  tasks: db.tasks,
  jobs: db.jobs,
  transactions: db.transactions,
  verificationTasks: db.verificationTasks,
  verificationRequests: db.verificationRequests,
  departments,
  jobCatalog: {
    jobRoles: JOB_ROLES,
    skills: SKILLS,
    dressCodes: DRESS_CODES,
    contractTypes: CONTRACT_TYPES,
    contactTypes: CONTACT_TYPES,
    freeTimeWindows: FREE_TIME_WINDOWS,
    expertiseLevels: EXPERTISE_LEVELS,
    mockCertificatesList: MOCK_CERTIFICATES_LIST,
    mockDocumentsList: MOCK_DOCUMENTS_LIST,
    certificates: CERTIFICATES,
    documents: DOCUMENTS,
    jobTemplates: JOB_TEMPLATES,
  },
};

mkdirSync(dirname(out), { recursive: true });
writeFileSync(out, JSON.stringify(bundle, null, 2), "utf8");
console.log(`Wrote ${out} (${(JSON.stringify(bundle).length / 1024).toFixed(1)} KB)`);