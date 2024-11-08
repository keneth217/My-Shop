import { Component, OnInit } from '@angular/core';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { LoaderService } from '../Services/loader.service';
import { SalesService } from '../Services/sales.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoaderComponent } from "../loader/loader.component";
import { SupplierService } from '../Services/supplier.service';
import { StockService } from '../Services/stock.service';

@Component({
  selector: 'app-stockreport',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, AngularToastifyModule, LoaderComponent],
  templateUrl: './stockreport.component.html',
  styleUrls: ['./stockreport.component.css']
})
export class StockreportComponent implements OnInit {

  stocks: any[] = [];
  startDate: string = '';
  endDate: string = '';
  selectedSupplier: number = 0;
  suppliers: any[] = [];
  isLoading: boolean = false;

  constructor(
    private salesService: SalesService,
    private toastService: ToastService,
    private loaderService: LoaderService,
    private supplierService: SupplierService,
    private stockService: StockService
  ) {}

  ngOnInit(): void {
    // Set default date to today's date
    const today = new Date().toISOString().split('T')[0]; // Format to yyyy-MM-dd
    this.startDate = today;
    this.endDate = today;
    this.fetchSuppliers();
  }

  fetchSuppliers(): void {
    this.isLoading = true;
    this.supplierService.getSuppliers().subscribe({
      next: (data) => {
        this.suppliers = data;
        this.isLoading = false;

        // Set the first supplier as the default selected supplier
        if (this.suppliers.length > 0) {
          this.selectedSupplier = this.suppliers[0].id;
        }
        
        // Fetch stocks with default date and supplier
        this.fetchStocks();
      },
      error: (error) => {
        console.error('Error fetching suppliers:', error);
        this.toastService.error('Failed to fetch supplier list.');
        this.isLoading = false;
      }
    });
  }

  fetchStocks(): void {
    this.isLoading = true;
    this.loaderService.show();
    this.stockService.getStockSupplierList(this.startDate, this.endDate, this.selectedSupplier).subscribe({
      next: (data) => {
        this.stocks = data;
        console.log(data)
        this.isLoading = false;
        this.loaderService.hide();
        if (this.stocks.length === 0) {
          this.toastService.warn('No stock found for the selected date range.');
        }
        if (this.suppliers.length === 0) {
          this.toastService.warn('No stock found for the selected Supplier for date range.');
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
        window.open(fileURL);
      },
      (error) => {
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
        a.download = `receipt-${saleId}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      (error) => {
        console.error('Error downloading receipt:', error);
      }
    );
  }
}
