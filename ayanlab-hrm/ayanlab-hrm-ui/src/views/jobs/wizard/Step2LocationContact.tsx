import React, { useState, useEffect, useRef } from 'react';
import { Input } from '../../../ui/components/Input';
import { Button } from '../../../ui/components/Button';
import { Plus, X, MapPin, Building2 } from 'lucide-react';
import type { JobFormData } from '../types';
import type { StepErrors } from './validation';
import { useAddressesByProfile } from '../../../data/hooks/useLocations';
import { useContactsByProfile } from '../../../data/hooks/useContacts';
import { AddLocationModal } from './AddLocationModal';
import { WizardStepHeader } from './WizardSectionHeading';
import { formatWizard, useJobWizardCopy } from '../i18n/wizardTexts';

const CONTACT_DROPDOWN_CLOSE_DELAY_MS = 1000;

interface Step2LocationContactProps {
  formData: JobFormData;
  updateFormData: (updates: Partial<JobFormData>) => void;
  onNext?: () => void;
  onBack?: () => void;
  canGoBack?: boolean;
  errors?: StepErrors;
}

function FieldError({ message }: { message?: string }) {
  if (!message) return null;
  return <p className="text-xs text-red-600 mt-1">{message}</p>;
}

export function Step2LocationContact({ 
  formData, 
  updateFormData, 
  onNext, 
  onBack, 
  canGoBack = false, 
  errors = {} 
}: Step2LocationContactProps) {
  const w = useJobWizardCopy();
  const c = w.common;
  const s = w.step2;
  const [isAddLocationModalOpen, setIsAddLocationModalOpen] = useState(false);
  const [showContactDropdown, setShowContactDropdown] = useState(false);
  const contactDropdownCloseTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const contactDropdownRef = useRef<HTMLDivElement>(null);

  // Mock profileId - should come from auth context
  const profileId = 'profile-123';

  // Close contact dropdown when clicking outside
  useEffect(() => {
    if (!showContactDropdown) return;
    const handleClickOutside = (e: MouseEvent) => {
      if (contactDropdownRef.current && !contactDropdownRef.current.contains(e.target as Node)) {
        setShowContactDropdown(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [showContactDropdown]);
  
  const { data: addresses, isLoading: isLoadingAddresses } = useAddressesByProfile(profileId);
  const { data: contacts, isLoading: isLoadingContacts } = useContactsByProfile(profileId);

  const selectedAddress = addresses?.find(addr => addr.id === formData.selectedAddressId);
  const selectedContacts = contacts?.filter(contact => 
    formData.selectedContactIds.includes(contact.id)
  ) || [];

  // Update form data when address is selected
  useEffect(() => {
    if (selectedAddress) {
      updateFormData({
        workLocationName: selectedAddress.name,
        workLocationStreet: selectedAddress.street,
        workLocationCity: selectedAddress.city,
        workLocationState: selectedAddress.state,
        workLocationZipcode: selectedAddress.zipcode,
        workLocationCountry: selectedAddress.country,
      });
    }
  }, [selectedAddress?.id]);

  const handleAddressChange = (addressId: string) => {
    updateFormData({ selectedAddressId: addressId });
  };

  const handleLocationSuccess = (addressId: string) => {
    updateFormData({ selectedAddressId: addressId });
  };

  const handleToggleContact = (contactId: string) => {
    const isSelected = formData.selectedContactIds.includes(contactId);
    const newContactIds = isSelected
      ? formData.selectedContactIds.filter(id => id !== contactId)
      : [...formData.selectedContactIds, contactId];

    updateFormData({ selectedContactIds: newContactIds });

    // Close dropdown after a short delay so user sees the selection
    if (contactDropdownCloseTimerRef.current) {
      clearTimeout(contactDropdownCloseTimerRef.current);
    }
    contactDropdownCloseTimerRef.current = setTimeout(() => {
      setShowContactDropdown(false);
      contactDropdownCloseTimerRef.current = null;
    }, CONTACT_DROPDOWN_CLOSE_DELAY_MS);
  };

  // Cleanup timer on unmount
  useEffect(() => () => {
    if (contactDropdownCloseTimerRef.current) {
      clearTimeout(contactDropdownCloseTimerRef.current);
    }
  }, []);

  const handleRemoveContact = (contactId: string) => {
    updateFormData({ 
      selectedContactIds: formData.selectedContactIds.filter(id => id !== contactId) 
    });
  };

  const hasErrors = Object.keys(errors).length > 0;

  return (
    <div className="space-y-6">
      <WizardStepHeader title={s.title} subtitle={s.subtitle} />

      {hasErrors && (
        <div className="rounded-xl border border-red-200 bg-red-50 p-4">
          <p className="text-sm font-medium text-red-800">{c.fixErrorsTitle}</p>
          <ul className="mt-2 list-inside list-disc text-sm text-red-700">
            {Object.entries(errors).map(([key, message]) => (
              <li key={key}>{message}</li>
            ))}
          </ul>
        </div>
      )}

      <div className="bg-white rounded-2xl border border-slate-200 p-6 space-y-6">
        {/* Select Location */}
        <div>
          <label className="block text-sm font-medium text-slate-700 mb-2">
            {s.selectWorkLocation} *
          </label>
          <div className="flex items-center gap-2">
            <select
              className={`flex-1 rounded-xl border bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 ${
                errors.selectedAddressId ? 'border-red-400 focus:ring-red-500' : 'border-slate-200 focus:ring-blue-500'
              }`}
              value={formData.selectedAddressId}
              onChange={(e) => handleAddressChange(e.target.value)}
              disabled={isLoadingAddresses}
            >
              <option value="">{s.selectLocation}</option>
              {addresses?.map((addr) => (
                <option key={addr.id} value={addr.id}>
                  {addr.name} - {addr.city}
                </option>
              ))}
            </select>
            <button
              type="button"
              onClick={() => setIsAddLocationModalOpen(true)}
              className="p-2 rounded-lg border border-slate-200 hover:bg-slate-50 text-slate-700"
            >
              <Plus size={16} />
            </button>
          </div>
          <FieldError message={errors.selectedAddressId} />
        </div>

        {/* Location Details - card-style when selected */}
        {(selectedAddress || (formData.workLocationName && formData.workLocationStreet)) && (
          <div className="rounded-xl border border-slate-200 bg-gradient-to-br from-slate-50 to-white p-4 shadow-sm">
            <div className="flex gap-3">
              <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-blue-100 text-blue-600">
                <Building2 size={20} />
              </div>
              <div className="min-w-0 flex-1">
                <p className="text-xs font-medium uppercase tracking-wide text-slate-500">
                  {s.selectedLocation}
                </p>
                <p className="mt-0.5 text-base font-semibold text-slate-900">
                  {selectedAddress?.name ?? formData.workLocationName}
                </p>
                <div className="mt-2 flex items-start gap-2 text-sm text-slate-600">
                  <MapPin size={16} className="mt-0.5 shrink-0 text-slate-400" />
                  <span>
                    {selectedAddress
                      ? [selectedAddress.street, selectedAddress.city, selectedAddress.state, selectedAddress.zipcode].filter(Boolean).join(', ')
                      : [formData.workLocationStreet, formData.workLocationCity, formData.workLocationState, formData.workLocationZipcode].filter(Boolean).join(', ')}
                  </span>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Location Notes */}
        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            {s.locationNotes}
          </label>
          <textarea
            className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 min-h-[100px]"
            value={formData.workLocationNotes}
            onChange={(e) => updateFormData({ workLocationNotes: e.target.value })}
            placeholder={s.locationNotesPlaceholder}
          />
        </div>

        {/* Divider */}
        <div className="border-t border-slate-200" />

        {/* Door Options - one line */}
        <div>
          <span className="block text-sm font-medium text-slate-700 mb-2">{s.entrance}</span>
          <div className="flex flex-wrap items-center gap-6">
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={formData.useBackDoor}
                onChange={(e) => updateFormData({ useBackDoor: e.target.checked })}
                className="rounded border-slate-300"
              />
              <span className="text-sm text-slate-700">{s.useBackDoor}</span>
            </label>
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={formData.useFrontDoor}
                onChange={(e) => updateFormData({ useFrontDoor: e.target.checked })}
                className="rounded border-slate-300"
              />
              <span className="text-sm text-slate-700">{s.useFrontDoor}</span>
            </label>
          </div>
        </div>

        {/* Horizontal line between Entrance and Meals */}
        <div className="border-t border-slate-200" />

        {/* Meal Options - one line */}
        <div>
          <span className="block text-sm font-medium text-slate-700 mb-2">{s.meals}</span>
          <div className="flex flex-wrap items-center gap-6">
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={formData.hasFreeBreakfast}
                onChange={(e) => updateFormData({ hasFreeBreakfast: e.target.checked })}
                className="rounded border-slate-300"
              />
              <span className="text-sm text-slate-700">{s.freeBreakfast}</span>
            </label>
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={formData.hasFreeLunch}
                onChange={(e) => updateFormData({ hasFreeLunch: e.target.checked })}
                className="rounded border-slate-300"
              />
              <span className="text-sm text-slate-700">{s.freeLunch}</span>
            </label>
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={formData.hasFreeDinner}
                onChange={(e) => updateFormData({ hasFreeDinner: e.target.checked })}
                className="rounded border-slate-300"
              />
              <span className="text-sm text-slate-700">{s.freeDinner}</span>
            </label>
          </div>
        </div>

        {/* Divider */}
        <div className="border-t border-slate-200" />

        {/* Onsite Contact */}
        <div>
          <label className="block text-sm font-medium text-slate-700 mb-2">
            {s.onsiteContact} *
          </label>
          
          {/* Selected Contacts Display */}
          {selectedContacts.length > 0 && (
            <div className="mb-3 space-y-2">
              {selectedContacts.map((contact) => (
                <div
                  key={contact.id}
                  className="flex items-center justify-between p-3 bg-blue-50 border border-blue-200 rounded-lg"
                >
                  <div className="flex-1">
                    <div className="text-sm font-medium text-slate-900">
                      {contact.firstName} {contact.lastName}
                    </div>
                    {contact.position && (
                      <div className="text-xs text-slate-600">{contact.position}</div>
                    )}
                    <div className="text-xs text-slate-600 mt-1 space-x-3">
                      {contact.emailVisible && (
                        <span>📧 {contact.email}</span>
                      )}
                      {contact.phoneVisible && (
                        <span>📞 {contact.phone}</span>
                      )}
                    </div>
                  </div>
                  <button
                    type="button"
                    onClick={() => handleRemoveContact(contact.id)}
                    className="p-1 hover:bg-blue-100 rounded-full"
                  >
                    <X size={16} className="text-slate-600" />
                  </button>
                </div>
              ))}
            </div>
          )}

          {/* Contact Selection Dropdown - opens upward so content below stays visible */}
          <div className="relative" ref={contactDropdownRef}>
            {showContactDropdown && contacts && (
              <div className="absolute bottom-full left-0 right-0 z-10 mb-1 max-h-56 overflow-y-auto rounded-xl border border-slate-200 bg-white shadow-lg">
                {contacts.map((contact) => {
                  const isSelected = formData.selectedContactIds.includes(contact.id);
                  return (
                    <label
                      key={contact.id}
                      className="flex cursor-pointer items-start gap-3 border-b border-slate-100 p-3 last:border-0 hover:bg-slate-50"
                      onClick={(e) => e.stopPropagation()}
                    >
                      <input
                        type="checkbox"
                        checked={isSelected}
                        onChange={() => handleToggleContact(contact.id)}
                        className="mt-0.5 rounded border-slate-300"
                      />
                      <div className="min-w-0 flex-1">
                        <div className="text-sm font-medium text-slate-900">
                          {contact.firstName} {contact.lastName}
                        </div>
                        {contact.position && (
                          <div className="text-xs text-slate-600">{contact.position}</div>
                        )}
                        <div className="mt-1 text-xs text-slate-500">
                          {contact.emailVisible && <div>{contact.email}</div>}
                          {contact.phoneVisible && <div>{contact.phone}</div>}
                        </div>
                      </div>
                    </label>
                  );
                })}
              </div>
            )}
            <button
              type="button"
              onClick={() => setShowContactDropdown(!showContactDropdown)}
              className={`w-full rounded-xl border bg-white px-3 py-2 text-left text-sm focus:outline-none focus:ring-2 ${
                errors.selectedContactIds ? 'border-red-400 focus:ring-red-500' : 'border-slate-200 focus:ring-blue-500'
              }`}
              disabled={isLoadingContacts}
            >
              {selectedContacts.length === 0
                ? s.selectContacts
                : formatWizard(s.contactsSelected, { count: selectedContacts.length })}
            </button>
          </div>
          <FieldError message={errors.selectedContactIds} />
        </div>

        {/* Navigation Buttons */}
        {(onNext || onBack) && (
          <div className="flex justify-between items-center gap-4 pt-6 mt-6 pb-2 border-t border-slate-200">
            <Button
              variant="secondary"
              onClick={onBack}
              disabled={!canGoBack}
            >
              {c.back}
            </Button>
            <Button onClick={onNext}>
              {c.next}
            </Button>
          </div>
        )}
      </div>

      {/* Add Location Modal */}
      <AddLocationModal
        isOpen={isAddLocationModalOpen}
        onClose={() => setIsAddLocationModalOpen(false)}
        onSuccess={handleLocationSuccess}
        profileId={profileId}
      />
    </div>
  );
}
