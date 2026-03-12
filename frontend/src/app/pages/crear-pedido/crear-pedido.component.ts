import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-crear-pedido',
  imports: [CommonModule, FormsModule],
  templateUrl: './crear-pedido.component.html',
  styleUrl: './crear-pedido.component.css'
})
export class CrearPedidoComponent implements OnInit {
  productos: any[] = [];
  productId: number | null = null;
  quantity: number = 1;
  loading = false;
  loadingProductos = true;
  exito = false;
  error = '';
  pedidoCreado: any = null;

  constructor(private productService: ProductService, private orderService: OrderService, private router: Router) {}

  ngOnInit() {
    this.productService.getProducts().subscribe({
      next: (data) => { this.productos = data; this.loadingProductos = false; },
      error: () => { this.loadingProductos = false; }
    });
  }

  crearPedido() {
    if (!this.productId || this.quantity < 1) return;
    this.error = '';
    this.exito = false;
    this.loading = true;
    this.orderService.crearPedido(this.productId, this.quantity).subscribe({
      next: (data) => { this.loading = false; this.exito = true; this.pedidoCreado = data; },
      error: (err) => {
        this.loading = false;
        if (err.status === 409) {
          this.error = 'âš ï¸ No hay stock suficiente para completar este pedido. Intenta con una cantidad menor.';
        } else {
          this.error = 'Ocurrio un error al crear el pedido. Intenta nuevamente.';
        }
      }
    });
  }

  volver() { this.router.navigate(['/catalogo']); }
  nuevoPedido() { this.exito = false; this.pedidoCreado = null; this.error = ''; this.productId = null; this.quantity = 1; }
}
