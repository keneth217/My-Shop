import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CartResponse } from '../model/cartResponse';
import { TokenService } from './token.service';
import { ShopData } from '../model/shop.model';

@Injectable({
  providedIn: 'root'
})
export class SuperService {

 
  private baseUrl = 'http://localhost:9091/api/shops';

  constructor(private http: HttpClient, private tokenService: TokenService) { }



  addShop(shopdata:ShopData): Observable<ShopData> {
    const url = `${this.baseUrl}/register`;
    return this.http.post<ShopData>(url, shopdata, {
      headers: this.createAuthorizationHeaders(),

    });
  }

  

  fetchShops(): Observable<any[]> {
    const url = `${this.baseUrl}`;
    return this.http.get<any[]>(url, {
      headers: this.createAuthorizationHeaders(),

    });
  }
  activateShop(shopId:number,shopData:any): Observable<any[]> {
    const url = `${this.baseUrl}/${shopId}/activate`;
    return this.http.post<any[]>(url,shopData, {
      headers: this.createAuthorizationHeaders(),

    });
  }
  deactivateShop(shopId: number): Observable<any[]> {
    const url = `${this.baseUrl}/${shopId}/deactivate`;
    return this.http.put<any[]>(url, {}, { 
      headers: this.createAuthorizationHeaders()
    });
  }

  // Create a stock purchase with productId, supplierId, and StockPurchase data
  createSale(productId: number, supplierId: number, stockPurchase: any): Observable<any> {
    const url = `${this.baseUrl}/stock-purchases/${productId}/${supplierId}`;
    return this.http.post<any>(url, stockPurchase, {
      headers: this.createAuthorizationHeaders(),
    });
  }
 // Create authorization headers
  private createAuthorizationHeaders(): HttpHeaders {
    const token = this.tokenService.getToken;
    let headers = new HttpHeaders();
    console.log(headers)
    console.log(token)
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
  
    return headers;
  }

 
 
}


