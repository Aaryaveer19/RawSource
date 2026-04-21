import { Component, OnInit } from '@angular/core';
import { ConsumerService } from '../../../services/consumer.service';
import { AuthService } from '../../../../auth/services/auth.service';

@Component({
  selector: 'app-orders',
  standalone: false,
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css'
})
export class OrdersComponent implements OnInit {
  orders: any[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private consumerService: ConsumerService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    // Assuming the backend user object has 'consumerId' based on earlier look at controller
    const consumerId = user?.consumerId;

    if (consumerId) {
      this.fetchOrders(consumerId);
    } else {
      this.isLoading = false;
      this.errorMessage = 'User information not found. Please log in again.';
    }
  }

  fetchOrders(id: number): void {
    this.consumerService.getOrders(id).subscribe(
      (data) => {
        this.orders = data;
        this.isLoading = false;
      },
      (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load orders. Please try again later.';
        console.error('Error fetching orders:', error);
      }
    );
  }

  getStatusClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'completed':
      case 'delivered':
        return 'bg-green-100 text-green-800';
      case 'processing':
      case 'shipped':
        return 'bg-yellow-100 text-yellow-800';
      case 'cancelled':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }
}
