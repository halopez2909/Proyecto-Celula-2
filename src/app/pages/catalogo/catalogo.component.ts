import { Component, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product';
import { ProductCardComponent } from '../../components/product-card/product-card.component';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CommonModule, FormsModule, ProductCardComponent],
  templateUrl: './catalogo.component.html',
  styleUrl: './catalogo.component.css'
})
export class CatalogoComponent implements OnInit {
  products = signal<Product[]>([]);
  loading = signal(true);
  error = signal('');
  searchTerm = signal('');
  priceMin = signal<number | null>(null);
  priceMax = signal<number | null>(null);

  filteredProducts = computed(() => {
    const list = this.products();
    const term = this.searchTerm().trim().toLowerCase();
    const min = this.priceMin();
    const max = this.priceMax();
    return list.filter((p) => {
      const matchName = !term || p.name.toLowerCase().includes(term);
      const price = p.price;
      const matchMin = min == null || price >= min;
      const matchMax = max == null || price <= max;
      return matchName && matchMin && matchMax;
    });
  });

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.productService.getProducts().subscribe({
      next: (data) => {
        this.products.set(data);
        this.loading.set(false);
        this.error.set('');
      },
      error: () => {
        this.error.set('No se pudieron cargar los productos.');
        this.loading.set(false);
      }
    });
  }

  onSearch(value: string): void {
    this.searchTerm.set(value);
  }

  onPriceMin(value: string): void {
    const n = value === '' ? null : Number(value);
    this.priceMin.set(n === null || !Number.isNaN(n) ? n : null);
  }

  onPriceMax(value: string): void {
    const n = value === '' ? null : Number(value);
    this.priceMax.set(n === null || !Number.isNaN(n) ? n : null);
  }
}
