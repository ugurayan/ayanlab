import { authHttp } from '../client';

export type ContactApi = {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  position?: string;
  profileId: string;
  emailVisible: boolean;
  phoneVisible: boolean;
  createdAt: string;
};

// Mock data for contacts
const MOCK_CONTACTS: ContactApi[] = [
  {
    id: 'contact-1',
    firstName: 'John',
    lastName: 'Smith',
    email: 'john.smith@acme.com',
    phone: '+41 44 123 4567',
    position: 'Operations Manager',
    profileId: 'profile-123',
    emailVisible: true,
    phoneVisible: true,
    createdAt: '2024-01-10T10:00:00Z',
  },
  {
    id: 'contact-2',
    firstName: 'Sarah',
    lastName: 'Johnson',
    email: 'sarah.johnson@acme.com',
    phone: '+41 44 234 5678',
    position: 'HR Director',
    profileId: 'profile-123',
    emailVisible: true,
    phoneVisible: false,
    createdAt: '2024-01-15T14:30:00Z',
  },
  {
    id: 'contact-3',
    firstName: 'Michael',
    lastName: 'Brown',
    email: 'michael.brown@acme.com',
    phone: '+41 44 345 6789',
    position: 'Facility Supervisor',
    profileId: 'profile-123',
    emailVisible: false,
    phoneVisible: true,
    createdAt: '2024-02-01T09:15:00Z',
  },
];

const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export async function apiGetContactsByProfile(profileId: string): Promise<ContactApi[]> {
  // TODO: Replace with real API call
  // const response = await authHttp.get(`/api/v1/contacts/${profileId}/list`);
  // return response.data.data;
  
  await delay(300);
  return MOCK_CONTACTS.filter(contact => contact.profileId === profileId);
}
