import {Component, OnInit} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {NewsService} from '../services/news-service';
import {News} from '../models/News';
import {environment} from '../../environments/environment';
import {DatePipe, SlicePipe} from '@angular/common';

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
  protected readonly apiUrlback = environment.apiBackendUrl;
 latestNews: News[] = [];
  errorMessage?: string;

  constructor(
    private readonly router: Router,
    private readonly newsService: NewsService) {}

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
  }

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

}
