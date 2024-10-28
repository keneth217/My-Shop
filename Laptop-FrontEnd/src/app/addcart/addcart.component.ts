import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { SupplierService } from '../Services/supplier.service';
import { SalesService } from '../Services/sales.service';

@Component({
  selector: 'app-addcart',
  standalone: true,
  imports: [ReactiveFormsModule, AngularToastifyModule, CommonModule],
  templateUrl: './addcart.component.html',
  styleUrl: './addcart.component.css'
})
export class AddcartComponent {

  @Input() productId!: number;
  @Output() close = new EventEmitter<void>();
  cartForm!: FormGroup;


  constructor(
    private fb: FormBuilder,
    private saleService: SalesService,
    private toastService: ToastService
  ) {
    this.cartForm = this.fb.group({
      quantity: ['', Validators.required],

    });
  }

  addCartData() {
    if (this.cartForm.valid) {
      const cartData = this.cartForm.value;


      this.saleService.addCart(this.productId,cartData).subscribe(
        (response) => {
          console.log(response)
          this.toastService.success('add to cart successfully!');
          this.cartForm.reset();
          //this.closeEForm();
        },
        (error) => {
          this.toastService.error('Failed to add to cart!');
          console.error(error);
        }
      );
    } else {
      this.toastService.warn('Please fill in all required fields!');
    }
  }
  closeForm() {
    this.close.emit();
  }


}
