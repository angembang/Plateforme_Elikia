import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthStorageService } from '../services/auth-storage.service';
import {catchError, throwError} from 'rxjs';

export const jwtInterceptor: HttpInterceptorFn =
  (req, next) => {
    // Dependency injection
    const authStorage = inject(AuthStorageService);

    // Public endpoints
    if (isPublicEndpoint(req.url)) {
      return next(req);
    }

    // Retrieve JWT token
    const token = authStorage.getToken();

    // Clone request if token exists
    const authReq = token
      ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      })
      : req;

    // Forward request + handle errors
    return next(authReq).pipe(
      catchError((error: HttpErrorResponse) => {

        // If backend says Unauthorized
        if (error.status === 401) {
          // Clear token (force logout)
          authStorage.clear();
        }

        // Propagate error
        return throwError(() => error);
      })
    );

};

/**
 * Determines if endpoint is public
 */
function isPublicEndpoint(url: string): boolean {
  return url.includes('/login') || url.includes('/register');
}
