import { authHttp, http } from "../client";
import { endpoints } from "../endpoints";
import type { AuthSession, User } from "../../types/auth";

// Generate correlation ID
const generateCorrelationId = () => {
  return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
};

// Get language from localStorage or default to "en"
const getLanguage = (): string => {
  if (typeof window !== "undefined") {
    return window.localStorage.getItem("language") || "en";
  }
  return "en";
};

// Login api call to get the token and refresh token
export async function apiLogin(emailOrMobile: string, password: string, language?: string): Promise<AuthSession> {
  const lang = language || getLanguage();
  const correlationId = generateCorrelationId();
  
  try {
    const res = await authHttp.post(
      `${endpoints.auth.login}?lang=${lang}`,
      { 
        emailOrMobile,
        password 
      },
      {
        headers: {
          'X-Correlation-ID': correlationId,
          'Content-Type': 'application/json'
        }
      }
    );
    console.log(res?.data);

    // Response structure: { success, data: { token, refreshToken, userId, profileId, orgId, displayName, email, roles, permissions, userStatus, profileStatus }, message, timestamp }
    if (!res || !res.data) {
      throw new Error("Invalid response from server");
    }

    if (!res.data.success) {
      throw new Error(res.data.message || "Invalid email or password");
    }
    
    if (!res.data.data || !res.data.data.token) {
      throw new Error("Login failed: No token received");
    }

    const responseData = res.data.data;
    const { token, refreshToken, userId, profileId, orgId, displayName, email, roles, permissions, userApproved, profileApproved } = responseData;
    
    // Store refresh token if available
    if (refreshToken) {
      localStorage.setItem('refreshToken', refreshToken);
    }
    
    // Use the user data directly from the API response
    const user: User = {
      id: userId || '',
      email: email || emailOrMobile,
      displayName: displayName || "",
      roles: Array.isArray(roles) ? roles : [],
      permissions: Array.isArray(permissions) ? permissions : [],
      scope: {
        orgId: orgId || undefined
      },
      userApproved: userApproved !== false, // Default to true if not explicitly false
      profileApproved: profileApproved !== false, // Default to true if not explicitly false
      profileId: profileId || undefined
    };
    
    return {
      accessToken: token,
      user
    };
  } catch (error: any) {
    // Handle API errors
    console.error("Login error:", error);
    
    if (error.response) {
      // Server responded with error status
      const errorMessage = error.response.data?.message || error.response.statusText || "Login failed";
      throw new Error(errorMessage);
    } else if (error.request) {
      // Request was made but no response received
      throw new Error("No response from server. Please check your connection.");
    } else if (error.message) {
      // Error in request setup
      throw new Error(error.message);
    } else {
      throw new Error("Invalid email or password");
    }
  }
}

export async function apiMe(): Promise<User> {
  const res = await http.get<User>(endpoints.auth.me);
  return res.data;
}
