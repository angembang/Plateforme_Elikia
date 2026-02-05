import {CanActivateFn, Router} from '@angular/router';
import {AuthService} from '../services/auth.service';
import {inject} from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // User not authenticated
  if(!authService.isAuthenticated()) {
    router.navigate(['/login'], {
      queryParams: {
        returnUrl: state.url
      }
    }).then(r => {});
    return false;
  }
  // User authenticated
  return true;
};
