import { Component, EventEmitter, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { EmployeeService } from '../Services/employee.service';
import { UserService } from '../Services/user.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-adduser',
  standalone: true,
  imports: [AngularToastifyModule,CommonModule,ReactiveFormsModule],
  templateUrl: './adduser.component.html',
  styleUrl: './adduser.component.css'
})
export class AdduserComponent {
  @Output() close = new EventEmitter<void>();
  userForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private toastService: ToastService
  ) {
    this.userForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      userName: ['', Validators.required],
      phone: ['',[ Validators.required, Validators.pattern('^[0-9]+$')]],
    });
  }

  closeEForm() {
    this.close.emit();
  }

  submitUserData() {
    if (this.userForm.valid) {
      const userData = this.userForm.value;

      this.userService.adduser(userData).subscribe(
        (response) => {
          this.toastService.success('user registered successfully!');
          this.userForm.reset();
          //this.closeEForm();
        },
        (error) => {
          this.toastService.error('Failed to register user!');
          console.error(error);
        }
      );
    } else {
      this.toastService.warn('Please fill in all required fields!');
    }
  }
}
