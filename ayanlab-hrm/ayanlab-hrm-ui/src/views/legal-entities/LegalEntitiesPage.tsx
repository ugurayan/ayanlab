import { StaffingScreen } from "../staffing/StaffingScreen";
import { useLegalEntities } from "../../data/hooks/useStaffing";
import { useAuth } from "../../state/auth";
import type { LegalEntity } from "../../api/services/staffing";

export function LegalEntitiesPage() {
  const query = useLegalEntities();
  const { user } = useAuth();
  return (
    <StaffingScreen<LegalEntity>
      title="Legal entities"
      subtitle="Legal names and VAT numbers for the organization"
      empty="No legal entities yet."
      read="staffiq:legal_entities:read"
      create="staffiq:legal_entities:create"
      update="staffiq:legal_entities:update"
      remove="staffiq:legal_entities:delete"
      columns={[
        { key: "legalName", label: "Legal name" },
        { key: "entityType", label: "Type" },
        { key: "vatNumber", label: "VAT" },
        { key: "email", label: "Email" }
      ]}
      fields={[
        { key: "platformOrganizationId", label: "Platform organization id", readOnly: Boolean(user?.scope?.orgId) },
        { key: "legalName", label: "Legal name" },
        { key: "entityType", label: "Entity type" },
        { key: "vatNumber", label: "VAT number" },
        { key: "email", label: "Email", type: "email" },
        { key: "phone", label: "Phone" }
      ]}
      blank={() => ({
        platformOrganizationId: user?.scope?.orgId ?? "",
        legalName: "",
        entityType: "AG",
        vatNumber: "",
        email: "",
        phone: ""
      })}
      query={query}
    />
  );
}
