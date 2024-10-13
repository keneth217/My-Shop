import { Component, EventEmitter, Output } from '@angular/core';
import { AngularToastifyModule } from 'angular-toastify';

@Component({
  selector: 'app-addproduct',
  standalone: true,
  imports: [AngularToastifyModule],
  templateUrl: './addproduct.component.html',
  styleUrl: './addproduct.component.css'
})
export class AddproductComponent {
  @Output() close = new EventEmitter<void>();

  closePForm() {
    this.close.emit();
  }
}
