import { useQuery } from '@tanstack/react-query';
import { apiGetContactsByProfile } from '../../api/services/contacts';

export function useContactsByProfile(profileId: string) {
  return useQuery({
    queryKey: ['contacts', profileId],
    queryFn: () => apiGetContactsByProfile(profileId),
    enabled: !!profileId,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}
