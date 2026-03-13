import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './catalogo.component.html',
  styleUrl: './catalogo.component.css'
})
export class CatalogoComponent implements OnInit {
  productos: any[] = [];
  loading = true;
  error = '';

  constructor(private http: HttpClient, private router: Router, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({ Authorization: 'Bearer ' + token });
    this.http.get<any[]>('http://localhost:8080/catalog/products', { headers }).subscribe({
      next: (data) => {
        console.log('Productos recibidos:', data);
        this.productos = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error:', err);
        this.error = 'No se pudieron cargar los productos.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  irACrearPedido() { this.router.navigate(['/crear-pedido']); }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}