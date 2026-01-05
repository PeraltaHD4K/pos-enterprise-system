import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Rol {
  id: number;
  nombre: string;
}

export interface Usuario {
  id: number;
  nombreCompleto: string;
  username: string;
  activo: boolean;
  rol?: Rol;
}

export interface UsuarioRegistro {
  nombreCompleto: string;
  username: string;
  password: string;
  rolId: number;
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
}
