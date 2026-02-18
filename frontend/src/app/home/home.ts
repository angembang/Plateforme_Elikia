import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {NewsService} from '../services/news/news-service';
import {News} from '../models/News';
import {environment} from '../../environments/environment';
import {DatePipe, SlicePipe} from '@angular/common';
import {EventService} from '../services/event/event-service';
import {EventElikia} from '../models/EventElikia';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';

@Component({
  selector: 'app-home',
  imports: [
    DatePipe,
    SlicePipe,
    RouterLink
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  protected readonly apiUrlBack = environment.apiBackendUrl;

  latestNews: News[] = [];

  latestEvent: EventElikia[] = [];
  errorMessage?: string;

  constructor(
    private readonly router: Router,
    private readonly newsService: NewsService,
    private readonly eventService: EventService,
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

  ngOnInit(): void {
    this.loadLatestNews();
    this.loadLatestEvent();
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


  getSafeYoutubeUrl(url: string): SafeResourceUrl {
    const embedUrl = url.replace('watch?v=', 'embed/');
    return this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl);
  }

}
