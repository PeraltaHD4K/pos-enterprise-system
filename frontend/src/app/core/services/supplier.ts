import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

export interface Proveedor {
  id: number;
  empresa: string;
  contacto?: string;
  telefono: string;
  email?: string;
  diaVisita?: string;
  activo: boolean;
}

export interface ProveedorRequest {
  empresa: string;
  contacto: string;
  telefono: string;
  email: string;
  diaVisita: string;
}

@Injectable({
  providedIn: 'root',
})
export class Supplier {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/compras/proveedores`;

  getAll(): Observable<Proveedor[]> {
    return this.http.get<Proveedor[]>(this.apiUrl);
  }

  getById(id: number): Observable<Proveedor> {
    return this.http.get<Proveedor>(`${this.apiUrl}/${id}`);
  }

  create(data: ProveedorRequest): Observable<Proveedor> {
    return this.http.post<Proveedor>(this.apiUrl, data);
  }

  update(id: number, data: ProveedorRequest): Observable<Proveedor> {
    return this.http.put<Proveedor>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
