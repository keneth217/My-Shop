import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { NgIconComponent } from '@ng-icons/core';
import { LoginComponent } from '../login/login.component';
import { TokenService } from '../Services/token.service';
import { ShopService } from '../Services/shop.service';
import { UserService } from '../Services/user.service';

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
  shopLogo: string | null = null; // For existing logo URL
  userLogo: string | null = null; // For existing logo URL
  currentTime: string = '';
  private timerInterval: any;

  constructor(private tokenService: TokenService,
    private shopService: ShopService,
    private userService:UserService,
    private router:Router) { }


  ngOnDestroy(): void {
    // Clear the interval when the component is destroyed
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
    }
  }

  loadUserDetails(): void {
    this.userService.getUserDetails().subscribe(
      (data) => {
        console.log(data);
      
        // Set the logo URL if it exists
        if (data.userImageUrl) {
          this.userLogo = data.userImageUrl;
        }
      },
      (error) => {
        
      }
    );
  }


  loadShopDetails(): void {
    this.shopService.getShopDetails().subscribe(
      (data) => {
        console.log(data);
      
        // Set the logo URL if it exists
        if (data.shopLogoUrl) {
          this.shopLogo = data.shopLogoUrl;
        }
      },
      (error) => {
        
      }
    );
  }


  startTimer(): void {
    this.timerInterval = setInterval(() => {
      const now = new Date();
      // Format time as HH:mm:ss Hrs
      const timeString = now.toLocaleTimeString('en-GB', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      }) + ' Hrs';
  
      // Format date as day of week, month, day, year
      const dateString = 
        now.toLocaleDateString('en-GB', { weekday: 'long' }) + ', ' +
        now.toLocaleDateString('en-GB', { month: 'long' }) + ', ' +
        now.toLocaleDateString('en-GB', { day: 'numeric' }) + ', ' +
        now.toLocaleDateString('en-GB', { year: 'numeric' });
  
      // Combine time and date
      this.currentTime = `${timeString}, ${dateString}`;
    }, 1000);
  }
  // Fetch the user role after initialization
  ngOnInit(): void {
   
    this.loadUserDetails()
    this.loadShopDetails();
    this.userRole = this.tokenService.getUserRole;
    this.links = this.getUserLinks(); // Set links based on the role
    this.shopDetails();
    this.userDetails();
    this.startTimer();
  }

  logout(): void {
    this.tokenService.removeToken();
    this.router.navigateByUrl('/');
    
  }

  shopDetails(): void{
   this.shopDetailsData= this.tokenService.getShopDetails
  // console.log(this.shopDetails)
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
    { label: 'Settings', icon: 'heroCog6Tooth', route: '/' },
  ];

  // Cashier Links
  adminLinks: Link[] = [
    { label: 'Analytics', icon: 'heroChartBar', route: '/dash' },
    { label: 'Product', icon: 'heroPlusCircle', route: '/dash/product' },
    { label: 'Stocks', icon: 'heroPlusCircle', route: '/dash/stocks' },
    { label: 'Supplier', icon: 'heroPercentBadge', route: '/dash/supplier' },
    { label: 'Employee', icon: 'heroPercentBadge', route: '/dash/employee' },
    { label: 'Sales', icon: 'heroPercentBadge', route: '/dash/sale' },
    { label: 'Expense', icon: 'heroPercentBadge', route: '/dash/expense' },
    { label: 'My-Shop', icon: 'heroPercentBadge', route: '/dash/shop' },
    { label: 'Investment', icon: 'heroPercentBadge', route: '/dash/investment' },
    { label: 'Users', icon: 'heroUsers', route: '/dash/users' },
    {
      label: 'Reports',
      icon: 'heroDocument',
      children: [
        { label: 'Daily Report', icon: 'heroSquares2x2', route: 'reports/daily' },
        { label: 'Monthly Report', icon: 'heroSquares2x2', route: 'reports/monthly' },
      ],
    },
    { label: 'Settings', icon: 'heroWrenchScrewdriver', route: '/dash/tenant-conf' },
  ];

  // Admin Links
  cashierLinks: Link[] = [
    { label: 'Analytics', icon: 'heroChartBar', route: '/dash/cash-analytics' },
    { label: 'Product', icon: 'heroPlusCircle', route: '/dash/product' },
    { label: 'Supplier', icon: 'heroPercentBadge', route: '/dash/supplier' },
    { label: 'Employee', icon: 'heroPercentBadge', route: '/dash/employee' },
    { label: 'Sales', icon: 'heroPercentBadge', route: '/dash/sale' },
    { label: 'Expense', icon: 'heroPercentBadge', route: '/dash/expense' },
    {
      label: 'Reports',
      icon: 'heroDocument',
      children: [
        { label: 'Sales', icon: 'heroSquares2x2', route: '/dash/reports/daily' },
        { label: 'Monthly Report', icon: 'heroSquares2x2', route: '/dash/reports/monthly' },
      ],
    },
    { label: 'Settings', icon: 'heroCog6Tooth', route: 'dash/tenant-conf' },
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
