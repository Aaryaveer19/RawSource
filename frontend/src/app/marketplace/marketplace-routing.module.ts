import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { MarketplaceListComponent } from './components/marketplace-list/marketplace-list.component';

const routes: Routes = [
  {
    path: '',
    component: MarketplaceListComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MarketplaceRoutingModule { }
