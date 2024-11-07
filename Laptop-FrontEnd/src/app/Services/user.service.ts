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

  changeRole(username: string, userRoleUpdateDto: { role: string }): Observable<any> {
    return this.http.patch<any>(`${this.apiUrl}/${username}/role`, userRoleUpdateDto, {
      headers: this.createAuthorizationHeaders(),
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
