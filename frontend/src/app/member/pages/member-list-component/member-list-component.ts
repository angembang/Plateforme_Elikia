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

  /**
   * Refuse une demande d'adhésion.
   * Un motif est demandé à l'administrateur avant l'envoi
   * de la requête au backend.
   */
  rejectMembership(memberId: number): void {
    const reason = prompt('Veuillez saisir le motif du refus');

    // Vérifie qu'un motif a bien été saisi.
    // Les chaînes vides ou contenant uniquement des espaces sont refusées.
    // trim() supprime les espaces inutiles au début et à la fin du texte.
    if (!reason || reason.trim().length === 0) {
        alert('Le motif du refus est obligatoire.');
        return;
      }

    // Envoie le motif de refus au backend
    // puis recharge la liste des membres.
    this.memberService.rejectMembership(memberId, reason.trim()).subscribe(result => {
        if (result.code === '200') {
          this.loadMembers();
        }
      });
}
}
