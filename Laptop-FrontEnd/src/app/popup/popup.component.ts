import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-popup',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './popup.component.html',
  styleUrl: './popup.component.css'
})
export class PopupComponent {
  @Input() message: string = '';
  @Input() popupType: 'success' | 'error' = 'success';
  isVisible: boolean = false;

  ngOnInit(): void {
    // if (this.isVisible) {
    //   // Automatically hide the popup after 2 seconds only if it's visible
    //   setTimeout(() => {
    //     this.isVisible = false;
    //   }, 2000);
    // }
  }

  showPopup(message: string, type: 'success' | 'error') {
    this.message = message;
    this.popupType = type;
    this.isVisible = true;

    if (this.isVisible) {
      // Automatically hide the popup after 2 seconds only if it's visible
      setTimeout(() => {
        this.isVisible = false;
      }, 4000);
    }
  }

  closePopup() {
    this.isVisible = false;
  }
}
