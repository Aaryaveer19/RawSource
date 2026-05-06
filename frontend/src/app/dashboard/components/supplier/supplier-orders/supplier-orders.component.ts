import { Component, OnInit } from '@angular/core';
import { SupplierService } from '../../../services/supplier.service';
import { AuthService } from '../../../../auth/services/auth.service';

@Component({
  selector: 'app-supplier-orders',
  standalone: false,
  templateUrl: './supplier-orders.component.html',
  styleUrl: './supplier-orders.component.css'
})
export class SupplierOrdersComponent implements OnInit {
  orders: any[] = [];
  isLoading = true;

  constructor(
    private supplierService: SupplierService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user?.supplierId) {
      this.fetchOrders(user.supplierId);
    }
  }

  fetchOrders(supplierId: number): void {
    this.supplierService.getOrders(supplierId).subscribe({
      next: (data) => {
        this.orders = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching supplier orders:', err);
        this.isLoading = false;
      }
    });
  }

  markAsDelivered(order: any): void {
    this.supplierService.updateOrderStatus(order.orderId, 'DELIVERED').subscribe({
      next: () => {
        order.status = 'DELIVERED';
      },
      error: (err) => console.error('Error updating order:', err)
    });
  }
}
