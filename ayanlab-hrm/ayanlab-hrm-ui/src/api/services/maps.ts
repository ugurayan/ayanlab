import { authHttp } from "../client";
import { endpoints } from "../endpoints";
import { getToken } from "../../state/authStorage";

// Generate correlation ID
const generateCorrelationId = () => {
  return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
};

export type AddressSuggestion = {
  formattedAddress: string;
  name: string;
  placeId: string;
  latitude: number;
  longitude: number;
};

export type AddressSearchResponse = {
  correlationId: string;
  success: boolean;
  message: string;
  data: AddressSuggestion[];
  timestamp: number;
};

export async function apiSearchAddress(address: string): Promise<AddressSuggestion[]> {
  const correlationId = generateCorrelationId();
  const token = getToken();

  if (!token) {
    throw new Error("Authentication token not found.");
  }

  if (!address || address.length < 5) {
    return [];
  }

  try {
    const response = await authHttp.get<AddressSearchResponse>(
      endpoints.maps.search(address),
      {
        headers: {
          "X-Correlation-ID": correlationId,
          "accept": "application/hal+json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!response.data.success || !response.data.data) {
      throw new Error(response.data.message || "Failed to search addresses");
    }

    return response.data.data;
  } catch (error: any) {
    console.error("Error searching addresses:", error);
    if (error.response) {
      throw new Error(error.response.data?.message || "Failed to search addresses");
    }
    throw new Error("Failed to search addresses");
  }
}

