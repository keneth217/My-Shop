import { Component, OnInit } from '@angular/core';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { SalesResponse } from '../model/sales.model';
import { LoaderService } from '../Services/loader.service';
import { SalesService } from '../Services/sales.service';
import { LoaderComponent } from "../loader/loader.component";
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SalesReportService } from '../Services/sales-report.service';

@Component({
  selector: 'app-daily-report',
  standalone: true,
  imports: [LoaderComponent, CommonModule, ReactiveFormsModule, FormsModule, AngularToastifyModule],
  templateUrl: './daily-report.component.html',
  styleUrls: ['./daily-report.component.css']
})
export class DailyReportComponent implements OnInit {

  sales: any[] = [];
  startDate: string = '';
  endDate: string = '';
  saleId: number = 0;
  isLoading: boolean = false;
  selectedYear: number = 0;
  selectedMonth: number = 0;
  years: number[] = [];
  months = [
    { name: 'January', value: 1 },
    { name: 'February', value: 2 },
    { name: 'March', value: 3 },
    { name: 'April', value: 4 },
    { name: 'May', value: 5 },
    { name: 'June', value: 6 },
    { name: 'July', value: 7 },
    { name: 'August', value: 8 },
    { name: 'September', value: 9 },
    { name: 'October', value: 10 },
    { name: 'November', value: 11 },
    { name: 'December', value: 12 },
  ];

  constructor(
    private salesService: SalesService,
    private toastService: ToastService,
    private loaderService: LoaderService,
    private reportService: SalesReportService
  ) {}

  ngOnInit(): void {
    this.initializeYears();
    this.setDefaultDates(); // Set today's date by default
    this.fetchSales(); // Fetch today's sales on load
  }

// Initialize years for the dropdown
initializeYears(): void {
  const currentYear = new Date().getFullYear();
  this.years = Array.from({ length: 6 }, (_, i) => currentYear - i); // Last five years including current year
  this.selectedYear = currentYear; // Set the selected year to the current year by default
  this.selectedMonth = new Date().getMonth() + 1; // Set the selected month to the current month by default
}

// Set the start and end dates based on the selected month and year
setDefaultDates(): void {
  this.updateDateRange();
}

// Update the start and end dates whenever the selected month or year changes
onMonthOrYearChange(): void {
  this.updateDateRange();
}

// Calculate the first and last day of the selected month and year
updateDateRange(): void {
  const firstDay = new Date(this.selectedYear, this.selectedMonth - 1, 1);
  const lastDay = new Date(this.selectedYear, this.selectedMonth, 0);

  this.startDate = firstDay.toISOString().split('T')[0];
  this.endDate = lastDay.toISOString().split('T')[0];
}
 
  // Fetch sales based on selected dates
  fetchSales(): void {
    this.isLoading = true;
    this.loaderService.show();
    this.reportService.getDailyReports(this.selectedYear, this.selectedMonth, this.startDate, this.endDate).subscribe({
      next: (data: SalesResponse) => {
        this.sales = data.content;
        this.isLoading = false;
        this.loaderService.hide();
        if (this.sales.length === 0) {
          this.toastService.warn('No sales found for the selected date range.');
        }
      },
      error: (error) => {
        console.error('Error fetching sales:', error);
        this.toastService.error('Failed to fetch sales data.');
        this.isLoading = false;
        this.loaderService.hide();
      },
    });
  }

  // Preview PDF in a new tab
  previewPdf(saleId: number): void {
    this.salesService.generatePrintableReceipt(saleId).subscribe(
      (response: Blob) => {
        const fileURL = URL.createObjectURL(response);
        window.open(fileURL);
      },
      (error) => {
        console.error('Error generating PDF for preview:', error);
      }
    );
  }

  // Download receipt as PDF
  downloadReceipt(saleId: number): void {
    this.salesService.generatePrintableReceipt(saleId).subscribe(
      (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `receipt-${saleId}.pdf`; // Customize the file name
        a.click();
        window.URL.revokeObjectURL(url);
      },
      (error) => {
        console.error('Error downloading receipt:', error);
      }
    );
  }
}
