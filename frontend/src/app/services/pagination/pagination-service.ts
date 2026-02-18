import { Injectable } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class PaginationService {
  constructor(
    private router: Router
  ) {}

  handlePaginationParams(
    params: any,
    route_checkbox: ActivatedRoute,
    pageSize: number
  ): { page: number; size: number } | null {

    // If page param is missing, redirect
    if (params['page'] === undefined || params['page'] === null) {

      this.router.navigate([], {
        relativeTo: route_checkbox,
        queryParams: {
          page: 0,
          size: pageSize
        },
        queryParamsHandling: 'merge'
      }).then(r => {});

      return null;
    }

    // Parse values
    const page = Number(params['page']) || 0;
    const size = Number(params['size']) || pageSize;

    return { page, size };
  }

}
