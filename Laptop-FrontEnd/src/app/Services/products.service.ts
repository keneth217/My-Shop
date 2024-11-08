import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenService } from './token.service';
import { TopProductsResponse } from '../model/top.model';


@Injectable({
  providedIn: 'root'
})
export class ProductsService {

  private baseUrl = 'http://localhost:9091/api/admin/products';
  private topUrl='http://localhost:9091/api/business/top-products';
  private pdfUrl='http://localhost:9091/api/pdf/reports';

  constructor(private http: HttpClient, private tokenService: TokenService) { }

  // Method to add a product
  addProduct(productData: FormData): Observable<any> {
    return this.http.post<any>(
      `${this.baseUrl}/add`,
      productData,
      { headers: this.createAuthorizationHeaders() }
    );
  }

  // Fetch products from the API
  getProducts(): Observable<any[]> {


    return this.http.get<any[]>(
      this.baseUrl,
      { headers: this.createAuthorizationHeaders() }
    );
  }

  getProductStocks(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/stock`,
      { headers: this.createAuthorizationHeaders() }
    );
  }

  // Fetch products from the API
  getTopProducts(): Observable<TopProductsResponse> {


    return this.http.get<TopProductsResponse>(
      this.topUrl,
      { headers: this.createAuthorizationHeaders() }
    );
  }

  // Fetch products from the API
  getSingleProduct(productId: number): Observable<any[]> {

    const url = `${this.baseUrl}/${productId}`;
    return this.http.get<any[]>(url,
      { headers: this.createAuthorizationHeaders() }
    );
  }

    // New method to generate a PDF receipt
    generatePrintabProductList(): Observable<Blob> {
      const url = `${this.pdfUrl}/products`; // Adjust this endpoint to return PDF
      return this.http.get(url, { responseType: 'blob', headers: this.createAuthorizationHeaders() });
    }
  // Create authorization headers
  private createAuthorizationHeaders(): HttpHeaders {
    const token = this.tokenService.getToken;
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }
}
