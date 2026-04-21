import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ConsumerService } from '../../../services/consumer.service';

@Component({
  selector: 'app-order-items',
  standalone: false,
  templateUrl: './order-items.component.html',
  styleUrl: './order-items.component.css'
})
export class OrderItemsComponent implements OnInit {
  orderId: number | null = null;
  orderItems: any[] = [];
  isLoading = true;
  errorMessage = '';
  orderTotal = 0;

  constructor(
    private route: ActivatedRoute,
    private consumerService: ConsumerService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.orderId = +idParam;
      this.fetchOrderItems(this.orderId);
    } else {
      this.isLoading = false;
      this.errorMessage = 'Order ID not found in the path.';
    }
  }

  fetchOrderItems(id: number): void {
    this.consumerService.getOrderItems(id).subscribe(
      (data) => {
        this.orderItems = data;
        this.calculateTotal();
        this.isLoading = false;
      },
      (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load order details. Please try again.';
        console.error('Error fetching order items:', error);
      }
    );
  }

  calculateTotal(): void {
    this.orderTotal = this.orderItems.reduce((acc, item) => {
      return acc + (item.quantity * item.pricePerUnit);
    }, 0);
  }
}
