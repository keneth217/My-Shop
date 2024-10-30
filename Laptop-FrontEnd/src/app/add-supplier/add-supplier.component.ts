import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NgIconsModule } from '@ng-icons/core';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { SupplierService } from '../Services/supplier.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-add-supplier',
  standalone: true,
  imports: [AngularToastifyModule, NgIconsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './add-supplier.component.html',
  styleUrls: ['./add-supplier.component.css'] // Corrected typo
})
export class AddSupplierComponent {

  @Output() close = new EventEmitter<void>();
  supplierForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private supplierService: SupplierService,
    private toastService: ToastService
  ) {
    // Initialize the form with validators
    this.supplierForm = this.fb.group({
      supplierName: ['', Validators.required],
      supplierPhone: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
      supplierAddress: ['', Validators.required],
      supplierLocation: ['', Validators.required],
    });
  }

  /**
   * Submits supplier data if the form is valid.
   */
  submitSupplierData(): void {
    if (this.supplierForm.valid) {
      const supplierData = { ...this.supplierForm.value };
  
      this.supplierService.addSupplier(supplierData).subscribe(
        (response) => {
          this.toastService.success('Supplier registered successfully!');
          this.supplierForm.reset();
          this.supplierForm.markAsPristine();
          this.supplierForm.markAsUntouched();
          this.closeForm();
        },
        (error) => {
          this.toastService.error('Failed to register supplier!');
          console.error(error);
        }
      );
    } else {
      this.supplierForm.markAllAsTouched();
      this.toastService.warn('Please fill in all required fields!');
    }
  }

  /**
   * Closes the form by emitting the close event.
   */
  closeForm(): void {
    this.close.emit();
  }
}
