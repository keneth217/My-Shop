import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NgIconsModule } from '@ng-icons/core';
import { AngularToastifyModule } from 'angular-toastify';
import { AddSupplierComponent } from "../add-supplier/add-supplier.component";

@Component({
  selector: 'app-suppliers-list',
  standalone: true,
  imports: [AngularToastifyModule, CommonModule, NgIconsModule, AddSupplierComponent],
  templateUrl: './suppliers-list.component.html',
  styleUrl: './suppliers-list.component.css'
})
export class SuppliersListComponent {
  suppliers: any;
showAddSupplier: Boolean=false;





openAddSupplier() {
this.showAddSupplier=true;
}
closeAddSupplier() {
  this.showAddSupplier=false;
  }


}
