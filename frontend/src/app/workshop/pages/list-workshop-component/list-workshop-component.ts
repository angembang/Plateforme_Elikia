import {Component, OnInit} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {Workshop} from '../../../models/Workshop';
import {WorkshopService} from '../../../services/workshop/workshop-service';
import {AuthService} from '../../../services/auth/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {PaginationService} from '../../../services/pagination/pagination-service';
import {DatePipe, NgOptimizedImage, SlicePipe} from '@angular/common';
import {BaseListComponent} from '../../../list/base-list-component/base-list-component';

@Component({
  selector: 'app-list-workshop-component',
  imports: [
    DatePipe,
    NgOptimizedImage,
    SlicePipe
  ],
  templateUrl: './list-workshop-component.html',
  styleUrl: './list-workshop-component.scss',
})
export class ListWorkshopComponent extends BaseListComponent implements OnInit {
  // Backend base URL (for images)
  protected readonly apiUrlBack = environment.apiBackendUrl;

  // List of event displayed on current page
  workshopList: Workshop[] = [];

  constructor(
    private readonly workshopService: WorkshopService,
    authService: AuthService,
    router: Router,
    route: ActivatedRoute,
    private readonly sanitizer: DomSanitizer,
    paginationService: PaginationService
  ) {
    super(authService, router, route, paginationService);
  }


  ngOnInit() {
    this.initRoleAndRouteContext();

    this.handlePagination(() => {

      if (this.isAdminPage) {
        // Load backend data for admin
        this.loadAdminWorkshop();
      } else if (this.isMemberPage) {
        // Load backend data for member
        this.loadMemberWorkshop();
      } else {
        // Load backend data for public
        this.loadPublicWorkshopPage();
      }

    });
  }


  /**
   * Load paginated workshops from backend
   */
  private loadAdminWorkshop() {
    this.workshopService
      .getWorkshopPage(this.currentPage, this.pageSize)
      .subscribe(result => {
        this.processWorkshopWorkshopResult(result);
      })

  }


  /**
   * Load paginated public workshops from backend
   */
  private loadPublicWorkshopPage() {
    this.workshopService
      .getPublicWorkshopPage(this.currentPage, this.pageSize)
      .subscribe(result => {
        this.processWorkshopWorkshopResult(result);
      })

  }


  /**
   * Load paginated member workshops from backend
   */
  private loadMemberWorkshop() {
    this.workshopService
      .getMemberWorkshopPage(this.currentPage, this.pageSize)
      .subscribe(result => {
        this.processWorkshopWorkshopResult(result);
      })

  }


  /**
   * Navigate to edit page (admin)
   */
  updateWorkshop(eventId: number): void {

    this.router.navigate(['/admin/workshop/edit', eventId]).then(r => {});
  }


  /**
   * Delete workshop (admin only)
   */
  deleteWorkshop(workshopId: number): void {

    if (!confirm('Are you sure you want to delete this workshop?')) {
      return;
    }

    this.workshopService.deleteWorkshop(workshopId).subscribe(result => {

      if (result.code === '200') {

        // Reload current page after delete
        this.loadAdminWorkshop();
      }
    });
  }



  getSafeYoutubeUrl(url: string): SafeResourceUrl {
    const embedUrl = url.replace('watch?v=', 'embed/');
    return this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl);
  }


  /**
   * Navigate to the detail page according to the role
   * @param workshopId
   */
  goToDetail(workshopId: number): void {

    if (this.isAdmin) {
      this.router.navigate(['/admin/workshop/detail', workshopId]).then(r => {});
    } else if(this.isMember) {
      this.router.navigate(['/member/workshop/detail', workshopId]).then(r => {});
    } else {
      this.router.navigate(['/workshop/detail', workshopId]).then(r => {});
    }
  }


  /**
   * helper method to process workshop result
   * @param result
   * @private
   */
  private processWorkshopWorkshopResult(result: any): void {
    if (result.code === '200' && result.data) {

      // Page content
      this.workshopList = result.data.content.map((workshop: { mediaList: any[]; }) => {

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
