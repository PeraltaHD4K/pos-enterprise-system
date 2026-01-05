import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Rol {
  id: number;
  nombre: string;
}

export interface Usuario {
  id: number;
  nombreCompleto: string;
  username: string;
  activo: boolean;
  rol: Rol;
}

@Injectable({
  providedIn: 'root',
})
export class User {
  private http = inject(HttpClient);
  private apiURL = 'http://localhost:8080/api/v1/usuarios';

  getUsuarios(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.apiURL);
  }
}
