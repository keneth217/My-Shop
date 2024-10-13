import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { AngularToastifyModule } from 'angular-toastify';

@Component({
  selector: 'app-add-product-stock',
  standalone: true,
  imports: [CommonModule,AngularToastifyModule],
  templateUrl: './add-product-stock.component.html',
  styleUrl: './add-product-stock.component.css'
})
export class AddProductStockComponent {



  @Output() close = new EventEmitter<void>();
suppliers: any=['keneth','korir','kennh'];
  closeSForm() {
    this.close.emit();
  }

}
