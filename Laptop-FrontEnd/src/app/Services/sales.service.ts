import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenService } from './token.service';
import { SalesResponse } from '../model/sales.model';


interface CartResponse {
  items: Array<{
    id: number;
    itemCosts: number;
    productName: string | null;
    quantity: number;
    shopCode: string;
    status: string;
  }>;
  total: number;
}


@Injectable({
  providedIn: 'root'
})
export class SalesService {
  private apiUrl = 'http://localhost:9091/api/business';
  private baseUrl = 'http://localhost:9091/api/sales';

  constructor(private http: HttpClient, private tokenService: TokenService) { }

  // Fetch sales with optional date filters
  getSales(startDate?: string, endDate?: string): Observable<SalesResponse> {
    let params: any = {};
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    return this.http.get<SalesResponse>(`${this.apiUrl}/sales`, {
      headers: this.createAuthorizationHeaders(),
      params,
    });
  }

  addCart(productId: number, quantity: number): Observable<any[]> {
    const url = `${this.baseUrl}/add/${productId}`;
    return this.http.post<any[]>(url, quantity, {
      headers: this.createAuthorizationHeaders(),

    });
  }

  checkOut(customerData:any): Observable<any[]> {
    const url = `${this.baseUrl}/checkout`;
    return this.http.post<any[]>(url, customerData, {
      headers: this.createAuthorizationHeaders(),

    });
  }

  fetchCartItems(): Observable<CartResponse> {
    const url = `${this.baseUrl}/items`;
    return this.http.get<CartResponse>(url, {
      headers: this.createAuthorizationHeaders(),

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
