import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class Auth {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = `${environment.apiUrl}/auth`;
  private tokenKey = 'auth_token';

  // --- 1. LOGIN ---
  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        if (response.token) {
          localStorage.setItem(this.tokenKey, response.token);
        }
      })
    );
  }

  // --- 2. GESTIÓN DE TOKEN ---
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getUsername(): string | null {
    const payload = this.getPayload();
    if (!payload) return null;
    return payload.sub; // 'sub' suele contener el username
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    this.router.navigate(['/login']);
  }

  // --- 3. ROLES Y PERMISOS (NUEVO) ---

  // Decodifica el token para leer datos ocultos
  getPayload(): any | null {
    const token = this.getToken();
    if (!token) return null;
    try {
      return jwtDecode(token);
    } catch (error) {
      return null;
    }
  }

  // Obtiene el rol del usuario (Ej. "ADMIN", "CAJERO")
  getRole(): string | null {
    const payload = this.getPayload();
    if (!payload) {
      console.warn('⚠️ Token vacío o inválido');
      return null;
    }

    const role = payload.role || payload.authorities || payload.roles || null;

    return role;
  }

  // Verifica si el usuario tiene uno de los roles permitidos
  hasRole(allowedRoles: string[]): boolean {
    const myRole = this.getRole();
    if (!myRole) return false;

    // Compara roles (normalizando a mayúsculas por si acaso)
    return allowedRoles.some(role =>
      role.toString().toUpperCase() === myRole.toString().toUpperCase() ||
      `ROLE_${role.toString().toUpperCase()}` === myRole.toString().toUpperCase()
    );
  }
}
