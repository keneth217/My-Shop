import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { SalesService } from '../Services/sales.service';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-sales-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule,FormsModule,AngularToastifyModule],
  templateUrl: './sales-list.component.html',
  styleUrls: ['./sales-list.component.css'],
})
export class SalesListComponent {
  sales: any[] = [];
  startDate: string = ''; // Bound to the form input
  endDate: string = ''; // Bound to the form input

  constructor(
    private salesService: SalesService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.setDefaultDates(); // Set today's date by default
    this.fetchSales(); // Fetch today's sales on load
  }

  // Set the default dates to today
  setDefaultDates(): void {
    const today = new Date().toISOString().split('T')[0]; // Format: YYYY-MM-DD
    this.startDate = today;
    this.endDate = today;
  }

  // Fetch sales based on selected dates
  fetchSales(): void {
    this.salesService.getSales(this.startDate, this.endDate).subscribe({
      next: (data) => {
        console.log(data)
        this.sales = data;
        if (this.sales.length === 0) {
          this.toastService.warn('No sales found for the selected date range.');
        }
      },
      error: (error) => {
        console.error('Error fetching sales:', error);
        this.toastService.error('Failed to fetch sales data.');
      },
    });
  }
}