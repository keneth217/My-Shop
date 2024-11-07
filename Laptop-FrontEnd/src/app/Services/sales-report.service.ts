import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SalesResponse } from '../model/sales.model';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class SalesReportService {
  private apiUrl = 'http://localhost:9091/api/business';

  constructor(private http: HttpClient, private tokenService: TokenService) {}

  // Fetch sales by daily range
  getDailyReports(year: number, month: number, startDate?: string, endDate?: string): Observable<SalesResponse> {
    let params: any = {};
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    params.year = year;
    params.month = month;

    return this.http.get<SalesResponse>(`${this.apiUrl}/daily-sales`, {
      headers: this.createAuthorizationHeaders(),
      params,
    });
  }

  // Fetch sales by month and year
  getMonthlyReports(year: number, month: number): Observable<SalesResponse> {
    let params: any = {
      year: year,
      month: month
    };

    return this.http.get<SalesResponse>(`${this.apiUrl}/monthly-sales`, {
      headers: this.createAuthorizationHeaders(),
      params,
    });
  }

  // Create authorization headers
  private createAuthorizationHeaders(): HttpHeaders {
    const token = this.tokenService.getToken;
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }
}
