// src/app/product-list/product-list.component.ts
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { NgIconsModule } from '@ng-icons/core';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { AddproductComponent } from "../addproduct/addproduct.component";
import { AddProductStockComponent } from "../add-product-stock/add-product-stock.component";
import { ViewSingleProductComponent } from "../view-single-product/view-single-product.component";
import { ProductsService } from '../Services/products.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [AngularToastifyModule, CommonModule, NgIconsModule, AddproductComponent, AddProductStockComponent, ViewSingleProductComponent],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'] // Note the corrected property here
})
export class ProductListComponent implements OnInit {
  products: any[] = []; // Initialize as an empty array
  showAddProduct: boolean = false;
  showAStockProduct: boolean = false;
  showViewProduct: boolean = false;

  constructor(
    private productService: ProductsService, // Inject the ProductService
    private toastService: ToastService,
  ) {}

  ngOnInit(): void {
    this.fetchProducts(); // Call the fetchProducts method on component initialization
  }

  fetchProducts(): void {
    this.productService.getProducts().subscribe({
      next: (data) => {
        this.products = data; // Set the products from the API response
      },
      error: (error) => {
        console.error('Error fetching products:', error);
      }
    });
  }

  openStockProduct() {
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

  addToCart() {
    this.toastService.success("Product added to cart successfully");
  }
}