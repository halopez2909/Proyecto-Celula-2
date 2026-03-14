import { Product } from '../models/product';

/**
 * Datos mock de relojes para desarrollo y cuando la API no esté disponible.
 */
export const MOCK_PRODUCTS: Product[] = [
  {
    id: 1,
    name: 'Rolex Submariner',
    price: 12000,
    stock: 5,
    image: 'https://images.unsplash.com/photo-1587836374828-4dbafa94cf0e?w=400',
    description: 'Diseño clásico, cronógrafo automático'
  },
  {
    id: 2,
    name: 'Omega Seamaster',
    price: 5800,
    stock: 12,
    image: 'https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=400',
    description: 'Elegante reloj .'
  },
  {
    id: 3,
    name: 'Tag Heuer Monaco',
    price: 6200,
    stock: 3,
    image: 'https://images.unsplash.com/photo-1542496658-e33a6d0d50f6?w=400',
    description: 'Diseño clásico, cronógrafo automático.'
  },
  {
    id: 4,
    name: 'Casio G-Shock',
    price: 180,
    stock: 45,
    image: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400',
    description: 'Resistente a golpes y agua. Ideal para deporte.'
  },
  {
    id: 5,
    name: 'Seiko Presage',
    price: 420,
    stock: 22,
    image: 'https://images.unsplash.com/photo-1614164185128-e4ec99c436d7?w=400',
    description: 'Caja de acero, esfera con textura esmaltada.'
  },
  {
    id: 6,
    name: 'Tissot Le Locle',
    price: 550,
    stock: 8,
    image: 'https://images.unsplash.com/photo-1587836374828-4dbafa94cf0e?w=400',
    description: 'Reloj clásico suizo de vestir.'
  },
  {
    id: 7,
    name: 'Hamilton Khaki Field',
    price: 495,
    stock: 15,
    image: 'https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=400',
    description: 'Estilo clasico, robusto y legible.'
  },
  {
    id: 8,
    name: 'Citizen Eco-Drive',
    price: 320,
    stock: 30,
    image: 'https://images.unsplash.com/photo-1542496658-e33a6d0d50f6?w=400',
    description: 'Energía solar, sin necesidad de pila.'
  }
];
