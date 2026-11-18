import React, { useEffect } from 'react';
import { useCreateCertificate, useUpdateCertificate } from '../../data/hooks/useCertificates';
import { useForm } from 'react-hook-form';
import type { Certificate } from '../../api/services/certificatesAdmin';
import { Button } from '../../ui/components/Button';
import { Modal } from '../../ui/components/Modal';

interface CertificateDetailProps {
  certificate?: Certificate;
  isOpen: boolean;
  onClose: () => void;
  isCreateMode?: boolean;
}

type CertificateFormData = {
  name: string;
  description: string;
};

export function CertificateDetail({ certificate, isOpen, onClose, isCreateMode = false }: CertificateDetailProps) {
  const createMutation = useCreateCertificate();
  const updateMutation = useUpdateCertificate();
  
  const { register, handleSubmit, reset, formState: { errors } } = useForm<CertificateFormData>({
    defaultValues: {
      name: '',
      description: '',
    },
  });

  useEffect(() => {
    if (certificate) {
      reset({
        name: certificate.name,
        description: certificate.description || '',
      });
    } else if (isCreateMode) {
      reset({
        name: '',
        description: '',
      });
    }
  }, [certificate, isCreateMode, reset]);

  const onSubmit = async (data: CertificateFormData) => {
    try {
      if (isCreateMode) {
        await createMutation.mutateAsync(data);
        alert('Certificate created successfully.');
      } else if (certificate?.id) {
        await updateMutation.mutateAsync({ id: certificate.id, data });
        alert('Certificate updated successfully.');
      }
      onClose();
    } catch (error) {
      console.error('Error saving certificate:', error);
      alert((error as Error)?.message || 'Failed to save certificate.');
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={isCreateMode ? 'Create Certificate' : 'Edit Certificate'}>
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

