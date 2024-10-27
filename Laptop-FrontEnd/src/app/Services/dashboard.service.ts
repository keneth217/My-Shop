import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private baseUrl = 'http://localhost:9091/api/dashboard';

  constructor(private http: HttpClient, private tokenService: TokenService) { }

  

  // Fetch products from the API
  getdashbord(): Observable<any[]> {
    return this.http.get<any[]>(
      this.baseUrl,
      { headers: this.createAuthorizationHeaders() }
    );
  }

  

  // Create authorization headers
  private createAuthorizationHeaders(): HttpHeaders {
    const token = this.tokenService.getToken;
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }
}
