import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { SharedModule } from '../shared/shared.module';
import { OrdersComponent } from './components/consumer/orders/orders.component';
import { OrderItemsComponent } from './components/consumer/order-items/order-items.component';
import { MaterialsComponent } from './components/supplier/materials/materials.component';
import { InventoryComponent } from './components/supplier/inventory/inventory.component';
import { ContractsComponent } from './components/supplier/contracts/contracts.component';
import { DashboardHomeComponent } from './components/dashboard-home/dashboard-home.component';

@NgModule({
  declarations: [
    DashboardComponent,
    OrdersComponent,
    OrderItemsComponent,
    MaterialsComponent,
    InventoryComponent,
    ContractsComponent,
    DashboardHomeComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    DashboardRoutingModule,
    SharedModule
  ]
})
export class DashboardModule { }
