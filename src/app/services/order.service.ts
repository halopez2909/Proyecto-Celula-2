import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  crearPedido(productId: number, quantity: number): Observable<any> {
    return this.http.post<any>(this.apiUrl + '/orders', {
      userId: 1,
      items: [{ productId, quantity }]
    });
  }

  getOrdersByUserId(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/orders?userId=${userId}`);
  }

  cancelOrder(orderId: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/orders/${orderId}/cancel`, {});
  }
}