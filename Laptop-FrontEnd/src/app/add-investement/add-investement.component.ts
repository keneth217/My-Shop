import { Component, EventEmitter, Output } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { ExpenseService } from '../Services/expense.service';
import { CommonModule } from '@angular/common';
import { InvestService } from '../Services/invest.service';

@Component({
  selector: 'app-add-investement',
  standalone: true,
  imports: [CommonModule,AngularToastifyModule,ReactiveFormsModule],
  templateUrl: './add-investement.component.html',
  styleUrl: './add-investement.component.css'
})
export class AddInvestementComponent {

  @Output() close = new EventEmitter<void>();


  investmentForm: FormGroup;
  
    constructor(
      private fb: FormBuilder,
      private invest: InvestService,
      private toastService: ToastService
    ) {
      this.investmentForm = this.fb.group({
        amount: ['', Validators.required],
        description: ['', Validators.required],
      });
    }
  
    closeEForm() {
      this.close.emit();
    }
  
    submitInvestmentData() {
      if (this.investmentForm.valid) {
        const supplierData = this.investmentForm.value;
  
  
        this.invest.addInvestement(supplierData).subscribe(
          (response) => {
            this.toastService.success('Investement registered successfully!');
            this.investmentForm.reset();
            //this.closeEForm();
          },
          (error) => {
            this.toastService.error('Failed to register imvestement!');
            console.error(error);
          }
        );
      } else {
        this.toastService.warn('Please fill in all required fields!');
      }
    }
  
  closeExForm() {
      this.close.emit();
    }
}
