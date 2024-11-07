import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { UserService } from '../Services/user.service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-change-role',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, AngularToastifyModule],
  templateUrl: './change-role.component.html',
  styleUrls: ['./change-role.component.css']
})
export class ChangeRoleComponent implements OnInit {
  @Output() close = new EventEmitter<void>();
  @Output() roleUpdated = new EventEmitter<void>(); 
  @Input() userName: string = '';  // username passed from parent component
  roleForm!: FormGroup;
  roles: string[] = ['ADMIN', 'CASHIER'];
  isSubmitting = false; // Flag to track submission state

  constructor(
    private userService: UserService,
    private toastService: ToastService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    // Initialize the form with the current role if the username is passed
    this.roleForm = this.fb.group({
      role: ['', Validators.required]  // Initially leave it empty, will be set later
    });
  }

  // Submit role change form
  submitRoleChange() {
    if (this.isSubmitting || this.roleForm.invalid) return;  // Prevent multiple submissions and check form validity
    
    this.isSubmitting = true;
  
    const newRole = this.roleForm.get('role')?.value;
    if (this.userName && newRole) {
      // Call API to change the role with the username and new role
      this.changeRole(this.userName, newRole);
    } else {
      this.isSubmitting = false;
      this.toastService.error('Invalid data');
    }
  }

  // Call API to change role
  changeRole(username: string, newRole: string) {
    if (!username || !newRole) {
      this.isSubmitting = false;
      return;
    }

    // Make the API call to change the role
    this.userService.changeRole(username, newRole).subscribe({
      next: (response) => {
        console.log('API Response:', response);  // Log API response
        this.toastService.success('Role updated successfully');
        this.close.emit();  // Close modal on success
        this.isSubmitting = false;  // Re-enable form submission
      },
      error: (error) => {
        console.error('API Error:', error);  // Log API error
        this.toastService.error('Failed to update role');
        this.isSubmitting = false;  // Re-enable form submission on error
      }
    });
  }

  // Method to close the modal
  closeForm() {
    this.close.emit();
  }
}
