import { Component } from '@angular/core';
import {AuthService} from '../services/auth/auth.service';
import {Router,} from '@angular/router';

@Component({
  selector: 'app-admin',
  imports: [
  ],
  templateUrl: './adminComponent.html',
  styleUrl: './adminComponent.scss',
})
export class AdminComponent {
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
