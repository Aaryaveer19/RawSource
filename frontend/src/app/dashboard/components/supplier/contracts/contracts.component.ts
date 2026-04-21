import { Component, OnInit } from '@angular/core';
import { SupplierService } from '../../../services/supplier.service';
import { AuthService } from '../../../../auth/services/auth.service';

@Component({
  selector: 'app-contracts',
  standalone: false,
  templateUrl: './contracts.component.html',
  styleUrl: './contracts.component.css'
})
export class ContractsComponent implements OnInit {
  contracts: any[] = [];
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
      this.fetchContracts(supplierId);
    } else {
      this.isLoading = false;
      this.errorMessage = 'Supplier session data missing.';
    }
  }

  fetchContracts(id: number): void {
    this.supplierService.getContracts(id).subscribe(
      (data) => {
        this.contracts = data;
        this.isLoading = false;
      },
      (error) => {
        this.isLoading = false;
        this.errorMessage = 'Could not sync contract agreements.';
        console.error('Error fetching contracts:', error);
      }
    );
  }
}
