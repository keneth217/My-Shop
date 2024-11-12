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
import { CartResponse } from '../model/cartResponse';
import { CheckoutComponent } from "../checkout/checkout.component";
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs';


@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [AngularToastifyModule,ReactiveFormsModule, CommonModule, NgIconsModule, AddproductComponent, AddProductStockComponent, ViewSingleProductComponent, LoaderComponent, AddcartComponent, CheckoutComponent],
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'] // Note the corrected property here
})
export class ProductListComponent implements OnInit {



  cartItems: {
    id: number;
    itemCosts: number;
    productName: string | null;
    quantity: number;
    shopCode: string;
    status: string;
  }[] = [];  // Initialize as an empty array

  totalInCart: number = 0; // Initialize to 0

  cartId:number=0;
  products: any[] = []; // Initialize as an empty array
  showAddProduct: boolean = false;
  showAStockProduct: boolean = false;
  showViewProduct: boolean = false;
  selectedProductId: number = 0;
  selectedProductName: string | null = null;
  showCartForm: boolean = false;
  showCheckOutForm: boolean = false;
  selectedTotalCart: number = 0;
  selectedCartId: number=0; 
isLoading: boolean=false;
isRefreshing: boolean=false;
isSearching: boolean=false;

searchTerm = new FormControl();
results: any[] = [];


  // Store the selected product ID

  constructor(
    private productService: ProductsService, // Inject the ProductService
    private toastService: ToastService,
    private saleService: SalesService,
    private loaderService: LoaderService,
  ) { 
   
  }
  ngOnInit(): void {
    this.fetchCartItems();
    this.fetchProducts();
    
    // Listen for search term changes with debounce
    this.searchTerm.valueChanges
      .pipe(
        debounceTime(300), // Wait 300ms after typing stops
        switchMap((term: string) => {
          if (term) {
            this.isLoading = true;
            return this.productService.searchItem(term); // Fetch filtered products if search term is provided
          } else {
            this.isLoading = true;
            return this.productService.getProducts(); // Fetch all products if no search term
          }
        })
      )
      .subscribe((data) => {
        if (this.searchTerm.value) {
          this.results = data; // Display search results
          this.products = [];  // Clear full product list when displaying search results
        } else {
          this.products = data; // Display full product list when no search term
          this.results = [];    // Clear search results
        }
        this.isLoading = false;
      });
  }
  
  
   
  
  refresh() {
    this.isRefreshing = true; 
    this.fetchCartItems();
    this.fetchProducts();
    this.isRefreshing = false; 
    }


  fetchProducts(): void {
    this.isLoading=true;
    this.loaderService.show();
    this.productService.getProducts().subscribe({
      next: (data) => {
        console.log(data)
        this.products = data;
        this.isLoading=false;
        this.loaderService.hide();// Set the products from the API response
      },
      error: (error) => {
        console.error('Error fetching products:', error);
        this.loaderService.hide();
        this.isLoading=false;
      }
    });
  }

  fetchCartItems(): void {
    this.loaderService.show();
    this.saleService.fetchCartItems().subscribe({
      next: (data: CartResponse) => {  // Use the CartResponse type here
        console.log(data);
        this.cartItems = data.items || [];  // Properly access 'items'
        this.totalInCart = data.total || 0; // Properly access 'total'
        this.loaderService.hide();
      },
      error: (error) => {
        console.error('Error fetching products:', error);
        this.loaderService.hide();
      }
    });
  }


  deleteItem(cartItemId: number) {
    console.log(`Deleting cart item with ID: ${cartItemId}`);  // Log the ID being deleted
    this.saleService.deleteCartItem(cartItemId).subscribe({
        next: () => {  // No data is returned
            console.log('Cart item deleted successfully.');  // Log success message
            this.fetchCartItems(); 
            this.fetchProducts() ;// Fetch updated cart items after deletion
            this.toastService.success("Item deleted successfully");  // Show success toast
        },
        error: (error) => {
            console.error('Error deleting cart item:', error);
            this.toastService.error(error.errorMessage || 'An error occurred while deleting the item');  // Handle error
            this.loaderService.hide();
        }
    });
}

previewproductsPdf(): void {
  this.productService.generatePrintabProductList().subscribe(
    (response: Blob) => {
      const fileURL = URL.createObjectURL(response);
      window.open(fileURL); // Open PDF in a new tab for preview
    },
    (error: any) => {
      console.error('Error generating PDF for preview:', error);
    }
  );
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


  openCheckOut(totalInCart: number) {
    this.selectedTotalCart = totalInCart;
  
    console.log(totalInCart)
    this.showCheckOutForm = true;
  }

  closeCheckOut() {
    this.showCheckOutForm = false;
  }

  closeCart() {
    this.showCartForm = false;
  }


}