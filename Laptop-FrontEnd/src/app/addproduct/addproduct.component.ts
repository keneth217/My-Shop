import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { 
  FormBuilder, FormGroup, FormArray, Validators, FormControl, 
  FormsModule, ReactiveFormsModule 
} from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { ProductsService } from '../Services/products.service';
import { Router } from '@angular/router';

interface PreviewImage {
  file: File;
  preview: string;
}

@Component({
  selector: 'app-addproduct',
  standalone: true,
  imports: [AngularToastifyModule, CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './addproduct.component.html',
  styleUrls: ['./addproduct.component.css']
})
export class AddproductComponent {
  @Output() close = new EventEmitter<void>();

  productForm: FormGroup;
  newFeature = new FormControl('', Validators.required); // New feature input
  selectedImages: PreviewImage[] = [];
  maxImageSize = 10 * 1024 * 1024; // Maximum image size: 10 MB

  constructor(
    private fb: FormBuilder,
    private toastService: ToastService,
    private productsService: ProductsService,
    private router:Router
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      productFeatures: this.fb.array([], Validators.required), // Ensure features array is required
    });
  }

  // Getter for the FormArray of features
  get features(): FormArray {
    return this.productForm.get('productFeatures') as FormArray; // Changed 'features' to 'productFeatures'
  }

  // Create a new FormGroup for a feature
  private createFeature(featureName: string): FormGroup {
    return this.fb.group({
      featureName: [featureName, Validators.required]
    });
  }

  // Add a new feature to the array
  addFeature(): void {
    // Check if newFeature is not null and has a valid value
    if (this.newFeature && this.newFeature.valid && this.newFeature.value?.trim()) {
      this.features.push(this.createFeature(this.newFeature.value.trim()));
      this.newFeature.reset();
    } else {
      this.toastService.error('Feature cannot be empty.');
    }
  }

  // Remove a feature from the array
  removeFeature(index: number): void {
    if (this.features.length > 1) {
      this.features.removeAt(index);
    } else {
      this.toastService.error('At least one feature is required.');
    }
  }

  // Handle image selection and generate previews
  async onImageSelect(event: Event): Promise<void> {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const files = Array.from(input.files).filter(file => file.size > 0);
      for (const file of files) {
        // Check if the file size exceeds the limit
        if (file.size > this.maxImageSize) {
          this.toastService.error('Image size exceeds the limit of 10 MB.');
          continue; // Skip adding this file to the preview
        }
        const preview = await this.generateImagePreview(file);
        this.selectedImages.push({ file, preview });
      }
    }
  }

  // Generate a preview for an image
  private generateImagePreview(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (event) => resolve(event.target?.result as string);
      reader.onerror = (error) => reject(error);
      reader.readAsDataURL(file);
    });
  }

  // Remove an image from the preview list
  removeImage(index: number): void {
    this.selectedImages.splice(index, 1);
  }

  // Submit the product form using FormData
  submitProduct(): void {
    if (this.productForm.valid && this.features.length > 0) {
      const formData = new FormData();
      formData.append('name', this.productForm.get('name')?.value);
  
      // Combine features into a single, comma-separated string
      const featuresString = this.features.controls
        .map(control => control.get('featureName')?.value.trim())
        .join(', ');
  
      formData.append('productFeatures', featuresString);
  
      this.selectedImages.forEach((image) => {
        formData.append('productImages', image.file);
      });
  
      this.productsService.addProduct(formData).subscribe({
        next: () => {
          this.toastService.success('Product uploaded successfully!');
          this.router.navigateByUrl('/dash/product');
          this.closePForm(); // Close the form after successful submission
        },
        error: () => {
          this.toastService.error('Failed to upload product.');
        }
      });
    } else {
      this.toastService.error('Please fill all required fields and add at least one feature.');
    }
  }
  // Close the form
  closePForm(): void {
    this.close.emit();
  }
}