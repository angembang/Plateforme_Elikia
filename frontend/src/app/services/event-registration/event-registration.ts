import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LogicResult } from '../../models/LogicResult';

export interface EventRegistrationModel {
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
export class EventRegistrationService {
  private readonly API_URL = `${environment.apiUrl}/event-registration`;

    constructor(private readonly http: HttpClient) {}

    /**
     * Retrieve registrations of an event for admin.
     *
     * @param eventId event identifier
     */
    getRegistrationsByEvent(
      eventId: number
    ): Observable<LogicResult<EventRegistrationModel[]>> {
      return this.http.get<LogicResult<EventRegistrationModel[]>>(
        `${this.API_URL}/admin/event/${eventId}`
      );
    }

    /**
     * Register a visitor to a public event.
     *
     * @param eventId event identifier
     * @param registrationData registration form data
     */
    registerVisitorToEvent(
      eventId: number,
      registrationData: {
        firstName: string;
        lastName: string;
        email: string;
      }
    ): Observable<LogicResult<void>> {
      return this.http.post<LogicResult<void>>(
        `${this.API_URL}/public/event/${eventId}`,
        registrationData
      );
    }

    /**
     * Approve an event registration.
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
     * Reject an event registration.
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
