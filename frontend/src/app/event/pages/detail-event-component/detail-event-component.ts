import {Component, OnInit} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {EventElikia} from '../../../models/EventElikia';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../../services/auth/auth.service';
import {EventService} from '../../../services/event/event-service';
import {DatePipe, NgOptimizedImage} from '@angular/common';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
  selector: 'app-detail-event-component',
  imports: [
    DatePipe,
    NgOptimizedImage
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

  // Error message
  errorMessage?: string;
  private handleError(err: any, fallbackMessage: string): void {
    this.errorMessage =
      err?.error?.message ?? fallbackMessage;
  }

  // Current index for slider
  currentIndex = 0;

  // Constructor
  constructor(
    private readonly route: ActivatedRoute,
    private readonly eventService: EventService,
    private readonly authService: AuthService,
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
