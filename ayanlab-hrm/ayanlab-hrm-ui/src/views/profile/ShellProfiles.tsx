import { Link } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { AccountSecurity } from "./AccountSecurity";

export function EmployerProfile() {
  return (
    <AccountSecurity
      title="Company profile"
      description="Account for the company admin profile. Worker cards stay on the worker profile."
    >
      <Card>
        <CardHeader>
          <CardTitle>What’s new</CardTitle>
        </CardHeader>
        <CardContent className="text-sm text-slate-600">
          Switching profile changes the menu and this page without signing out. Wage and cost, dress codes, auto-hire, and stop-hiring are not in this product yet.
        </CardContent>
      </Card>
    </AccountSecurity>
  );
}

export function AdminProfile() {
  return (
    <AccountSecurity
      title="System admin profile"
      description="Account for the system admin profile. Catalogs stay in the admin menu."
    >
      <Card>
        <CardHeader>
          <CardTitle>Catalogs</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-2 text-sm">
          <Link className="text-slate-700 underline" to="/app/verification">Verification</Link>
          <Link className="text-slate-700 underline" to="/app/job-roles">Job roles</Link>
          <Link className="text-slate-700 underline" to="/app/industries">Industries</Link>
          <Link className="text-slate-700 underline" to="/app/skills">Skills</Link>
          <Link className="text-slate-700 underline" to="/app/certificates">Certificates</Link>
          <Link className="text-slate-700 underline" to="/app/document-types">Document types</Link>
        </CardContent>
      </Card>
    </AccountSecurity>
  );
}
