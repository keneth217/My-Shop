import { Component } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { CartResponse } from '../model/cartResponse';
import { LoaderService } from '../Services/loader.service';
import { ProductsService } from '../Services/products.service';
import { SalesService } from '../Services/sales.service';
import { CommonModule } from '@angular/common';
import { NgIconsModule } from '@ng-icons/core';
import { AddProductStockComponent } from '../add-product-stock/add-product-stock.component';
import { AddcartComponent } from '../addcart/addcart.component';
import { AddproductComponent } from '../addproduct/addproduct.component';
import { CheckoutComponent } from '../checkout/checkout.component';
import { LoaderComponent } from '../loader/loader.component';
import { ViewSingleProductComponent } from '../view-single-product/view-single-product.component';

@Component({
  selector: 'app-cartitems',
  standalone: true,
  imports: [AngularToastifyModule,ReactiveFormsModule, CommonModule, NgIconsModule, AddproductComponent, AddProductStockComponent, ViewSingleProductComponent, LoaderComponent, AddcartComponent, CheckoutComponent],
  templateUrl: './cartitems.component.html',
  styleUrl: './cartitems.component.css'
})
export class CartitemsComponent {

  cartItems: {
    id: number;
    itemCosts: number;
    productName: string | null;
    quantity: number;
    shopCode: string;
    status: string;
  }[] = [];  // Initialize as an empty array

  totalInCart: number = 0; // Initialize to 0

  cartId: number = 0;
  products: any[] = []; // Initialize as an empty array
  showAddProduct: boolean = false;
  showAStockProduct: boolean = false;
  showViewProduct: boolean = false;
  selectedProductId: number = 0;
  selectedProductName: string | null = null;
  showCartForm: boolean = false;
  showCheckOutForm: boolean = false;
  selectedTotalCart: number = 0;
  selectedCartId: number = 0;
 
 



  // Store the selected product ID

  constructor(
    private productService: ProductsService, // Inject the ProductService
    private toastService: ToastService,
    private saleService: SalesService,
    private loaderService: LoaderService,
  ) { }


  ngOnInit(): void {
    this.fetchCartItems();
    
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
      
        this.toastService.success("Item deleted successfully");  // Show success toast
      },
      error: (error) => {
        console.error('Error deleting cart item:', error);
        this.toastService.error(error.errorMessage || 'An error occurred while deleting the item');  // Handle error
        this.loaderService.hide();
      }
    });
  }

  openCheckOut(totalInCart: number) {
    this.selectedTotalCart = totalInCart;
  
    console.log(totalInCart)
    this.showCheckOutForm = true;
  }

  closeCheckOut() {
    this.showCheckOutForm = false;
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
