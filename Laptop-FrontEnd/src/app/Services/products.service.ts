import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenService } from './token.service';


@Injectable({
  providedIn: 'root'
})
export class ProductsService {

  private baseUrl = 'http://localhost:9091/api/admin/products';

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

  // Fetch products from the API
  getSingleProduct(productId:number): Observable<any[]> {

    const url = `${this.baseUrl}/${productId}`;
    return this.http.get<any[]>(url,
      { headers: this.createAuthorizationHeaders() }
    );
  }

  // Create authorization headers
  private createAuthorizationHeaders(): HttpHeaders {
    const token = this.tokenService.getToken;
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }
}
