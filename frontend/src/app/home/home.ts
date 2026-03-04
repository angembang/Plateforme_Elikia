import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {NewsService} from '../services/news/news-service';
import {News} from '../models/News';
import {environment} from '../../environments/environment';
import {DatePipe, NgOptimizedImage} from '@angular/common';
import {EventService} from '../services/event/event-service';
import {EventElikia} from '../models/EventElikia';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {Workshop} from '../models/Workshop';
import {WorkshopService} from '../services/workshop/workshop-service';
import {TruncatePipe} from '../pipe/shared/truncate-pipe';
import {SafeUrlPipe} from '../pipe/url/safe-url-pipe';

@Component({
  selector: 'app-home',
  imports: [
    DatePipe,
    RouterLink,
    TruncatePipe,
    SafeUrlPipe,
    NgOptimizedImage
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  protected readonly apiUrlBack = environment.apiBackendUrl;

  latestNews: News[] = [];
  latestEvent: EventElikia[] = [];
  latestWorkshop: Workshop[] = [];
  errorMessage?: string;

  constructor(
    private readonly router: Router,
    private readonly newsService: NewsService,
    private readonly eventService: EventService,
    private readonly workshopService: WorkshopService,
    private readonly sanitizer: DomSanitizer) {}

  private handleError(err: any, fallbackMessage: string): void {
    this.errorMessage =
      err?.error?.message ?? fallbackMessage;
  }

  goToRegister() {
    this.router.navigate(["/register"]).then(r => {})
  }

  goToLogin() {
    this.router.navigate(["/login"]).then(r => {})
  }

  goToHome() {
    this.router.navigate([""]).then(r => {})
  }

  ngOnInit(): void {
    this.loadLatestNews();
    this.loadLatestEvent();
    this.loadLatestWorkshop();
  }

  /**
   * load the latest news (limit 4)
   * @private
   */
  private loadLatestNews(): void {
    this.newsService.getLatestPublishedNews(4).subscribe({
      next: result => {
        if (result.code === '200' && result.data) {
          this.latestNews = result.data;
        }
      },
      error: err => this.handleError(err, 'Error loading latest news')
    });
  }


  /**
   * load the latest event (limit 4)
   * @private
   */
  private loadLatestEvent(): void {
    this.eventService.getLatestEvent().subscribe({
      next: result => {
        if (result.code === '200' && result.data) {

          this.latestEvent = result.data.map(event => {

            const media = event.mediaList?.[0];

            let displayType: 'IMAGE' | 'VIDEO' | 'NONE' = 'NONE';

            if (media?.imagePath) {
              displayType = 'IMAGE';
            }
            else if (media?.videoUrl) {
              displayType = 'VIDEO';
            }

            return {
              ...event,
              displayType
            };
          });

        }

      },
      error: err => this.handleError(err, 'Error loading latest event')
    });
  }


  /**
   * load the latest workshop (limit 4)
   * @private
   */
  private loadLatestWorkshop(): void {
    this.workshopService.getLatestWorkshop().subscribe({
      next: result => {
        if (result.code === '200' && result.data) {

          this.latestWorkshop = result.data.map(workshop => {

            const media = workshop.mediaList?.[0];

            let displayType: 'IMAGE' | 'VIDEO' | 'NONE' = 'NONE';

            if (media?.imagePath) {
              displayType = 'IMAGE';
            }
            else if (media?.videoUrl) {
              displayType = 'VIDEO';
            }

            return {
              ...workshop,
              displayType
            };
          });

        }

      },
      error: err => this.handleError(err, 'Error loading latest workshop')
    });
  }



  getSafeYoutubeUrl(url: string): SafeResourceUrl {
    const embedUrl = url.replace('watch?v=', 'embed/');
    return this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl);
  }

  // Google map url
  googleMapUrl = 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2677.6573783837503!2d-1.672440624244541!3d47.8462305712128!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x480f3dedf633f7e1%3A0xd9a10ce1969c9845!2s4%20Rue%20des%20Chardonnerets%2C%2035470%20Bain-de-Bretagne!5e0!3m2!1sfr!2sfr!4v1772636525316!5m2!1sfr!2sfr';

}
