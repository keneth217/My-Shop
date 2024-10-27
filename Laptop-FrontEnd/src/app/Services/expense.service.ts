import { Injectable } from '@angular/core';
import { TokenService } from './token.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ExpenseService {

  private apiUrl = 'http://localhost:9091/api/expense';

  constructor(private http: HttpClient,private tokenService: TokenService)  {}

  addExpense(expenses: any): Observable<any> {
    return this.http.post( `${this.apiUrl}/add`, expenses,
      { headers: this.createAuthorizationHeaders() }
    );
  }

  // Fetch products from the API
  getPExpenses(): Observable<any[]> {
    return this.http.get<any[]>(
      this.apiUrl,
      { headers: this.createAuthorizationHeaders() }
    );
  }

   // Fetch products from the API
   getPExpensesTotals(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.apiUrl}/totals`,
      { headers: this.createAuthorizationHeaders() }
    );
  }

   // Create authorization headers
   private createAuthorizationHeaders(): HttpHeaders {
    const token = this.tokenService.getToken;
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }
}
