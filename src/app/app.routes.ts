import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent) },
  {
    path: 'catalog',
    loadComponent: () => import('./pages/catalogo/catalogo.component').then(m => m.CatalogoComponent)
  },
  { path: 'catalogo', redirectTo: 'catalog', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'orders',
    loadComponent: () => import('./pages/mis-pedidos/mis-pedidos.component').then(m => m.MisPedidosComponent),
    canActivate: [authGuard]
  },
  { path: 'mis-pedidos', redirectTo: 'orders', pathMatch: 'full' },
  {
    path: 'crear-pedido',
    loadComponent: () => import('./pages/crear-pedido/crear-pedido.component').then(m => m.CrearPedidoComponent),
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: '' }
];
