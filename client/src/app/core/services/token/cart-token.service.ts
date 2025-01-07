import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class CartTokenService {

  constructor(
    private router: Router
  ) { }

  setToken(token: string): void {
    localStorage.setItem('cart-token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('cart-token');
  }

  logout() {
    localStorage.removeItem('token');
  }
}