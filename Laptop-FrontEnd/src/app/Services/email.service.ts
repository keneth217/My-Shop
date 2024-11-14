import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenService } from './token.service';

@Injectable({
  providedIn: 'root'
})
export class EmailService {
  private apiUrl = 'http://localhost:9091/api/email/send';

  constructor(private http: HttpClient,private tokenService: TokenService)  {}
  sendEmail(emailData: any): Observable<any> {
    return this.http.post(this.apiUrl, emailData, {
      headers: this.createAuthorizationHeaders(),
      responseType: 'text' as 'json'  // Handle response as plain text
    });
}
   // Create authorization headers
   private createAuthorizationHeaders(): HttpHeaders {
    const token = this.tokenService.getToken;
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }
}
