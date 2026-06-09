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
   * Charge les membres depuis le backend.
   */
  private loadMembers(): void {
    this.memberService.getAllMembers().subscribe(result => {
      if (result.code === '200' && result.data) {
        this.members = result.data;
      }
    });
  }
}
