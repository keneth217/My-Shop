import { Component } from '@angular/core';
import { DashboardService } from '../Services/dashboard.service';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { LoaderService } from '../Services/loader.service';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [AngularToastifyModule],
  templateUrl: './analytics.component.html',
  styleUrl: './analytics.component.css'
})
export class AnalyticsComponent {
analytics:any;

  constructor(
    private dashbord: DashboardService, // Inject the ProductService
    private toastService: ToastService,
    private loaderService: LoaderService,
  ) {}
  ngOnInit(): void {
    this.fetchAnalytics(); // Call the fetchProducts method on component initialization
  }

  fetchAnalytics(): void {
    this.loaderService.show(); 
    this.dashbord.getdashbord().subscribe({
      next: (data) => {
        console.log(data)
        this.analytics = data; 
        this.loaderService.hide();
      },
      error: (error) => {
        console.error('Error fetching  analytics:', error);
        this.loaderService.hide();
      }
    });
  }
}
