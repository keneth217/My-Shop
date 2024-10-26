import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class SupplierService {

  private apiUrl = 'http://localhost:9091/api/supplier';

  constructor(private http: HttpClient,private tokenService: TokenService)  {}

  addSupplier(supplier: any): Observable<any> {
    return this.http.post(this.apiUrl, supplier,
      { headers: this.createAuthorizationHeaders() }
    );
  }
  // Fetch products from the API
  getSuppliers(): Observable<any[]> {
    return this.http.get<any[]>(
      this.apiUrl,
      { headers: this.createAuthorizationHeaders() }
    );
  }

   // Create authorization headers
   private createAuthorizationHeaders(): HttpHeaders {
    const token = this.tokenService.getToken;
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }
}
