import {Component, OnInit} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {EventElikia} from '../../../models/EventElikia';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../../services/auth/auth.service';
import {EventService} from '../../../services/event/event-service';
import {DatePipe, NgOptimizedImage} from '@angular/common';
import {DomSanitizer} from '@angular/platform-browser';
import {FormsModule} from '@angular/forms';
import { EventRegistrationModel, EventRegistrationService } from '../../../services/event-registration/event-registration';

@Component({
  selector: 'app-detail-event-component',
  imports: [
    DatePipe,
    NgOptimizedImage,
    FormsModule
  ],
  templateUrl: './detail-event-component.html',
  styleUrl: './detail-event-component.scss',
})
export class DetailEventComponent implements OnInit {
  // Expose environment to the template
  protected  readonly apiUrlBack = environment.apiBackendUrl;

  // Event to display
  event?: EventElikia;

  // User role
  isAdmin = false;
  isMember = false;

  // Media
  images: string[] = [];
  videoUrl?: string;

  // Success message
  successMessage?: string;
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

  // Error message
  errorMessage?: string;

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

  // Registration form display
  showRegistration = false;

  // Admin registrations table display
  showRegistrations = false;

  // Reject registration modal display
  showRejectModal = false;

  // Selected registration to reject
  selectedRegistrationId?: number;

  // Refusal reason entered by the administrator
  refusalReason = '';

  // Registrations linked to the current event, displayed in the admin space
  eventRegistrations: EventRegistrationModel[] = [];

  // Visitor registration form data
  registrationForm = {
    firstName: '',
    lastName: '',
    email: ''
  };

  // Current index for slider
  currentIndex = 0;

  // Constructor
  constructor(
    private readonly route: ActivatedRoute,
    private readonly eventService: EventService,
    private readonly authService: AuthService,
    private readonly eventRegistrationService: EventRegistrationService,
    private readonly router: Router,
    private readonly sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    // Check is admin
    this.isAdmin = this.authService.getUserRole() === 'ADMIN';

    // Check is member
    this.isMember = this.authService.getUserRole() === 'MEMBER';

    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadEvent(id);
    }
  }


  getSafeYoutubeUrl(url: string) {
    const embed = url.replace('watch?v=','embed/');
    return this.sanitizer.bypassSecurityTrustResourceUrl(embed);
  }


  private loadEvent(id: number) {
    this.eventService.getEventById(id).subscribe({
      next: result => {
        if (result.code === '200') {
          this.event = result.data!;
          const medias = this.event.mediaList ?? [];
          // separate images and video
          this.images = medias
            .filter(m => m.imagePath)
            .map(m => m.imagePath!);
          this.videoUrl = medias.find(m => m.videoUrl)?.videoUrl;
        }
      },
      error: err => this.handleError(err, 'Error loading event detail')
    })

  }

  /**
   * Show or hide the registration form
   */
  showRegistrationForm(): void {
    this.showRegistration = !this.showRegistration;
  }

  /**
   * Submit event registration form
   */
  submitRegistration(): void {
    if (!this.event) {
        return;
      }

      this.eventRegistrationService
        .registerVisitorToEvent(
          this.event.eventId,
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
                "Votre demande d'inscription a été envoyée avec succès."
              );
            }
          },
          error: err => this.handleError(err, 'Error creating event registration')
        });
  }

  /**
   * Show or hide the admin registrations table.
   * When the table is opened, registrations are loaded from the backend.
   */
  showRegistrationsTable(): void {
    this.showRegistrations = !this.showRegistrations;

      if (this.showRegistrations) {
        this.loadRegistrations();
      }
  }

  /**
   * Approve an event registration
   *
   * @param registrationId registration identifier
   */
  approveRegistration(registrationId: number): void {

    this.eventRegistrationService
      .approveRegistration(registrationId)
      .subscribe({
        next: result => {

          if (result.code === '200') {

            // Reload registrations after approval
           this.loadRegistrations();

           this.showSuccessMessage(
               "L'inscription a été approuvée avec succès."
             );
          }

        },
        error: err =>
          this.handleError(err, 'Error approving registration')
      });

  }

  /**
   * Reject the selected event registration.
   */
  rejectRegistration(): void {

    if (!this.selectedRegistrationId) {
      return;
    }

    if (!this.refusalReason.trim()) {
      return;
    }

    this.eventRegistrationService
      .rejectRegistration(
        this.selectedRegistrationId,
        this.refusalReason
      )
      .subscribe({
        next: result => {

          if (result.code === '200') {

            // Close the rejection modal
            this.showRejectModal = false;

            // Clear the refusal reason
            this.refusalReason = '';

            // Refresh registrations table
            this.loadRegistrations();

            this.showSuccessMessage(
                "L'inscription a été refusée avec succès."
              );
          }

        },
        error: err =>
          this.handleError(err, 'Error rejecting registration')
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
   * Load registrations linked to the current event.
   */
  private loadRegistrations(): void {
    if (!this.event) {
      return;
    }

    this.eventRegistrationService
      .getRegistrationsByEvent(this.event.eventId)
      .subscribe({
        next: result => {
          if (result.code === '200') {
            this.eventRegistrations = result.data ?? [];
          }
        },
        error: err => this.handleError(err, 'Error loading event registrations')
      });
  }

  // Navigation
  goBack(): void {

    if (this.isAdmin) {
      this.router.navigate(['/admin/event']).then(r => {});
    } else if (this.isMember) {
      this.router.navigate(['/member/event']).then(r => {});
    }
    else {
      this.router.navigate(['/event']).then(r => {});
    }
  }


  goToEdit(): void {

    if (!this.event) return;

    this.router.navigate(['/admin/event/edit', this.event.eventId]).then(r => {});
  }


  deleteEvent(): void {

    if (!this.event) return;

    if (!confirm('Voulez-vous supprimer cet évènement ?')) {
      return;
    }

    this.eventService.deleteEvent(this.event.eventId).subscribe(result => {

      if (result.code === '200') {

        // Return to the event list management
        this.router.navigate(['/admin/event']).then(r => {});
      }

    });
  }


  /**
   * slider next button
   */
  nextImage() {
    if (this.currentIndex < this.images.length - 1) {
      this.currentIndex++;
    }
  }


  /**
   * Slider preview button
   */
  prevImage() {
    if (this.currentIndex > 0) {
      this.currentIndex--;
    }
  }

}
