import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NgIconsModule } from '@ng-icons/core';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { AddSupplierComponent } from "../add-supplier/add-supplier.component";
import { SupplierService } from '../Services/supplier.service';
import { LoaderComponent } from "../loader/loader.component";

@Component({
  selector: 'app-suppliers-list',
  standalone: true,
  imports: [AngularToastifyModule, CommonModule, NgIconsModule, AddSupplierComponent, LoaderComponent],
  templateUrl: './suppliers-list.component.html',
  styleUrl: './suppliers-list.component.css'
})
export class SuppliersListComponent {
  suppliers: any[]=[];
showAddSupplier: Boolean=false;
isLoading: boolean=false;


constructor(
  private supplierService: SupplierService, // Inject the ProductService
  private toastService: ToastService,
) {}

ngOnInit(): void {
  this.fetchSuppliers(); // Call the fetchProducts method on component initialization
}

fetchSuppliers(): void {
  this.isLoading=true;
  this.supplierService.getSuppliers().subscribe({
    next: (data) => {
      this.suppliers = data; 
      this.isLoading=false;
      console.log(data)
      // Set the products from the API response
      console.log(data)
    },
    error: (error) => {
      console.error('Error fetching SUPPLIERS:', error);
    }
  });
}



openAddSupplier() {
this.showAddSupplier=true;
}
closeAddSupplier() {
  this.showAddSupplier=false;
  }


}
