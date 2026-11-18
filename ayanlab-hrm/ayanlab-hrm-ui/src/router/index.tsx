import { createBrowserRouter, Navigate } from "react-router-dom";
import { ProtectedRoute } from "./guards";
import { AppShell } from "../ui/layout/AppShell";
import { LoginPage } from "../views/auth/login/LoginPage";
import { SignupPage } from "../views/auth/signup/SignupPage";
import { ForbiddenPage } from "../views/ForbiddenPage";
import { NotFoundPage } from "../views/NotFoundPage";
import { DashboardPage } from "../views/dashboard/DashboardPage";
import { InboxPage } from "../views/inbox/InboxPage";
import { TasksPage } from "../views/tasks/TasksPage";
import { JobsExplorePage } from "../views/jobs/JobsExplorePage";
import { JobsCreatePage } from "../views/jobs/JobsCreatePage";
import { JobCreationWizard } from "../views/jobs/JobCreationWizard";
import { SettingsPage } from "../views/settings/SettingsPage";
import { OrgsPage } from "../views/orgs/OrgsPage";
import { LocationsPage } from "../views/locations/LocationsPage";
import { ShiftsPage } from "../views/shifts/ShiftsPage";
import { CreateShiftPage } from "../views/shifts/CreateShiftPage";
import { ApplicationsPage } from "../views/applications/ApplicationsPage";
import { WorkersPage } from "../views/workers/WorkersPage";
import { DocumentsPage } from "../views/documents/DocumentsPage";
import { UploadDocumentPage } from "../views/documents/UploadDocumentPage";
import { FinancePage } from "../views/finance/FinancePage";
import { TransactionsPage } from "../views/finance/TransactionsPage";
import { UserAuditPage } from "../views/audit/UserAuditPage";
import { SystemAuditPage } from "../views/audit/SystemAuditPage";
import { ProfilePage } from "../views/profile/ProfilePage";
import { CreateProfilePage } from "../views/profile/CreateProfilePage";
import { SupportLayout } from "../views/support/SupportLayout";
import { HelpCenterPage } from "../views/support/HelpCenterPage";
import { CancellationPolicyPage } from "../views/support/CancellationPolicyPage";
import { JobRolesList } from "../views/job-roles/JobRolesList";
import { JobRoleCategoriesList } from "../views/job-role-categories/JobRoleCategoriesList";
import { MobileVerificationPage } from "../views/verification/MobileVerificationPage";
import { EmailVerificationPage } from "../views/verification/EmailVerificationPage";
import { DocumentVerificationPage } from "../views/verification/DocumentVerificationPage";
import { SkillsPage } from "../views/skills/SkillsPage";
import { CertificatesPage } from "../views/certificates/CertificatesPage";
import { DocumentTypesPage } from "../views/document-types/DocumentTypesPage";
import { UploadFilePage } from "../views/upload/UploadFilePage";

export const router = createBrowserRouter([
  { path: "/login", element: <LoginPage /> },
  { path: "/signup", element: <SignupPage /> },
  { path: "/forbidden", element: <ForbiddenPage /> },
  { path: "/verify/mobile", element: <MobileVerificationPage /> },
  { path: "/verify/email", element: <EmailVerificationPage /> },
  {
    path: "/app",
    element: (
      <ProtectedRoute>
        <AppShell />
      </ProtectedRoute>
    ),
    children: [
      { index: true, element: <Navigate to="/app/dashboard" replace /> },
      { path: "dashboard", element: <DashboardPage /> },
      { path: "tasks", element: <TasksPage /> },
      { path: "jobs", element: <JobsExplorePage /> },
      { path: "jobs/new", element: <JobCreationWizard /> },
      { path: "jobs/create", element: <JobsCreatePage /> },
      { path: "inbox", element: <InboxPage /> },
      { path: "orgs", element: <OrgsPage /> },
      { path: "locations", element: <LocationsPage /> },
      { path: "shifts", element: <ShiftsPage /> },
      { path: "shifts/new", element: <CreateShiftPage /> },
      { path: "applications", element: <ApplicationsPage /> },
      { path: "workers", element: <WorkersPage /> },
      { path: "documents", element: <DocumentsPage /> },
      { path: "documents/upload", element: <UploadDocumentPage /> },
      { path: "upload", element: <UploadFilePage /> },
      { path: "finance", element: <FinancePage /> },
      { path: "transactions", element: <TransactionsPage /> },
      { path: "audit", element: <Navigate to="/app/audit/user" replace /> },
      { path: "audit/user", element: <UserAuditPage /> },
      { path: "audit/system", element: <SystemAuditPage /> },
      { path: "verification", element: <DocumentVerificationPage /> },
      {
        path: "support",
        element: <SupportLayout />,
        children: [
          { index: true, element: <HelpCenterPage /> },
          { path: "cancellation-policy", element: <CancellationPolicyPage /> },
        ],
      },
      { path: "settings", element: <SettingsPage /> },
      { path: "profile", element: <ProfilePage /> },
      { path: "profile/new", element: <CreateProfilePage /> },
      { path: "job-roles", element: <JobRolesList /> },
      { path: "industries", element: <JobRoleCategoriesList /> },
      { path: "skills", element: <SkillsPage /> },
      { path: "certificates", element: <CertificatesPage /> },
      { path: "document-types", element: <DocumentTypesPage /> }
    ]
  },
  { path: "/", element: <Navigate to="/app" replace /> },
  { path: "*", element: <NotFoundPage /> }
]);
