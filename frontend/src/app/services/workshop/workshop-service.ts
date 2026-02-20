import { Injectable } from '@angular/core';
import {environment} from '../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Workshop} from '../../models/Workshop';
import {Page} from '../../models/Page';
import {LogicResult} from '../../models/LogicResult';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class WorkshopService {
  // Backend base url
  private readonly API_URL = `${environment.apiUrl}/workshop`;

  constructor(private readonly http: HttpClient) {}

  /**
   * Retrieve paginated workshop
   *
   * @param page page index (0-based)
   * @param size number of items per page
   */
  getWorkshopPage(
    page = 0,
    size = 12
  ): Observable<LogicResult<Page<Workshop>>> {

    return this.http.get<LogicResult<Page<Workshop>>>(
      `${this.API_URL}/page?page=${page}&size=${size}`
    );
  }


  /**
   * Retrieve member paginated workshop
   *
   * @param page page index (0-based)
   * @param size number of items per page
   */
  getMemberWorkshopPage(
    page = 0,
    size = 12
  ): Observable<LogicResult<Page<Workshop>>> {

    return this.http.get<LogicResult<Page<Workshop>>>(
      `${this.API_URL}/member/page?page=${page}&size=${size}`
    );
  }


  /**
   * Retrieve public paginated workshop
   *
   * @param page page index (0-based)
   * @param size number of items per page
   */
  getPublicWorkshopPage(
    page = 0,
    size = 12
  ): Observable<LogicResult<Page<Workshop>>> {

    return this.http.get<LogicResult<Page<Workshop>>>(
      `${this.API_URL}/public/page?page=${page}&size=${size}`
    );
  }


  /**
   * Retrieve the fourth last workshop
   */
  getLatestWorkshop(): Observable<LogicResult<Workshop[]>> {
    return this.http.get<LogicResult<Workshop[]>>(
      `${this.API_URL}/latest`
    );
  }


  /**
   * Retrieve workshop by its unique identifier
   */
  getWorkshopById(id: number): Observable<LogicResult<Workshop>>  {
    return this.http.get<LogicResult<Workshop>>(`${this.API_URL}/${id}`);

  }


  /**
   * Create workshop (reserve to admin)
   */
  createWorkshop(formData: FormData): Observable<LogicResult<void>> {
    return this.http.post<LogicResult<void>>(`${this.API_URL}/add`, formData);
  }


  /**
   * Update workshop (reserve to admin)
   */
  updateWorkshop(id: number,
              formData: FormData): Observable<LogicResult<void>> {
    return this.http.put<LogicResult<void>>(
      `${this.API_URL}/${id}`, formData
    );
  }


  /**
   * Delete workshop (reserve to admin)
   */
  deleteWorkshop(id: number): Observable<LogicResult<void>> {
    return this.http.delete<LogicResult<void>>(`${this.API_URL}/${id}`);
  }

}
