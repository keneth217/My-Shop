import { Component } from '@angular/core';
import { AddEmployeeComponent } from "../add-employee/add-employee.component";
import { CommonModule } from '@angular/common';
import { AngularToastifyModule } from 'angular-toastify';
import { NgIconsModule } from '@ng-icons/core';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  imports: [AddEmployeeComponent, CommonModule, AngularToastifyModule, NgIconsModule],
  templateUrl: './employee-list.component.html',
  styleUrl: './employee-list.component.css'
})
export class EmployeeListComponent {
  Employees: any;
  showAddEmployee: Boolean = false;
  closeAddEmployee() {
    this.showAddEmployee = false;
  }

  openAddEmployee() {
    this.showAddEmployee = true;
  }

}
