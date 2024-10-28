import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ToastService } from 'angular-toastify';
import { LoaderService } from '../Services/loader.service';
import { ProductsService } from '../Services/products.service';
import { SalesService } from '../Services/sales.service';

@Component({
  selector: 'app-view-single-product',
  standalone: true,
  imports: [],
  templateUrl: './view-single-product.component.html',
  styleUrl: './view-single-product.component.css'
})
export class ViewSingleProductComponent {

  @Input() productId!: number;
  product: any[] = [];
  @Output() close = new EventEmitter<void>();
  constructor(
    private productService: ProductsService, // Inject the ProductService
    private toastService: ToastService,
    private saleService: SalesService,
    private loaderService: LoaderService,

  ) { }

  ngOnInit(): void {
    this.fetchSingleProduct(); // Call the fetchProducts method on component initialization
  }


  fetchSingleProduct(): void {
    this.loaderService.show();
    this.productService.getSingleProduct(this.productId).subscribe({
      next: (data) => {
        console.log(data)
        this.product = data;
        this.loaderService.hide();// Set the products from the API response
      },
      error: (error) => {
        console.error('Error fetching product:', error);
        this.loaderService.hide();
      }
    });
  }

  updateProduct() {
    throw new Error('Method not implemented.');
    }
    deleteProduct() {
    throw new Error('Method not implemented.');
    }

  closeViewForm() {
    this.close.emit();
  }

}
