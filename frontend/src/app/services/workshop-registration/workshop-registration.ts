import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LogicResult } from '../../models/LogicResult';

export interface WorkshopRegistrationModel {
  registrationId: number;
  firstName: string;
  lastName: string;
  email: string;
  registrationDate: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';
}

@Injectable({
  providedIn: 'root',
})
export class WorkshopRegistrationService {
  private readonly API_URL = `${environment.apiUrl}/workshop-registration`;

  constructor(private readonly http: HttpClient) {}

  /**
   * Retrieve registrations of a workshop for admin.
   *
   * @param workshopId workshop identifier
   */
  getRegistrationsByWorkshop(
    workshopId: number
  ): Observable<LogicResult<WorkshopRegistrationModel[]>> {
    return this.http.get<LogicResult<WorkshopRegistrationModel[]>>(
      `${this.API_URL}/admin/workshop/${workshopId}`
    );
  }

  /**
   * Register a visitor to a workshop.
   *
   * @param workshopId workshop identifier
   * @param registrationData registration form data
   */
  registerVisitorToWorkshop(
    workshopId: number,
    registrationData: {
      firstName: string;
      lastName: string;
      email: string;
    }
  ): Observable<LogicResult<void>> {
    return this.http.post<LogicResult<void>>(
      `${this.API_URL}/public/workshop/${workshopId}`,
      registrationData
    );
  }

  /**
   * Approve a workshop registration.
   *
   * @param registrationId registration identifier
   */
  approveRegistration(registrationId: number): Observable<LogicResult<void>> {
    return this.http.put<LogicResult<void>>(
      `${this.API_URL}/admin/${registrationId}/approve`,
      {}
    );
  }

  /**
   * Reject a workshop registration.
   *
   * @param registrationId registration identifier
   * @param reason refusal reason
   */
  rejectRegistration(
    registrationId: number,
    reason: string
  ): Observable<LogicResult<void>> {
    return this.http.put<LogicResult<void>>(
      `${this.API_URL}/admin/${registrationId}/reject`,
      { reason }
    );
  }
}
