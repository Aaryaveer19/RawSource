import { Component } from '@angular/core';
import { AuthService, UserRole } from '../../../../auth/services/auth.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  userRole$: Observable<UserRole | null>;
  UserRole = UserRole;

  constructor(private authService: AuthService, private router: Router) {
    this.userRole$ = this.authService.userRole$;
  }

  get currentUser() {
    return this.authService.getCurrentUser();
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
