import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'catalogo',
    loadComponent: () => import('./pages/catalogo/catalogo.component').then(m => m.CatalogoComponent),
    canActivate: [authGuard]
  },
  {
    path: 'crear-pedido',
    loadComponent: () => import('./pages/crear-pedido/crear-pedido.component').then(m => m.CrearPedidoComponent),
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: 'login' }
];
