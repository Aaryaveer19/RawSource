import { Component, OnInit } from '@angular/core';
import { MarketplaceService } from '../../services/marketplace.service';
import { AuthService } from '../../../auth/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-marketplace-list',
  standalone: false,
  templateUrl: './marketplace-list.component.html',
  styleUrl: './marketplace-list.component.css'
})
export class MarketplaceListComponent implements OnInit {
  materials: any[] = []; // Now stores MarketplaceListingDTO objects
  isLoading = true;
  errorMessage = '';
  isOrdering = false;
  searchTerm: string = '';

  get filteredMaterials(): any[] {
    if (!this.searchTerm) {
      return this.materials;
    }
    const term = this.searchTerm.toLowerCase();
    return this.materials.filter(m => 
      (m.materialName && m.materialName.toLowerCase().includes(term)) ||
      (m.materialDescription && m.materialDescription.toLowerCase().includes(term)) ||
      (m.supplierName && m.supplierName.toLowerCase().includes(term))
    );
  }

  constructor(
    private marketplaceService: MarketplaceService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchMaterials();
  }

  fetchMaterials(): void {
    this.marketplaceService.getListings().subscribe(
      (data) => {
        this.materials = data.map((item: any) => ({ ...item, orderQuantity: 1 }));
        this.isLoading = false;
      },
      (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load materials from the network.';
        console.error('Error fetching marketplace materials:', error);
      }
    );
  }

  orderItem(listing: any): void {
    const user = this.authService.getCurrentUser();
    const role = this.authService.getUserRole();
    
    if (!user || role !== 'CONSUMER') {
      alert("Please log in as a Consumer to place orders.");
      this.router.navigate(['/auth/login']);
      return;
    }

    // Use the quantity selected by the user
    const orderQuantity = listing.orderQuantity || 1;

    if (orderQuantity > listing.quantityAvailable) {
        alert("Not enough stock available from this supplier!");
        return;
    }

    this.isOrdering = true;
    const payload = {
        pricingId: listing.pricingId,
        consumerId: user.consumerId,
        quantity: orderQuantity
    };

    this.marketplaceService.purchase(payload).subscribe(
        () => {
            this.isOrdering = false;
            alert(`Order placed successfully with ${listing.supplierName}!`);
            this.fetchMaterials(); // Refresh stock
        },
        (err) => {
            this.isOrdering = false;
            alert("Failed to place order.");
            console.error(err);
        }
    );
  }

  // Helper for generating placeholder images if backend doesn't provide them
  getPlaceholderImage(name: string): string {
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(name || 'M')}&background=random&color=fff&size=512`;
  }
}
