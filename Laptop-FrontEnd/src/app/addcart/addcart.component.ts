import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { SupplierService } from '../Services/supplier.service';
import { SalesService } from '../Services/sales.service';
import { CartResponse } from '../model/cartResponse';
import { Router } from '@angular/router';
import { LoaderService } from '../Services/loader.service';

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
  cartItems: {
    id: number;
    itemCosts: number;
    productName: string | null;
    quantity: number;
    shopCode: string;
    status: string;
  }[] = [];  // Initialize as an empty array
  loading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private saleService: SalesService,
    private toastService: ToastService,
    private router: Router,
    private loaderService: LoaderService,
  ) {
    this.cartForm = this.fb.group({
      quantity: ['', Validators.required],

    });
  }

  ngOnInit(): void {
    this.fetchCartItems();


  }

  addCartData() {
    if (this.cartForm.valid) {
      const cartData = this.cartForm.value;
      this.loading = true; // Set loading state to true

      this.saleService.addCart(this.productId, cartData).subscribe(
        (response) => {
          console.log(response);
          this.fetchCartItems(); // Refresh cart items regardless of response

          // Display success message and delay navigation to give time for feedback
          const message = response ? "Item added successfully!" : "Item added!";
          this.toastService.success(message);

          setTimeout(() => {
            this.router.navigate(['/dash/product']);  // Navigate to the product page after delay
          }, 2000); // 2-second delay for user feedback

        },
        (error) => {
          console.error('Error adding to cart:', error);
          const errorMessage = error.error?.errorMessage || "Something went wrong!";
          this.toastService.error(errorMessage);
        },
        () => {
          this.loading = false; // Reset loading state when request completes
        }
      );
    } else {
      this.toastService.warn('Please fill in all required fields!');
    }
  }


  fetchCartItems(): void {
    this.loaderService.show();
    this.saleService.fetchCartItems().subscribe({
      next: (data: CartResponse) => {  // Use the CartResponse type here
        console.log(data);
        this.cartItems = data.items || [];  // Properly access 'items'

        this.loaderService.hide();
      },
      error: (error) => {
        console.error('Error fetching products:', error);
        this.loaderService.hide();
      }
    });
  }

  closeForm() {
    this.close.emit();
    this.fetchCartItems(); // Refresh cart items regardless of response
  }


}
