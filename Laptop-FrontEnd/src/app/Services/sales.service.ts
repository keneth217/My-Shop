import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class SalesService {
  private apiUrl = 'http://localhost:9091/api/business';
  private baseUrl = 'http://localhost:9091/api/sales';

  constructor(private http: HttpClient, private tokenService: TokenService) {}

  // Fetch sales with optional date filters
  getSales(startDate?: string, endDate?: string): Observable<any[]> {
    let params: any = {};
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    return this.http.get<any[]>(`${this.apiUrl}/sales`, {
      headers: this.createAuthorizationHeaders(),
      params,
    });
  }

  addCart(productId: number, quantity: number): Observable<any[]> {
    // Construct URL with query parameters
    const params = new HttpParams()
        .set('productId', productId.toString())
        .set('quantity', quantity.toString());

    return this.http.post<any[]>(`${this.baseUrl}/add`, {}, { // Send an empty body
        headers: this.createAuthorizationHeaders(),
        params: params
    });
}

  // Create a stock purchase with productId, supplierId, and StockPurchase data
  createSale(productId: number, supplierId: number, stockPurchase: any): Observable<any> {
    const url = `${this.apiUrl}/stock-purchases/${productId}/${supplierId}`;
    return this.http.post<any>(url, stockPurchase, {
      headers: this.createAuthorizationHeaders(),
    });
  }

  // Create authorization headers
  private createAuthorizationHeaders(): HttpHeaders {
    const token = this.tokenService.getToken;
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }
}