import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './components/layout/header/header.component';
import { FooterComponent } from './components/layout/footer/footer.component';
import { LandingComponent } from './components/layout/landing/landing.component';
import { AppRoutingModule } from '../app-routing.module';



@NgModule({
  declarations: [
    HeaderComponent,
    FooterComponent,
    LandingComponent
  ],
  exports:[HeaderComponent,
    FooterComponent,
    LandingComponent] ,
  imports: [
    CommonModule, AppRoutingModule
  ]
})
export class CoreModule { }
