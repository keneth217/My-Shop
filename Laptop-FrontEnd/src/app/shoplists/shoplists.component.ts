import { Component, Input } from '@angular/core';
import { SuperAddShopComponent } from "../super-add-shop/super-add-shop.component";
import { CommonModule } from '@angular/common';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { NgIconsModule } from '@ng-icons/core';
import { ExpenseService } from '../Services/expense.service';
import { SuperService } from '../Services/super.service';
import { ActivateShopComponent } from "../activate-shop/activate-shop.component";
import { Router } from '@angular/router';
import { LoaderComponent } from "../loader/loader.component";

@Component({
  selector: 'app-shoplists',
  standalone: true,
  imports: [SuperAddShopComponent, CommonModule, AngularToastifyModule, NgIconsModule, ActivateShopComponent, LoaderComponent],
  templateUrl: './shoplists.component.html',
  styleUrl: './shoplists.component.css'
})
export class ShoplistsComponent {


  @Input() shopId!: number;
  shops: any[] = [];
  showAddShop: boolean = false;
  showActivateForm: boolean = false;
  selectedShopId: number = 0;
isLoading:  boolean = false;




  constructor(
    private superService: SuperService, // Inject the ProductService
    private toastService: ToastService,
    private router:Router
  ) { }
  ngOnInit(): void {
    this.fetchShops(); // Call the fetchProducts method on component initialization
  }

  fetchShops(): void {
    this.isLoading=true;
    this.superService.fetchShops().subscribe({
      next: (data) => {
        console.log(data)
        this.shops = data; // Set the products from the API response
        this.isLoading=false;
      },
      error: (error) => {
        console.error('Error fetching employees:', error);
      }
    });
  }

  deActivate(shopId: number): void {
    this.superService.deactivateShop(shopId).subscribe({
      next: (data) => {
        console.log(data);
        this.fetchShops()
        this.toastService.success("Shop deactivated");
        this.router.navigateByUrl('/admin/shops')
      },
      error: (error) => {
        console.error('Error deactivating:', error);
      }
    });
  }
  activate(shopId: number) {
    this.selectedShopId = shopId;
    console.log("selected shop id" + shopId)
    this.showActivateForm = true;
  }


  openAddShop() {
    this.showAddShop = true;
  }
  closeAddShop() {
    this.showAddShop = false;
  }

  closeActivateShop() {
    this.showActivateForm = false;
  }

}
