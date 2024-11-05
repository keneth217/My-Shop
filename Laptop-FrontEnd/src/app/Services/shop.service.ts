import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class ShopService {
  private apiUrl = 'http://localhost:9091/api/shop';

  constructor(private http: HttpClient,private tokenService: TokenService) {}

  getShopDetails(): Observable<any> {
    return this.http.get(`${this.apiUrl}/my-shop`, { headers: this.createAuthorizationHeaders() }
  );
  }

  updateShopDetails(data: FormData): Observable<any> {
    return this.http.put(`${this.apiUrl}/update`, data, { headers: this.createAuthorizationHeaders() });
}

  
   // Create authorization headers
   private createAuthorizationHeaders(): HttpHeaders {
    const token = this.tokenService.getToken;
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }
}
