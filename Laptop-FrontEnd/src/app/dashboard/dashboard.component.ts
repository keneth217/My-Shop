import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { NgIconComponent } from '@ng-icons/core';
import { LoginComponent } from '../login/login.component';
import { TokenService } from '../Services/token.service';

// Interface for defining links
interface Link {
  label: string;
  icon: string;
  route?: string;
  children?: Link[];
  isOpen?: boolean;  // Dropdown open/close state
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    RouterOutlet,
    CommonModule,
    RouterModule,
    NgIconComponent,
    LoginComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  isSidebarCollapsed = false;
  isUserDropdownOpen = false;
  shopDetailsData: any; 
  userDetailsData: any; 
  userRole: string = ''; // Initialize with an empty string to avoid issues
  links: Link[] = []; // Holds the active links based on role

  constructor(private tokenService: TokenService,private router:Router) { }

  // Fetch the user role after initialization
  ngOnInit(): void {
    this.userRole = this.tokenService.getUserRole;
    this.links = this.getUserLinks(); // Set links based on the role
    this.shopDetails();
    this.userDetails();
  }

  logout(): void {
    this.tokenService.removeToken();
    this.router.navigateByUrl('/');
    
  }
  shopDetails(): void{
   this.shopDetailsData= this.tokenService.getShopDetails
  }

  userDetails(): void{
    this.userDetailsData=this.tokenService.getUser
  }

  // Define the links for different roles
  private getUserLinks(): Link[] {
    switch (this.userRole) {
      case 'SUPER_USER':
        return this.superUserLinks;
      case 'CASHIER':
        return this.cashierLinks;
      case 'ADMIN':
        return this.adminLinks;
      default:
        return [];
    }
  }

  // Super User Links
  superUserLinks: Link[] = [
    { label: 'Dashboard', icon: 'heroSquares2x2', route: '/dash' },
    { label: 'Shops', icon: 'heroUsers', route: '/shop' },
    {
      label: 'Reports',
      icon: 'heroDocument',
      children: [
        { label: 'Shops Report', icon: 'heroSquares2x2', route: '/shops' },
        { label: 'Payments Report', icon: 'heroSquares2x2', route: '/reports/payments' },
        { label: 'Status Report', icon: 'heroSquares2x2', route: '/reports/status' },
      ],
    },
    { label: 'Settings', icon: 'heroCog6Tooth', route: '/settings' },
  ];

  // Cashier Links
  adminLinks: Link[] = [
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
        { label: 'Sales', icon: 'heroSquares2x2', route: '/reports/daily' },
        { label: 'Monthly Report', icon: 'heroSquares2x2', route: '/reports/monthly' },
      ],
    },
    { label: 'Settings', icon: 'heroCog6Tooth', route: '/settings' },
  ];

  // Admin Links
  cashierLinks: Link[] = [

    { label: 'Product', icon: 'heroPlusCircle', route: '/dash/product' },
    {
      label: 'Reports',
      icon: 'heroDocument',
      children: [
        { label: 'Sales', icon: 'heroSquares2x2', route: '/reports/daily' },
        { label: 'Monthly Report', icon: 'heroSquares2x2', route: '/reports/monthly' },
      ],
    },
    { label: 'Settings', icon: 'heroCog6Tooth', route: '/settings' },
  ];

  // Sidebar toggle functionality
  toggleSidebar() {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  // User dropdown toggle functionality
  toggleUserDropdown() {
    this.isUserDropdownOpen = !this.isUserDropdownOpen;
  }

  // Handle link clicks to toggle dropdowns with children
  handleLinkClick(link: Link) {
    if (link.children) {
      link.isOpen = !link.isOpen;
    }
  }
}
