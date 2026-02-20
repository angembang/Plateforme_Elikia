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
import {CreateEventComponent} from './event/pages/create-event-component/create-event-component';
import {ListEventComponent} from './event/pages/list-event-component/list-event-component';
import {DetailEventComponent} from './event/pages/detail-event-component/detail-event-component';
import {EditEventComponent} from './event/pages/edit-event-component/edit-event-component';
import {CreateWorkshopComponent} from './workshop/pages/create-workshop-component/create-workshop-component';
import {ListWorkshopComponent} from './workshop/pages/list-workshop-component/list-workshop-component';
import {DetailWorkshopComponent} from './workshop/pages/detail-workshop-component/detail-workshop-component';
import {EditWorkshopComponent} from './workshop/pages/edit-workshop-component/edit-workshop-component';

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
      { path: 'news', component: NewsListComponent },
      { path: 'news/detail/:id', component: NewsDetailComponent },
      { path: 'event', component: ListEventComponent },
      { path: 'event/detail/:id', component: DetailEventComponent },
      { path: 'event/edit/:id', component: EditEventComponent },
      { path: 'workshop', component: ListWorkshopComponent },
      { path: 'workshop/detail/:id', component: DetailWorkshopComponent },
      { path: 'workshop/edit/:id', component: DetailWorkshopComponent },
    ]
  },
  // ------------------- Admin layout ----------------
  {
    path: 'admin', component: AdminLayout,
    canActivate: [adminGuard],
    children: [
      { path: '', component: AdminComponent},
      { path:'news/management', component: NewsListComponent },
      { path: 'news/create', component: NewsCreateComponent },
      { path: 'news/edit/:id', component: NewsEditComponent },
      { path: 'news/detail/:id', component: NewsDetailComponent },
      { path: 'event/create', component: CreateEventComponent },
      { path: 'event', component: ListEventComponent },
      { path: 'event/detail/:id', component: DetailEventComponent },
      { path: 'event/edit/:id', component: EditEventComponent },
      { path: 'workshop/create', component: CreateWorkshopComponent },
      { path: 'workshop', component: ListWorkshopComponent },
      { path: 'workshop/detail/:id', component: DetailWorkshopComponent },
      { path: 'workshop/edit/:id', component: EditWorkshopComponent },
    ]
  }

];
