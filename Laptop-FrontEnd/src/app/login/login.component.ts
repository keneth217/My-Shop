import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
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
  imports: [PopupComponent, CommonModule, ReactiveFormsModule,AngularToastifyModule, FormsModule]
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
  ) { }

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      shopCode: [null, Validators.required],
      username: [null, Validators.required],
      password: [null, Validators.required]
    });
  }

  togglePassword() {
    this.hidePassword = !this.hidePassword;
  }

  showSuccess(message: string) {
    this.popup.showPopup(message, 'success');
  }

  showError(message: string) {
    this.popup.showPopup(message, 'error');
  }

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.authService.login(this.loginForm.value).subscribe(
      (response: any) => {
        if (response) {
          // Store token and user details via TokenService
          this.tokenService.saveToken = response.token;
          this.tokenService.saveUser = response.user;

          this.showSuccess(response.responseDto?.statusMessage || 'Login successful');
          this.toastService.success('Login success')


          // Navigate based on user role
          setTimeout(() => {
            const userRole = response.user.role;
            if (userRole === 'ADMIN') {
              this.router.navigateByUrl('/admin');
            } else if (userRole === 'CASHIER') {
              this.router.navigateByUrl('/cash');
            }
            else if (userRole === 'SUPER_USER') {
              this.router.navigateByUrl('/super');
            }
          }, 2000);
        }
      },
      (error: any) => {
        console.error(error);
        this.showError(error.errorMessage || 'Invalid login details');
      }
    );
  }

  showSu() {
    this.toastService.success('Operation successful!');
  }

  showEr() {
    this.toastService.error('Operation failed!');
  }
}
