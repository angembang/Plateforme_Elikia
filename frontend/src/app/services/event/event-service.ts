import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {EventElikia} from '../../models/EventElikia';
import {LogicResult} from '../../models/LogicResult';
import {Page} from '../../models/Page';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  // Backend base url
  private readonly API_URL = `${environment.apiUrl}/event`;

  constructor(private readonly http: HttpClient) {}

  /**
   * Retrieve paginated event
   *
   * @param page page index (0-based)
   * @param size number of items per page
   */
  getEventPage(
    page = 0,
    size = 12
  ): Observable<LogicResult<Page<EventElikia>>> {

    return this.http.get<LogicResult<Page<EventElikia>>>(
      `${this.API_URL}/page?page=${page}&size=${size}`
    );
  }


  /**
   * Retrieve member paginated event
   *
   * @param page page index (0-based)
   * @param size number of items per page
   */
  getMemberEventPage(
    page = 0,
    size = 12
  ): Observable<LogicResult<Page<EventElikia>>> {

    return this.http.get<LogicResult<Page<EventElikia>>>(
      `${this.API_URL}/member/page?page=${page}&size=${size}`
    );
  }


  /**
   * Retrieve public paginated event
   *
   * @param page page index (0-based)
   * @param size number of items per page
   */
  getPublicEventPage(
    page = 0,
    size = 12
  ): Observable<LogicResult<Page<EventElikia>>> {

    return this.http.get<LogicResult<Page<EventElikia>>>(
      `${this.API_URL}/public/page?page=${page}&size=${size}`
    );
  }


  /**
   * Retrieve the fourth last event
   */
  getLatestEvent(): Observable<LogicResult<EventElikia[]>> {
    return this.http.get<LogicResult<EventElikia[]>>(
      `${this.API_URL}/latest`
    );
  }


  /**
   * Retrieve event by its unique identifier
   */
  getEventById(id: number): Observable<LogicResult<EventElikia>>  {
    return this.http.get<LogicResult<EventElikia>>(`${this.API_URL}/${id}`);

  }


  /**
   * Create event (reserve to admin)
   */
  createEvent(formData: FormData): Observable<LogicResult<void>> {
    return this.http.post<LogicResult<void>>(`${this.API_URL}/add`, formData);
  }


  /**
   * Update event (reserve to admin)
   */
  updateEvent(id: number,
             formData: FormData): Observable<LogicResult<void>> {
    return this.http.put<LogicResult<void>>(
      `${this.API_URL}/${id}`, formData
    );
  }


  /**
   * Delete event (reserve to admin)
   */
  deleteEvent(id: number): Observable<LogicResult<void>> {
    return this.http.delete<LogicResult<void>>(`${this.API_URL}/${id}`);
  }


}
