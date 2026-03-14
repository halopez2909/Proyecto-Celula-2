# Script para copiar todos los archivos del frontend
$base = "C:\Users\bauti\Downloads\Proyecto-Celula-2\frontend\src\app"

# Crear carpetas necesarias
New-Item -ItemType Directory -Force -Path "$base\services" | Out-Null
New-Item -ItemType Directory -Force -Path "$base\interceptors" | Out-Null
New-Item -ItemType Directory -Force -Path "$base\guards" | Out-Null
New-Item -ItemType Directory -Force -Path "$base\pages\login" | Out-Null
New-Item -ItemType Directory -Force -Path "$base\pages\catalogo" | Out-Null
New-Item -ItemType Directory -Force -Path "$base\pages\crear-pedido" | Out-Null

Write-Host "Carpetas creadas correctamente" -ForegroundColor Green

# ===================== STYLES.CSS =====================
@"
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  background-color: #f0f2f5;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
"@ | Set-Content "C:\Users\bauti\Downloads\Proyecto-Celula-2\frontend\src\styles.css" -Encoding UTF8

# ===================== APP.TS =====================
@"
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  template: '<router-outlet />',
  styles: []
})
export class App {}
"@ | Set-Content "$base\app.ts" -Encoding UTF8

# ===================== APP.CONFIG.TS =====================
@"
import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
};
"@ | Set-Content "$base\app.config.ts" -Encoding UTF8

# ===================== APP.ROUTES.TS =====================
@"
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
"@ | Set-Content "$base\app.routes.ts" -Encoding UTF8

# ===================== AUTH.SERVICE.TS =====================
@"
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(`\${this.apiUrl}/auth/login`, { email, password }).pipe(
      tap(response => {
        if (response.token) {
          localStorage.setItem('token', response.token);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
"@ | Set-Content "$base\services\auth.service.ts" -Encoding UTF8

# ===================== PRODUCT.SERVICE.TS =====================
@"
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  getProducts(): Observable<any[]> {
    return this.http.get<any[]>(`\${this.apiUrl}/products`);
  }
}
"@ | Set-Content "$base\services\product.service.ts" -Encoding UTF8

# ===================== ORDER.SERVICE.TS =====================
@"
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  crearPedido(productId: number, quantity: number): Observable<any> {
    return this.http.post<any>(`\${this.apiUrl}/orders`, {
      userId: 1,
      items: [{ productId, quantity }]
    });
  }
}
"@ | Set-Content "$base\services\order.service.ts" -Encoding UTF8

# ===================== AUTH.INTERCEPTOR.TS =====================
@"
import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  if (token) {
    const cloned = req.clone({
      setHeaders: { Authorization: `Bearer \${token}` }
    });
    return next(cloned);
  }
  return next(req);
};
"@ | Set-Content "$base\interceptors\auth.interceptor.ts" -Encoding UTF8

# ===================== AUTH.GUARD.TS =====================
@"
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.isLoggedIn()) return true;
  router.navigate(['/login']);
  return false;
};
"@ | Set-Content "$base\guards\auth.guard.ts" -Encoding UTF8

# ===================== LOGIN COMPONENT =====================
@"
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  login() {
    this.error = '';
    this.loading = true;
    this.authService.login(this.email, this.password).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/catalogo']);
      },
      error: () => {
        this.loading = false;
        this.error = 'Credenciales incorrectas. Verifica tu email y contrasena.';
      }
    });
  }
}
"@ | Set-Content "$base\pages\login\login.component.ts" -Encoding UTF8

