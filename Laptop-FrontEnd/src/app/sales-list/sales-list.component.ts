import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { SalesService } from '../Services/sales.service';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SalesResponse } from '../model/sales.model';
import { LoaderService } from '../Services/loader.service';
import { LoaderComponent } from "../loader/loader.component";

@Component({
  selector: 'app-sales-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, AngularToastifyModule, LoaderComponent],
  templateUrl: './sales-list.component.html',
  styleUrls: ['./sales-list.component.css'],
})
export class SalesListComponent {

  sales: any[] = [];
  startDate: string = ''; // Bound to the form input
  endDate: string = ''; // Bound to the form input

  saleid:number=0;
isLoading: boolean=false;

  constructor(
    private salesService: SalesService,
    private toastService: ToastService,
    private loaderService: LoaderService
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
    this.isLoading=true;
    this.loaderService.show();
    this.salesService.getSales(this.startDate, this.endDate).subscribe({
      next: (data: SalesResponse) => {
        console.log(data);
        this.isLoading=false;
        this.sales = data.content;
        this.loaderService.hide();
       
        if (this.sales.length === 0) {
          this.loaderService.hide();// Set the products from the API response
          this.toastService.warn('No sales found for the selected date range.');
        }
      },
      error: (error) => {
        console.error('Error fetching sales:', error);
        this.toastService.error('Failed to fetch sales data.');
      },
    });
  }

  previewPdf(saleId: number): void {
    this.salesService.generatePrintableReceipt(saleId).subscribe(
      (response: Blob) => {
        const fileURL = URL.createObjectURL(response);
        window.open(fileURL); // Open PDF in a new tab for preview
      },
      (error: any) => {
        console.error('Error generating PDF for preview:', error);
      }
    );
  }
  



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