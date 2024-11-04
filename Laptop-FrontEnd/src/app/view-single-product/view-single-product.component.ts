import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { ToastService } from 'angular-toastify';
import { LoaderService } from '../Services/loader.service';
import { ProductsService } from '../Services/products.service';
import { SalesService } from '../Services/sales.service';
import { CommonModule } from '@angular/common';
import { NgIconsModule } from '@ng-icons/core';

@Component({
  selector: 'app-view-single-product',
  standalone: true,
  imports:[CommonModule,NgIconsModule],
  templateUrl: './view-single-product.component.html',
  styleUrls: ['./view-single-product.component.css']
})
export class ViewSingleProductComponent implements OnInit {
  @Input() productId!: number; // Product ID input to fetch product details
  @Output() close = new EventEmitter<void>(); // Close event emitter

  product: any = {}; // Store the fetched product details

  constructor(
    private productService: ProductsService,
    private toastService: ToastService,
    private saleService: SalesService,
    private loaderService: LoaderService
  ) {}

  ngOnInit(): void {
    this.fetchSingleProduct(); // Fetch product details on component initialization
  }

  fetchSingleProduct(): void {
    this.loaderService.show();
    this.productService.getSingleProduct(this.productId).subscribe({
      next: (data) => {
        console.log(data);
        this.product = data; // Set the product details from the API response
        this.loaderService.hide();
      },
      error: (error) => {
        console.error('Error fetching product:', error);
        this.toastService.error('Failed to fetch product details.');
        this.loaderService.hide();
      }
    });
  }

  updateProduct(): void {
    // Implement the logic to update the product
    console.log('Updating product...');
  }

  deleteProduct(): void {
    // Implement the logic to delete the product
    console.log('Deleting product...');
  }

  closeViewForm(): void {
    this.close.emit(); // Emit the close event to the parent component
  }
}