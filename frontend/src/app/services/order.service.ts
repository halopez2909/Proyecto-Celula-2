import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  crearPedido(productId: number, quantity: number): Observable<any> {
    return this.http.post<any>(this.apiUrl + '/orders/orders', {
      userId: 1,
      items: [{ productId, quantity }]
    });
  }
}
