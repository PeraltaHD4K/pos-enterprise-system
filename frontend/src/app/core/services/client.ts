import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Cliente {
  id: number;
  nombre: string;
  telefono?: string;
  email?: string;
  puntosFidelidad?: number;
}

export interface ClienteDTO {
  nombre: string;
  telefono: string;
  email: string;
}

@Injectable({
  providedIn: 'root',
})
export class Client {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/clientes`;

  search(query: string): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.apiUrl}/buscar?query=${query}`);
  }

  getAll(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.apiUrl);
  }

  getById(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${id}`);
  }

  create(client: ClienteDTO): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, client);
  }

  update(id: number, client: ClienteDTO): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/${id}`, client);
  }
}
