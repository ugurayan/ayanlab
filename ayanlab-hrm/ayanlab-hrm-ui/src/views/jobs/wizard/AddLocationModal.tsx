import React, { useState, useEffect } from 'react';
import { Modal } from '../../../ui/components/Modal';
import { Button } from '../../../ui/components/Button';
import { Input } from '../../../ui/components/Input';
import { Loader2 } from 'lucide-react';
import { useSearchGooglePlaces, useCreateAddress } from '../../../data/hooks/useLocations';
import type { GooglePlaceResult } from '../../../api/services/locations';

const DEBOUNCE_MS = 500;

interface AddLocationModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: (addressId: string) => void;
  profileId: string;
}

export function AddLocationModal({ isOpen, onClose, onSuccess, profileId }: AddLocationModalProps) {
  const [searchQuery, setSearchQuery] = useState('');
  const [debouncedQuery, setDebouncedQuery] = useState('');
  const [locationName, setLocationName] = useState('');
  const [selectedPlace, setSelectedPlace] = useState<GooglePlaceResult | null>(null);

  // After 500ms of no typing (and at least 3 chars), run search
  useEffect(() => {
    if (searchQuery.trim().length < 3) {
      setDebouncedQuery('');
      return;
    }
    const timer = window.setTimeout(() => {
      setDebouncedQuery(searchQuery.trim());
    }, DEBOUNCE_MS);
    return () => window.clearTimeout(timer);
  }, [searchQuery]);

  const { data: places, isLoading: isSearching } = useSearchGooglePlaces(
    debouncedQuery,
    import.meta.env.VITE_GOOGLE_MAPS_API_KEY
  );

  const showResults = debouncedQuery.length >= 3;

  const createAddressMutation = useCreateAddress();

  const handleSelectPlace = (place: GooglePlaceResult) => {
    setSelectedPlace(place);
    setSearchQuery(place.formattedAddress);
    setDebouncedQuery('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!selectedPlace || !locationName.trim()) {
      return;
    }

    try {
      const result = await createAddressMutation.mutateAsync({
        name: locationName,
        street: selectedPlace.street,
        city: selectedPlace.city,
        state: selectedPlace.state,
        zipcode: selectedPlace.zipcode,
        country: selectedPlace.country,
        profileId,
      });

      onSuccess(result.id);
      handleClose();
    } catch (error) {
      console.error('Failed to create address:', error);
    }
  };

  const handleClose = () => {
    setSearchQuery('');
    setDebouncedQuery('');
    setLocationName('');
    setSelectedPlace(null);
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Add New Location">
      <form onSubmit={handleSubmit} className="space-y-4">
        {/* Location Name */}
        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Location Name *
          </label>
          <Input
            value={locationName}
            onChange={(e) => setLocationName(e.target.value)}
            placeholder="e.g. Main Restaurant Location"
            required
          />
        </div>

        {/* Address Search - auto-search after 3 characters, 0.5s debounce */}
        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1">
            Search Address *
          </label>
          <div className="relative">
            <Input
              value={searchQuery}
              onChange={(e) => {
                setSearchQuery(e.target.value);
                setSelectedPlace(null);
              }}
              placeholder="Enter address (min 3 characters)..."
              className="pr-10"
              required
            />
            {isSearching && (
              <div className="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none">
                <Loader2 size={18} className="animate-spin text-slate-400" />
              </div>
            )}
          </div>
          {showResults && searchQuery.trim().length >= 3 && (
            <p className="text-xs text-slate-500 mt-1">
              Results update automatically after you stop typing.
            </p>
          )}
        </div>

        {/* Search Results */}
        {showResults && places && places.length > 0 && (
          <div className="border border-slate-200 rounded-xl p-2 max-h-48 overflow-y-auto">
            <div className="text-xs font-medium text-slate-600 mb-2 px-2">
              Select an address:
            </div>
            {places.map((place) => (
              <button
                key={place.placeId}
                type="button"
                onClick={() => handleSelectPlace(place)}
                className={`w-full text-left p-2 rounded-lg text-sm hover:bg-blue-50 transition-colors ${
                  selectedPlace?.placeId === place.placeId ? 'bg-blue-100' : ''
                }`}
              >
                {place.formattedAddress}
              </button>
            ))}
          </div>
        )}

        {showResults && places && places.length === 0 && !isSearching && (
          <div className="text-sm text-slate-500 text-center py-2">
            No results found. Try a different search.
          </div>
        )}

        {/* Address Details (if selected) */}
        {selectedPlace && (
          <div className="bg-slate-50 rounded-xl p-4 space-y-2">
            <div className="text-sm font-medium text-slate-700">Selected Address:</div>
            <div className="text-sm text-slate-600">
              <div><strong>Street:</strong> {selectedPlace.street}</div>
              <div><strong>City:</strong> {selectedPlace.city}</div>
              <div><strong>State:</strong> {selectedPlace.state}</div>
              <div><strong>Zipcode:</strong> {selectedPlace.zipcode}</div>
              <div><strong>Country:</strong> {selectedPlace.country}</div>
            </div>
          </div>
        )}

        {/* Actions */}
        <div className="flex justify-end gap-2 pt-4">
          <Button
            type="button"
            variant="secondary"
            onClick={handleClose}
            disabled={createAddressMutation.isPending}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            disabled={!selectedPlace || !locationName.trim() || createAddressMutation.isPending}
          >
            {createAddressMutation.isPending ? 'Adding...' : 'Add Location'}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
