import {Component, OnInit} from '@angular/core';
import {News} from '../../../models/News';
import {NewsService} from '../../../services/news-service';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthService} from '../../../services/auth.service';
import {environment} from '../../../../environments/environment';
import {DatePipe, NgClass, NgOptimizedImage, SlicePipe} from '@angular/common';

@Component({
  selector: 'app-news-list-component',
  imports: [
    SlicePipe,
    DatePipe,
    NgOptimizedImage,
    NgClass,
  ],
  templateUrl: './news-list-component.html',
  styleUrl: './news-list-component.scss',
})
export class NewsListComponent implements OnInit {
  // Backend base URL (for images)
  protected readonly apiUrlBack = environment.apiBackendUrl;

  // List of news displayed on current page
  newsList: News[] = [];

  // Page indexes for pagination buttons
  pages: number[] = [];

  // Pagination state
  currentPage = 0;
  pageSize = 12;
  totalPages = 0;
  isFirstPage = true;
  isLastPage = false;

  // User role
  isAdmin = false;

  // User page
  isAdminPage = false;

  constructor(
    private readonly newsService: NewsService,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit(): void {

    // Check if user is admin
    this.isAdmin = this.authService.getUserRole() === 'ADMIN';

    // check if current route is admin page
    this.isAdminPage = this.router.url.includes('/admin');

    // Listen to query params (?page= & size=)
    this.route.queryParams.subscribe(params => {

      // If no page param → redirect to page 0
      if (!params['page']) {

        this.router.navigate([], {
          relativeTo: this.route,
          queryParams: {
            page: 0,
            size: this.pageSize
          },
          queryParamsHandling: 'merge'
        }).then(r => {});

        return;
      }

      // Read pagination params from URL
      this.currentPage = Number(params['page']) || 0;
      this.pageSize = Number(params['size']) || 12;

      // Load backend data for admin
      if(this.isAdminPage) {
        this.loadNews()
      }
      // Load backend data for public
      this.loadPublishedNewsPage();
    });
  }


  /**
   * Load all news from backend
   */
  private loadNews(): void {
    this.newsService.getAllNews().subscribe(result => {
      if (result.code === '200' && result.data) {
        this.newsList = result.data;
      }
    });
  }


  /**
   * Load paginated published news from backend
   */
  private loadPublishedNewsPage(): void {

    this.newsService
      .getPublishedNewsPage(this.currentPage, this.pageSize)
      .subscribe(result => {

        if (result.code === '200' && result.data) {

          // Page content
          this.newsList = result.data.content;

          // Pagination metadata
          this.totalPages = result.data.totalPages;
          this.isFirstPage = result.data.first;
          this.isLastPage = result.data.last;

          // Generate page numbers
          this.pages = Array.from(
            { length: this.totalPages },
            (_, i) => i
          );
        }
      });
  }


  /**
   * Go to previous page
   */
  previousPage(): void {

    if (this.isFirstPage) return;

    this.goToPage(this.currentPage - 1);
  }


  /**
   * Go to next page
   */
  nextPage(): void {

    if (this.isLastPage) return;

    this.goToPage(this.currentPage + 1);
  }


  /**
   * Navigate to a specific page
   */
  goToPage(page: number): void {

    if (page < 0 || page >= this.totalPages) {
      return;
    }

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        page: page,
        size: this.pageSize
      },
      queryParamsHandling: 'merge'
    }).then(r => {});
  }


  /**
   * Delete news (admin only)
   */
  deleteNews(newsId: number): void {

    if (!confirm('Are you sure you want to delete this news?')) {
      return;
    }

    this.newsService.deleteNews(newsId).subscribe(result => {

      if (result.code === '200') {

        // Reload current page after delete
        this.loadNews();
      }
    });
  }


  /**
   * Navigate to edit page (admin)
   */
  updateNews(newsId: number): void {

    this.router.navigate(['/admin/news/edit', newsId]).then(r => {});
  }


  /**
   * Navigate to the detail page according to the role
   * @param newsId
   */
  goToDetail(newsId: number): void {

    if (this.isAdmin) {
      this.router.navigate(['/admin/news/detail', newsId]).then(r => {});
    } else {
      this.router.navigate(['/news/detail', newsId]).then(r => {});
    }
  }

  /**
   * Get the content status of the news
   * @param status
   */
  getStatusLabel(status: string): string {
    return status === 'PUBLISHED' ? 'Publié' : 'Brouillon';
  }

  getStatusClass(status: string): string {
    return status === 'PUBLISHED' ? 'published' : 'draft';
  }

  /**
   * Check the active page
   * @param page
   */
  isActivePage(page: number): boolean {
    return page === this.currentPage;
  }


  /**
   * Retrieve the news content status for display text according to the status
   * @param news
   */
  getPublishedLabel(news: News): string {
    return news.contentStatus === "PUBLISHED" ? "Publiée le " : "Sera publiée le";

  }

}
