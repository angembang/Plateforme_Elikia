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
  private readonly ADMIN_API_URL = `${environment.apiUrl}/admin`;

  constructor(private readonly http: HttpClient) {}

   /**
     * Récupère la liste complète des membres enregistrés.
     */
    getAllMembers(): Observable<LogicResult<Member[]>> {
      return this.http.get<LogicResult<Member[]>>(
        `${this.ADMIN_API_URL}/members`
      );
    }

  /**
   * Accepte une demande d'adhésion.
   */
  acceptMembership(id: number): Observable<LogicResult<Member>> {
    return this.http.patch<LogicResult<Member>>(
      `${this.ADMIN_API_URL}/membership-requests/${id}/accept`,
      {}
    );
  }

  /**
   * Refuse une demande d'adhésion avec un motif.
   */
  rejectMembership(id: number, reason: string): Observable<LogicResult<Member>> {
    return this.http.patch<LogicResult<Member>>(
      `${this.ADMIN_API_URL}/membership-requests/${id}/reject`,
      { reason }
    );
  }
}
