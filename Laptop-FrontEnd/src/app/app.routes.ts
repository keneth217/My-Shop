import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProductListComponent } from './product-list/product-list.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { SuppliersListComponent } from './suppliers-list/suppliers-list.component';
import { EmployeeListComponent } from './employee-list/employee-list.component';
import { SalesListComponent } from './sales-list/sales-list.component';
import { AnalyticsComponent } from './analytics/analytics.component';
import { ExpenseListComponent } from './expense-list/expense-list.component';
import { SuperLoginComponent } from './super-login/super-login.component';
import { SuperDashboardComponent } from './super-dashboard/super-dashboard.component';
import { ShoplistsComponent } from './shoplists/shoplists.component';
import { SuperAnalyticsComponent } from './super-analytics/super-analytics.component';
import { StocksComponent } from './stocks/stocks.component';
import { MyShopComponent } from './my-shop/my-shop.component';
import { InvestementlistComponent } from './investementlist/investementlist.component';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { ConfigurationsComponent } from './configurations/configurations.component';
import { ContactusComponent } from './contactus/contactus.component';
import { UsersComponent } from './users/users.component';
import { DailyReportComponent } from './daily-report/daily-report.component';
import { MonthlyReportComponent } from './monthly-report/monthly-report.component';
import { CashAAnalyticsComponent } from './cash-aanalytics/cash-aanalytics.component';
import { StockPurchasesComponent } from './stock-purchases/stock-purchases.component';
import { StockreportComponent } from './stockreport/stockreport.component';
import { SendEmailComponent } from './send-email/send-email.component';
import { SendbulkemailComponent } from './sendbulkemail/sendbulkemail.component';
import { CartitemsComponent } from './cartitems/cartitems.component';

export const routes: Routes = [

    { path: "", redirectTo: "login", pathMatch: "full" },
    { path: 'login', title: 'Login Page', component: LoginComponent },
    { path: 'super', title: 'Super Page', component: SuperLoginComponent },
    {
        path: 'dash', title: 'DashBoard', component: DashboardComponent,
        children: [
            { path: '', component: AnalyticsComponent },
            { path: 'product', component: ProductListComponent },
            { path: 'stocks', component: StocksComponent },
            { path: 'supplier', component: SuppliersListComponent },
            { path: 'employee', component: EmployeeListComponent },
            { path: 'sale', component: SalesListComponent },
            { path: 'expense', component: ExpenseListComponent },
            { path: 'shop', component: MyShopComponent },
            { path: 'investment', component: InvestementlistComponent },
            { path: 'profile', component: UserProfileComponent },
            { path: 'tenant-conf', component: ConfigurationsComponent },
            { path: 'contact', component: ContactusComponent },
            { path: 'cart', component: CartitemsComponent },
            { path: 'users', component: UsersComponent },
            { path: 'purchases', component: StockPurchasesComponent },
            { path: 'cash-analytics', component: CashAAnalyticsComponent },
            { path: 'reports/daily', component: DailyReportComponent },
            { path: 'reports/monthly', component: MonthlyReportComponent },
            { path: 'reports/stock', component: StockreportComponent },

        ],
    },


    {
        path: 'admin', title: 'DashBoard', component: SuperDashboardComponent,
        children: [
            { path: '', component: SuperAnalyticsComponent },
            { path: 'shops', component: ShoplistsComponent },
            { path: 'mail', component: SendEmailComponent },
            { path: 'bulk', component: SendbulkemailComponent },
        
        ],
    },
    { path: '**', component: PageNotFoundComponent }
];
