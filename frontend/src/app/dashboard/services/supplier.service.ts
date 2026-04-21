import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SupplierService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Get raw materials for the supplier
  getMaterials(supplierId: number): Observable<any[]> {
    // In this backend, suppliers usually have materials tied to them.
    // Assuming /api/suppliers/{id}/materials endpoint exists or similar
    return this.http.get<any[]>(`${this.apiUrl}/suppliers/${supplierId}/materials`);
  }

  getAvailability(supplierId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/suppliers/${supplierId}/availability`);
  }

  getPricing(supplierId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/suppliers/${supplierId}/pricing`);
  }

  getContracts(supplierId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/suppliers/${supplierId}/contracts`);
  }
}
