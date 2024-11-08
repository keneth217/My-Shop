import { Component } from '@angular/core';
import { ToastService } from 'angular-toastify';
import { SalesResponse } from '../model/sales.model';
import { LoaderService } from '../Services/loader.service';
import { SalesService } from '../Services/sales.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoaderComponent } from "../loader/loader.component";
import { StockService } from '../Services/stock.service';

@Component({
  selector: 'app-stock-purchases',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoaderComponent,FormsModule],
  templateUrl: './stock-purchases.component.html',
  styleUrl: './stock-purchases.component.css'
})
export class StockPurchasesComponent {

  stocks: any[] = [];
  startDate: string = ''; // Bound to the form input
  endDate: string = ''; // Bound to the form input

  saleid: number = 0;
  isLoading: boolean = false;

  constructor(
    private loaderService: LoaderService,
    private toastService: ToastService,
    private stockService:StockService
  ) { }

  ngOnInit(): void {
    this.setDefaultDates(); // Set today's date by default
    this.fetchStockList(); // Fetch today's sales on load
  }

  // Set the default dates to today
  setDefaultDates(): void {
    const today = new Date().toISOString().split('T')[0]; // Format: YYYY-MM-DD
    this.startDate = today;
    this.endDate = today;
  }

  // Fetch sales based on selected dates
  fetchStockList(): void {
    this.isLoading = true;
    this.loaderService.show();
    this.stockService.getStockList(this.startDate, this.endDate).subscribe({
      next: (data) => {
        console.log(data);
        this.isLoading = false;
        this.stocks = data;
        this.loaderService.hide();

        if (this.stocks.length === 0) {
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

  previewPdf(): void {
    this.stockService.generatePrintableStockList(this.startDate,this.endDate).subscribe(
      (response: Blob) => {
        const fileURL = URL.createObjectURL(response);
        window.open(fileURL); // Open PDF in a new tab for preview
      },
      (error: any) => {
        console.error('Error generating PDF for preview:', error);
      }
    );
  }

  downloadpdf(): void {
    this.stockService.generatePrintableStockList(this.startDate,this.endDate).subscribe(
      (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `stock-purchases-list.pdf`; // Customize the file name
        a.click();
        window.URL.revokeObjectURL(url);
      },
      (error) => {
        console.error('Error downloading receipt:', error);
      }
    );
  }
}
