// src/app/product-list/product-list.component.ts
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { NgIconsModule } from '@ng-icons/core';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { AddproductComponent } from "../addproduct/addproduct.component";
import { AddProductStockComponent } from "../add-product-stock/add-product-stock.component";
import { ViewSingleProductComponent } from "../view-single-product/view-single-product.component";
import { ProductsService } from '../Services/products.service';
import { LoaderService } from '../Services/loader.service';
import { LoaderComponent } from "../loader/loader.component";
import { SalesService } from '../Services/sales.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [AngularToastifyModule, CommonModule, NgIconsModule, AddproductComponent, AddProductStockComponent, ViewSingleProductComponent, LoaderComponent],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'] // Note the corrected property here
})
export class ProductListComponent implements OnInit {
  products: any[] = []; // Initialize as an empty array
  showAddProduct: boolean = false;
  showAStockProduct: boolean = false;
  showViewProduct: boolean = false;
  selectedProductId: number  = 0;
  selectedProductName: string | null = null;
   // Store the selected product ID

  constructor(
    private productService: ProductsService, // Inject the ProductService
    private toastService: ToastService,
    private saleService:SalesService,
    private loaderService: LoaderService,
  ) {}

  ngOnInit(): void {
    this.fetchProducts(); // Call the fetchProducts method on component initialization
  }

  fetchProducts(): void {
    this.loaderService.show(); 
    this.productService.getProducts().subscribe({
      next: (data) => {
        console.log(data)
        this.products = data; 
        this.loaderService.hide();// Set the products from the API response
      },
      error: (error) => {
        console.error('Error fetching products:', error);
        this.loaderService.hide();
      }
    });
  }

  openStockProduct(productId:number,productName:string) {

    this.selectedProductId = productId; 
    this.selectedProductName = productName;
    // Store the product ID
    console.log('Selected Product ID:', productId);
    this.showAStockProduct = true;
  }
  closeStockProduct() {
    this.showAStockProduct = false;
  }

  openAddProduct(): void {
    this.showAddProduct = true;
  }
  closeAddProduct(): void {
    this.showAddProduct = false;
  }
  closeViewProduct() {
    this.showViewProduct = false;
  }
  openViewProduct() {
    this.showViewProduct = true;
  }

  addToCart(productId: number, quantity: number = 1): void {
    if (!productId) {
        this.toastService.error("Product ID is required.");
        return;
    }

    this.saleService.addCart(productId, quantity).subscribe({
        next: (response) => {
            this.toastService.success("Product added to cart successfully");
            console.log(response); // For debugging
        },
        error: (error) => {
            console.error("Error adding to cart:", error);
            this.toastService.error("Failed to add product to cart");
        }
    });
}
}