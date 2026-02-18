import {CanActivateFn, Router} from '@angular/router';
import {AuthService} from '../services/auth/auth.service';
import {inject} from '@angular/core';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Not authenticated
  if(!authService.isAuthenticated()) {
    router.navigate(['/login'], {
      queryParams: {
        returnUrl: state.url
      }
    }).then(r => {})
    return false;
  }
  // Not admin
  if(authService.getUserRole() !== 'ADMIN') {
    router.navigate(['/']).then(r => {});
    return false;
  }
  return true;
};
