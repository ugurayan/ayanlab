import { authHttp } from '../client';

export type JobRoleWagesResponse = {
  minHourly: number;
  maxHourly: number;
  avgHourly: number;
  currency: string;
  zipcode: string;
};

/** Mock data per zipcode - in production replace with real API call */
const MOCK_WAGES_BY_ZIP: Record<string, JobRoleWagesResponse> = {
  '8001': { minHourly: 22, maxHourly: 38, avgHourly: 28.5, currency: 'USD', zipcode: '8001' },
  '14052': { minHourly: 18, maxHourly: 32, avgHourly: 24, currency: 'USD', zipcode: '14052' },
  '10001': { minHourly: 25, maxHourly: 45, avgHourly: 32, currency: 'USD', zipcode: '10001' },
};

const delay = (ms: number) => new Promise((r) => setTimeout(r, ms));

/**
 * GET /api/v1/job-roles/wages/{zipcode}
 * Returns min, max and avg hourly costs for the job role in the given area.
 */
export async function apiGetJobRoleWages(zipcode: string): Promise<JobRoleWagesResponse> {
  const trimmed = (zipcode || '').trim();
  if (!trimmed) {
    return getDefaultMockWages('');
  }

  try {
    const res = await authHttp.get<{ data: JobRoleWagesResponse }>(`/job-roles/wages/${encodeURIComponent(trimmed)}`);
    if (res.data?.data) return res.data.data;
  } catch {
    // Fallback to mock when API not available
  }

  await delay(300);
  return MOCK_WAGES_BY_ZIP[trimmed] ?? getDefaultMockWages(trimmed);
}

function getDefaultMockWages(zipcode: string): JobRoleWagesResponse {
  return {
    minHourly: 20,
    maxHourly: 40,
    avgHourly: 28,
    currency: 'USD',
    zipcode: zipcode || '—',
  };
}
