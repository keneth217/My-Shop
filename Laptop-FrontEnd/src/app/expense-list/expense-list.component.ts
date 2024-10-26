import { Component } from '@angular/core';
import { NgIconsModule } from '@ng-icons/core';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { AddExpenseComponent } from "../add-expense/add-expense.component";
import { CommonModule } from '@angular/common';
import { ExpenseService } from '../Services/expense.service';

@Component({
  selector: 'app-expense-list',
  standalone: true,
  imports: [AngularToastifyModule, NgIconsModule, AddExpenseComponent,CommonModule],
  templateUrl: './expense-list.component.html',
  styleUrl: './expense-list.component.css'
})
export class ExpenseListComponent {
  expenses:any[]=[];
showAddExpense: Boolean=false;


constructor(
  private expenseService: ExpenseService, // Inject the ProductService
  private toastService: ToastService,
) {}

ngOnInit(): void {
  this.fetchExpenses(); // Call the fetchProducts method on component initialization
}

fetchExpenses(): void {
  this.expenseService.getPExpenses().subscribe({
    next: (data) => {
      this.expenses = data; // Set the products from the API response
    },
    error: (error) => {
      console.error('Error fetching expenses:', error);
    }
  });
}

closeAddExpense() {
this.showAddExpense=false
}
openAddExpense() {
this.showAddExpense=true
}

}
