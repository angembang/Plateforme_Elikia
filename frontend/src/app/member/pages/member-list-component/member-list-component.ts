import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MemberService } from '../../../services/member/member-service';
import { Member } from '../../../models/Member';

/**
 * Composant permettant à l'administrateur
 * de consulter la liste des membres enregistrés.
 */
@Component({
  selector: 'app-member-list-component',
  imports: [DatePipe],
  templateUrl: './member-list-component.html',
  styleUrl: './member-list-component.scss',
})
export class MemberListComponent implements OnInit {
  members: Member[] = [];

  constructor(private readonly memberService: MemberService) {}

  ngOnInit(): void {
    this.loadMembers();
  }

  /**
   * Composant permettant à l'administrateur
   * de consulter et traiter les demandes d'adhésion.
   */
  private loadMembers(): void {
      this.memberService.getAllMembers().subscribe(result => {
        if (result.code === '200' && result.data) {
          this.members = result.data;
        }
      });
    }

  acceptMembership(memberId: number): void {
    this.memberService.acceptMembership(memberId).subscribe(result => {
      if (result.code === '200') {
        this.loadMembers();
      }
    });
  }

  rejectMembership(memberId: number): void {
    const reason = prompt('Motif du refus');

    if (!reason) {
      return;
    }

    this.memberService.rejectMembership(memberId, reason).subscribe(() => {
      this.loadMembers();
    });
  }
}
