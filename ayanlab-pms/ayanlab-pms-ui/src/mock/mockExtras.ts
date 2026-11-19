import { db } from "./mockDb";

const delay = (ms = 200) => new Promise((r) => setTimeout(r, ms));

export async function fetchListings() {
  await delay();
  return [...db.propertyListings];
}
export async function fetchProspects() {
  await delay();
  return [...db.prospects];
}
export async function fetchApplications() {
  await delay();
  return [...db.rentalApplications];
}
export async function fetchRenewals() {
  await delay();
  return [...db.leaseRenewals];
}
export async function fetchDraftLeases() {
  await delay();
  return [...db.draftLeases];
}
export async function fetchAssociationUnits() {
  await delay();
  return [...db.associationUnits];
}
export async function fetchAssessmentPlans() {
  await delay();
  return [...db.assessmentPlans];
}
export async function fetchWorkOrders() {
  await delay();
  return [...db.workOrders];
}
export async function fetchVendors() {
  await delay();
  return [...db.vendors];
}
export async function fetchInspections() {
  await delay();
  return [...db.inspections];
}
export async function fetchTasks() {
  await delay();
  return [...db.tasks];
}
export async function fetchRecurringTasks() {
  await delay();
  return [...db.recurringTasks];
}
export async function fetchAnnouncements() {
  await delay();
  return [...db.announcements];
}
export async function fetchEmailTemplates() {
  await delay();
  return [...db.emailTemplates];
}
export async function fetchSmsLogs() {
  await delay();
  return [...db.smsLogs];
}
export async function fetchMailings() {
  await delay();
  return [...db.mailings];
}
export async function fetchInboxMessages() {
  await delay();
  return [...db.inboxMessages];
}
export async function fetchFilesTree() {
  await delay();
  return [...db.filesTree];
}
export async function fetchReportCatalog() {
  await delay();
  return [...db.reportCatalog];
}
export async function fetchMarketplace() {
  await delay();
  return { apps: [...db.marketplaceApps], addons: [...db.addons] };
}
export async function fetchParcels() {
  await delay();
  return [...db.parcels];
}
export async function fetchPatrolRoutes() {
  await delay();
  return [...db.patrolRoutes];
}
export async function fetchAccessEvents() {
  await delay();
  return [...db.accessEvents];
}
export async function fetchTimesheets() {
  await delay();
  return [...db.timesheets];
}
export async function fetchWhatsappTemplates() {
  await delay();
  return [...db.whatsappTemplates];
}
export async function fetchPublicSite() {
  await delay();
  return { ...db.publicSite };
}
export async function fetchOrgUsers() {
  await delay();
  return [...db.orgUsers];
}
export async function fetchCommercialLeaseTerms() {
  await delay();
  return [...db.commercialLeaseTerms];
}
export async function fetchCooperativeShareRecords() {
  await delay();
  return [...db.cooperativeShareRecords];
}
export async function fetchCoopBoardPackets() {
  await delay();
  return [...db.coopBoardPackets];
}