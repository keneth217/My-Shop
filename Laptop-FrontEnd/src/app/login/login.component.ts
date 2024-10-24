import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PopupComponent } from '../popup/popup.component';
import { CommonModule } from '@angular/common';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { AuthService } from '../Services/auth.service';
import { TokenService } from '../Services/token.service';

@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [PopupComponent, CommonModule, ReactiveFormsModule, AngularToastifyModule, FormsModule]
})
export class LoginComponent implements OnInit {
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
      shopCode: [null, Validators.required],
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
    if (this.loginForm.invalid) return;

    this.authService.login(this.loginForm.value).subscribe(
      (response: any) => {
        if (response) {
          // Store token and user details using setter
        this.tokenService.saveToken = response.token;
        this.tokenService.saveUser = response.user;

          this.showSuccess(response.responseDto?.statusMessage || 'Login successful');
          this.toastService.success('Login success');

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
                this.router.navigateByUrl('/super');
                break;
              default:
                this.router.navigateByUrl('/');
            }
          }, 2000);
        }
      },
      (error: any) => {
        console.error('Login Error:', error);
        this.showError(error.error?.errorMessage || 'Invalid login details');
        this.toastService.error('Login failed');
      }
    );
  }

  showSu(): void {
    this.toastService.success('Operation successful!');
  }

  showEr(): void {
    this.toastService.error('Operation failed!');
  }
}
