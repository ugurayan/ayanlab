import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';
import {
  apiListSkills,
  apiCreateSkill,
  apiUpdateSkill,
  apiDeleteSkill,
  type SkillPage,
  type CreateSkillDto,
  type UpdateSkillDto,
} from '../../api/services/skillsAdmin';

export function useSkills(page: number, size: number) {
  return useQuery<SkillPage>({
    queryKey: ['skills', page, size],
    queryFn: () => apiListSkills({ page, size, sortBy: 'name', order: 'asc' }),
    placeholderData: keepPreviousData,
  });
}

export function useCreateSkill() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (data: CreateSkillDto) => apiCreateSkill(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['skills'] });
    },
  });
}

export function useUpdateSkill() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: async ({ id, data }: { id: string; data: UpdateSkillDto }) => {
      return apiUpdateSkill(id, data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['skills'] });
    },
  });
}

export function useDeleteSkill() {
  const queryClient = useQueryClient();
  
  return useMutation({
    mutationFn: (id: string) => apiDeleteSkill(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['skills'] });
    },
  });
}

