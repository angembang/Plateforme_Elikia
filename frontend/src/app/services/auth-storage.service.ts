// JWT storage
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AuthStorageService {
  private readonly TOKEN_KEY = 'elikia_token';

  // Save JWT token
  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  // Retrieve JWT token
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  // Remove JWT token (logout)
  clear(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  // Check if token exists
  hasToken(): boolean {
    return !!this.getToken();
  }

}
