import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { ExpenseService } from '../Services/expense.service';
import { SuperService } from '../Services/super.service';
import { ShopData } from '../model/shop.model';

@Component({
  selector: 'app-super-add-shop',
  standalone: true,
  imports: [ReactiveFormsModule, AngularToastifyModule],
  templateUrl: './super-add-shop.component.html',
  styleUrl: './super-add-shop.component.css'
})
export class SuperAddShopComponent {


  @Output() close = new EventEmitter<void>();
  shopForm: any;
  constructor(
    private fb: FormBuilder,
    private shopService: SuperService,
    private toastService: ToastService
  ) {
    this.shopForm = this.fb.group({
      shopName: ['', Validators.required],
      owner: ['', Validators.required],
      adminUsername: ['', Validators.required],
      phoneNumber: ['', Validators.required],
      address: ['', Validators.required],
      email: ['', Validators.required],
    });
  }

  closeForm() {
    this.close.emit();
  }

  submitShopData() {
    if (this.shopForm.valid) {
      const shopData = this.shopForm.value;
      this.shopService.addShop(shopData).subscribe(
        (response: ShopData) => {
          this.toastService.success(response.responseMessage);
          this.shopForm.reset();
          //this.closeEForm();
        },
        (error) => {
          this.toastService.error('Failed to register shop!');
          console.error(error);
        }
      );
    } else {
      this.toastService.warn('Please fill in all required fields!');

    }
  }


}
