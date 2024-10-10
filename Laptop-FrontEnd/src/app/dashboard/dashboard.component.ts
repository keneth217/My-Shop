import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { NgIconComponent } from '@ng-icons/core';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterOutlet,CommonModule,RouterModule,NgIconComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent {

  isSidebarCollapsed = false;
  isHeaderTransparent = false;
  isReportsDropdownOpen = false;
  isUserDropdownOpen=false;


  toggleSidebar() {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  toggleReportsDropdown() {
    this.isReportsDropdownOpen = !this.isReportsDropdownOpen;
  }
  toggleUserDropdown() {
    this.isUserDropdownOpen = !this.isUserDropdownOpen; // Toggle dropdown visibility
  }
}
