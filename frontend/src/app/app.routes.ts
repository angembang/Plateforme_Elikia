import { Routes } from '@angular/router';
import {authGuard} from './security/auth-guard';
import {LoginComponent} from './login/loginComponent';
import {RegisterComponent} from './register/registerComponent';
import {Header} from './header/header';
import {Footer} from './footer/footer';
import {Home} from './home/home';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'header', component: Header },
  { path: 'footer', component: Footer },
  { path: '', component: Home },

  {
    path: 'admin',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./admin/adminComponent')
        .then(m => m.AdminComponent)
  }
];
