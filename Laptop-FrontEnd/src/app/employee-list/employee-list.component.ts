import { Component } from '@angular/core';
import { AddEmployeeComponent } from "../add-employee/add-employee.component";
import { CommonModule } from '@angular/common';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { NgIconsModule } from '@ng-icons/core';
import { EmployeeService } from '../Services/employee.service';
import { LoaderComponent } from "../loader/loader.component";

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [AddEmployeeComponent, CommonModule, AngularToastifyModule, NgIconsModule, LoaderComponent],
  templateUrl: './employee-list.component.html',
  styleUrl: './employee-list.component.css'
})
export class EmployeeListComponent {
 
  showAddEmployee: Boolean = false;
employees: any[]=[];
isLoading: boolean=false;
  closeAddEmployee() {
    this.showAddEmployee = false;
  }

  constructor(
    private employeeService: EmployeeService, // Inject the ProductService
    private toastService: ToastService,
  ) {}
  
  ngOnInit(): void {
    this.fetchEmployees(); // Call the fetchProducts method on component initialization
  }
  
  fetchEmployees(): void {
    this.isLoading=true;
    this.employeeService.getEmployees().subscribe({
      next: (data) => {
        console.log(data)
        console.log(data)
        this.isLoading=false;
        this.employees = data; // Set the products from the API response
      },
      error: (error) => {
        console.error('Error fetching employees:', error);
      }
    });
  }

  openAddEmployee() {
    this.showAddEmployee = true;
  }

}
