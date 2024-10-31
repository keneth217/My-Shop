import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { SalesService } from '../Services/sales.service';
import { Router } from '@angular/router';
import { SuperService } from '../Services/super.service';

@Component({
  selector: 'app-activate-shop',
  standalone: true,
  imports: [AngularToastifyModule,ReactiveFormsModule],
  templateUrl: './activate-shop.component.html',
  styleUrl: './activate-shop.component.css'
})
export class ActivateShopComponent {
  @Input() shopId!: number;
  @Output() close = new EventEmitter<void>();
activateForm!: FormGroup;



constructor(
  private fb: FormBuilder,
  private router:Router,
  private superService: SuperService,
  private toastService: ToastService
) {
  this.activateForm = this.fb.group({
    expiryDate: ['', Validators.required],

  });
}



activateShop() {
  if (this.activateForm.valid) {
    const shopData = this.activateForm.value;

    this.superService.activateShop(this.shopId,shopData).subscribe(
      (response) => {
        console.log(response);
        this.toastService.success('shop activatedsuccessfully!');
        this.router.navigateByUrl('/admin/shops');
       


        // this.checkOutForm.reset();
        //this.closeForm(); // Emit close event after successful sale
      },
      (error) => {
        this.toastService.error('Failed to activate shop!');
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
