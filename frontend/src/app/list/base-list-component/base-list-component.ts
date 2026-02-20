import {AuthService} from '../../services/auth/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {PaginationService} from '../../services/pagination/pagination-service';

export class BaseListComponent {
  // Pagination
  currentPage = 0;
  pageSize = 12;
  totalPages = 0;
  pages: number[] = [];
  isFirstPage = true;
  isLastPage = false;

  // Role
  isAdmin = false;
  isMember = false;
  isAdminPage = false;
  isMemberPage = false;

  protected constructor(
    protected authService: AuthService,
    protected router: Router,
    protected route: ActivatedRoute,
    protected paginationService: PaginationService
  ) {}

  /**
   * Retrieve user role
   * @protected
   */
  protected initRoleAndRouteContext(): void {
    const role = this.authService.getUserRole();

    this.isAdmin = role === 'ADMIN';
    this.isMember = role === 'MEMBER';

    const url = this.router.url;

    // Check if user is admin
    this.isAdminPage = url.includes('/admin');
    // check if current route is member page
    this.isMemberPage = url.includes('/member');
  }


  /**
   * Pagination
   * @param callback
   * @protected
   */
  protected handlePagination(callback: () => void): void {
    this.route.queryParams.subscribe((params: any) => {

      const pagination =
        this.paginationService.handlePaginationParams(
          params,
          this.route,
          this.pageSize
        );

      if (!pagination) return;

      this.currentPage = pagination.page;
      this.pageSize = pagination.size;

      callback();
    });
  }


  /**
   * Pagination metadata
   * @param data
   * @protected
   */
  protected updatePaginationMetadata(data: any): void {
    this.totalPages = data.totalPages;
    this.isFirstPage = data.first;
    this.isLastPage = data.last;

    this.pages = Array.from(
      { length: this.totalPages },
      (_, i) => i
    );
  }


  /**
   * Navigate to a specific page
    * @param page
   */
  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages) return;

    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {page, size: this.pageSize},
      queryParamsHandling: 'merge'
    }).then(r => {});
  }


  /**
   * Go to previous page
   */
  previousPage(): void {
    if (!this.isFirstPage) {
      this.goToPage(this.currentPage - 1);
    }
  }

  /**
   * Go to next page
   */
  nextPage(): void {
    if (!this.isLastPage) {
      this.goToPage(this.currentPage + 1);
    }
  }

  /**
   * Check the active page
   * @param page
   */
  isActivePage(page: number): boolean {
    return page === this.currentPage;
  }

}
