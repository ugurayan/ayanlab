import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { staffingApi } from "../../api/services/staffing";

function useList<T>(key: string, queryFn: () => Promise<T>) {
  return useQuery({ queryKey: [key], queryFn });
}

function useSaver(key: string, save: (body: any, id?: string) => Promise<unknown>, remove: (id: string) => Promise<unknown>) {
  const client = useQueryClient();
  const invalidate = () => client.invalidateQueries({ queryKey: [key] });
  return {
    save: useMutation({
      mutationFn: (input: { body: any; id?: string }) => save(input.body, input.id),
      onSuccess: invalidate
    }),
    remove: useMutation({
      mutationFn: (id: string) => remove(id),
      onSuccess: invalidate
    })
  };
}

export function useOrganizationProfiles() {
  const query = useList("org-profiles", staffingApi.listOrgs);
  const mutations = useSaver("org-profiles", staffingApi.saveOrg, staffingApi.deleteOrg);
  return { ...query, ...mutations };
}

export function useLegalEntities() {
  const query = useList("legal-entities", staffingApi.listLegal);
  const mutations = useSaver("legal-entities", staffingApi.saveLegal, staffingApi.deleteLegal);
  return { ...query, ...mutations };
}

export function useBrands() {
  const query = useList("brands", staffingApi.listBrands);
  const mutations = useSaver("brands", staffingApi.saveBrand, staffingApi.deleteBrand);
  return { ...query, ...mutations };
}

export function useWorkLocations() {
  const query = useList("work-locations", () => staffingApi.listLocations());
  const mutations = useSaver("work-locations", staffingApi.saveLocation, staffingApi.deleteLocation);
  return { ...query, ...mutations };
}

export function useShiftTemplates() {
  const client = useQueryClient();
  const query = useList("shift-templates", staffingApi.listTemplates);
  const mutations = useSaver("shift-templates", staffingApi.saveTemplate, staffingApi.deleteTemplate);
  const generate = useMutation({
    mutationFn: (input: { id: string; dates?: string[]; from?: string; to?: string }) =>
      staffingApi.generate(input.id, input),
    onSuccess: async () => {
      await client.invalidateQueries({ queryKey: ["shift-instances"] });
    }
  });
  return { ...query, ...mutations, generate };
}

export function useShiftInstances() {
  const query = useList("shift-instances", staffingApi.listShifts);
  const mutations = useSaver("shift-instances", staffingApi.saveShift, staffingApi.deleteShift);
  return { ...query, ...mutations };
}
