import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { EmailService } from '../Services/email.service';
import { SuperService } from '../Services/super.service';

@Component({
  selector: 'app-sendbulkemail',
  standalone: true,
  imports: [ReactiveFormsModule, AngularToastifyModule, CommonModule],
  templateUrl: './sendbulkemail.component.html',
  styleUrls: ['./sendbulkemail.component.css']
})
export class SendbulkemailComponent {
  emailForm!: FormGroup;
  isSending: boolean = false;
  isLoading: boolean = false;
  customers: any[] = [];
  selectedEmails: string[] = [];

  constructor(private fb: FormBuilder, private emailService: EmailService, private superService: SuperService, private toastService: ToastService) {
    this.emailForm = this.fb.group({
      subject: ['', [Validators.required]],
      messageBody: ['', [Validators.required]],
    });
  }

  ngOnInit(): void {
    this.fetchShops(); // Call the fetchShops method on component initialization
  }

  fetchShops(): void {
    this.isLoading = true;
    this.superService.fetchShops().subscribe({
      next: (data) => {
        console.log(data);
        this.customers = data; // Set the products from the API response
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching shops:', error);
      }
    });
  }

  // Handle email selection
  onEmailSelection(event: any) {
    const email = event.target.value;
    if (event.target.checked) {
      this.selectedEmails.push(email);
    } else {
      this.selectedEmails = this.selectedEmails.filter((e) => e !== email);
    }

    // Update the recipient field with selected emails
    this.emailForm.patchValue({ customerEmails: this.selectedEmails });
  }

  // Submit bulk email
  submitBulkEmail() {
    if (this.emailForm.invalid || this.selectedEmails.length === 0) {
      this.toastService.warn("Please fill in all fields and select at least one recipient.");
      return;
    }

    const emailData = {
      ...this.emailForm.value,
      customerEmails: this.selectedEmails,  // Use selected emails for sending
    };

    this.isSending = true;
    this.emailService.sendBulkEmailToCustomer(emailData).subscribe(
      (response) => {
        this.toastService.success("Emails sent successfully.");
        console.log('Emails sent successfully:', response);
        
        this.isSending = false;
        // Optionally reset the form and selected emails after successful submission
        this.emailForm.reset();
        this.selectedEmails = [];
      },
      (error) => {
        this.toastService.error("Error sending emails.");
        console.error('Error sending emails:', error);
        this.isSending = false;
      }
    );
  }
}
