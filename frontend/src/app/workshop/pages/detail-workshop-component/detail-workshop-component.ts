import {Component, OnInit} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {Workshop} from '../../../models/Workshop';
import {ActivatedRoute, Router} from '@angular/router';
import {WorkshopService} from '../../../services/workshop/workshop-service';
import {AuthService} from '../../../services/auth/auth.service';
import {DomSanitizer} from '@angular/platform-browser';
import {DatePipe, NgOptimizedImage} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {
  WorkshopRegistrationModel,
  WorkshopRegistrationService
} from '../../../services/workshop-registration/workshop-registration';

@Component({
  selector: 'app-detail-workshop-component',
  imports: [
    DatePipe,
    NgOptimizedImage,
    FormsModule
  ],
  templateUrl: './detail-workshop-component.html',
  styleUrl: './detail-workshop-component.scss',
})
export class DetailWorkshopComponent implements OnInit {
  protected readonly apiUrlBack = environment.apiBackendUrl;

  workshop?: Workshop;

  isAdmin = false;
  isMember = false;

  images: string[] = [];
  videoUrl?: string;

  // Success message
  successMessage?: string;

  // Error message
  errorMessage?: string;

  // Visitor registration modal display
  showRegistration = false;

  // Admin registrations table display
  showRegistrations = false;

  // Reject registration modal display
  showRejectModal = false;

  // Selected registration to reject
  selectedRegistrationId?: number;

  // Refusal reason entered by the administrator
  refusalReason = '';

  // Registrations linked to the current workshop, displayed in the admin space
  workshopRegistrations: WorkshopRegistrationModel[] = [];

  // Visitor registration form data
  registrationForm = {
    firstName: '',
    lastName: '',
    email: ''
  };

  currentIndex = 0;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly workshopService: WorkshopService,
    private readonly workshopRegistrationService: WorkshopRegistrationService,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.getUserRole() === 'ADMIN';
    this.isMember = this.authService.getUserRole() === 'MEMBER';

    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadWorkshop(id);
    }
  }

  getSafeYoutubeUrl(url: string) {
    const embed = url.replace('watch?v=', 'embed/');
    return this.sanitizer.bypassSecurityTrustResourceUrl(embed);
  }

  private loadWorkshop(id: number): void {
    this.workshopService.getWorkshopById(id).subscribe({
      next: result => {
        if (result.code === '200') {
          this.workshop = result.data!;
          const medias = this.workshop.mediaList ?? [];

          this.images = medias
            .filter(m => m.imagePath)
            .map(m => m.imagePath!);

          this.videoUrl = medias.find(m => m.videoUrl)?.videoUrl;
        }
      },
      error: err => this.handleError(err, 'Error loading workshop detail')
    });
  }

  /**
   * Display a success message temporarily.
   *
   * @param message success message to display
   */
  private showSuccessMessage(message: string): void {
    this.successMessage = message;
    this.errorMessage = undefined;

    setTimeout(() => {
      this.successMessage = undefined;
    }, 4000);
  }

  /**
   * Display an error message temporarily.
   *
   * @param err backend error
   * @param fallbackMessage default error message
   */
  private handleError(err: any, fallbackMessage: string): void {
    this.successMessage = undefined;

    this.errorMessage =
      err?.error?.message ?? fallbackMessage;

    setTimeout(() => {
      this.errorMessage = undefined;
    }, 4000);
  }

  /**
   * Show or hide the visitor registration form.
   */
  showRegistrationForm(): void {
    this.showRegistration = !this.showRegistration;
  }

  /**
   * Submit workshop registration form.
   */
  submitRegistration(): void {
    if (!this.workshop) {
      return;
    }

    this.workshopRegistrationService
      .registerVisitorToWorkshop(
        this.workshop.workshopId,
        this.registrationForm
      )
      .subscribe({
        next: result => {
          if (result.code === '201') {
            this.showRegistration = false;

            this.registrationForm = {
              firstName: '',
              lastName: '',
              email: ''
            };

            this.showSuccessMessage(
              "Votre demande d'inscription à l'atelier a été envoyée avec succès."
            );
          }
        },
        error: err => this.handleError(err, 'Error creating workshop registration')
      });
  }

  /**
   * Load registrations linked to the current workshop.
   */
  private loadRegistrations(): void {
    if (!this.workshop) {
      return;
    }

    this.workshopRegistrationService
      .getRegistrationsByWorkshop(this.workshop.workshopId)
      .subscribe({
        next: result => {
          if (result.code === '200') {
            this.workshopRegistrations = result.data ?? [];
          }
        },
        error: err => this.handleError(err, 'Error loading workshop registrations')
      });
  }

  /**
   * Show or hide the admin registrations table.
   */
  showRegistrationsTable(): void {
    this.showRegistrations = !this.showRegistrations;

    if (this.showRegistrations) {
      this.loadRegistrations();
    }
  }

  /**
   * Approve a workshop registration.
   *
   * @param registrationId registration identifier
   */
  approveRegistration(registrationId: number): void {
    this.workshopRegistrationService
      .approveRegistration(registrationId)
      .subscribe({
        next: result => {
          if (result.code === '200') {
            this.loadRegistrations();

            this.showSuccessMessage(
              "L'inscription à l'atelier a été approuvée avec succès."
            );
          }
        },
        error: err => this.handleError(err, 'Error approving workshop registration')
      });
  }

  /**
   * Open the rejection modal for the selected registration.
   *
   * @param registrationId registration identifier
   */
  openRejectModal(registrationId: number): void {
    this.selectedRegistrationId = registrationId;
    this.refusalReason = '';
    this.showRejectModal = true;
  }

  /**
   * Reject the selected workshop registration.
   */
  rejectRegistration(): void {
    if (!this.selectedRegistrationId) {
      return;
    }

    if (!this.refusalReason.trim()) {
      return;
    }

    this.workshopRegistrationService
      .rejectRegistration(
        this.selectedRegistrationId,
        this.refusalReason
      )
      .subscribe({
        next: result => {
          if (result.code === '200') {
            this.showRejectModal = false;
            this.refusalReason = '';

            this.loadRegistrations();

            this.showSuccessMessage(
              "L'inscription à l'atelier a été refusée avec succès."
            );
          }
        },
        error: err => this.handleError(err, 'Error rejecting workshop registration')
      });
  }

  goBack(): void {
    if (this.isAdmin) {
      this.router.navigate(['/admin/workshop']).then(r => {});
    } else if (this.isMember) {
      this.router.navigate(['/member/workshop']).then(r => {});
    } else {
      this.router.navigate(['/workshop']).then(r => {});
    }
  }

  goToEdit(): void {
    if (!this.workshop) return;

    this.router.navigate(['/admin/workshop/edit', this.workshop.workshopId]).then(r => {});
  }

  deleteWorkshop(): void {
    if (!this.workshop) return;

    if (!confirm('Voulez-vous supprimer cet atelier ?')) {
      return;
    }

    this.workshopService.deleteWorkshop(this.workshop.workshopId).subscribe(result => {
      if (result.code === '200') {
        this.router.navigate(['/admin/workshop']).then(r => {});
      }
    });
  }

  nextImage(): void {
    if (this.currentIndex < this.images.length - 1) {
      this.currentIndex++;
    }
  }

  prevImage(): void {
    if (this.currentIndex > 0) {
      this.currentIndex--;
    }
  }
}
