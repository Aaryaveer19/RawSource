import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConsumerService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Get all orders for the logged-in consumer
  getOrders(consumerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/consumers/${consumerId}/orders`);
  }

  // Get detailed items for a specific order
  getOrderItems(orderId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/orders/${orderId}/items`);
  }
}
