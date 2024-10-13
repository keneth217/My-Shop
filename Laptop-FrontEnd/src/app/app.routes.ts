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

export const routes: Routes = [

    { path: "", redirectTo: "login", pathMatch: "full" },
    { path: 'login', title: 'Login Page', component: LoginComponent },
    {
        path: 'dash', title: 'DashBoard', component: DashboardComponent,
        children: [
            { path: '', component: AnalyticsComponent },
            { path: 'product', component: ProductListComponent },
            { path: 'supplier', component: SuppliersListComponent },
            { path: 'employee', component: EmployeeListComponent },
            { path: 'sale', component: SalesListComponent },
            { path: 'expense', component: ExpenseListComponent },
        ],
    },
    { path: '**', component: PageNotFoundComponent }
];
