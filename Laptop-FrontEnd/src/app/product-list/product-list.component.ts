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
import { AddcartComponent } from "../addcart/addcart.component";

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [AngularToastifyModule, CommonModule, NgIconsModule, AddproductComponent, AddProductStockComponent, ViewSingleProductComponent, LoaderComponent, AddcartComponent],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'] // Note the corrected property here
})
export class ProductListComponent implements OnInit {



  products: any[] = []; // Initialize as an empty array
  showAddProduct: boolean = false;
  showAStockProduct: boolean = false;
  showViewProduct: boolean = false;
  selectedProductId: number = 0;
  selectedProductName: string | null = null;
  showCartForm: boolean = false;
  cartsItems: any[] = [];
  // Store the selected product ID

  constructor(
    private productService: ProductsService, // Inject the ProductService
    private toastService: ToastService,
    private saleService: SalesService,
    private loaderService: LoaderService,
  ) { }

  ngOnInit(): void {
    this.fetchCartItems();
    this.fetchProducts(); 
    // Call the fetchProducts method on component initialization
    
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

  fetchCartItems(): void {
    this.loaderService.show();
    this.saleService.fetchCartItems().subscribe({
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

  checkOut() {
    throw new Error('Method not implemented.');
    }

  openStockProduct(productId: number, productName: string) {

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
  openViewProduct(productId: number) {

    this.selectedProductId = productId;

    // Store the product ID
    console.log('Selected Product ID:', productId);
    this.showViewProduct = true;
  }

  openAddToCart(productId: number) {
    this.selectedProductId = productId;

    // Store the product ID
    console.log('Selected Product ID:', productId);
    this.showCartForm = true;
  }

  closeCart() {
    this.showCartForm = false;
  }


}