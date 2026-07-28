import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment'; // Importar el environment BASE
import { Observable } from 'rxjs';

export interface Categoria {
  id: string;
  nombre: string;
  descripcion?: string;
  activo?: boolean;
}

// DTO para enviar datos (sin ID, ni fecha)
export interface CategoriaRequest {
  nombre: string;
  descripcion: string;
}

@Injectable({
  providedIn: 'root',
})
export class Category {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/inventario/categorias`;

  getAll(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(this.apiUrl);
  }

  getById(id: string): Observable<Categoria> {
    return this.http.get<Categoria>(`${this.apiUrl}/${id}`);
  }

  create(data: CategoriaRequest): Observable<Categoria> {
    return this.http.post<Categoria>(this.apiUrl, data);
  }

  update(id: string, data: CategoriaRequest): Observable<Categoria> {
    return this.http.put<Categoria>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
