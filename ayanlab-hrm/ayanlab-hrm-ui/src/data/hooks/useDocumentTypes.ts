import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import {
  apiListDocumentTypes,
  apiCreateDocumentType,
  apiUpdateDocumentType,
  apiDeleteDocumentType,
  type DocumentTypePage,
  type CreateDocumentTypeDto,
  type UpdateDocumentTypeDto,
} from '../../api/services/documentTypesAdmin';

export function useDocumentTypes(page: number, size: number) {
  return useQuery<DocumentTypePage>({
    queryKey: ['document-types', page, size],
    queryFn: () => apiListDocumentTypes({ page, size, sortBy: 'name', order: 'asc' }),
    placeholderData: keepPreviousData,
  });
}

export function useCreateDocumentType() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateDocumentTypeDto) => apiCreateDocumentType(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['document-types'] });
    },
  });
}

export function useUpdateDocumentType() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, data }: { id: string; data: UpdateDocumentTypeDto }) => {
      return apiUpdateDocumentType(id, data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['document-types'] });
    },
  });
}

export function useDeleteDocumentType() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => apiDeleteDocumentType(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['document-types'] });
    },
  });
}


