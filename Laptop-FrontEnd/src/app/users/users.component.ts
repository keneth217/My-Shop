import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NgIconsModule } from '@ng-icons/core';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { AddExpenseComponent } from '../add-expense/add-expense.component';
import { LoaderComponent } from '../loader/loader.component';
import { ExpenseService } from '../Services/expense.service';
import { UserService } from '../Services/user.service';
import { Roles } from '../model/roles.model';
import { AdduserComponent } from "../adduser/adduser.component";

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [AngularToastifyModule, NgIconsModule, AddExpenseComponent, CommonModule, LoaderComponent, AdduserComponent],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent {
  users:any[]=[];
  showAddExpense: Boolean=false;
  isLoading: boolean=false;
   // Initialize roles (if needed for select dropdown)
//roles: Roles[] = [Roles.ADMIN, Roles.CASHIER];  // Example roles enum
roles: string[] = ['ADMIN', 'CASHIER']; 
  constructor(
    private userService: UserService, // Inject the ProductService
    private toastService: ToastService,
  ) {}
  
  ngOnInit(): void {
    this.fetchExpenses(); // Call the fetchProducts method on component initialization
  }
  
  fetchExpenses(): void {
    this.isLoading=true;
    this.userService.getUsersForShop().subscribe({
      next: (data) => {
        this.users = data; // Set the products from the API response
        this.isLoading=false;
      },
      error: (error) => {
        console.error('Error fetching expenses:', error);
      }
    });
  }
  changeUserRole(username: string, newRole: string) {
    const userRoleUpdateDto = { role: newRole };
  
    this.userService.changeRole(username, userRoleUpdateDto).subscribe({
      next: (response) => {
        console.log(response);
        this.toastService.success('Role updated successfully');
      },
      error: (error) => {
        console.log(error);
        this.toastService.error('Failed to update role');
      }
    });
  }
  
  onRoleChange(userName: string | undefined, event: Event): void {
    const selectElement = event.target as HTMLSelectElement;
    const selectedRole = selectElement.value;
  
    if (userName) {
      this.changeUserRole(userName, selectedRole);
    }
  }
  
  
  closeAddExpense() {
  this.showAddExpense=false
  }
  openAddExpense() {
  this.showAddExpense=true
  }
  
}
