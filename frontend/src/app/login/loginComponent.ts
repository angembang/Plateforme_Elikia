import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../services/auth/auth.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
  ],
  templateUrl: './loginComponent.html',
  styleUrl: './loginComponent.scss',
})
export class LoginComponent {
  errorMessage?: string;
  loginForm!: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {
    // Reactive form
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  submit(): void {

    if (this.loginForm.invalid) {
      return;
    }

    this.authService.login(this.loginForm.value)
      .subscribe({
        next: result => {
          if (result.code === '200') {
            const role = this.authService.getUserRole();

            if(role === 'ADMIN') {
              this.router.navigate(['/admin'])
                .then(r => () => {})
                .catch(() => {});
              return;
            }

            // if no admin, redirect to member space
            const returnUrl =
              this.route.snapshot.queryParamMap.get('returnUrl') || '/';

            this.router.navigateByUrl(returnUrl)
              .then(r => () => {})
              .catch(() => {})
          } else {
            this.errorMessage = result.message;
          }
        },
        error: () => {
          this.errorMessage = 'Login failed';
        }
      });
  }

}
