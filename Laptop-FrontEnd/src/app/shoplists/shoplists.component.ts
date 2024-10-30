import { Component } from '@angular/core';
import { SuperAddShopComponent } from "../super-add-shop/super-add-shop.component";
import { CommonModule } from '@angular/common';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { NgIconsModule } from '@ng-icons/core';
import { ExpenseService } from '../Services/expense.service';
import { SuperService } from '../Services/super.service';

@Component({
  selector: 'app-shoplists',
  standalone: true,
  imports: [SuperAddShopComponent,CommonModule,AngularToastifyModule,NgIconsModule],
  templateUrl: './shoplists.component.html',
  styleUrl: './shoplists.component.css'
})
export class ShoplistsComponent {

shops: any[]=[];
showAddShop: boolean=false;




constructor(
  private superService: SuperService, // Inject the ProductService
  private toastService: ToastService,
) {}
ngOnInit(): void {
  this.fetchEmployees(); // Call the fetchProducts method on component initialization
}

fetchEmployees(): void {
  this.superService.fetchShops().subscribe({
    next: (data) => {
      console.log(data)
      this.shops = data; // Set the products from the API response
    },
    error: (error) => {
      console.error('Error fetching employees:', error);
    }
  });
}


openAddShop() {
  this.showAddShop=true;
  }
closeAddShop() {
this.showAddShop=false;
}

}
