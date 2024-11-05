import { Component } from '@angular/core';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { EmployeeService } from '../Services/employee.service';
import { CommonModule } from '@angular/common';
import { AddInvestementComponent } from "../add-investement/add-investement.component";
import { NgIconsModule } from '@ng-icons/core';
import { InvestService } from '../Services/invest.service';
import { LoaderComponent } from "../loader/loader.component";

@Component({
  selector: 'app-investementlist',
  standalone: true,
  imports: [CommonModule, AngularToastifyModule, AddInvestementComponent, NgIconsModule, LoaderComponent],
  templateUrl: './investementlist.component.html',
  styleUrl: './investementlist.component.css'
})
export class InvestementlistComponent {
  showAddInvestement: Boolean = false;
  investments: any[]=[];
isLoading:  Boolean=false;
    closeAddEmployee() {
      this.showAddInvestement = false;
    }
  
    constructor(
      private invest: InvestService, // Inject the ProductService
      private toastService: ToastService,
    ) {}
    
    ngOnInit(): void {
      this.fetchInvestement(); // Call the fetchProducts method on component initialization
    }
    
    fetchInvestement(): void {
      this.isLoading=true;
      this.invest.getInvestements().subscribe({
        next: (data) => {
          this.isLoading=false;
          console.log(data)
          console.log(data)
          this.investments = data; // Set the products from the API response
        },
        error: (error) => {
          console.error('Error fetching employees:', error);
        }
      });
    }
  
    openAddInvestement() {
      this.showAddInvestement = true;
    }
  
}
