import { Component, EventEmitter, Output } from '@angular/core';
import { AngularToastifyModule } from 'angular-toastify';

@Component({
  selector: 'app-add-employee',
  standalone: true,
  imports: [AngularToastifyModule],
  templateUrl: './add-employee.component.html',
  styleUrl: './add-employee.component.css'
})
export class AddEmployeeComponent {
  @Output() close = new EventEmitter<void>();

  closeEForm() {
    this.close.emit();
  }
}
