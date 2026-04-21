import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService, UserRole } from '../../../services/auth.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule]
})
export class RegisterComponent {
  registerForm = new FormGroup({
    name: new FormControl('', [Validators.required, Validators.minLength(3)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    phone: new FormControl(''),
    password: new FormControl('', [Validators.required, Validators.minLength(6)]),
    confirmPassword: new FormControl('', Validators.required)
  });

  selectedRole: UserRole = UserRole.CONSUMER;
  UserRole = UserRole;

  isLoading = false;
  errorMessage = '';

  get f() {
    return this.registerForm.controls;
  }

  constructor(private authService: AuthService, private router: Router) {}

  setRole(role: UserRole) {
    this.selectedRole = role;
    this.errorMessage = '';
  }

  onSubmit() {
    this.errorMessage = '';

    if (this.registerForm.invalid) {
      return;
    }

    if (this.registerForm.value.password !== this.registerForm.value.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      return;
    }

    this.isLoading = true;

    const payload = {
      name: this.registerForm.value.name || '',
      email: this.registerForm.value.email || '',
      phone: this.registerForm.value.phone || null,
      password: this.registerForm.value.password || '',
      confirmPassword: this.registerForm.value.confirmPassword || ''
    };

    const registerObs = this.selectedRole === UserRole.CONSUMER
      ? this.authService.registerConsumer(payload)
      : this.authService.registerSupplier(payload);

    registerObs.subscribe(
      (response) => {
        this.isLoading = false;
        if (response.token) {
          this.router.navigate(['/dashboard']);
        } else {
          this.errorMessage = response.errorDesc || 'Registration failed';
        }
      },
      (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.errorDesc || 'Registration failed. Please try again.';
        console.error('Registration error:', error);
      }
    );
  }
}
