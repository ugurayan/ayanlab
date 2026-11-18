import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { 
  apiGetAddressesByProfile, 
  apiCreateAddress, 
  apiSearchGooglePlaces,
  type CreateAddressRequest,
  type GooglePlaceResult 
} from '../../api/services/locations';

export function useAddressesByProfile(profileId: string) {
  return useQuery({
    queryKey: ['addresses', profileId],
    queryFn: () => apiGetAddressesByProfile(profileId),
    enabled: !!profileId,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
}

export function useCreateAddress() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: CreateAddressRequest) => apiCreateAddress(data),
    onSuccess: (newAddress) => {
      // Invalidate and refetch addresses for this profile
      queryClient.invalidateQueries({
        queryKey: ['addresses', newAddress.profileId],
      });
    },
  });
}

export function useSearchGooglePlaces(query: string, apiKey?: string) {
  return useQuery({
    queryKey: ['googlePlaces', query],
    queryFn: () => apiSearchGooglePlaces(query, apiKey),
    enabled: query.length >= 3,
    staleTime: 60 * 1000, // 1 minute
  });
}
