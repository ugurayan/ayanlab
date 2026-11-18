import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { 
  CreateJobRoleCategoryDto, 
  UpdateJobRoleCategoryDto 
} from '../../types/jobRoles';
import {
  apiListJobRoleCategories,
  apiCreateJobRoleCategory,
  apiUpdateJobRoleCategory,
  apiDeleteJobRoleCategory,
  type JobRoleCategoryPage
} from '../../api/services/jobRoleCategories';

export function useJobRoleCategories(page: number, size: number) {
  return useQuery<JobRoleCategoryPage>({
    queryKey: ['jobRoleCategories', page, size],
    queryFn: () => apiListJobRoleCategories({ page, size, sortBy: 'name', order: 'asc' }),
    placeholderData: keepPreviousData,
  });
}

export function useCreateJobRoleCategory() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: CreateJobRoleCategoryDto) => apiCreateJobRoleCategory(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['jobRoleCategories'] });
    },
  });
}

export function useUpdateJobRoleCategory() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async ({ id, data }: { id: string; data: UpdateJobRoleCategoryDto }) => {
      return apiUpdateJobRoleCategory(id, data);
    },
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: ['jobRoleCategories'] });
    },
  });
}

export function useDeleteJobRoleCategory() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: string) => apiDeleteJobRoleCategory(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['jobRoleCategories'] });
    },
  });
}
