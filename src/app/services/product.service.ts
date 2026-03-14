import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, map, catchError } from 'rxjs';
import { Product } from '../models/product';
import { MOCK_PRODUCTS } from '../data/mock-products';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  /**
   * Obtiene todos los productos. Si la API falla, usa datos mock.
   */
  getProducts(): Observable<Product[]> {
    return this.http.get<any[]>(this.apiUrl + '/catalog/products').pipe(
      map((list) => this.normalizeProducts(list)),
      catchError(() => of(MOCK_PRODUCTS))
    );
  }

  /**
   * Productos destacados para la home (primeros 4 del catálogo).
   */
  getFeaturedProducts(): Observable<Product[]> {
    return this.getProducts().pipe(
      map((products) => products.slice(0, 4))
    );
  }

  /**
   * Convierte la respuesta de la API al modelo Product (por si viene con otros campos).
   */
  private normalizeProducts(list: any[]): Product[] {
    return (list || []).map((item) => ({
      id: item.id ?? item.productId ?? 0,
      name: item.name ?? item.productName ?? '',
      price: Number(item.price) ?? 0,
      stock: Number(item.stock) ?? 0,
      image: item.image ?? 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400',
      description: item.description,
      sku: item.sku
    }));
  }
}
