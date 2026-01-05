import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Proveedor } from './supplier'; // Reutilizamos tu interfaz de Proveedor
import { Usuario } from './user';

// 1. Lo que viene del Backend (Lectura)
export interface DetalleCompra {
  id: number;
  producto: {
    id: number;
    nombre: string;
    sku: string;
    // ... otros campos de producto si los necesitas visualmente
  };
  cantidadPedida: number;
  cantidadRecibida: number;
  unidadesPorCaja: number;
  costoTotalRenglon: number;
  costoUnitarioCalculado: number;
  totalPiezasReales: number;
}

export interface Compra {
  id: number;
  folioFactura?: string;
  proveedor: Proveedor;
  usuario: Usuario;
  fechaPedido: string;
  fechaRecepcion?: string;
  fechaEstimadaEntrega?: string;
  estado: 'PENDIENTE' | 'COMPLETADA' | 'CANCELADA';
  total: number;
  observaciones?: string;
  detalles?: DetalleCompra[]; // Opcional porque en la lista general a veces no vienen
}

// 2. Lo que enviamos al Backend (Escritura)
export interface ItemCompraRequest {
  productoId: number;
  cantidadPedida: number;
  unidadesPorCaja: number; // Por defecto 1
  costoTotal: number; // El costo global de esa línea
  cantidadRecibida?: number; // Opcional, backend asume igual a pedida si es COMPLETADA
}

export interface CompraRequest {
  proveedorId: number;
  folioFactura: string;
  observaciones?: string;
  estado: 'PENDIENTE' | 'COMPLETADA'; // Para soportar tus flujos de preventa
  fechaEstimadaEntrega?: string;
  items: ItemCompraRequest[];
}

@Injectable({
  providedIn: 'root',
})
export class Purchase {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/compras`;

  getAll(): Observable<Compra[]> {
    return this.http.get<Compra[]>(this.apiUrl);
  }

  getById(id: number): Observable<Compra> {
    return this.http.get<Compra>(`${this.apiUrl}/${id}`);
  }

  create(data: CompraRequest): Observable<Compra> {
    return this.http.post<Compra>(this.apiUrl, data);
  }

  // Endpoint extra que vi en tu controller
  confirmarRecepcion(id: number): Observable<Compra> {
    return this.http.post<Compra>(`${this.apiUrl}/confirmar/${id}`, {});
  }
}
