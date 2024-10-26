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
  styleUrl: './add-supplier.component.css'
})
export class AddSupplierComponent {

  @Output() close = new EventEmitter<void>();
  supplierForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private supplierService: SupplierService,
    private toastService: ToastService
  ) {
    this.supplierForm = this.fb.group({
      supplierName: ['', Validators.required],
      supplierPhone: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
      supplierAddress: ['', Validators.required],
      supplierLocation: ['', Validators.required],
    });
  }

  closeEForm() {
    this.close.emit();
  }

  submitSupplierData() {
    if (this.supplierForm.valid) {
      const supplierData = this.supplierForm.value;


      this.supplierService.addSupplier(supplierData).subscribe(
        (response) => {
          console.log(response)
          this.toastService.success('Supplier registered successfully!');
          this.supplierForm.reset();
          //this.closeEForm();
        },
        (error) => {
          this.toastService.error('Failed to register supplier!');
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
