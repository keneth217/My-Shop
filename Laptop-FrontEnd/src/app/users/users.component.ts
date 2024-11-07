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
import { ChangeRoleComponent } from "../change-role/change-role.component";

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [AngularToastifyModule, NgIconsModule, AddExpenseComponent, CommonModule, LoaderComponent, AdduserComponent, ChangeRoleComponent],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent {


  users: any[] = [];
  showAddExpense: Boolean = false;
  isLoading: boolean = false;
  showChangeRole: Boolean = false;
  selectedUser: any=null;
  constructor(
    private userService: UserService, // Inject the ProductService
    private toastService: ToastService,
  ) { }

  ngOnInit(): void {
    this.fetchUsers(); // Call the fetchProducts method on component initialization
  }

  refreshUsers() {
    this.userService.getUsersForShop().subscribe((users) => {
      this.users = users;  // Update the list of users with fresh data
    });
  }

  fetchUsers(): void {
    this.isLoading = true;
    this.userService.getUsersForShop().subscribe({
      next: (data) => {
        console.log(data)
        this.users = data; // Set the products from the API response
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching users:', error);
      }
    });
  }
  OpenChangeRole(userName: any) {
    console.log('User passed to OpenChangeRole:', userName); // Log to check if user is passed
    this.selectedUser = userName;
    console.log('Selected User:', this.selectedUser); // Check if selectedUser is assigned correctly
    this.showChangeRole = true;
  }

  closeChangeRole() {
    this.showChangeRole = false;
  }


  closeAddExpense() {
    this.showAddExpense = false
  }
  openAddExpense() {
    this.showAddExpense = true
  }

}
