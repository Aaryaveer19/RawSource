import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService, UserRole } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule]
})
export class LoginComponent {
  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(6)])
  });

  selectedRole: UserRole = UserRole.CONSUMER;
  UserRole = UserRole; // Make enum accessible in template

  isLoading = false;
  errorMessage = '';

  get f() {
    return this.loginForm.controls;
  }

  constructor(private authService: AuthService, private router: Router) {}

  setRole(role: UserRole) {
    this.selectedRole = role;
    this.errorMessage = '';
  }

  onSubmit() {
    this.errorMessage = '';

    if (this.loginForm.invalid) {
      return;
    }

    this.isLoading = true;

    const payload = {
      email: this.loginForm.value.email || '',
      password: this.loginForm.value.password || ''
    };

    const loginObs = this.selectedRole === UserRole.CONSUMER 
      ? this.authService.loginConsumer(payload)
      : this.authService.loginSupplier(payload);

    loginObs.subscribe(
      (response) => {
        this.isLoading = false;
        if (response.token) {
          this.router.navigate(['/dashboard']);
        } else {
          this.errorMessage = response.errorDesc || 'Login failed. Invalid response from server.';
        }
      },
      (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.errorDesc || 'Login failed. Please check your credentials and backend connection.';
        console.error('Login error:', error);
      }
    );
  }
}
