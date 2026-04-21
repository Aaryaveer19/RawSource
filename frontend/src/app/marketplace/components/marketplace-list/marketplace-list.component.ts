import { Component, OnInit } from '@angular/core';
import { MarketplaceService } from '../../services/marketplace.service';

@Component({
  selector: 'app-marketplace-list',
  standalone: false,
  templateUrl: './marketplace-list.component.html',
  styleUrl: './marketplace-list.component.css'
})
export class MarketplaceListComponent implements OnInit {
  materials: any[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(private marketplaceService: MarketplaceService) {}

  ngOnInit(): void {
    this.fetchMaterials();
  }

  fetchMaterials(): void {
    this.marketplaceService.getAllMaterials().subscribe(
      (data) => {
        this.materials = data;
        this.isLoading = false;
      },
      (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to load materials from the network.';
        console.error('Error fetching marketplace materials:', error);
      }
    );
  }

  // Helper for generating placeholder images if backend doesn't provide them
  getPlaceholderImage(name: string): string {
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(name)}&background=random&color=fff&size=512`;
  }
}
