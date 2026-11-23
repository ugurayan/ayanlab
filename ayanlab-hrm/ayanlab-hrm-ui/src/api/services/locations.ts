import { authHttp } from '../client';

/** Backend Google Maps search response item */
export type GoogleMapsSearchItem = {
  formattedAddress: string;
  name: string;
  placeId: string;
  latitude: number;
  longitude: number;
  city: string | null;
  state: string | null;
  zipCode: string | null;
  country: string | null;
};

function correlationId(): string {
  return `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;
}

/** Parse formattedAddress (e.g. "18960 Sara Park Cir, East Aurora, NY 14052, United States") when API returns null for city/state/zip/country */
function parseFormattedAddress(formatted: string): { street: string; city: string; state: string; zipcode: string; country: string } {
  const parts = formatted.split(',').map((p) => p.trim()).filter(Boolean);
  const street = parts[0] ?? '';
  const city = parts[1] ?? '';
  const country = parts.length > 3 ? (parts[parts.length - 1] ?? '') : '';
  let state = '';
  let zipcode = '';
  if (parts.length >= 3) {
    const stateZipPart = parts[2];
    const zipMatch = stateZipPart.match(/\b(\d{5}(?:-\d{4})?)\b/);
    if (zipMatch) {
      zipcode = zipMatch[1];
      state = stateZipPart.replace(zipMatch[1], '').trim();
    } else {
      state = stateZipPart;
    }
  }
  return { street, city, state, zipcode, country };
}

/** Call backend GET /api/v1/google-maps/search?address=... */
export async function apiGoogleMapsSearch(address: string): Promise<GooglePlaceResult[]> {
  if (!address || address.trim().length < 3) return [];
  const res = await authHttp.get<{ data: GoogleMapsSearchItem[] }>('/google-maps/search', {
    params: { address: address.trim() },
    headers: {
      'Accept': 'application/hal+json',
      'X-Correlation-ID': correlationId(),
    },
  });
  const items = res.data?.data ?? [];
  return items.map((item) => {
    const hasStructured = item.city != null || item.state != null || item.zipCode != null || item.country != null;
    const street = item.formattedAddress.split(',')[0]?.trim() || item.name || item.formattedAddress;
    const parsed = hasStructured
      ? null
      : parseFormattedAddress(item.formattedAddress);
    return {
      placeId: item.placeId,
      formattedAddress: item.formattedAddress,
      street: hasStructured ? street : (parsed?.street ?? street),
      city: item.city ?? parsed?.city ?? '',
      state: item.state ?? parsed?.state ?? '',
      zipcode: (item.zipCode ?? parsed?.zipcode ?? '').toString(),
      country: item.country ?? parsed?.country ?? '',
      lat: item.latitude,
      lng: item.longitude,
    };
  });
}

export type AddressApi = {
  id: string;
  name: string;
  street: string;
  city: string;
  state: string;
  zipcode: string;
  country: string;
  profileId: string;
  createdAt: string;
};

export type AddressPage = {
  content: AddressApi[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
};

export type CreateAddressRequest = {
  name: string;
  street: string;
  city: string;
  state: string;
  zipcode: string;
  country: string;
  profileId: string;
};

export type GooglePlaceResult = {
  placeId: string;
  formattedAddress: string;
  street: string;
  city: string;
  state: string;
  zipcode: string;
  country: string;
  lat: number;
  lng: number;
};

// Mock data for addresses
const MOCK_ADDRESSES: AddressApi[] = [
  {
    id: '1',
    name: 'Acme — Downtown - Zurich',
    street: '123 Bahnhofstrasse',
    city: 'Zurich',
    state: 'ZH',
    zipcode: '8001',
    country: 'Switzerland',
    profileId: 'profile-123',
    createdAt: '2024-01-15T10:00:00Z',
  },
  {
    id: '2',
    name: 'Main Restaurant Location',
    street: '456 Limmatquai',
    city: 'Zurich',
    state: 'ZH',
    zipcode: '8001',
    country: 'Switzerland',
    profileId: 'profile-123',
    createdAt: '2024-02-20T14:30:00Z',
  },
  {
    id: '3',
    name: 'Warehouse District Office',
    street: '789 Europaallee',
    city: 'Zurich',
    state: 'ZH',
    zipcode: '8004',
    country: 'Switzerland',
    profileId: 'profile-123',
    createdAt: '2024-03-10T09:15:00Z',
  },
];

// Mock Google Places API response
const MOCK_GOOGLE_PLACES: GooglePlaceResult[] = [
  {
    placeId: 'ChIJplace1',
    formattedAddress: '100 Paradeplatz, 8001 Zürich, Switzerland',
    street: '100 Paradeplatz',
    city: 'Zürich',
    state: 'ZH',
    zipcode: '8001',
    country: 'Switzerland',
    lat: 47.3686,
    lng: 8.5391,
  },
  {
    placeId: 'ChIJplace2',
    formattedAddress: '50 Bellevueplatz, 8001 Zürich, Switzerland',
    street: '50 Bellevueplatz',
    city: 'Zürich',
    state: 'ZH',
    zipcode: '8001',
    country: 'Switzerland',
    lat: 47.3667,
    lng: 8.5450,
  },
];

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export async function apiGetAddressesByProfile(profileId: string): Promise<AddressApi[]> {
  // TODO: Replace with real API call
  // const response = await authHttp.get(`/api/v1/address/${profileId}`);
  // return response.data.data;
  
  await delay(300);
  return MOCK_ADDRESSES.filter(addr => addr.profileId === profileId);
}

export async function apiCreateAddress(data: CreateAddressRequest): Promise<AddressApi> {
  // TODO: Replace with real API call
  // const response = await authHttp.post('/api/v1/address/create', data);
  // return response.data.data;
  
  await delay(500);
  const newAddress: AddressApi = {
    id: `addr-${Date.now()}`,
    name: data.name,
    street: data.street,
    city: data.city,
    state: data.state,
    zipcode: data.zipcode,
    country: data.country,
    profileId: data.profileId,
    createdAt: new Date().toISOString(),
  };
  
  // Add to mock data
  MOCK_ADDRESSES.push(newAddress);
  
  return newAddress;
}

export async function apiSearchGooglePlaces(query: string, _apiKey?: string): Promise<GooglePlaceResult[]> {
  if (!query || query.trim().length < 3) return [];

  const trimmed = query.trim();

  try {
    const results = await apiGoogleMapsSearch(trimmed);
    if (results.length > 0) return results;
  } catch (err) {
    console.warn('Google Maps search API failed, using mock', err);
  }

  // Fallback: mock results when API fails or returns empty
  await delay(400);
  const q = trimmed.toLowerCase();
  const matching = MOCK_GOOGLE_PLACES.filter(
    (place) =>
      place.formattedAddress.toLowerCase().includes(q) ||
      place.street.toLowerCase().includes(q) ||
      place.city.toLowerCase().includes(q) ||
      place.zipcode.includes(trimmed)
  );
  const suggested: GooglePlaceResult = {
    placeId: `suggested-${Date.now()}`,
    formattedAddress: trimmed,
    street: trimmed,
    city: '',
    state: '',
    zipcode: '',
    country: '',
    lat: 0,
    lng: 0,
  };
  return matching.length > 0 ? matching : [suggested];
}

export async function apiListLocations(): Promise<AddressApi[]> {
  const USE_MOCKS = (import.meta.env.VITE_USE_MOCKS ?? "false") === "true";
  if (USE_MOCKS) {
    await delay(300);
    return MOCK_ADDRESSES;
  }
  const { http } = await import("../client");
  const { endpoints } = await import("../endpoints");
  const { unwrapHrmData } = await import("../hrmApi");
  const res = await http.get(endpoints.locations.list);
  const rows = unwrapHrmData<Array<{ id: string; name: string; city?: string; orgId?: string }>>(res.data);
  return rows.map((row) => ({
    id: row.id,
    name: row.name,
    street: row.name,
    city: row.city ?? "",
    state: "",
    zipcode: "",
    country: "",
    lat: 0,
    lng: 0,
  }));
}
