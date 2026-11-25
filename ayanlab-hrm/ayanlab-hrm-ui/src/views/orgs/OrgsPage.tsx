import { StaffingScreen } from "../staffing/StaffingScreen";
import { useOrganizationProfiles } from "../../data/hooks/useStaffing";
import { useAuth } from "../../state/auth";
import { P } from "../../security/permissions";
import type { OrganizationProfile } from "../../api/services/staffing";

export function OrgsPage() {
  const query = useOrganizationProfiles();
  const { user } = useAuth();
  const scopedOrgId = user?.scope?.orgId;
  return (
    <StaffingScreen<OrganizationProfile>
      title="Organizations"
      subtitle="Staffing profile for a platform organization"
      empty="No organization profiles yet."
      read={P.orgsRead}
      create="staffiq:organizations:create"
      update="staffiq:organizations:update"
      remove="staffiq:organizations:delete"
      columns={[
        { key: "name", label: "Name" },
        { key: "email", label: "Email" },
        { key: "timezone", label: "Timezone" },
        { key: "currency", label: "Currency" }
      ]}
      fields={[
        { key: "platformOrganizationId", label: "Platform organization id", readOnly: Boolean(scopedOrgId) },
        { key: "name", label: "Name" },
        { key: "email", label: "Email", type: "email" },
        { key: "phone", label: "Phone" },
        { key: "timezone", label: "Timezone" },
        { key: "currency", label: "Currency" }
      ]}
      blank={() => ({
        platformOrganizationId: scopedOrgId ?? "",
        name: "",
        email: "",
        phone: "",
        timezone: "Europe/Zurich",
        currency: "CHF"
      })}
      query={query}
    />
  );
}
