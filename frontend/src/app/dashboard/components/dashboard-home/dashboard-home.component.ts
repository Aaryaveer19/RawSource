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
    avgRating: 4.8
  };

  recentActivity: any[] = [];
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
      
      // Populate feed with recent orders
      this.recentActivity = [...orders].sort((a, b) => new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime()).slice(0, 5);
      
      this.isLoading = false;
    });
  }

  loadSupplierStats(): void {
    forkJoin({
      contracts: this.supplierService.getContracts(this.user.supplierId).pipe(catchError(() => of([]))),
      availability: this.supplierService.getAvailability(this.user.supplierId).pipe(catchError(() => of([])))
    }).subscribe(result => {
      this.stats.activeContracts = result.contracts.filter((c: any) => c.isActive || !c.isActive).length; // using length as mock for active
      this.stats.materialsCount = result.availability.length;
      
      // Populate feed with recent contracts
      this.recentActivity = [...result.contracts].slice(0, 5); // Assuming latest first or just taking first 5

      this.isLoading = false;
    });
  }
}

