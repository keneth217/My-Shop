import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { SalesService } from '../Services/sales.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, AngularToastifyModule],
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent {

  @Input() totalInCart!: number | null; // Input for sale total
  @Input() productId!: number;
  @Output() close = new EventEmitter<void>();
  checkOutForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private saleService: SalesService,
    private toastService: ToastService
  ) {
    // Initialize the checkout form
    this.checkOutForm = this.fb.group({

      customerName: ['', Validators.required],
      customerPhone: ['', Validators.required],
      customerAddress: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.totalInCart;
    console.log("total in cart" + this.totalInCart)
  }


  CompleteSale() {
    if (this.checkOutForm.valid) {
      const customerData = this.checkOutForm.value;

      this.saleService.checkOut(customerData).subscribe(
        (response) => {
          console.log(response);
          this.toastService.success('Sale completed successfully!');
          this.checkOutForm.reset();
          this.closeForm(); // Emit close event after successful sale
        },
        (error) => {
          this.toastService.error('Failed to complete sale!');
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
