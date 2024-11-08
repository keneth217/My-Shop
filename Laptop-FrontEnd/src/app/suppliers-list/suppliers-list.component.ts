import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NgIconsModule } from '@ng-icons/core';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { AddSupplierComponent } from "../add-supplier/add-supplier.component";
import { SupplierService } from '../Services/supplier.service';
import { LoaderComponent } from "../loader/loader.component";
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-suppliers-list',
  standalone: true,
  imports: [AngularToastifyModule, ReactiveFormsModule, FormsModule, CommonModule, NgIconsModule, AddSupplierComponent, LoaderComponent],
  templateUrl: './suppliers-list.component.html',
  styleUrl: './suppliers-list.component.css'
})
export class SuppliersListComponent {


  suppliers: any[] = [];
  showAddSupplier: Boolean = false;
  isLoading: boolean = false;
  supplierForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private supplierService: SupplierService,
    private toastService: ToastService,
    private router: Router
  ) {
    // Initialize the form with validators
    this.supplierForm = this.fb.group({
      supplierName: ['', Validators.required],
      supplierPhone: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
      supplierAddress: ['', Validators.required],
      supplierLocation: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.fetchSuppliers(); // Call the fetchProducts method on component initialization
  }

  fetchSuppliers(): void {
    this.isLoading = true;
    this.supplierService.getSuppliers().subscribe({
      next: (data) => {
        this.suppliers = data;
        this.isLoading = false;
        console.log(data)
        // Set the products from the API response
        console.log(data)
      },
      error: (error) => {
        console.error('Error fetching SUPPLIERS:', error);
      }
    });
  }

  submitSupplierData(): void {
    if (this.supplierForm.valid) {
      const supplierData = { ...this.supplierForm.value };

      this.supplierService.addSupplier(supplierData).subscribe(
        (response) => {
          this.toastService.success('Supplier registered successfully!');
          this.fetchSuppliers();
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
  showAddSupplierForm: boolean = false;

  // Toggles the visibility of the Add Supplier form
  toggleAddSupplierForm() {
    this.showAddSupplierForm = !this.showAddSupplierForm;
  }

  // Function to close the Add Supplier form
  closeAddSupplier() {
    this.showAddSupplierForm = false;
  }

  // Toggles the visibility of the Add Supplier form
  toggleFormVisibility() {
    this.showAddSupplierForm = !this.showAddSupplierForm;
  }

  closeForm() {
    this.showAddSupplierForm = false;
  }



}
