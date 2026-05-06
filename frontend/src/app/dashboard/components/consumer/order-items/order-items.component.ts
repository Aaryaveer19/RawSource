import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ConsumerService } from '../../../services/consumer.service';
import { AuthService } from '../../../../auth/services/auth.service';

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
  orderStatus: string = 'PENDING';
  supplierId: number | null = null;

  constructor(
    private route: ActivatedRoute,
    private consumerService: ConsumerService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.orderId = +idParam;
      this.fetchOrderData(this.orderId);
    } else {
      this.isLoading = false;
      this.errorMessage = 'Order ID not found in the path.';
    }
  }

  fetchOrderData(id: number): void {
    // Fetch parent order for status + supplierId
    this.consumerService.getOrder(id).subscribe({
      next: (orderData) => {
        this.orderStatus = orderData.status;
        this.supplierId = orderData.supplierId || null;

        // Now fetch items (they also include supplierId as a flat field)
        this.consumerService.getOrderItems(id).subscribe({
          next: (data) => {
            this.orderItems = data.map((item: any) => ({
              ...item,
              // Use supplierId from item if available, else fallback to order-level
              resolvedSupplierId: item.supplierId || this.supplierId,
              rating: 0,
              comments: '',
              reviewed: false,
              hoveredStar: 0
            }));
            this.calculateTotal();
            this.isLoading = false;
          },
          error: (error) => {
            this.isLoading = false;
            this.errorMessage = 'Failed to load order details. Please try again.';
            console.error('Error fetching order items:', error);
          }
        });
      },
      error: () => {
        // If order fetch fails, still try to get items
        this.consumerService.getOrderItems(id).subscribe({
          next: (data) => {
            this.orderItems = data.map((item: any) => ({
              ...item,
              resolvedSupplierId: item.supplierId || this.supplierId,
              rating: 0,
              comments: '',
              reviewed: false,
              hoveredStar: 0
            }));
            this.calculateTotal();
            this.isLoading = false;
          },
          error: (error) => {
            this.isLoading = false;
            this.errorMessage = 'Failed to load order details. Please try again.';
          }
        });
      }
    });
  }

  setRating(item: any, star: number): void {
    item.rating = star;
  }

  setHover(item: any, star: number): void {
    item.hoveredStar = star;
  }

  clearHover(item: any): void {
    item.hoveredStar = 0;
  }

  submitReview(item: any): void {
    if (!item.rating || item.rating < 1 || item.rating > 5) {
      alert('Please select a star rating before submitting.');
      return;
    }

    const consumerId = this.authService.getCurrentUser()?.consumerId;
    if (!consumerId) {
      alert('You must be logged in to submit a review.');
      return;
    }

    const supplierId = item.resolvedSupplierId || this.supplierId;

    const payload: any = {
      consumer: { consumerId: consumerId },
      order: { orderId: this.orderId },
      rating: item.rating,
      comments: item.comments || ''
    };

    // Only include supplier if we have the ID
    if (supplierId) {
      payload.supplier = { supplierId: supplierId };
    }

    // Only include material if we have a valid materialId
    if (item.materialId) {
      payload.material = { materialId: item.materialId };
    }

    item.submitting = true;
    this.consumerService.submitReview(payload).subscribe({
      next: () => {
        item.reviewed = true;
        item.submitting = false;
      },
      error: (err) => {
        console.error('Error submitting review', err);
        item.submitting = false;
        alert('Failed to submit review. Please try again.');
      }
    });
  }

  calculateTotal(): void {
    this.orderTotal = this.orderItems.reduce((acc, item) => {
      return acc + (item.quantity * item.pricePerUnit);
    }, 0);
  }
}
