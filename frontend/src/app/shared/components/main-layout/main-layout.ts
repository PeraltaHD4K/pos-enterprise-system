import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { Auth } from '../../../core/services/auth';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './main-layout.html',
  styleUrl: './main-layout.css',
})
export class MainLayout {
  private auth = inject(Auth);
  private router = inject(Router);

  username = this.auth.getUsername();
  role = this.auth.getRole();
  canAccessPos = this.auth.hasRole(['ADMIN', 'GERENTE', 'CAJERO']);

  // Definición del menú TODO menú desplegable
  allMenuItems = [
    { label: 'Dashboard', path: '/dashboard', icon: '📊', roles: ['ADMIN', 'GERENTE'] },
    { label: 'Ventas (POS)', path: '/pos', icon: '🛒', roles: ['ADMIN', 'GERENTE', 'CAJERO'] },
    { label: 'Productos', path: '/inventory/products', icon: '📦', roles: ['ADMIN', 'GERENTE'] },
    { label: 'Categorías', path: '/inventory/categories', icon: '🏷️', roles: ['ADMIN', 'GERENTE'] },
    { label: 'Proveedores', path: '/purchases/suppliers', icon: '🚚', roles: ['ADMIN', 'GERENTE'] },
    { label: 'Compras', path: '/purchases/list', icon: '📉', roles: ['ADMIN', 'GERENTE'] },
    { label: 'Clientes', path: '/customers', icon: '👥', roles: ['ADMIN', 'GERENTE'] },
    { label: 'Usuarios', path: '/users', icon: '🔐', roles: ['ADMIN'] },
    { label: 'Configuración', path: '/settings', icon: '⚙️', roles: ['ADMIN'] },
  ];

  // Filtramos el menú según el rol del usuario actual
  get menuItems() {
    return this.allMenuItems.filter(item => this.auth.hasRole(item.roles));
  }

  logout() {
    this.auth.logout();
  }
}
