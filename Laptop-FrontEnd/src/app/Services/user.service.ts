import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TokenService } from './token.service';
import { UserUpdateRequestDto } from '../model/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUrl = 'http://localhost:9091/api/v1/auth';

  constructor(private http: HttpClient, private tokenService: TokenService) {}
  // Get user details
  getUsersForShop(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/shop-users`, {
      headers: this.createAuthorizationHeaders()
    });
  }
  // Get user details
  getUserDetails(): Observable<UserUpdateRequestDto> {
    return this.http.get<UserUpdateRequestDto>(`${this.apiUrl}/me`, {
      headers: this.createAuthorizationHeaders()
    });
  }

  adduser(userData:any): Observable<UserUpdateRequestDto> {
    return this.http.post<UserUpdateRequestDto>(`${this.apiUrl}/create-user`,userData, {
      headers: this.createAuthorizationHeaders()
    });
  }

  changeRole(username: string, newRole: string): Observable<any> {
    // Make sure apiUrl is set to 'http://localhost:9091/api/v1/auth'
    const url = 'http://localhost:9091/api/v1/auth'; 
  
    // Use the correct URL structure and send the 'role' as a JSON body
    return this.http.put<any>(`${url}/${username}/role`, { role: newRole }, {
      headers: this.createAuthorizationHeaders()
    });
  }


  // Update user details
  updateUserDetails(data: FormData): Observable<UserUpdateRequestDto> {
    return this.http.put<UserUpdateRequestDto>(`${this.apiUrl}/me`, data, {
      headers: this.createAuthorizationHeaders()
    });
  }

  // Create authorization headers with Bearer token
  private createAuthorizationHeaders(): HttpHeaders {
    const token = this.tokenService.getToken; // Corrected: getToken is a function
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }
}
