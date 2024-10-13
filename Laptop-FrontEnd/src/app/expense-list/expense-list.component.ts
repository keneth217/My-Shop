import { Component } from '@angular/core';
import { NgIconsModule } from '@ng-icons/core';
import { AngularToastifyModule } from 'angular-toastify';
import { AddExpenseComponent } from "../add-expense/add-expense.component";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-expense-list',
  standalone: true,
  imports: [AngularToastifyModule, NgIconsModule, AddExpenseComponent,CommonModule],
  templateUrl: './expense-list.component.html',
  styleUrl: './expense-list.component.css'
})
export class ExpenseListComponent {
showAddExpense: Boolean=false;



closeAddExpense() {
this.showAddExpense=false
}
openAddExpense() {
this.showAddExpense=true
}

}
