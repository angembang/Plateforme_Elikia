import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {AuthService} from '../services/auth/auth.service';
import {Router} from '@angular/router';


@Component({
  selector: 'app-register',
  imports: [
    ReactiveFormsModule,
  ],
  templateUrl: './registerComponent.html',
  styleUrl: './registerComponent.scss',
})
export class RegisterComponent {
  successMessage?: string;
  errorMessage?: string | null;
  registerForm!: FormGroup

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required]
    });
  }

  submit(): void {
    if (this.registerForm.invalid) {
      this.errorMessage = "Le formulaire est invalide";
      return;
    }

    const { password, confirmPassword } = this.registerForm.value;

    if (password !== confirmPassword) {
      this.errorMessage = 'Les mots de passe ne correspondent pas';
      return;
    }

    this.authService.register(this.registerForm.value)
      .subscribe({
        next: result => {
          if (result.code === '201') {
            this.router.navigate(['/register/success']).then(r => {});
          } else {
            this.errorMessage = result.message;
          }
        },
        error: () => {
          this.errorMessage = 'Registration failed';
        }
      });
  }


}
