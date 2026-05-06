import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { MarketplaceRoutingModule } from './marketplace-routing.module';
import { MarketplaceListComponent } from './components/marketplace-list/marketplace-list.component';


@NgModule({
  declarations: [
    MarketplaceListComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    MarketplaceRoutingModule
  ]
})
export class MarketplaceModule { }
