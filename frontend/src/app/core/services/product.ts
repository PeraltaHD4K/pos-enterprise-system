import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Categoria } from './category';
import { Page } from './page';

export interface MovimientoInventario {
  id: string;
  tipoMovimiento: string; // VENTA, COMPRA, AJUSTE, etc.
  cantidad: number;
  stockAnterior: number;
  stockResultante: number;
  motivo?: string;
  referencia?: string;
  fecha: string; // LocalDateTime viene como string ISO
  usuario: {
    username: string;
  };
}

export interface Producto {
  id: string;
  sku: string;
  codigoBarras: string;
  nombre: string;
  descripcion?: string;
  precioVenta: number;
  costoPromedio: number;
  stockActual: number;
  stockMinimo: number;
  activo: boolean;
  categoria: Categoria; // Objeto completo (Backend lo manda con JOIN FETCH)
}

// Interfaz que coincide con tu ProductoRegistroDTO.java
export interface ProductoRequest {
  sku: string;
  codigoBarras: string;
  nombre: string;
  descripcion: string;
  precioVenta: number;
  costoPromedio: number;
  stockMinimo: number;
  categoriaId: string; // Solo el ID, no el objeto
  ultimoCostoCompra?: number;
}

export interface AjusteStockRequest {
  cantidad: number; // Puede ser positivo (entrada) o negativo (salida)
  motivo: string;
  autorizacion?: any; // Opcional por ahora
}

@Injectable({
  providedIn: 'root',
})
export class Product {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/inventario/productos`;

  getKardex(productoId: string): Observable<MovimientoInventario[]> {
    // Apunta al nuevo controller que creamos
    return this.http.get<MovimientoInventario[]>(`${environment.apiUrl}/inventario/movimientos/producto/${productoId}`);
  }

  // Listar todos con paginación
  getAll(page: number = 0, size: number = 10): Observable<Page<Producto>> {
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<Page<Producto>>(this.apiUrl, { params });
  }

  // Buscar por query (tu backend tiene /buscar?q=...)
  search(query: string): Observable<Producto[]> {
    const params = new HttpParams().set('q', query);
    return this.http.get<Producto[]>(`${this.apiUrl}/buscar`, { params });
  }

  // Obtener por ID (necesario para editar)
  getById(id: string): Observable<Producto> {
    // Nota: Tu controller actual NO tiene getById directo, usa la lista o búsqueda.
    // Te sugiero agregarlo o filtrar en el front. 
    // Por ahora, asumiremos que agregas @GetMapping("/{id}") en Java o usamos filter en el componente.
    // Lo ideal es tenerlo en backend:
    return this.http.get<Producto>(`${this.apiUrl}/${id}`);
  }

  create(data: ProductoRequest): Observable<Producto> {
    return this.http.post<Producto>(this.apiUrl, data);
  }

  update(id: string, data: ProductoRequest): Observable<Producto> {
    return this.http.put<Producto>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateStock(id: string, data: AjusteStockRequest): Observable<Producto> {
    return this.http.patch<Producto>(`${this.apiUrl}/${id}/stock`, data);
  }
}
