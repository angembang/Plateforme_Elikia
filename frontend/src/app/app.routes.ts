import { Routes } from '@angular/router';
import {authGuard} from './security/auth-guard';
import {LoginComponent} from './login/loginComponent';
import {RegisterComponent} from './register/registerComponent';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  {
    path: 'admin',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./admin/adminComponent')
        .then(m => m.AdminComponent)
  }
];
