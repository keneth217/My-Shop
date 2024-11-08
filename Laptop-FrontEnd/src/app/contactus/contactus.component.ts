import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AngularToastifyModule } from 'angular-toastify';

@Component({
  selector: 'app-contactus',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule,AngularToastifyModule],
  templateUrl: './contactus.component.html',
  styleUrl: './contactus.component.css'
})
export class ContactusComponent {
contactForm!: FormGroup;
submitData() {
throw new Error('Method not implemented.');
}

}
