import { Component } from '@angular/core';
import {Router, RouterLink, RouterOutlet} from "@angular/router";
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-admin-layout',
  imports: [
    RouterOutlet,
    RouterLink
  ],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.scss',
})
export class AdminLayout {
  constructor(private readonly authService: AuthService,
              private readonly router: Router) {}


  /**
   * Logout method
   */
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']).then(r => {});
  }

}
