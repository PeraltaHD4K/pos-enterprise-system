import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Rol {
  id: string;
  nombre: string;
}

export interface Usuario {
  id: string;
  nombreCompleto: string;
  username: string;
  activo: boolean;
  rol?: Rol;
}

export interface UsuarioRegistro {
  nombreCompleto: string;
  username: string;
  password: string;
  rolId: string;
}

export interface UsuarioEdicion {
  nombreCompleto: string;
  username: string;
  rolId: string;
  password?: string; // Opcional
}

@Injectable({
  providedIn: 'root',
})
export class User {
  private http = inject(HttpClient);
  private apiURL = `${environment.apiUrl}/usuarios`;

  getUsuarios(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.apiURL);
  }

  crearUsuario(usuario: UsuarioRegistro): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiURL, usuario);
  }

  getUsuario(id: string): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiURL}/${id}`);
  }

  actualizarUsuario(id: string, usuario: UsuarioEdicion): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.apiURL}/${id}`, usuario);
  }

  toggleEstado(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiURL}/${id}`);
  }
}
