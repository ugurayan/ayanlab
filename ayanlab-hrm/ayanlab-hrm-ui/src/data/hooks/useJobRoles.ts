import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import { JobRole, CreateJobRoleDto, UpdateJobRoleDto } from '../../types/jobRoles';
import { 
  apiListJobRoles, 
  apiCreateJobRole, 
  apiUpdateJobRole, 
  apiDeleteJobRole, 
  type JobRolePage 
} from '../../api/services/jobRolesAdmin';

export function useJobRoles(page: number, size: number) {
  return useQuery<JobRolePage>({
    queryKey: ['jobRoles', page, size],
    queryFn: () => apiListJobRoles({ page, size, sortBy: 'name', order: 'asc' }),
    placeholderData: keepPreviousData,
  });
}

export function useCreateJobRole() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: CreateJobRoleDto) => apiCreateJobRole(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['jobRoles'] });
    },
  });
}

export function useUpdateJobRole() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async ({ id, data }: { id: string; data: UpdateJobRoleDto }) => {
      return apiUpdateJobRole(id, data);
    },
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: ['jobRoles'] });
      queryClient.invalidateQueries({ queryKey: ['jobRole', id] });
    },
  });
}

export function useDeleteJobRole() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async (id: string) => {
      await apiDeleteJobRole(id);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['jobRoles'] });
    },
  });
}
