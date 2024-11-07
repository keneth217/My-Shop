import { CommonModule } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { PopupComponent } from "../popup/popup.component";
import { UserService } from '../Services/user.service';
import { Roles } from '../model/roles.model';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, AngularToastifyModule, PopupComponent],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']  // Fixed the style URL typo from styleUrl to styleUrls
})
export class UserProfileComponent {

  @ViewChild('popup') popup!: PopupComponent;
  userForm!: FormGroup;
  userImage: string | null = null; // For existing user image URL
  newImage: { file: File, preview: string } | null = null; // For the new user image
 // Initialize roles (if needed for select dropdown)
 roles: Roles[] = [Roles.ADMIN, Roles.CASHIER];  // Example roles enum
  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private toastService: ToastService,
  ) {
    this.userForm = this.fb.group({
      shopCode: [{ value: '', disabled: true }, Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      userName: ['', Validators.required],
      phone: ['', [Validators.required, Validators.pattern('[0-9]{10}')]],  // Assuming 10 digit phone number
      currentPassword: ['', Validators.required],
      newPassword: ['', Validators.required],
      role: ['', Validators.required],
      userImage: [null]  // Handling user image file upload
    });
  }

  showSuccess(message: string): void {
    this.popup.showPopup(message, 'success');
  }

  showError(message: string): void {
    this.popup.showPopup(message, 'error');
  }

  ngOnInit(): void {
    this.loadUserDetails();
    this.userForm.get('shopCode')?.disable(); // Disable the shopCode control
    this.userForm.get('role')?.disable(); // Disable the shopCode control
  }

  loadUserDetails(): void {
    this.userService.getUserDetails().subscribe(
      (data) => {
        console.log(data);
        this.userForm.patchValue({
          shopCode: data.shopCode,
          firstName: data.firstName,
          lastName: data.lastName,
          userName: data.userName,
          phone: data.phone,
          currentPassword: data.currentPassword,
          newPassword: data.newPassword,
          role: data.role
        });

        // Set the user image URL if it exists
        if (data.userImageUrl) {
          this.userImage = data.userImageUrl;
        }
      },
      (error) => {
        this.showError('Failed to load user details');
      }
    );
  }

  onImageSelect(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.newImage = { file, preview: e.target.result };
        this.userImage = null; // Clear the existing image URL when a new one is selected
      };
      reader.readAsDataURL(file);
    }
  }

  removeExistingImage(): void {
    this.userImage = null; // Reset the existing image URL
  }

  removeImage(): void {
    this.newImage = null; // Clear the new image if needed
  }

  updateUser(): void {
    const formData = new FormData();
    formData.append('shopCode', this.userForm.value.shopCode);
    formData.append('firstName', this.userForm.value.firstName);
    formData.append('lastName', this.userForm.value.lastName);
    formData.append('userName', this.userForm.value.userName);
    formData.append('phone', this.userForm.value.phone);
    // formData.append('role', this.userForm.value.role); // Commented out, if role should not be updated
    formData.append('currentPassword', this.userForm.value.currentPassword);
    formData.append('newPassword', this.userForm.value.newPassword);
  
    // Append user image if selected
    if (this.newImage) {
      formData.append('userImage', this.newImage.file);
    }
  
    this.userService.updateUserDetails(formData).subscribe(
      () => {
        this.loadUserDetails();
        this.showSuccess('User details updated successfully');
      },
      (error) => {
        // Check for a specific error message or status
        if (error.status === 400) {
          // Handle validation errors or bad requests
          const message = error.error?.errorMessage || 'Validation failed, please check your input.';
          this.showError(message);
        } else if (error.status === 401) {
          // Handle unauthorized errors (e.g., user not logged in)
          this.showError('You are not authorized to perform this action.');
        } else if (error.status === 500) {
          // Handle server-side errors

          const message = error.error?.errorMessage || 'An error occurred on the server. Please try again later.';
          this.showError(message);
        } else {
          // Default fallback error message
          const message = error.error?.errorMessage || 'Something went wrong. Please try again.';
          this.showError(message);
        }
        console.error(error);
      }
    );
  }
  

  closeForm(): void {
    // Logic to close the form modal (if applicable)
  }
}
