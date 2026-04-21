import { Component, OnInit } from '@angular/core';
import { SupplierService } from '../../../services/supplier.service';
import { AuthService } from '../../../../auth/services/auth.service';

@Component({
  selector: 'app-materials',
  standalone: false,
  templateUrl: './materials.component.html',
  styleUrl: './materials.component.css'
})
export class MaterialsComponent implements OnInit {
  materials: any[] = [];
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
      this.fetchMaterials(supplierId);
    } else {
      this.isLoading = false;
      this.errorMessage = 'Supplier information not found. Please log in as a Supplier.';
    }
  }

  fetchMaterials(id: number): void {
    // Note: Availability lists the materials a supplier currently offers
    this.supplierService.getAvailability(id).subscribe(
      (data) => {
        this.materials = data;
        this.isLoading = false;
      },
      (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load your material catalog.';
        console.error('Error fetching materials:', error);
      }
    );
  }
}
