import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AngularToastifyModule, ToastService } from 'angular-toastify';
import { ProductsService } from '../Services/products.service';


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
  selectedImages: PreviewImage[] = [];

  constructor(
    private fb: FormBuilder, 
    private toastService: ToastService,
    private productsService: ProductsService // Inject service
  ) {
    this.productForm = this.fb.group({
      name: ['', Validators.required],
      features: this.fb.array([this.createFeature()]) // Initialize with one feature input
    });
  }

  // Getter for features FormArray
  get features(): FormArray {
    return this.productForm.get('features') as FormArray;
  }

  // Create a FormGroup for a new feature with additional properties
  createFeature(): FormGroup {
    return this.fb.group({
      featureName: ['', Validators.required],  // Name of the feature
      featureDescription: ['', Validators.required]  // Description of the feature
    });
  }

  // Add a new feature to the array
  addFeature(): void {
    this.features.push(this.createFeature());
  }

  // Remove a feature from the array
  removeFeature(index: number): void {
    this.features.removeAt(index);
  }

  // Handle image selection and generate previews
  async onImageSelect(event: Event): Promise<void> {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      const files = Array.from(input.files);
      for (const file of files) {
        const preview = await this.generateImagePreview(file);
        this.selectedImages.push({ file, preview });
      }
    }
  }

  // Generate a preview URL for the selected image
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
    if (this.productForm.valid) {
      const formData = new FormData();
      formData.append('name', this.productForm.get('name')?.value);
      this.features.controls.forEach((control, index) => {
        formData.append(`features[${index}][featureName]`, control.get('featureName')?.value);
        formData.append(`features[${index}][featureDescription]`, control.get('featureDescription')?.value);
      });
      this.selectedImages.forEach((image, index) => {
        formData.append('images', image.file);
      });

      // Call the service to submit the product
      this.productsService.addProduct(formData).subscribe({
        next: () => {
          this.toastService.success('Product uploaded successfully!');
          this.closePForm(); // Close the form on successful submission
        },
        error: () => {
          this.toastService.error('Failed to upload product.');
        }
      });
    } else {
      this.toastService.error('Please fill all required fields.');
    }
  }

  // Close the form
  closePForm() {
    this.close.emit();
  }
}