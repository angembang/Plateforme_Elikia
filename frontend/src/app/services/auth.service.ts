import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AuthStorageService} from './auth-storage.service';
import {LoginRequest} from '../models/LoginRequest';
import {Observable, tap} from 'rxjs';
import {LogicResult} from '../models/LogicResult';
import {RegisterRequest} from '../models/RegisterRequest';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly API_URL = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private storage: AuthStorageService
  ) {}

  /**
   * LoginComponent user and store JWT token
   */
  login(payload: LoginRequest): Observable<LogicResult<string>> {
    return this.http
      .post<LogicResult<string>>(`${this.API_URL}/login`, payload)
      .pipe(
        tap(result => {
          // If login is successful, store JWT token
          if (result.code === '200' && result.data) {
            this.storage.setToken(result.data);
          }
        })
      );
  }

  /**
   * RegisterComponent a new member
   */
  register(payload: RegisterRequest): Observable<LogicResult<void>> {
    return this.http.post<LogicResult<void>>(
      `${this.API_URL}/register`,
      payload
    );
  }

  /**
   * Logout user
   */
  logout(): void {
    this.storage.clear();
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return this.storage.hasToken();
  }
}
