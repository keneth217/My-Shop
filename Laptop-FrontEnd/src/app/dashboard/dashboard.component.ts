import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterModule, RouterOutlet } from '@angular/router';
import { NgIconComponent } from '@ng-icons/core';
import { LoginComponent } from "../login/login.component";

// Define interface for links
interface Link {
  label: string;
  icon: string;
  route?: string;
  children?: Link[];  // Nested links for dropdown functionality
  isOpen?: boolean;   // Track dropdown open/close state
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterOutlet, CommonModule, RouterModule, NgIconComponent, LoginComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent {
  isSidebarCollapsed = false;
  isUserDropdownOpen = false;

  // Define arrays of links with nested children for dropdowns
  superUserLinks: Link[] = [
    { label: 'Dashboard', icon: 'heroSquares2x2', route: '/dash' },
    { label: 'Shops', icon: 'heroUsers', route: '/shop' },
    {
      label: 'Reports',
      icon: 'heroDocument',
      children: [
        { label: 'Shops Report',icon: 'heroSquares2x2', route: '/shops' },
        { label: 'Payments Report',icon: 'heroSquares2x2', route: '/reports/payments' },
        { label: 'Status Report',icon: 'heroSquares2x2', route: '/reports/status' },
      ],
    },
    { label: 'Settings', icon: 'heroCog6Tooth', route: '/settings' },
  ];

  cashierLinks: Link[] = [
    { label: 'Analytics', icon: 'heroPlusCircle', route: '/dash' },
    { label: 'Product', icon: 'heroPlusCircle', route: '/dash/product' },
    { label: 'Supplier', icon: 'heroPercentBadge', route: '/dash/supplier' },
    { label: 'Employee', icon: 'heroPercentBadge', route: '/dash/employee' },
    { label: 'Sales', icon: 'heroPercentBadge', route: '/dash/sale' },
    { label: 'Expense', icon: 'heroPercentBadge', route: '/dash/expense' },
     {
      label: 'Reports',
      icon: 'heroDocument',
      children: [
        { label: 'sales', icon: 'heroSquares2x2', route: '/reports/daily' },
        { label: 'Monthly Report',icon: 'heroSquares2x2', route: '/reports/monthly' },
      ],
    },
    { label: 'Settings', icon: 'heroCog6Tooth', route: '/settings' },
  ];

  adminLinks: Link[] = [
    {
      label: 'Reports',
      icon: 'heroDocument',
      children: [
        { label: 'sales', icon: 'heroSquares2x2', route: '/reports/daily' },
        { label: 'Monthly Report',icon: 'heroSquares2x2', route: '/reports/monthly' },
      ],
    },
    { label: 'Settings', icon: 'heroCog6Tooth', route: '/settings' },
  ];

  // Set the user's role (could come from authentication logic)
  userRole: 'superuser' | 'cashier' | 'admin' = 'cashier'; 

  // Get links based on user role
  get userLinks(): Link[] {
    switch (this.userRole) {
      case 'superuser':
        return this.superUserLinks;
      case 'cashier':
        return this.cashierLinks;
      case 'admin':
        return this.adminLinks;
      default:
        return [];
    }
  }

  toggleSidebar() {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  toggleUserDropdown() {
    this.isUserDropdownOpen = !this.isUserDropdownOpen;
  }

  // Handle link clicks to toggle dropdowns for links with children
  handleLinkClick(link: Link) {
    if (link.children) {
      link.isOpen = !link.isOpen;
    }
  }
}
