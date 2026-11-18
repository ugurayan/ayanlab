import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import {
  apiListCertificates,
  apiCreateCertificate,
  apiUpdateCertificate,
  apiDeleteCertificate,
  type CertificatePage,
  type CreateCertificateDto,
  type UpdateCertificateDto,
} from '../../api/services/certificatesAdmin';

export function useCertificates(page: number, size: number) {
  return useQuery<CertificatePage>({
    queryKey: ['certificates', page, size],
    queryFn: () => apiListCertificates({ page, size, sortBy: 'name', order: 'asc' }),
    placeholderData: keepPreviousData,
  });
}

export function useCreateCertificate() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: CreateCertificateDto) => apiCreateCertificate(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['certificates'] });
    },
  });
}

export function useUpdateCertificate() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async ({ id, data }: { id: string; data: UpdateCertificateDto }) => {
      return apiUpdateCertificate(id, data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['certificates'] });
    },
  });
}

export function useDeleteCertificate() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: string) => apiDeleteCertificate(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['certificates'] });
    },
  });
}

