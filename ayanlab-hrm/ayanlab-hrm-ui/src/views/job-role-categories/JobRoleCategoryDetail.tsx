import React, { useEffect } from 'react';
import { useCreateJobRoleCategory, useUpdateJobRoleCategory } from '../../data/hooks/useJobRoleCategories';
import { useForm } from 'react-hook-form';
import { CreateJobRoleCategoryDto, JobRoleCategory } from '../../types/jobRoles';
import { Button } from '../../ui/components/Button';
import { Modal } from '../../ui/components/Modal';
import { LoadingSpinner } from '../../ui/components/LoadingSpinner';

interface JobRoleCategoryDetailProps {
  category?: JobRoleCategory;
  isOpen: boolean;
  onClose: () => void;
  isCreateMode?: boolean;
}

export function JobRoleCategoryDetail({ category, isOpen, onClose, isCreateMode = false }: JobRoleCategoryDetailProps) {
  const createMutation = useCreateJobRoleCategory();
  const updateMutation = useUpdateJobRoleCategory();
  const { register, handleSubmit, reset, formState: { errors } } = useForm<CreateJobRoleCategoryDto>();

  // Reset form when category data changes
  useEffect(() => {
    if (category) {
      reset({
        name: category.name,
        description: category.description || '',
      });
    } else if (isCreateMode) {
      reset({
        name: '',
        description: '',
      });
    }
  }, [category, isCreateMode, reset]);

  const onSubmit = async (data: CreateJobRoleCategoryDto) => {
    try {
      if (isCreateMode) {
        await createMutation.mutateAsync(data);
        alert('Category created successfully.');
      } else if (category?.id) {
        await updateMutation.mutateAsync({ id: category.id, data });
        alert('Category updated successfully.');
      }
      onClose();
    } catch (error) {
      console.error('Error saving category:', error);
      alert((error as Error)?.message || 'Failed to save category.');
    }
  };

  const isSaving = createMutation.isPending || updateMutation.isPending;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={isCreateMode ? 'Create Category' : 'Edit Category'}>
      {isSaving && <LoadingSpinner type="ring" overlay text="Saving..." />}
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

        <div className="flex justify-end space-x-3 pt-4">
          <Button type="button" variant="secondary" onClick={onClose}>
            Cancel
          </Button>
          <Button type="submit" disabled={isSaving}>
            {isSaving ? 'Saving...' : (isCreateMode ? 'Create' : 'Save Changes')}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
