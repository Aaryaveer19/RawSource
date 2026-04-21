import { Component, Input, input } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: false,
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent {
  @Input()
  applicationName:string ='';
  currentDate: Date =new Date();
  currentYear: number = new Date().getFullYear();

}
