import { CommonModule } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { ReactiveFormsModule, FormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { PopupComponent } from '../popup/popup.component';
import { AuthService } from '../Services/auth.service';
import { TokenService } from '../Services/token.service';

@Component({
  selector: 'app-super-login',
  standalone: true,
  imports: [PopupComponent, CommonModule, ReactiveFormsModule, AngularToastifyModule, FormsModule],
  templateUrl: './super-login.component.html',
  styleUrl: './super-login.component.css'
})
export class SuperLoginComponent {
  loginForm!: FormGroup;
  hidePassword = true;

  @ViewChild('popup') popup!: PopupComponent;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private tokenService: TokenService,
    private toastService: ToastService,
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      
      userName: [null, Validators.required],
      password: [null, Validators.required]
    });
  }

  togglePassword(): void {
    this.hidePassword = !this.hidePassword;
  }

  showSuccess(message: string): void {
    this.popup.showPopup(message, 'success');
  }

  showError(message: string): void {
    this.popup.showPopup(message, 'error');
  }

  submitLogin(): void {
    // Check if the form is invalid
    if (this.loginForm.invalid) {
      this.toastService.warn('Fill all fields to log in');
      return; // Exit if the form is invalid
    }
  
    // Call the login method from AuthService
    this.authService.superLogin(this.loginForm.value).subscribe(
      (response: any) => {
        if (response) {

         
          // Store token and user details using TokenService
          this.tokenService.saveToken=response.token; // Save token
          this.tokenService.saveUser=response.user;   // Save user details
          this.tokenService.saveShop=response.shop;    // Save shop details
  
          // Determine success message based on response
          const successMessage = response.responseDto?.statusMessage || 'Login successful';
          this.showSuccess(successMessage);
          this.toastService.success(successMessage); // Show success message only once
  
          // Navigate based on user role after 2 seconds
          setTimeout(() => {
            const userRole = response.user.role;
            switch (userRole) {
              case 'ADMIN':
                this.router.navigateByUrl('/dash');
                break;
              case 'CASHIER':
                this.router.navigateByUrl('/cash');
                break;
              case 'SUPER_USER':
                this.router.navigateByUrl('/admin');
                break;
              default:
                this.router.navigateByUrl('/');
            }
          }, 2000);
        }
      },
      (error: any) => {
        console.error('Login Error:', error);
        const errorMessage = error.error?.errorMessage || 'Invalid login details';
        this.showError(errorMessage);
        this.toastService.error(errorMessage); // Show error message only once
      }
    );
  }

}