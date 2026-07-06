import { Component, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MemberService } from '../../../services/member/member-service';
import { Member } from '../../../models/Member';

/**
 * Composant permettant à l'administrateur
 * de consulter les membres et de traiter les demandes d'adhésion.
 */
@Component({
  selector: 'app-member-list-component',
  imports: [DatePipe, FormsModule],
  templateUrl: './member-list-component.html',
  styleUrl: './member-list-component.scss',
})
export class MemberListComponent implements OnInit {
  members: Member[] = [];

  showRejectModal = false;
  selectedMemberId: number | null = null;
  rejectReason = '';

  constructor(private readonly memberService: MemberService) {}

  ngOnInit(): void {
    this.loadMembers();
  }

  /**
   * Charge tous les membres depuis le backend.
   */
  private loadMembers(): void {
    this.memberService.getAllMembers().subscribe(result => {
      if (result.code === '200' && result.data) {
        this.members = result.data;
      }
    });
  }

  /**
   * Accepte une demande d'adhésion puis recharge la liste.
   */
  acceptMembership(memberId: number): void {
    this.memberService.acceptMembership(memberId).subscribe(result => {
      if (result.code === '200') {
        this.loadMembers();
      }
    });
  }

  /**
   * Ouvre la fenêtre de refus pour le membre sélectionné.
   */
  openRejectModal(memberId: number): void {
    this.selectedMemberId = memberId;
    this.rejectReason = '';
    this.showRejectModal = true;
  }

  /**
   * Ferme la fenêtre de refus.
   */
  closeRejectModal(): void {
    this.showRejectModal = false;
    this.selectedMemberId = null;
    this.rejectReason = '';
  }

  /**
   * Confirme le refus après saisie du motif.
   */
  confirmReject(): void {
    if (!this.selectedMemberId || this.rejectReason.trim().length === 0) {
      alert('Le motif du refus est obligatoire.');
      return;
    }

    this.memberService
      .rejectMembership(this.selectedMemberId, this.rejectReason.trim())
      .subscribe(result => {
        if (result.code === '200') {
          this.closeRejectModal();
          this.loadMembers();
        }
      });
  }
}
