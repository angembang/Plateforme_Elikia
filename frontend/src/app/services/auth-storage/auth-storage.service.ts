// JWT storage
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthStorageService {
  private readonly TOKEN_KEY = 'elikia_token';
  private readonly ROLE_KEY = 'auth_role';

  // Save JWT token
  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  // Save the token role
  setRole(role: string): void {
    localStorage.setItem(this.ROLE_KEY, role)
  }

  // Retrieve JWT token
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  // Retrieve the token role
  getRole(): string | null {
    return localStorage.getItem(this.ROLE_KEY);
  }

  // Remove JWT token (logout)
  clear(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.ROLE_KEY);
  }

  // Check if token exists
  hasToken(): boolean {
    return !!this.getToken();
  }

}
