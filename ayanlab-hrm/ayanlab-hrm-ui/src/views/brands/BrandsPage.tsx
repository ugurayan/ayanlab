import { StaffingScreen } from "../staffing/StaffingScreen";
import { useBrands } from "../../data/hooks/useStaffing";
import { useAuth } from "../../state/auth";
import type { Brand } from "../../api/services/staffing";

export function BrandsPage() {
  const query = useBrands();
  const { user } = useAuth();
  return (
    <StaffingScreen<Brand>
      title="Brands"
      subtitle="An organization can have no brands, or several brands in different areas"
      empty="This organization has no brands. That is allowed."
      read="staffiq:brands:read"
      create="staffiq:brands:create"
      update="staffiq:brands:update"
      remove="staffiq:brands:delete"
      columns={[
        { key: "name", label: "Name" },
        { key: "area", label: "Area" }
      ]}
      fields={[
        { key: "platformOrganizationId", label: "Platform organization id", readOnly: Boolean(user?.scope?.orgId) },
        { key: "name", label: "Name" },
        { key: "area", label: "Area" }
      ]}
      blank={() => ({
        platformOrganizationId: user?.scope?.orgId ?? "",
        name: "",
        area: ""
      })}
      query={query}
    />
  );
}
