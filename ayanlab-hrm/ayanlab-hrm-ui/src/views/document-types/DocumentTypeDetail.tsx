import React, { useEffect } from 'react';
import { useCreateDocumentType, useUpdateDocumentType } from '../../data/hooks/useDocumentTypes';
import { useForm } from 'react-hook-form';
import type { DocumentType } from '../../api/services/documentTypesAdmin';
import { Button } from '../../ui/components/Button';
import { Modal } from '../../ui/components/Modal';

interface DocumentTypeDetailProps {
  item?: DocumentType;
  isOpen: boolean;
  onClose: () => void;
  isCreateMode?: boolean;
}

type DocumentTypeFormData = {
  name: string;
  description: string;
};

export function DocumentTypeDetail({
  item,
  isOpen,
  onClose,
  isCreateMode = false,
}: DocumentTypeDetailProps) {
  const createMutation = useCreateDocumentType();
  const updateMutation = useUpdateDocumentType();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<DocumentTypeFormData>({
    defaultValues: {
      name: '',
      description: '',
    },
  });

  useEffect(() => {
    if (item) {
      reset({
        name: item.name,
        description: item.description || '',
      });
    } else if (isCreateMode) {
      reset({
        name: '',
        description: '',
      });
    }
  }, [item, isCreateMode, reset]);

  const onSubmit = async (data: DocumentTypeFormData) => {
    try {
      if (isCreateMode) {
        await createMutation.mutateAsync(data);
        alert('Document type created successfully.');
      } else if (item?.id) {
        await updateMutation.mutateAsync({ id: item.id, data });
        alert('Document type updated successfully.');
      }
      onClose();
    } catch (error) {
      console.error('Error saving document type:', error);
      alert((error as Error)?.message || 'Failed to save document type.');
    }
  };

  const isSubmitting = createMutation.isPending || updateMutation.isPending;

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={isCreateMode ? 'Create Document Type' : 'Edit Document Type'}
    >
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700">Name *</label>
          <input
            type="text"
            {...register('name', { required: 'Name is required' })}
            className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
          />
          {errors.name && (
            <p className="mt-1 text-sm text-red-600">{errors.name.message}</p>
          )}
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
          <Button type="submit" disabled={isSubmitting}>
            {isSubmitting
              ? isCreateMode
                ? 'Creating...'
                : 'Saving...'
              : isCreateMode
              ? 'Create'
              : 'Save Changes'}
          </Button>
        </div>
      </form>
    </Modal>
  );
}


