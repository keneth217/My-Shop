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
  selector: 'app-monthly-report',
  standalone: true,
  imports: [LoaderComponent, CommonModule, ReactiveFormsModule, FormsModule, AngularToastifyModule],
  templateUrl: './monthly-report.component.html',
  styleUrls: ['./monthly-report.component.css']
})
export class MonthlyReportComponent implements OnInit {
  sales: any[] = [];
  saleid: number = 0;
  selectedYear: number = 0;
  selectedMonth: number = 0;
  years: number[] = [];
  isLoading:boolean=false;
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
    this.setDefaultDates();
  }

  initializeYears() {
    const currentYear = new Date().getFullYear();
    this.years = Array.from({ length: 6 }, (_, i) => currentYear - i);
    this.selectedYear = currentYear;
  }

  setDefaultDates(): void {
    const today = new Date();
    this.selectedMonth = today.getMonth() + 1;
    this.selectedYear = today.getFullYear();
    this.fetchSales(); // Call fetchSales only once here after setting default dates
  }

  fetchSales(): void {
    if (this.selectedMonth < 1 || this.selectedMonth > 12) {
        this.toastService.error('Invalid month selected.');
        return;
    }

    if (this.selectedYear < 1900 || this.selectedYear > new Date().getFullYear()) {
        this.toastService.error('Invalid year selected.');
        return;
    }

    this.isLoading = true;
    this.loaderService.show();

    // Log for debugging
    console.log(`Fetching sales for ${this.selectedMonth}/${this.selectedYear}`);
    this.reportService.getMonthlyReports(this.selectedYear, this.selectedMonth)
    .subscribe({
      next: (data: SalesResponse) => {
        console.log(data);
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
      }
    });
}

  previewPdf(saleId: number): void {
    this.salesService.generatePrintableReceipt(saleId).subscribe(
      (response: Blob) => {
        const fileURL = URL.createObjectURL(response);
        const pdfWindow = window.open(fileURL);
        pdfWindow?.addEventListener('beforeunload', () => URL.revokeObjectURL(fileURL));
      },
      (error) => {
        console.error('Error generating PDF for preview:', error);
        this.toastService.error('Could not preview the PDF.');
      }
    );
  }

  downloadReceipt(saleId: number): void {
    this.salesService.generatePrintableReceipt(saleId).subscribe(
      (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `receipt-${saleId}.pdf`;
        a.click();
        URL.revokeObjectURL(url);
      },
      (error) => {
        console.error('Error downloading receipt:', error);
        this.toastService.error('Could not download the receipt.');
      }
    );
  }
}
