import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../services/auth.service';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './loginComponent.html',
  styleUrl: './loginComponent.scss',
})
export class LoginComponent {
  errorMessage?: string;
  loginForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
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

    this.authService.login(this.loginForm.value as any)
      .subscribe({
        next: result => {
          if (result.code === '200') {

            // Redirect after login
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
