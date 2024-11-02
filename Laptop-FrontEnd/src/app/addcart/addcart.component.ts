import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { SupplierService } from '../Services/supplier.service';
import { SalesService } from '../Services/sales.service';
import { CartResponse } from '../model/cartResponse';
import { Router } from '@angular/router';

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
    private toastService: ToastService,
    private router:Router
  ) {
    this.cartForm = this.fb.group({
      quantity: ['', Validators.required],

    });
  }

  addCartData() {
    if (this.cartForm.valid) {
      const cartData = this.cartForm.value;
  
      this.saleService.addCart(this.productId, cartData).subscribe(
        (response) => {
          console.log(response);
          if (response) {
            this.toastService.success("Item added successfully!");
            this.router.navigateByUrl('/dash/product');
          } else {
            this.toastService.info("Item added successfully!"); // Fallback message if responseMessage is undefined
          }
         // this.cartForm.reset();
          this.router.navigateByUrl('/dash/product');
        },
        (error) => {
          console.error('Error adding to cart:', error);
          const errorMessage = error.error?.errorMessage || "Something went wrong!";
          this.toastService.error(errorMessage);
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
