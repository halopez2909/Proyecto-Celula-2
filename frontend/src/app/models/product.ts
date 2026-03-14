export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  sku: string;
  stock: number;
  image?: string;
  createdAt?: string;
  updatedAt?: string;
}