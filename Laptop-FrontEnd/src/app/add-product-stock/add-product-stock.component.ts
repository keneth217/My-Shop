import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { SupplierService } from '../Services/supplier.service';
import { SalesService } from '../Services/sales.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

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
    private toastService: ToastService,
    private router:Router
  ) {
    this.stockForm = this.fb.group({
      quantity: ['', [Validators.required, Validators.min(1)]],
      buyingPrice: ['', [Validators.required, Validators.min(0)]],
      sellingPrice: ['', [Validators.required, Validators.min(0)]],
      supplierName: ['', Validators.required],
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
      next: (data) => {
        this.suppliers = data;
        console.log(this.suppliers); // Log to check supplier IDs
      },
      error: (error) => console.error('Error fetching suppliers:', error),
    });
  }

  submitStockData(): void {
    if (this.stockForm.invalid) {
      this.toastService.error('Please fill in all required fields correctly.');
      return;
    }
  
    const { quantity, buyingPrice, sellingPrice, supplierName } = this.stockForm.value;
    console.log(this.stockForm.value); // Log the form values
    this.salesService
      .createSale(this.productId, supplierName, { quantity, buyingPrice, sellingPrice })
      .subscribe({
        next: (response) => {
          console.log(response);
          console.log(this.stockForm.value); // Log the form values
          this.toastService.success(response.responseMessage);
          this.router.navigateByUrl('/dash/product');
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