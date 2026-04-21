import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from './components/layout/header/header.component';
import { LandingComponent } from './components/layout/landing/landing.component';
import { FooterComponent } from './components/layout/footer/footer.component';
import { SidebarComponent } from './components/layout/sidebar/sidebar.component';

@NgModule({
  declarations: [
    HeaderComponent,
    LandingComponent,
    FooterComponent,
    SidebarComponent
  ],
  exports: [
    HeaderComponent,
    LandingComponent,
    FooterComponent,
    SidebarComponent
  ],
  imports: [
    CommonModule,
    RouterModule
  ]
})
export class SharedModule { }
