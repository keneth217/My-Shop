import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NgIconsModule } from '@ng-icons/core';
import { AngularToastifyModule } from 'angular-toastify';
import { AddproductComponent } from "../addproduct/addproduct.component";
import { AddProductStockComponent } from "../add-product-stock/add-product-stock.component";

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [AngularToastifyModule, CommonModule, AngularToastifyModule, NgIconsModule, AddproductComponent, AddProductStockComponent],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css'
})
export class ProductListComponent {

  products: any;
  showAddProduct: Boolean = false;
  showAStockProduct: Boolean = false;




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


}
