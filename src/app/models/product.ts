/**
 * Modelo de producto para la tienda de relojes.
 */
export interface Product {
  id: number;
  name: string;
  price: number;
  stock: number;
  image: string;
  description?: string;
  sku?: string;
}