@"
<div class="login-wrapper">
  <div class="login-card">
    <div class="login-header">
      <div class="logo">🛒</div>
      <h1>Microservicios</h1>
      <p>Ingresa tus credenciales para continuar</p>
    </div>
    <form (ngSubmit)="login()">
      <div class="form-group">
        <label>Email</label>
        <input type="email" [(ngModel)]="email" name="email" placeholder="correo@ejemplo.com" required class="form-control" />
      </div>
      <div class="form-group">
        <label>Contrasena</label>
        <input type="password" [(ngModel)]="password" name="password" placeholder="••••••••" required class="form-control" />
      </div>
      <div class="alert alert-danger" *ngIf="error">⚠️ {{ error }}</div>
      <button type="submit" class="btn-login" [disabled]="loading">
        <span *ngIf="!loading">Iniciar Sesion</span>
        <span *ngIf="loading">Cargando...</span>
      </button>
    </form>
  </div>
</div>
"@ | Set-Content "$base\pages\login\login.component.html" -Encoding UTF8

@"
.login-wrapper { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%); }
.login-card { background: white; border-radius: 20px; padding: 48px 40px; width: 100%; max-width: 420px; box-shadow: 0 25px 60px rgba(0,0,0,0.3); }
.login-header { text-align: center; margin-bottom: 36px; }
.logo { font-size: 48px; margin-bottom: 12px; }
.login-header h1 { font-size: 26px; font-weight: 700; color: #1a1a2e; margin-bottom: 6px; }
.login-header p { color: #888; font-size: 14px; }
.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 6px; font-size: 13px; font-weight: 600; color: #444; text-transform: uppercase; letter-spacing: 0.5px; }
.form-control { width: 100%; padding: 12px 16px; border: 2px solid #e8e8e8; border-radius: 10px; font-size: 15px; transition: border-color 0.2s; outline: none; }
.form-control:focus { border-color: #0f3460; }
.alert-danger { background: #fff5f5; border: 1px solid #fed7d7; color: #c53030; padding: 12px 16px; border-radius: 10px; font-size: 14px; margin-bottom: 16px; }
.btn-login { width: 100%; padding: 14px; background: linear-gradient(135deg, #0f3460, #533483); color: white; border: none; border-radius: 10px; font-size: 16px; font-weight: 600; cursor: pointer; transition: opacity 0.2s; }
.btn-login:hover:not(:disabled) { opacity: 0.9; }
.btn-login:disabled { opacity: 0.6; cursor: not-allowed; }
"@ | Set-Content "$base\pages\login\login.component.css" -Encoding UTF8

# ===================== CATALOGO COMPONENT =====================
@"
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-catalogo',
  imports: [CommonModule],
  templateUrl: './catalogo.component.html',
  styleUrl: './catalogo.component.css'
})
export class CatalogoComponent implements OnInit {
  productos: any[] = [];
  loading = true;
  error = '';

  constructor(private productService: ProductService, private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.productService.getProducts().subscribe({
      next: (data) => { this.productos = data; this.loading = false; },
      error: () => { this.error = 'No se pudieron cargar los productos.'; this.loading = false; }
    });
  }

  irACrearPedido() { this.router.navigate(['/crear-pedido']); }
  logout() { this.authService.logout(); this.router.navigate(['/login']); }
}
"@ | Set-Content "$base\pages\catalogo\catalogo.component.ts" -Encoding UTF8

@"
<div class="catalogo-wrapper">
  <nav class="navbar">
    <div class="nav-brand">🛒 Microservicios</div>
    <div class="nav-actions">
      <button class="btn-pedido" (click)="irACrearPedido()">+ Crear Pedido</button>
      <button class="btn-logout" (click)="logout()">Cerrar Sesion</button>
    </div>
  </nav>
  <div class="container">
    <div class="page-header">
      <h1>Catalogo de Productos</h1>
      <p>{{ productos.length }} productos disponibles</p>
    </div>
    <div class="loading" *ngIf="loading"><div class="spinner"></div><p>Cargando productos...</p></div>
    <div class="alert-error" *ngIf="error">⚠️ {{ error }}</div>
    <div class="productos-grid" *ngIf="!loading && !error">
      <div class="producto-card" *ngFor="let producto of productos">
        <div class="producto-icon">📦</div>
        <div class="producto-info">
          <h3>{{ producto.name }}</h3>
          <p class="sku">SKU: {{ producto.sku }}</p>
          <p class="descripcion">{{ producto.description }}</p>
        </div>
        <div class="producto-footer">
          <span class="precio">\$`{{ producto.price | number:'1.2-2' }}</span>
          <span class="stock" [class.stock-bajo]="producto.stock < 10">Stock: {{ producto.stock }}</span>
        </div>
      </div>
    </div>
  </div>
</div>
"@ | Set-Content "$base\pages\catalogo\catalogo.component.html" -Encoding UTF8

@"
.catalogo-wrapper { min-height: 100vh; background: #f0f2f5; }
.navbar { background: linear-gradient(135deg, #1a1a2e, #0f3460); padding: 16px 32px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 20px rgba(0,0,0,0.2); }
.nav-brand { color: white; font-size: 20px; font-weight: 700; }
.nav-actions { display: flex; gap: 12px; }
.btn-pedido { background: #e94560; color: white; border: none; padding: 10px 20px; border-radius: 8px; font-weight: 600; cursor: pointer; }
.btn-pedido:hover { opacity: 0.85; }
.btn-logout { background: transparent; color: white; border: 2px solid rgba(255,255,255,0.4); padding: 10px 20px; border-radius: 8px; font-weight: 600; cursor: pointer; }
.btn-logout:hover { background: rgba(255,255,255,0.1); }
.container { max-width: 1200px; margin: 0 auto; padding: 40px 24px; }
.page-header { margin-bottom: 32px; }
.page-header h1 { font-size: 28px; font-weight: 700; color: #1a1a2e; margin-bottom: 4px; }
.page-header p { color: #888; font-size: 15px; }
.loading { text-align: center; padding: 80px; color: #888; }
.spinner { width: 40px; height: 40px; border: 4px solid #e8e8e8; border-top-color: #0f3460; border-radius: 50%; animation: spin 0.8s linear infinite; margin: 0 auto 16px; }
@keyframes spin { to { transform: rotate(360deg); } }
.alert-error { background: #fff5f5; border: 1px solid #fed7d7; color: #c53030; padding: 16px; border-radius: 10px; margin-bottom: 24px; }
.productos-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 24px; }
.producto-card { background: white; border-radius: 16px; padding: 24px; box-shadow: 0 2px 12px rgba(0,0,0,0.06); transition: transform 0.2s, box-shadow 0.2s; display: flex; flex-direction: column; gap: 12px; }
.producto-card:hover { transform: translateY(-4px); box-shadow: 0 8px 30px rgba(0,0,0,0.12); }
.producto-icon { font-size: 36px; }
.producto-info h3 { font-size: 17px; font-weight: 700; color: #1a1a2e; margin-bottom: 4px; }
.sku { font-size: 12px; color: #aaa; text-transform: uppercase; letter-spacing: 0.5px; }
.descripcion { font-size: 13px; color: #666; margin-top: 6px; line-height: 1.5; }
.producto-footer { display: flex; justify-content: space-between; align-items: center; margin-top: auto; padding-top: 12px; border-top: 1px solid #f0f0f0; }
.precio { font-size: 20px; font-weight: 700; color: #0f3460; }
.stock { background: #e8f5e9; color: #2e7d32; padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: 600; }
.stock-bajo { background: #fff3e0; color: #e65100; }
"@ | Set-Content "$base\pages\catalogo\catalogo.component.css" -Encoding UTF8

# ===================== CREAR PEDIDO COMPONENT =====================
@"
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-crear-pedido',
  imports: [CommonModule, FormsModule],
  templateUrl: './crear-pedido.component.html',
  styleUrl: './crear-pedido.component.css'
})
export class CrearPedidoComponent implements OnInit {
  productos: any[] = [];
  productId: number | null = null;
  quantity: number = 1;
  loading = false;
  loadingProductos = true;
  exito = false;
  error = '';
  pedidoCreado: any = null;

  constructor(private productService: ProductService, private orderService: OrderService, private router: Router) {}

  ngOnInit() {
    this.productService.getProducts().subscribe({
      next: (data) => { this.productos = data; this.loadingProductos = false; },
      error: () => { this.loadingProductos = false; }
    });
  }

  crearPedido() {
    if (!this.productId || this.quantity < 1) return;
    this.error = '';
    this.exito = false;
    this.loading = true;
    this.orderService.crearPedido(this.productId, this.quantity).subscribe({
      next: (data) => { this.loading = false; this.exito = true; this.pedidoCreado = data; },
      error: (err) => {
        this.loading = false;
        if (err.status === 409) {
          this.error = '⚠️ No hay stock suficiente para completar este pedido. Intenta con una cantidad menor.';
        } else {
          this.error = 'Ocurrio un error al crear el pedido. Intenta nuevamente.';
        }
      }
    });
  }

  volver() { this.router.navigate(['/catalogo']); }
  nuevoPedido() { this.exito = false; this.pedidoCreado = null; this.error = ''; this.productId = null; this.quantity = 1; }
}
"@ | Set-Content "$base\pages\crear-pedido\crear-pedido.component.ts" -Encoding UTF8

@"
<div class="pedido-wrapper">
  <nav class="navbar">
    <div class="nav-brand">🛒 Microservicios</div>
    <button class="btn-volver" (click)="volver()">← Volver al Catalogo</button>
  </nav>
  <div class="container">
    <div class="page-header">
      <h1>Crear Pedido</h1>
      <p>Selecciona un producto y la cantidad que deseas ordenar</p>
    </div>
    <div class="pedido-card" *ngIf="!exito">
      <form (ngSubmit)="crearPedido()">
        <div class="form-group">
          <label>Producto</label>
          <select [(ngModel)]="productId" name="productId" required class="form-control">
            <option [value]="null" disabled>Selecciona un producto...</option>
            <option *ngFor="let p of productos" [value]="p.id">{{ p.name }} - Stock: {{ p.stock }} - \$`{{ p.price }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>Cantidad</label>
          <input type="number" [(ngModel)]="quantity" name="quantity" min="1" required class="form-control" placeholder="Ej: 2" />
        </div>
        <div class="alert-error" *ngIf="error">{{ error }}</div>
        <div class="form-actions">
          <button type="button" class="btn-cancelar" (click)="volver()">Cancelar</button>
          <button type="submit" class="btn-crear" [disabled]="loading || !productId || quantity < 1">
            <span *ngIf="!loading">Crear Pedido</span>
            <span *ngIf="loading">Procesando...</span>
          </button>
        </div>
      </form>
    </div>
    <div class="exito-card" *ngIf="exito && pedidoCreado">
      <div class="exito-icon">✅</div>
      <h2>Pedido creado exitosamente!</h2>
      <p>Tu pedido fue registrado en el sistema.</p>
      <div class="pedido-detalle">
        <div class="detalle-row"><span class="detalle-label">ID del Pedido</span><span class="detalle-valor"># {{ pedidoCreado.id }}</span></div>
        <div class="detalle-row"><span class="detalle-label">Estado</span><span class="badge-estado">{{ pedidoCreado.status }}</span></div>
        <div class="detalle-row"><span class="detalle-label">Correlation ID</span><span class="detalle-valor small">{{ pedidoCreado.correlationId }}</span></div>
      </div>
      <div class="exito-actions">
        <button class="btn-nuevo" (click)="nuevoPedido()">Crear otro pedido</button>
        <button class="btn-catalogo" (click)="volver()">Ver Catalogo</button>
      </div>
    </div>
  </div>
</div>
"@ | Set-Content "$base\pages\crear-pedido\crear-pedido.component.html" -Encoding UTF8

@"
.pedido-wrapper { min-height: 100vh; background: #f0f2f5; }
.navbar { background: linear-gradient(135deg, #1a1a2e, #0f3460); padding: 16px 32px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 2px 20px rgba(0,0,0,0.2); }
.nav-brand { color: white; font-size: 20px; font-weight: 700; }
.btn-volver { background: transparent; color: white; border: 2px solid rgba(255,255,255,0.4); padding: 10px 20px; border-radius: 8px; font-weight: 600; cursor: pointer; }
.btn-volver:hover { background: rgba(255,255,255,0.1); }
.container { max-width: 600px; margin: 0 auto; padding: 40px 24px; }
.page-header { margin-bottom: 32px; }
.page-header h1 { font-size: 28px; font-weight: 700; color: #1a1a2e; margin-bottom: 4px; }
.page-header p { color: #888; font-size: 15px; }
.pedido-card { background: white; border-radius: 20px; padding: 40px; box-shadow: 0 4px 20px rgba(0,0,0,0.08); }
.form-group { margin-bottom: 24px; }
.form-group label { display: block; margin-bottom: 8px; font-size: 13px; font-weight: 600; color: #444; text-transform: uppercase; letter-spacing: 0.5px; }
.form-control { width: 100%; padding: 12px 16px; border: 2px solid #e8e8e8; border-radius: 10px; font-size: 15px; outline: none; transition: border-color 0.2s; background: white; }
.form-control:focus { border-color: #0f3460; }
.alert-error { background: #fff5f5; border: 1px solid #fed7d7; color: #c53030; padding: 16px; border-radius: 10px; font-size: 14px; margin-bottom: 20px; line-height: 1.5; }
.form-actions { display: flex; gap: 12px; margin-top: 8px; }
.btn-cancelar { flex: 1; padding: 14px; background: #f5f5f5; color: #555; border: none; border-radius: 10px; font-size: 15px; font-weight: 600; cursor: pointer; }
.btn-cancelar:hover { background: #ebebeb; }
.btn-crear { flex: 2; padding: 14px; background: linear-gradient(135deg, #0f3460, #533483); color: white; border: none; border-radius: 10px; font-size: 15px; font-weight: 600; cursor: pointer; }
.btn-crear:hover:not(:disabled) { opacity: 0.9; }
.btn-crear:disabled { opacity: 0.5; cursor: not-allowed; }
.exito-card { background: white; border-radius: 20px; padding: 48px 40px; box-shadow: 0 4px 20px rgba(0,0,0,0.08); text-align: center; }
.exito-icon { font-size: 64px; margin-bottom: 16px; }
.exito-card h2 { font-size: 24px; font-weight: 700; color: #1a1a2e; margin-bottom: 8px; }
.exito-card > p { color: #888; margin-bottom: 32px; }
.pedido-detalle { background: #f8f9fa; border-radius: 12px; padding: 20px; margin-bottom: 32px; text-align: left; }
.detalle-row { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; border-bottom: 1px solid #eee; }
.detalle-row:last-child { border-bottom: none; }
.detalle-label { font-size: 13px; font-weight: 600; color: #888; text-transform: uppercase; letter-spacing: 0.5px; }
.detalle-valor { font-size: 15px; font-weight: 600; color: #1a1a2e; }
.detalle-valor.small { font-size: 12px; font-family: monospace; color: #666; }
.badge-estado { background: #e8f5e9; color: #2e7d32; padding: 4px 12px; border-radius: 20px; font-size: 13px; font-weight: 700; }
.exito-actions { display: flex; gap: 12px; }
.btn-nuevo { flex: 1; padding: 14px; background: #f5f5f5; color: #555; border: none; border-radius: 10px; font-weight: 600; cursor: pointer; }
.btn-nuevo:hover { background: #ebebeb; }
.btn-catalogo { flex: 1; padding: 14px; background: linear-gradient(135deg, #0f3460, #533483); color: white; border: none; border-radius: 10px; font-weight: 600; cursor: pointer; }
.btn-catalogo:hover { opacity: 0.9; }
"@ | Set-Content "$base\pages\crear-pedido\crear-pedido.component.css" -Encoding UTF8

Write-Host ""
Write-Host "✅ Todos los archivos fueron creados correctamente!" -ForegroundColor Green
Write-Host ""
