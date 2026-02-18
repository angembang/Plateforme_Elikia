import {Component, OnInit} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {AuthService} from '../../../services/auth/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {EventService} from '../../../services/event/event-service';
import {EventElikia} from '../../../models/EventElikia';
import {DatePipe, NgOptimizedImage, SlicePipe} from '@angular/common';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {PaginationService} from '../../../services/pagination/pagination-service';

@Component({
  selector: 'app-list-event-component',
  imports: [
    NgOptimizedImage,
    SlicePipe,
    DatePipe
  ],
  templateUrl: './list-event-component.html',
  styleUrl: './list-event-component.scss',
})
export class ListEventComponent implements OnInit {

  // Backend base URL (for images)
  protected readonly apiUrlBack = environment.apiBackendUrl;

  // List of event displayed on current page
  eventList: EventElikia[] = [];

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
  isMember = false;

  // User page
  isAdminPage = false;
  isMemberPage = false;

  constructor(
    private readonly eventService: EventService,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly sanitizer: DomSanitizer,
    private readonly paginationService: PaginationService
  ) {}

  ngOnInit() {
    // Check if user is admin
    this.isAdmin = this.authService.getUserRole() === 'ADMIN';

    // Check if user is member
    this.isMember = this.authService.getUserRole() === 'MEMBER';

    // check if current route is admin page
    this.isAdminPage = this.router.url.includes('/admin');

    // check if current route is member page
    this.isMemberPage = this.router.url.includes('/member');

    // Listen to query params (?page= & size=)
    this.route.queryParams.subscribe(params => {
      const pagination =
        this.paginationService.handlePaginationParams(
          params,
          this.route,
          this.pageSize
        );

      if (!pagination) return;

      this.currentPage = pagination.page;
      this.pageSize = pagination.size;

      // Load backend data for admin
      if (this.isAdminPage) {
        this.loadAdminEvent();
      }

      // Load backend data for member
      if (this.isMemberPage) {
        this.loadMemberEvent();
      }

      // Load backend data for public
      this.loadPublicEventPage();
    })
  }


  /**
   * Load paginated events from backend
   */
  private loadAdminEvent() {
    this.eventService
      .getEventPage(this.currentPage, this.pageSize)
      .subscribe(result => {
        this.processEventEventResult(result);
      })

  }


  /**
   * Load paginated member events from backend
   */
  private loadMemberEvent() {
    this.eventService
      .getMemberEventPage(this.currentPage, this.pageSize)
      .subscribe(result => {
        this.processEventEventResult(result);
      })

  }

  /**
   * Load paginated public events from backend
   */
  private loadPublicEventPage() {
    this.eventService
      .getPublicEventPage(this.currentPage, this.pageSize)
      .subscribe(result => {
        this.processEventEventResult(result);
      })

  }


  /**
   * Navigate to edit page (admin)
   */
  updateEvent(eventId: number): void {

    this.router.navigate(['/admin/event/edit', eventId]).then(r => {});
  }


  /**
   * Delete event (admin only)
   */
  deleteEvent(eventId: number): void {

    if (!confirm('Are you sure you want to delete this event?')) {
      return;
    }

    this.eventService.deleteEvent(eventId).subscribe(result => {

      if (result.code === '200') {

        // Reload current page after delete
        this.loadAdminEvent();
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
   * Check the active page
   * @param page
   */
  isActivePage(page: number): boolean {
    return page === this.currentPage;
  }


  getSafeYoutubeUrl(url: string): SafeResourceUrl {
    const embedUrl = url.replace('watch?v=', 'embed/');
    return this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl);
  }


  /**
   * Navigate to the detail page according to the role
   * @param eventId
   */
  goToDetail(eventId: number): void {

    if (this.isAdmin) {
      this.router.navigate(['/admin/event/detail', eventId]).then(r => {});
    } else if(this.isMember) {
      this.router.navigate(['/member/event/detail', eventId]).then(r => {});
    } else {
      this.router.navigate(['/event/detail', eventId]).then(r => {});
    }
  }


  /**
   * helper method to process event result
   * @param result
   * @private
   */
  private processEventEventResult(result: any): void {
    if (result.code === '200' && result.data) {

      // Page content
      this.eventList = result.data.content.map((event: { mediaList: any[]; }) => {

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
  }
}
