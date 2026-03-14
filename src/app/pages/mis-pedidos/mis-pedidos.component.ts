import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-mis-pedidos',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './mis-pedidos.component.html',
  styleUrls: ['./mis-pedidos.component.css']
})
export class MisPedidosComponent implements OnInit {

  orders: any[] = [];
  isLoading = true;
  errorMessage = '';
  successMessage = '';
  cancellingId: number | null = null;
  selectedOrder: any | null = null;

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getUserId();
    if (!userId) {
      this.router.navigate(['/login']);
      return;
    }
    this.cargarPedidos(userId);
  }

  cargarPedidos(userId: number): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.orderService.getOrdersByUserId(userId).subscribe({
      next: (data) => {
        this.orders = [...data];
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('[MisPedidos] Error:', err);
        this.errorMessage = 'No se pudieron cargar los pedidos. Intenta de nuevo.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  cancelarPedido(order: any): void {
    if (order.status !== 'CREATED') return;
    this.cancellingId = order.id;
    this.errorMessage = '';
    this.successMessage = '';

    this.orderService.cancelOrder(order.id).subscribe({
      next: (updated) => {
        const idx = this.orders.findIndex(o => o.id === updated.id);
        if (idx !== -1) {
          this.orders[idx] = updated;
          this.orders = [...this.orders];
        }
        if (this.selectedOrder?.id === updated.id) this.selectedOrder = updated;
        this.cancellingId = null;
        this.successMessage = 'Pedido #' + updated.id + ' cancelado correctamente.';
        this.cdr.detectChanges();
        setTimeout(() => { this.successMessage = ''; this.cdr.detectChanges(); }, 4000);
      },
      error: (err) => {
        this.cancellingId = null;
        if (err.status === 409 || err.status === 400) {
          this.errorMessage = 'Este pedido ya fue cancelado anteriormente.';
        } else if (err.status === 404) {
          this.errorMessage = 'Pedido no encontrado.';
        } else {
          this.errorMessage = 'Error al cancelar el pedido. Intenta de nuevo.';
        }
        this.cdr.detectChanges();
      }
    });
  }

  verDetalle(order: any): void {
    this.selectedOrder = this.selectedOrder?.id === order.id ? null : order;
    this.cdr.detectChanges();
  }

  puedeCancel(order: any): boolean {
    return order.status === 'CREATED';
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      CREATED: 'Pendiente',
      CONFIRMED: 'Confirmado',
      SHIPPED: 'Enviado',
      DELIVERED: 'Entregado',
      CANCELLED: 'Cancelado'
    };
    return map[status] ?? status;
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      CREATED: 'badge-pending',
      CONFIRMED: 'badge-confirmed',
      SHIPPED: 'badge-shipped',
      DELIVERED: 'badge-delivered',
      CANCELLED: 'badge-cancelled'
    };
    return map[status] ?? '';
  }
}