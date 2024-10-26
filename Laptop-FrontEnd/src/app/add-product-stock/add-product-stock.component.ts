import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { SupplierService } from '../Services/supplier.service';
import { SalesService } from '../Services/sales.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-add-product-stock',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, AngularToastifyModule],
  templateUrl: './add-product-stock.component.html',
  styleUrls: ['./add-product-stock.component.css'],
})
export class AddProductStockComponent implements OnInit, OnChanges {
  @Input() productId!: number;

  @Input() productName!: string | null; // New Input for product name
  @Output() close = new EventEmitter<void>();


  stockForm: FormGroup;
  suppliers: any[] = [];

  constructor(
    private fb: FormBuilder,
    private supplierService: SupplierService,
    private salesService: SalesService,
    private toastService: ToastService
  ) {
    this.stockForm = this.fb.group({
      stock: ['', [Validators.required, Validators.min(1)]],
      itemCost: ['', [Validators.required, Validators.min(0)]],
      supplier: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.fetchSuppliers();
  }

  // Handle changes to input properties
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['productId'] && changes['productId'].currentValue) {
      console.log('Product ID changed:', this.productId);
    }
  }

  fetchSuppliers(): void {
    this.supplierService.getSuppliers().subscribe({
      next: (data) => (this.suppliers = data),
      error: (error) => console.error('Error fetching suppliers:', error),
    });
  }

  submitStockData(): void {
    if (this.stockForm.invalid) {
      this.toastService.error('Please fill in all required fields correctly.');
      return;
    }

    const { stock, itemCost, supplier } = this.stockForm.value;

    this.salesService
      .createSale(this.productId, supplier, { stock, itemCost })
      .subscribe({
        next: (response) => {
          console.log(response);
          this.toastService.success(response.responseMessage);
          this.closeSForm();
        },
        error: (error) => {
          console.error('Error adding stock:', error);
          this.toastService.error('Failed to add stock.');
        },
      });
  }

  closeSForm(): void {
    this.close.emit();
  }
}