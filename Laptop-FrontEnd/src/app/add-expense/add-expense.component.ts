import { Component, EventEmitter, Output } from '@angular/core';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { ExpenseService } from '../Services/expense.service';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-expense',
  standalone: true,
  imports: [AngularToastifyModule,ReactiveFormsModule,FormsModule],
  templateUrl: './add-expense.component.html',
  styleUrl: './add-expense.component.css'
})
export class AddExpenseComponent {

@Output() close = new EventEmitter<void>();


expenseForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private expenseService: ExpenseService,
    private toastService: ToastService
  ) {
    this.expenseForm = this.fb.group({
      amount: ['', Validators.required],
      date: ['', Validators.required],
      description: ['', Validators.required],
    });
  }

  closeEForm() {
    this.close.emit();
  }

  submitExpenseData() {
    if (this.expenseForm.valid) {
      const supplierData = this.expenseForm.value;


      this.expenseService.addExpense(supplierData).subscribe(
        (response) => {
          this.toastService.success('Expense registered successfully!');
          this.expenseForm.reset();
          //this.closeEForm();
        },
        (error) => {
          this.toastService.error('Failed to register expense!');
          console.error(error);
        }
      );
    } else {
      this.toastService.warn('Please fill in all required fields!');
    }
  }

closeExForm() {
    this.close.emit();
  }
}
