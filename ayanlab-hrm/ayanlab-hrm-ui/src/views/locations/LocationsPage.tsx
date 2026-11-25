import { useState } from "react";
import { StaffingScreen } from "../staffing/StaffingScreen";
import { useWorkLocations } from "../../data/hooks/useStaffing";
import { apiSearchGooglePlaces } from "../../api/services/locations";
import { Button } from "../../ui/components/Button";
import { Input } from "../../ui/components/Input";
import { P } from "../../security/permissions";
import type { WorkLocation } from "../../api/services/staffing";

export function LocationsPage() {
  const query = useWorkLocations();
  const [search, setSearch] = useState("");
  const [hits, setHits] = useState<Awaited<ReturnType<typeof apiSearchGooglePlaces>>>([]);

  return (
    <StaffingScreen<WorkLocation>
      title="Locations"
      subtitle="A location can belong to an organization, a brand, a legal entity, a profile, a user, or a bank"
      empty="No locations yet."
      read={P.locationsRead}
      create="staffiq:locations:create"
      update="staffiq:locations:update"
      remove="staffiq:locations:delete"
      columns={[
        { key: "name", label: "Name" },
        { key: "referenceEntity", label: "Attached to" },
        { key: "city", label: "City" },
        { key: "formattedAddress", label: "Address" }
      ]}
      fields={[
        { key: "referenceEntity", label: "Reference type (ORGANIZATION, BRAND, LEGAL_ENTITY, PROFILE, USER, BANK)" },
        { key: "referenceId", label: "Reference id" },
        { key: "name", label: "Name" },
        { key: "formattedAddress", label: "Formatted address" },
        { key: "street", label: "Street" },
        { key: "city", label: "City" },
        { key: "state", label: "State" },
        { key: "zipCode", label: "Postal code" },
        { key: "country", label: "Country" },
        { key: "placeId", label: "Place id" },
        { key: "latitude", label: "Latitude" },
        { key: "longitude", label: "Longitude" },
        { key: "timezone", label: "Timezone override" },
        { key: "notes", label: "Parking and directions" }
      ]}
      blank={() => ({
        referenceEntity: "ORGANIZATION",
        referenceId: "",
        name: "",
        formattedAddress: ""
      })}
      query={query}
      formExtra={(draft, setDraft) => (
        <div className="space-y-2 border border-slate-100 rounded-2xl p-3">
          <div className="text-sm font-semibold">Google Maps</div>
          <div className="flex gap-2">
            <Input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search an address" />
            <Button
              type="button"
              variant="secondary"
              onClick={async () => setHits(await apiSearchGooglePlaces(search))}
            >
              Search
            </Button>
          </div>
          {hits.map((hit) => (
            <button
              type="button"
              key={hit.placeId}
              className="block w-full text-left text-sm rounded-xl px-2 py-1 hover:bg-slate-50"
              onClick={() => {
                setDraft({
                  ...draft,
                  name: draft.name || hit.formattedAddress.split(",")[0],
                  formattedAddress: hit.formattedAddress,
                  street: hit.street,
                  city: hit.city,
                  state: hit.state,
                  zipCode: hit.zipcode,
                  country: hit.country,
                  placeId: hit.placeId,
                  latitude: hit.lat,
                  longitude: hit.lng
                });
                setHits([]);
              }}
            >
              {hit.formattedAddress}
            </button>
          ))}
        </div>
      )}
    />
  );
}
