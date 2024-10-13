import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NgIconsModule } from '@ng-icons/core';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { AddproductComponent } from "../addproduct/addproduct.component";
import { AddProductStockComponent } from "../add-product-stock/add-product-stock.component";
import { ViewSingleProductComponent } from "../view-single-product/view-single-product.component";

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [AngularToastifyModule, CommonModule, AngularToastifyModule, NgIconsModule, AddproductComponent, AddProductStockComponent, ViewSingleProductComponent],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css'
})
export class ProductListComponent {



  products: any;
  showAddProduct: Boolean = false;
  showAStockProduct: Boolean = false;
  showViewProduct:Boolean=false;


  constructor(
 
    private toastService: ToastService,
  ) { }



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
    this.showAddProduct = false
  }
  closeViewProduct() {
    this.showViewProduct=false;
  }
  openViewProduct() {
  this.showViewProduct=true;
  }

  addToCart() {
    this.toastService.success("product added to cart succesfully")
    }
}
