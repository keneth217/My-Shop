import { Component, EventEmitter, Output } from '@angular/core';
import { ToastService } from 'angular-toastify';

@Component({
  selector: 'app-view-single-product',
  standalone: true,
  imports: [],
  templateUrl: './view-single-product.component.html',
  styleUrl: './view-single-product.component.css'
})
export class ViewSingleProductComponent {

  @Output() close = new EventEmitter<void>();
  constructor(
 
    private toastService: ToastService,
  ) { }

closeViewForm() {
  this.close.emit();
}

}
