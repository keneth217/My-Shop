import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { ShopService } from '../Services/shop.service';
import { PopupComponent } from '../popup/popup.component';

@Component({
  selector: 'app-my-shop',
  standalone: true,
  imports: [AngularToastifyModule, ReactiveFormsModule, CommonModule, PopupComponent],
  templateUrl: './my-shop.component.html',
  styleUrls: ['./my-shop.component.css']
})
export class MyShopComponent implements OnInit {
  @ViewChild('popup') popup!: PopupComponent;
  shopForm: FormGroup;
  shopLogo: string | null = null; // For existing logo URL
  newLogo: { file: File, preview: string } | null = null; // For the new logo

  constructor(
    private fb: FormBuilder,
    private shopService: ShopService,
    private toastService: ToastService,
    
  ) {
    this.shopForm = this.fb.group({
      shopCode: [{ value: '', disabled: true }, Validators.required],
      shopName: ['', Validators.required],
      owner: ['', Validators.required],
      phoneNumber: ['', Validators.required],
      address: ['', Validators.required],
      shopLogo: [null]
    });
  }
  showSuccess(message: string): void {
    this.popup.showPopup(message, 'success');
  }

  showError(message: string): void {
    this.popup.showPopup(message, 'error');
  }

  ngOnInit(): void {
    this.loadShopDetails();
   // this.shopForm.get('shopCode')?.disable(); // Disable the shopCode control
  }

  loadShopDetails(): void {
    this.shopService.getShopDetails().subscribe(
      (data) => {
        console.log(data);
        this.shopForm.patchValue({
          shopCode: data.shopCode,
          shopName: data.shopName,
          owner: data.owner,
          phoneNumber: data.phoneNumber,
          address: data.address
        });

        // Set the logo URL if it exists
        if (data.shopLogoUrl) {
          this.shopLogo = data.shopLogoUrl;
        }
      },
      (error) => {
        this.showError('Failed to load shop details');
       
      }
    );
  }

  onImageSelect(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.newLogo = { file, preview: e.target.result };
        this.shopLogo = null; // Clear the existing logo URL when a new one is selected
      };
      reader.readAsDataURL(file);
    }
  }

  removeExistingLogo(): void {
    this.shopLogo = null; // Reset the existing logo URL
  }

  removeImage(): void {
    this.newLogo = null; // Clear the new logo if needed
  }

  updateShop(): void {
    const formData = new FormData();
    formData.append('shopCode', this.shopForm.value.shopCode);
    formData.append('shopName', this.shopForm.value.shopName);
    formData.append('owner', this.shopForm.value.owner);
    formData.append('phoneNumber', this.shopForm.value.phoneNumber);
    formData.append('address', this.shopForm.value.address);

    // Append new logo if selected
    if (this.newLogo) {
      formData.append('shopLogo', this.newLogo.file);
    }

    this.shopService.updateShopDetails(formData).subscribe(
      () => {
        this.loadShopDetails();
        this.showSuccess('Shop details updated successfully')
        //this.toastService.success('Shop details updated successfully');
      },
      (error) => {
        
        this.showError('Failed to update shop details');
        console.log(error);
      }
    );
  }

  closeForm(): void {
    // Logic to close the form modal
  }
}
