import { Component, EventEmitter, Output } from '@angular/core';
import { AngularToastifyModule } from 'angular-toastify';

@Component({
  selector: 'app-add-expense',
  standalone: true,
  imports: [AngularToastifyModule],
  templateUrl: './add-expense.component.html',
  styleUrl: './add-expense.component.css'
})
export class AddExpenseComponent {

@Output() close = new EventEmitter<void>();

closeExForm() {
    this.close.emit();
  }
}
