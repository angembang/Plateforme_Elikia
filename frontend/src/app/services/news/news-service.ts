import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {LogicResult} from '../../models/LogicResult';
import {environment} from '../../../environments/environment';
import {News} from '../../models/News';
import {Page} from '../../models/Page';

@Injectable({
  providedIn: 'root',
})
export class NewsService {
  // Backend base url
  private readonly API_URL = `${environment.apiUrl}/news`;
  private readonly API_URL_ADMIN = `${environment.apiUrl}/news/management`;

  constructor(private readonly http: HttpClient) {}

  /**
   * Retrieve all news
   */
  getAllNews(): Observable<LogicResult<News[]>> {
    return this.http.get<LogicResult<News[]>>(this.API_URL_ADMIN);
  }

  /**
   * Retrieve all published news
   */
  getAllPublishedNews(): Observable<LogicResult<News[]>> {
    return this.http.get<LogicResult<News[]>>(this.API_URL);
  }


  /**
   * Retrieve last published news limit 4
   */
  getLatestPublishedNews(limit = 4): Observable<LogicResult<News[]>> {
    return this.http.get<LogicResult<News[]>>(
      `${this.API_URL}/latest?limit=${limit}`
    );
  }


  /**
   * Retrieve paginated published news
   *
   * @param page page index (0-based)
   * @param size number of items per page
   */
  getPublishedNewsPage(
    page = 0,
    size = 12
  ): Observable<LogicResult<Page<News>>> {

    return this.http.get<LogicResult<Page<News>>>(
      `${this.API_URL}/page?page=${page}&size=${size}`
    );
  }


  /**
   * Retrieve news by its unique identifier
   */
  getNewsById(id: number): Observable<LogicResult<News>>  {
    return this.http.get<LogicResult<News>>(`${this.API_URL}/${id}`);

  }


  /**
   * Create news (reserve to admin)
   */
  createNews(formData: FormData): Observable<LogicResult<void>> {
    return this.http.post<LogicResult<void>>(`${this.API_URL}/add`, formData);
  }


  /**
   * Update news (reserve to admin)
   */
  updateNews(id: number,
             formData: FormData): Observable<LogicResult<void>> {
    return this.http.put<LogicResult<void>>(
      `${this.API_URL}/${id}`, formData
    );
  }


  /**
   * Delete news (reserve to admin)
   */
  deleteNews(id: number): Observable<LogicResult<void>> {
    return this.http.delete<LogicResult<void>>(`${this.API_URL}/${id}`);
  }

}
