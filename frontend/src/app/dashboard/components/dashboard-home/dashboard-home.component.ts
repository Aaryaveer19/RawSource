import { Component, OnInit } from '@angular/core';
import { AuthService, UserRole } from '../../../auth/services/auth.service';
import { ConsumerService } from '../../services/consumer.service';
import { SupplierService } from '../../services/supplier.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard-home',
  standalone: false,
  templateUrl: './dashboard-home.component.html',
  styleUrl: './dashboard-home.component.css'
})
export class DashboardHomeComponent implements OnInit {
  userRole: UserRole | null = null;
  UserRole = UserRole;
  user: any = null;

  stats = {
    totalOrders: 0,
    activeContracts: 0,
    pendingShipments: 0,
    materialsCount: 0,
    avgRating: 4.8 // Mocking rating as there's no easy aggregate yet
  };

  isLoading = true;

  constructor(
    private authService: AuthService,
    private consumerService: ConsumerService,
    private supplierService: SupplierService
  ) {}

  ngOnInit(): void {
    this.userRole = this.authService.getUserRole();
    this.user = this.authService.getCurrentUser();

    if (this.userRole === UserRole.CONSUMER) {
      this.loadConsumerStats();
    } else if (this.userRole === UserRole.SUPPLIER) {
      this.loadSupplierStats();
    } else {
      this.isLoading = false;
    }
  }

  loadConsumerStats(): void {
    this.consumerService.getOrders(this.user.consumerId).pipe(
      catchError(() => of([]))
    ).subscribe((orders: any[]) => {
      this.stats.totalOrders = orders.length;
      this.stats.pendingShipments = orders.filter((o: any) => o.status?.toLowerCase() === 'processing').length;
      // Note: In this simple implementation, we don't fetch consumer contracts yet
      this.isLoading = false;
    });
  }

  loadSupplierStats(): void {
    forkJoin({
      contracts: this.supplierService.getContracts(this.user.supplierId).pipe(catchError(() => of([]))),
      availability: this.supplierService.getAvailability(this.user.supplierId).pipe(catchError(() => of([])))
    }).subscribe(result => {
      this.stats.activeContracts = result.contracts.filter((c: any) => c.isActive).length;
      this.stats.materialsCount = result.availability.length;
      this.isLoading = false;
    });
  }
}
