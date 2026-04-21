import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { SupplierService } from '../../../services/supplier.service';
import { AuthService } from '../../../../auth/services/auth.service';

@Component({
  selector: 'app-inventory',
  standalone: false,
  templateUrl: './inventory.component.html',
  styleUrl: './inventory.component.css'
})
export class InventoryComponent implements OnInit {
  inventoryData: any[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private supplierService: SupplierService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    const supplierId = user?.supplierId;

    if (supplierId) {
      this.loadCombinedData(supplierId);
    } else {
      this.isLoading = false;
      this.errorMessage = 'Supplier session not found.';
    }
  }

  loadCombinedData(id: number): void {
    forkJoin({
      availability: this.supplierService.getAvailability(id),
      pricing: this.supplierService.getPricing(id)
    }).subscribe({
      next: (result) => {
        // Simple merge logic: assumption is and availability record and pricing record exist for the same material
        // In a real app, we'd join on materialId.
        this.inventoryData = result.availability.map((avail: any) => {
          const priceRecord = result.pricing.find((p: any) => p.materialId === avail.materialId);
          return {
            ...avail,
            price: priceRecord?.price || 0
          };
        });
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to sync inventory and pricing modules. Check connection.';
        console.error('Supplier data error:', err);
      }
    });
  }

  onSave(item: any): void {
    // Placeholder for update logic
    console.log('Update material:', item);
    alert('Changes for ' + (item.materialName || 'Material') + ' saved locally! (Backend Update Pending)');
  }
}
