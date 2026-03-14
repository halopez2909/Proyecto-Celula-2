import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './catalogo.component.html',
  styleUrl: './catalogo.component.css'
})
export class CatalogoComponent implements OnInit {
  productos: any[] = [];
  productosFiltrados: any[] = [];
  loading = true;
  error = '';
  busqueda = '';
  precioMin: number | null = null;
  precioMax: number | null = null;

  private imagenesRelojes: Record<string, string> = {
    'rolex submariner':  'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400',
    'rolex daytona':     'https://images.unsplash.com/photo-1614164185128-e4ec99c436d7?w=400',
    'omega':             'https://images.unsplash.com/photo-1548171915-e79a380a2a4b?w=400',
    'tag heuer':         'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400',
    'casio':             'https://images.unsplash.com/photo-1614164185128-e4ec99c436d7?w=400',
    'seiko':             'https://images.unsplash.com/photo-1548171915-e79a380a2a4b?w=400',
    'patek':             'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400',
    'audemars':          'https://images.unsplash.com/photo-1614164185128-e4ec99c436d7?w=400',
    'iwc':               'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400',
    'jaeger':            'https://images.unsplash.com/photo-1548171915-e79a380a2a4b?w=400',
    'vacheron':          'https://images.unsplash.com/photo-1614164185128-e4ec99c436d7?w=400',
    'breguet':           'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400',
    'lange':             'https://images.unsplash.com/photo-1548171915-e79a380a2a4b?w=400',
    'panerai':           'https://images.unsplash.com/photo-1614164185128-e4ec99c436d7?w=400',
    'cartier':           'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400',
    'breitling':         'https://images.unsplash.com/photo-1548171915-e79a380a2a4b?w=400',
    'hublot':            'https://images.unsplash.com/photo-1614164185128-e4ec99c436d7?w=400',
    'tudor':             'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400',
    'longines':          'https://images.unsplash.com/photo-1548171915-e79a380a2a4b?w=400',
    'zenith':            'https://images.unsplash.com/photo-1614164185128-e4ec99c436d7?w=400',
    'grand seiko':       'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400',
    'chopard':           'https://images.unsplash.com/photo-1548171915-e79a380a2a4b?w=400',
    'girard':            'https://images.unsplash.com/photo-1614164185128-e4ec99c436d7?w=400',
    'ulysse':            'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400',
    'fp journe':         'https://images.unsplash.com/photo-1548171915-e79a380a2a4b?w=400'
  };

  constructor(
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  getImagen(nombre: string): string {
    const nombreLower = nombre.toLowerCase();
    const key = Object.keys(this.imagenesRelojes)
      .find(k => nombreLower.includes(k));
    return key
      ? this.imagenesRelojes[key]
      : 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400';
  }

  ngOnInit() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ Authorization: 'Bearer ' + token });
    this.http.get<any[]>('http://localhost:8080/catalog/products', { headers }).subscribe({
      next: (data) => {
        this.productos = data;
        this.productosFiltrados = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'No se pudieron cargar los relojes.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  filtrar() {
    this.productosFiltrados = this.productos.filter(p => {
      const coincideNombre = p.name.toLowerCase().includes(this.busqueda.toLowerCase());
      const coincideMin = this.precioMin === null || p.price >= this.precioMin;
      const coincideMax = this.precioMax === null || p.price <= this.precioMax;
      return coincideNombre && coincideMin && coincideMax;
    });
    this.cdr.detectChanges();
  }

  limpiarFiltros() {
    this.busqueda = '';
    this.precioMin = null;
    this.precioMax = null;
    this.productosFiltrados = this.productos;
    this.cdr.detectChanges();
  }

  irACrearPedido() { this.router.navigate(['/crear-pedido']); }
}