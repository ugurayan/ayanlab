import React, { useEffect } from 'react';
import { useCreateJobRole, useUpdateJobRole } from '../../data/hooks/useJobRoles';
import { useJobRoleCategories } from '../../data/hooks/useJobRoleCategories';
import { useForm } from 'react-hook-form';
import { JobRole, CreateJobRoleDto } from '../../types/jobRoles';
import { Button } from '../../ui/components/Button';
import { Modal } from '../../ui/components/Modal';
import { LoadingSpinner } from '../../ui/components/LoadingSpinner';

interface JobRoleDetailProps {
  role?: JobRole;
  isOpen: boolean;
  onClose: () => void;
  isCreateMode?: boolean;
}

export function JobRoleDetail({ role, isOpen, onClose, isCreateMode = false }: JobRoleDetailProps) {
  const { data: categoryPage } = useJobRoleCategories(0, 100);
  const categories = categoryPage?.items ?? [];
  const createMutation = useCreateJobRole();
  const updateMutation = useUpdateJobRole();
  
  const { register, handleSubmit, reset, formState: { errors } } = useForm<CreateJobRoleDto>({
    defaultValues: {
      name: '',
      description: '',
      categoryId: '',
      requiredSkills: [],
      isActive: true,
    },
  });

  // Reset form when role data changes
  useEffect(() => {
    if (role) {
      reset({
        name: role.name,
        description: role.description,
        categoryId: role.categoryId,
        requiredSkills: role.requiredSkills,
        isActive: role.isActive,
      });
    } else if (isCreateMode) {
      const firstCategoryId = categories[0]?.id ?? '';
      reset({
        name: '',
        description: '',
        categoryId: firstCategoryId,
        requiredSkills: [],
        isActive: true,
      });
    }
  }, [role, isCreateMode, reset, categories]);

  const onSubmit = async (data: CreateJobRoleDto) => {
    try {
      if (isCreateMode) {
        await createMutation.mutateAsync(data);
        alert('Job role created successfully.');
      } else if (role?.id) {
        await updateMutation.mutateAsync({ id: role.id, data });
        alert('Job role updated successfully.');
      }
      onClose();
    } catch (error) {
      console.error('Error saving job role:', error);
      alert((error as Error)?.message || 'Failed to save job role.');
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={isCreateMode ? 'Create Job Role' : 'Edit Job Role'}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700">Name</label>
          <input
            type="text"
            {...register('name', { required: 'Name is required' })}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
          />
          {errors.name && <p className="mt-1 text-sm text-red-600">{errors.name.message}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Description</label>
          <textarea
            {...register('description')}
            rows={3}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Category</label>
          <select
            {...register('categoryId', { required: 'Category is required' })}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
          >
            <option value="">Select a category</option>
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
              </option>
            ))}
          </select>
          {errors.categoryId && <p className="mt-1 text-sm text-red-600">{errors.categoryId.message}</p>}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">Status</label>
          <div className="mt-1">
            <label className="inline-flex items-center">
              <input
                type="checkbox"
                {...register('isActive')}
                className="rounded border-gray-300 text-indigo-600 shadow-sm focus:border-indigo-500 focus:ring-indigo-500"
              />
              <span className="ml-2">Active</span>
            </label>
          </div>
        </div>

        <div className="flex justify-end space-x-3 pt-4">
          <Button type="button" variant="secondary" onClick={onClose}>
            Cancel
          </Button>
          <Button type="submit" isLoading={createMutation.isLoading || updateMutation.isLoading}>
            {isCreateMode ? 'Create' : 'Save Changes'}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
