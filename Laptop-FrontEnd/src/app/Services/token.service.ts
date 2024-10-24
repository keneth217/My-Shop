import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  isAuthentication: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor() {
    const token = this.getToken;
    if (token) {
      this.updateToken(true);
    }
  }

  private localStorageAvailable(): boolean {
    try {
      const testKey = '__test__';
      localStorage.setItem(testKey, testKey);
      localStorage.removeItem(testKey);
      return true;
    } catch (e) {
      return false;
    }
  }

  updateToken(status: boolean) {
    this.isAuthentication.next(status);
  }

  set saveToken(token: string) {
    this.updateToken(true);
    if (this.localStorageAvailable()) {
      localStorage.setItem('token', token);
    } else {
      console.warn('LocalStorage is not available');
    }
  }

  get getToken(): string | null {
    if (this.localStorageAvailable()) {
      return localStorage.getItem('token');
    } else {
      console.warn('LocalStorage is not available');
      return null;
    }
  }

  set saveUser(user: any) {
    if (this.localStorageAvailable()) {
      localStorage.setItem('user', JSON.stringify(user));
    } else {
      console.warn('LocalStorage is not available');
    }
  }

  get getUser(): any {
    if (this.localStorageAvailable()) {
      const userData = localStorage.getItem('user');
      return userData ? JSON.parse(userData) : null;
    } else {
      console.warn('LocalStorage is not available');
      return null;
    }
  }

  get getUserRole(): string {
    try {
      const user = this.getUser;
      return user?.role || ''; 
      // Use optional chaining for safety
       console.error(' user role:', user.role);
    } catch (error) {
      console.error('Error fetching user role:', error);
      return 'error'; // Return a meaningful fallback value if needed
    }
  }

  get getUserId(): string {
    try {
      const user = this.getUser;
      return user ? user.id : '';
    } catch (error) {
      console.error(error);
      return 'error';
    }
  }

  get isadmin(): boolean {
    try {
      const role: string = this.getUserRole;
      return role === 'ADMIN';
    } catch (error) {
      return false;
    }
  }

  get isCashier(): boolean {
    try {
      const role: string = this.getUserRole;
      return role === 'CASHIER';
    } catch (error) {
      console.error(error);
      return false;
    }
  }

  removeToken() {
    this.updateToken(false);
    if (this.localStorageAvailable()) {
      localStorage.removeItem('token');
      localStorage.removeItem('user')
    } else {
      console.warn('LocalStorage is not available');
    }
  }
}
