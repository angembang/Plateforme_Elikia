import {Component, OnInit} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {Workshop} from '../../../models/Workshop';
import {ActivatedRoute, Router} from '@angular/router';
import {WorkshopService} from '../../../services/workshop/workshop-service';
import {AuthService} from '../../../services/auth/auth.service';
import {DomSanitizer} from '@angular/platform-browser';
import {DatePipe, NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-detail-workshop-component',
  imports: [
    DatePipe,
    NgOptimizedImage
  ],
  templateUrl: './detail-workshop-component.html',
  styleUrl: './detail-workshop-component.scss',
})
export class DetailWorkshopComponent implements OnInit {
  // Expose environment to the template
  protected  readonly apiUrlBack = environment.apiBackendUrl;

  // Event to display
  workshop?: Workshop;

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
    private readonly workshopService: WorkshopService,
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
      this.loadWorkshop(id);
    }
  }


  getSafeYoutubeUrl(url: string) {
    const embed = url.replace('watch?v=','embed/');
    return this.sanitizer.bypassSecurityTrustResourceUrl(embed);
  }


  private loadWorkshop(id: number) {
    this.workshopService.getWorkshopById(id).subscribe({
      next: result => {
        if (result.code === '200') {
          this.workshop = result.data!;
          const medias = this.workshop.mediaList ?? [];
          // separate images and video
          this.images = medias
            .filter(m => m.imagePath)
            .map(m => m.imagePath!);
          this.videoUrl = medias.find(m => m.videoUrl)?.videoUrl;
        }
      },
      error: err => this.handleError(err, 'Error loading workshop detail')
    })

  }


  // Navigation
  goBack(): void {

    if (this.isAdmin) {
      this.router.navigate(['/admin/workshop']).then(r => {});
    } else if (this.isMember) {
      this.router.navigate(['/member/workshop']).then(r => {});
    }
    else {
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

        // Return to the workshop list management
        this.router.navigate(['/admin/workshop']).then(r => {});
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
