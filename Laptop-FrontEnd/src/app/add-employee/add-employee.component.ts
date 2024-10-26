import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { EmployeeService } from '../Services/employee.service';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-add-employee',
  standalone: true,
  imports: [AngularToastifyModule,CommonModule,ReactiveFormsModule],
  templateUrl: './add-employee.component.html',
  styleUrls: ['./add-employee.component.css']
})
export class AddEmployeeComponent {
  @Output() close = new EventEmitter<void>();
  employeeForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    private toastService: ToastService
  ) {
    this.employeeForm = this.fb.group({
      name: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
      address: ['', Validators.required],
      location: ['', Validators.required],
    });
  }

  closeEForm() {
    this.close.emit();
  }

  submitEmployeeData() {
    if (this.employeeForm.valid) {
      const employeeData = this.employeeForm.value;

      this.employeeService.addEmployee(employeeData).subscribe(
        (response) => {
          this.toastService.success('Employee registered successfully!');
          this.employeeForm.reset();
          //this.closeEForm();
        },
        (error) => {
          this.toastService.error('Failed to register employee!');
          console.error(error);
        }
      );
    } else {
      this.toastService.warn('Please fill in all required fields!');
    }
  }
}