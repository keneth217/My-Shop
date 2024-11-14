import { Component } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { EmailService } from '../Services/email.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-send-email',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule,AngularToastifyModule],
  templateUrl: './send-email.component.html',
  styleUrl: './send-email.component.css'
})
export class SendEmailComponent {
  contactForm!: FormGroup;


  constructor(private fb: FormBuilder,private emailService:EmailService, private toastService: ToastService) {}
  
  ngOnInit(): void {
    this.contactForm = this.fb.group({
      recipient: ['', [Validators.required, Validators.email]],
      subject: ['', Validators.required],
      messageBody: ['', Validators.required]
    });
  }
  
  submitEmail() {
    if (this.contactForm.valid) {
      const emailData = this.contactForm.value;
      
      // Call the service method and handle the observable
      this.emailService.receiveEmailfromCustomer(emailData).subscribe({
        next: response => {
          this.toastService.success('Email sent successfully:');
          console.log('Email sent successfully:', response);
          // You could add toast notifications here or redirect if needed
        },
        error: err => {
          this.toastService.error(err);
          console.error('Error sending email:', err);
          // Handle error cases here, like showing a notification
        }
      });
      
      console.log('Form submitted', this.contactForm.value);
    } else {
      console.log('Form is invalid, please correct the errors');
      // Optionally, mark all fields as touched to show validation messages
      this.contactForm.markAllAsTouched();
    }
  }
  
  }
  
