import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashboardComponent } from './components/dashboard/dashboard.component';
import { DashboardHomeComponent } from './components/dashboard-home/dashboard-home.component';
import { OrdersComponent } from './components/consumer/orders/orders.component';
import { OrderItemsComponent } from './components/consumer/order-items/order-items.component';
import { MaterialsComponent } from './components/supplier/materials/materials.component';
import { InventoryComponent } from './components/supplier/inventory/inventory.component';
import { ContractsComponent } from './components/supplier/contracts/contracts.component';
import { SupplierOrdersComponent } from './components/supplier/supplier-orders/supplier-orders.component';

const routes: Routes = [
  {
    path: '',
    component: DashboardComponent,
    children: [
      { path: '', component: DashboardHomeComponent },
      { path: 'consumer/orders', component: OrdersComponent },
      { path: 'consumer/orders/:id', component: OrderItemsComponent },
      { path: 'supplier/materials', component: MaterialsComponent },
      { path: 'supplier/inventory', component: InventoryComponent },
      { path: 'supplier/contracts', component: ContractsComponent },
      { path: 'supplier/orders', component: SupplierOrdersComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule { }
