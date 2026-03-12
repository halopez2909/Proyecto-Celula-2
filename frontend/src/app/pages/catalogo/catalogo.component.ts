import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-catalogo',
  imports: [CommonModule],
  templateUrl: './catalogo.component.html',
  styleUrl: './catalogo.component.css'
})
export class CatalogoComponent implements OnInit {
  productos: any[] = [];
  loading = true;
  error = '';

  constructor(private productService: ProductService, private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.productService.getProducts().subscribe({
      next: (data) => { this.productos = data; this.loading = false; },
      error: () => { this.error = 'No se pudieron cargar los productos.'; this.loading = false; }
    });
  }

  irACrearPedido() { this.router.navigate(['/crear-pedido']); }
  logout() { this.authService.logout(); this.router.navigate(['/login']); }
}
