import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-crear-pedido',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './crear-pedido.component.html',
  styleUrl: './crear-pedido.component.css'
})
export class CrearPedidoComponent implements OnInit {
  productos: any[] = [];
  productId: number | null = null;
  quantity: number = 1;
  loading = false;
  exito = false;
  error = '';
  pedidoCreado: any = null;
  private apiUrl = 'http://localhost:8080';

  constructor(
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({ Authorization: 'Bearer ' + token });
  }

  private getUserId(): number {
    const token = localStorage.getItem('token');
    if (!token) return 0;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.userId ?? payload.id ?? 0;
    } catch {
      return 0;
    }
  }

  ngOnInit() {
    this.http.get<any[]>(this.apiUrl + '/catalog/products', {
      headers: this.getHeaders()
    }).subscribe({
      next: (data) => { this.productos = data; this.cdr.detectChanges(); },
      error: (err) => { console.error('Error cargando productos:', err); }
    });
  }

  crearPedido() {
    if (!this.productId || this.quantity < 1) return;
    this.error = '';
    this.exito = false;
    this.loading = true;

    const userId = this.getUserId();

    this.http.post<any>(this.apiUrl + '/orders', {
      userId: userId,
      items: [{ productId: this.productId, quantity: this.quantity }]
    }, { headers: this.getHeaders() }).subscribe({
      next: (data) => {
        this.loading = false;
        this.exito = true;
        this.pedidoCreado = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 409) {
          this.error = 'No hay stock suficiente para este pedido.';
        } else {
          this.error = 'Error al crear el pedido. Intenta nuevamente.';
        }
        this.cdr.detectChanges();
      }
    });
  }

  verMisPedidos() { this.router.navigate(['/mis-pedidos']); }
  volver() { this.router.navigate(['/catalogo']); }
  nuevoPedido() {
    this.exito = false;
    this.pedidoCreado = null;
    this.error = '';
    this.productId = null;
    this.quantity = 1;
  }
}