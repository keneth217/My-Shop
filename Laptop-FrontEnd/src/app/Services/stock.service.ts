import { Injectable } from '@angular/core';
import { TokenService } from './token.service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StockService {



  private pdfUrl = 'http://localhost:9091/api/pdf/reports';
  private apiUrl = 'http://localhost:9091/api/business';
  constructor(private http: HttpClient, private tokenService: TokenService) { }
  // Fetch sales with optional date filters
  getStockList(startDate?: string, endDate?: string): Observable<any[]> {
    let params: any = {};
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;

    return this.http.get<any[]>(`${this.apiUrl}/stock-purchases`, {
      headers: this.createAuthorizationHeaders(),
      params,
    });
  }

  getStockSupplierList(startDate?: string, endDate?: string, supplierId?: number): Observable<any[]> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    if (supplierId) params = params.set('supplierId', supplierId); 

    return this.http.get<any[]>(`${this.apiUrl}/by-supplier`, {
      headers: this.createAuthorizationHeaders(),
      params,
    });
  }

  // New method to generate a PDF receipt
  generatePrintableStockList(startDate?: string, endDate?: string): Observable<Blob> {
    let params: any = {};
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;




    const url = `${this.pdfUrl}/stock-purchases`; // Adjust this endpoint to return PDF
    return this.http.get(url, { responseType: 'blob', headers: this.createAuthorizationHeaders(), params, });
  }

  // Create authorization headers
  private createAuthorizationHeaders(): HttpHeaders {
    const token = this.tokenService.getToken;
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }
}
