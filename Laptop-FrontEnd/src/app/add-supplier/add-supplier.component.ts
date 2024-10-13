import { Component, EventEmitter, Output } from '@angular/core';
import { NgIconsModule } from '@ng-icons/core';
import { AngularToastifyModule } from 'angular-toastify';

@Component({
  selector: 'app-add-supplier',
  standalone: true,
  imports: [AngularToastifyModule,NgIconsModule],
  templateUrl: './add-supplier.component.html',
  styleUrl: './add-supplier.component.css'
})
export class AddSupplierComponent {
 
  @Output() close = new EventEmitter<void>();

  closeForm() {
    this.close.emit();
  }


  


}
