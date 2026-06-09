import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LogicResult } from '../../models/LogicResult';
import { Member } from '../../models/Member';

@Injectable({
  providedIn: 'root',
})
export class MemberService {
  private readonly API_URL = `${environment.apiUrl}/admin/members`;

  constructor(private readonly http: HttpClient) {}

  /**
   * Récupère la liste complète des membres enregistrés.
   */
  getAllMembers(): Observable<LogicResult<Member[]>> {
    return this.http.get<LogicResult<Member[]>>(this.API_URL);
  }
}
