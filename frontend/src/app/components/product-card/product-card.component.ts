import { Component, input } from '@angular/core';
import { Product } from '../../models/product';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.css'
})
export class ProductCardComponent {
  /** Producto a mostrar en la tarjeta */
  product = input.required<Product>();
}
