import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class InvestService {

  private apiUrl = 'http://localhost:9091/api/invest';

  constructor(private http: HttpClient,private tokenService: TokenService)  {}

  addInvestement(investement: any): Observable<any> {
    return this.http.post(this.apiUrl, investement,
      { headers: this.createAuthorizationHeaders() }
    );
  }

  // Fetch products from the API
  getInvestements(): Observable<any[]> {
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
