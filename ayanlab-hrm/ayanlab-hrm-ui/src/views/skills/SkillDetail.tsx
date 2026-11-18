import React, { useEffect } from 'react';
import { useCreateSkill, useUpdateSkill } from '../../data/hooks/useSkills';
import { useForm } from 'react-hook-form';
import type { Skill } from '../../api/services/skillsAdmin';
import { Button } from '../../ui/components/Button';
import { Modal } from '../../ui/components/Modal';

interface SkillDetailProps {
  skill?: Skill;
  isOpen: boolean;
  onClose: () => void;
  isCreateMode?: boolean;
}

type SkillFormData = {
  name: string;
  description: string;
};

export function SkillDetail({ skill, isOpen, onClose, isCreateMode = false }: SkillDetailProps) {
  const createMutation = useCreateSkill();
  const updateMutation = useUpdateSkill();
  
  const { register, handleSubmit, reset, formState: { errors } } = useForm<SkillFormData>({
    defaultValues: {
      name: '',
      description: '',
    },
  });

  useEffect(() => {
    if (skill) {
      reset({
        name: skill.name,
        description: skill.description || '',
      });
    } else if (isCreateMode) {
      reset({
        name: '',
        description: '',
      });
    }
  }, [skill, isCreateMode, reset]);

  const onSubmit = async (data: SkillFormData) => {
    try {
      if (isCreateMode) {
        await createMutation.mutateAsync(data);
        alert('Skill created successfully.');
      } else if (skill?.id) {
        await updateMutation.mutateAsync({ id: skill.id, data });
        alert('Skill updated successfully.');
      }
      onClose();
    } catch (error) {
      console.error('Error saving skill:', error);
      alert((error as Error)?.message || 'Failed to save skill.');
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={isCreateMode ? 'Create Skill' : 'Edit Skill'}>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700">Name *</label>
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
          <Button type="submit" disabled={createMutation.isPending || updateMutation.isPending}>
            {createMutation.isPending || updateMutation.isPending 
              ? (isCreateMode ? 'Creating...' : 'Saving...') 
              : (isCreateMode ? 'Create' : 'Save Changes')}
          </Button>
        </div>
      </form>
    </Modal>
  );
}

