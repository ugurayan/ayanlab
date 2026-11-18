// Validation utility functions

export function validatePhone(phone: string): boolean {
  // Remove all non-digit characters
  const cleaned = phone.replace(/\D/g, '');
  // Check if it's 10 digits (US format) or 11 digits (with country code)
  return cleaned.length >= 10 && cleaned.length <= 15;
}

export function validateEmail(email: string): boolean {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

export function validateSSN(ssn: string): boolean {
  // Remove dashes and spaces
  const cleaned = ssn.replace(/[\s-]/g, '');
  // SSN should be 9 digits
  return /^\d{9}$/.test(cleaned);
}

export function validateITIN(itin: string): boolean {
  // Remove dashes and spaces
  const cleaned = itin.replace(/[\s-]/g, '');
  // ITIN should be 9 digits and start with 9
  return /^9\d{8}$/.test(cleaned);
}

export function validateName(name: string): boolean {
  // Minimum 2 characters, only letters, spaces, hyphens, and apostrophes
  return name.trim().length >= 2 && /^[a-zA-Z\s'-]+$/.test(name.trim());
}

export function validatePassword(password: string): boolean {
  // Must contain: uppercase, lowercase, number, and special character
  const hasUpperCase = /[A-Z]/.test(password);
  const hasLowerCase = /[a-z]/.test(password);
  const hasNumber = /\d/.test(password);
  const hasSpecialChar = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password);
  
  return hasUpperCase && hasLowerCase && hasNumber && hasSpecialChar && password.length >= 8;
}

export function formatPhone(phone: string): string {
  // Remove all non-digit characters
  return phone.replace(/\D/g, '');
}

export function formatSSN(ssn: string): string {
  // Remove all non-digit characters
  return ssn.replace(/\D/g, '');
}

export function formatITIN(itin: string): string {
  // Remove all non-digit characters
  return itin.replace(/\D/g, '');
}

export function formatDate(date: string): string {
  // Convert YYYY-MM-DD to MM/DD/YYYY
  if (!date) return '';
  const parts = date.split('-');
  if (parts.length === 3) {
    return `${parts[1]}/${parts[2]}/${parts[0]}`;
  }
  return date;
}

