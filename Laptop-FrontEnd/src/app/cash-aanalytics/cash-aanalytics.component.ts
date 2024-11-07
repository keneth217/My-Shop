import { Component } from '@angular/core';
import { ToastService } from 'angular-toastify';
import { Chart, ChartOptions } from 'chart.js';
import { TopProductsResponse } from '../model/top.model';
import { DashboardService } from '../Services/dashboard.service';
import { ExpenseService } from '../Services/expense.service';
import { LoaderService } from '../Services/loader.service';
import { ProductsService } from '../Services/products.service';
import { CommonModule } from '@angular/common';
import { LoaderComponent } from "../loader/loader.component";

@Component({
  selector: 'app-cash-aanalytics',
  standalone: true,
  imports: [CommonModule, LoaderComponent],
  templateUrl: './cash-aanalytics.component.html',
  styleUrl: './cash-aanalytics.component.css'
})
export class CashAAnalyticsComponent {

  analytics: any;

  toProducts: any[] = [];
  expenses: any[] = [];

  ExpenseTotals: { [key: string]: number } = {
    SALARY: 0,
    UTILITIES: 0,
    STOCKPURCHASE: 0,
  };

  private pieChart1: Chart<'bar', number[], string> | undefined;
  private pieChart2: Chart<'pie', number[], string> | undefined;
  private pieChart3: Chart<'pie', number[], string> | undefined; // Third chart
isLoading: boolean=false;

  constructor(
    private dashboardService: DashboardService,
    private toastService: ToastService,
    private productsService:ProductsService,
    private expenseService: ExpenseService,
    private loaderService: LoaderService
  ) {}

  ngOnInit(): void {
    this.isLoading=true;
    this.loaderService.show();
    Promise.all([this.fetchAnalytics(),this.fetchTopProducts(), this.fetchExpenseTotals()]).finally(() =>
      this.loaderService.hide()
   
    );
    this.isLoading=false;
  }

  ngOnDestroy(): void {
    if (this.pieChart1) this.pieChart1.destroy();
    if (this.pieChart2) this.pieChart2.destroy();
    if (this.pieChart3) this.pieChart3.destroy(); // Destroy third chart
  }

  fetchTopProducts(): Promise<void> {
    this.isLoading=true;
    return new Promise((resolve, reject) => {
      this.productsService.getTopProducts().subscribe({
        next: (data:TopProductsResponse) => {
          console.log(data);
          this.toProducts = data.content;
          this.isLoading=false;
      
          resolve();
        },
        error: (error) => {
          console.error('Error fetching analytics:', error);
          this.toastService.error('Failed to load analytics data.');
          reject(error);
        },
      });
    });
  }

  fetchAnalytics(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.dashboardService.getdashbord().subscribe({
        next: (data) => {
          console.log(data);
          this.analytics = data;
          this.createPieCharts();
          resolve();
        },
        error: (error) => {
          console.error('Error fetching analytics:', error);
          this.toastService.error('Failed to load analytics data.');
          reject(error);
        },
      });
    });
  }

  fetchExpenseTotals(): void {
    this.loaderService.show();
    this.expenseService.getPExpensesTotals().subscribe({
      next: (data) => {
        console.log('Expense Totals Response:', data);
        if (this.isObject(data)) {
          this.mapExpenseTotals(data);
        } else {
          console.error('Invalid data format:', data);
          this.toastService.error('Failed to load expense totals.');
        }
        this.loaderService.hide();
      },
      error: (error) => {
        console.error('Error fetching expenses:', error);
        this.loaderService.hide();
      },
    });
  }

  isObject(data: any): data is { [key: string]: number } {
    return data && typeof data === 'object' && !Array.isArray(data);
  }

  mapExpenseTotals(data: { [key: string]: number }): void {
    Object.entries(data).forEach(([key, total]) => {
      if (this.ExpenseTotals.hasOwnProperty(key)) {
        this.ExpenseTotals[key] = total;
      }
    });
  }

  createPieCharts(): void {
    if (this.pieChart1) this.pieChart1.destroy();
    if (this.pieChart2) this.pieChart2.destroy();
    if (this.pieChart3) this.pieChart3.destroy(); // Destroy the third chart if it exists

    if (this.analytics) {
      const { totalSales,totalExpenses,totalUsers, totalEmployees, totalProducts } =
        this.analytics;

      // Pie Chart 1: Sales,  Expenses
      this.pieChart1 = new Chart('pieChart1', {
        type: 'bar',
        data: {
          labels: ['Sales', 'Expenses'],
          datasets: [
            {
              data: [totalSales, totalExpenses],
              backgroundColor: ['#FF6384', '#36A2EB'],
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
        } as ChartOptions<'bar'>,
      });

      // Pie Chart 2: Expense Types Distribution
      this.pieChart2 = new Chart('pieChart2', {
        type: 'pie',
        data: {
          labels: ['Salary', 'Utilities', 'Stock Purchase'],
          datasets: [
            {
              data: [
                this.ExpenseTotals['SALARY'],
                this.ExpenseTotals['UTILITIES'],
                this.ExpenseTotals['STOCKPURCHASE'],
              ],
              backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56'],
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
        } as ChartOptions<'pie'>,
      });

      // Pie Chart 3: Total Employees and Products
      this.pieChart3 = new Chart('pieChart3', {
        type: 'pie',
        data: {
          labels: ['Employees','users', ' Products'],
          datasets: [
            {
              data: [totalEmployees,totalUsers, totalProducts],
              backgroundColor: ['#36A2EB', '#1549F4','#FF6384'],
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
        } as ChartOptions<'pie'>,
      });
    }
  }
  deleteProduct(arg0: any) {
    throw new Error('Method not implemented.');
    }
}
