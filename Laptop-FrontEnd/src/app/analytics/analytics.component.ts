import { Component, OnInit, OnDestroy } from '@angular/core';
import { DashboardService } from '../Services/dashboard.service';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { LoaderService } from '../Services/loader.service';
import { ExpenseService } from '../Services/expense.service';
import { Chart, ChartOptions, registerables } from 'chart.js';

// Register Chart.js components
Chart.register(...registerables);

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [AngularToastifyModule],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.css'],
})
export class AnalyticsComponent implements OnInit, OnDestroy {
  analytics: any;
  expenses: any[] = [];

  ExpenseTotals: { [key: string]: number } = {
    SALARY: 0,
    UTILITIES: 0,
    STOCKPURCHASE: 0,
  };

  private pieChart1: Chart<'bar', number[], string> | undefined;
  private pieChart2: Chart<'pie', number[], string> | undefined;
  private pieChart3: Chart<'pie', number[], string> | undefined; // Third chart

  constructor(
    private dashboardService: DashboardService,
    private toastService: ToastService,
    private expenseService: ExpenseService,
    private loaderService: LoaderService
  ) {}

  ngOnInit(): void {
    this.loaderService.show();
    Promise.all([this.fetchAnalytics(), this.fetchExpenseTotals()]).finally(() =>
      this.loaderService.hide()
    );
  }

  ngOnDestroy(): void {
    if (this.pieChart1) this.pieChart1.destroy();
    if (this.pieChart2) this.pieChart2.destroy();
    if (this.pieChart3) this.pieChart3.destroy(); // Destroy third chart
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
      const { totalSales, netProfit, grossProfit, totalExpenses,totalUsers, totalEmployees, totalProducts } =
        this.analytics;

      // Pie Chart 1: Sales, Net Profit, Gross Profit, Expenses
      this.pieChart1 = new Chart('pieChart1', {
        type: 'bar',
        data: {
          labels: ['Sales', 'Net Profit', 'Gross Profit', 'Expenses'],
          datasets: [
            {
              data: [totalSales, netProfit, grossProfit, totalExpenses],
              backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0'],
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
              backgroundColor: ['#36A2EB', '#FF6384','#FF6384'],
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
}
