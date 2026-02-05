import {Component, OnInit} from '@angular/core';
import {News} from '../../../models/News';
import {environment} from '../../../../environments/environment';
import {ActivatedRoute, Router} from '@angular/router';
import {NewsService} from '../../../services/news-service';
import {DatePipe, NgOptimizedImage} from '@angular/common';
import {AuthService} from '../../../services/auth.service';

@Component({
  selector: 'app-news-detail-component',
  imports: [
    DatePipe,
    NgOptimizedImage
  ],
  templateUrl: './news-detail-component.html',
  styleUrl: './news-detail-component.scss',
})
export class NewsDetailComponent implements OnInit {
  // Expose environment to the template
  protected  readonly apiUrlBack = environment.apiBackendUrl;
  // News to displayed
  news?: News;

  // User role
  isAdmin = false;

  errorMessage?: string;

  private handleError(err: any, fallbackMessage: string): void {
    this.errorMessage =
      err?.error?.message ?? fallbackMessage;
  }

  constructor(
    private readonly route: ActivatedRoute,
    private readonly newsService: NewsService,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {

    // Check is admin
    this.isAdmin = this.authService.getUserRole() === 'ADMIN';

    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (id) {
      this.loadNews(id);
    }
  }

  private loadNews(id: number): void {
    this.newsService.getNewsById(id).subscribe({
      next: result => {
        if (result.code === '200') {
          this.news = result.data!;
        }
      },
      error: err => this.handleError(err, 'Error loading news detail')
    });
  }


  // Navigation
  goBack(): void {

    if (this.isAdmin) {
      this.router.navigate(['/admin/news/management']).then(r => {});
    } else {
      this.router.navigate(['/news']).then(r => {});
    }
  }


  goToEdit(): void {

    if (!this.news) return;

    this.router.navigate(['/admin/news/edit', this.news.newsId]).then(r => {});
  }


  deleteNews(): void {

    if (!this.news) return;

    if (!confirm('Voulez-vous supprimer cette actualité ?')) {
      return;
    }

    this.newsService.deleteNews(this.news.newsId).subscribe(result => {

      if (result.code === '200') {

        // Return to the news list management
        this.router.navigate(['/admin/news/management']).then(r => {});
      }

    });
  }

}

