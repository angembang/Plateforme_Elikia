import { Routes } from '@angular/router';
import {LoginComponent} from './login/loginComponent';
import {RegisterComponent} from './register/registerComponent';
import {Header} from './header/header';
import {Footer} from './footer/footer';
import {Home} from './home/home';
import {adminGuard} from './security/admin-guard';
import {MainLayout} from './layout/main-layout/main-layout';
import {AdminLayout} from './layout/admin-layout/admin-layout';
import {NewsListComponent} from './news/pages/news-list-component/news-list-component';
import {AdminComponent} from './admin/adminComponent';
import {NewsCreateComponent} from './news/pages/news-create-component/news-create-component';
import {NewsEditComponent} from './news/pages/news-edit-component/news-edit-component';
import {NewsDetailComponent} from './news/pages/news-detail-component/news-detail-component';

export const routes: Routes = [
  // ------- Public / main layout --------------
  {
    path: '', component: MainLayout,
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'header', component: Header },
      { path: 'footer', component: Footer },
      { path: '', component: Home },
      { path: 'news', component: NewsListComponent},
      { path: 'news/detail/:id', component: NewsDetailComponent},

    ]
  },
  // ------------------- Admin layout ----------------
  {
    path: 'admin', component: AdminLayout,
    canActivate: [adminGuard],
    children: [
      {
        path: '', component: AdminComponent
      },
      {
        path:'news/management', component: NewsListComponent
      },
      {
        path: 'news/create', component: NewsCreateComponent
      },
      {
        path: 'news/edit/:id', component: NewsEditComponent
      },
      { path: 'news/detail/:id', component: NewsDetailComponent},
    ]
  }

];
