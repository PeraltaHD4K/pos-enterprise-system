import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

export interface ItemVentaRequest {
  productoId: number;
  cantidad: number;
}

export interface VentaRequest {
  clienteId?: number | null; // Null para venta público general
  metodoPago: 'EFECTIVO' | 'TARJETA' | 'TRANSFERENCIA';
  montoPagado: number; // Cuánto dinero entregó el cliente
  items: ItemVentaRequest[];
}

export interface VentaResponse {
  id: number;
  folio: string;
  totalVenta: number;
  cambio: number;
  fecha: string;
}

@Injectable({
  providedIn: 'root',
})
export class Sale {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/ventas`;

  registrarVenta(data: VentaRequest): Observable<VentaResponse> {
    return this.http.post<VentaResponse>(this.apiUrl, data);
  }

  // Obtiene el ticket en texto plano listo para imprimir
  obtenerTicket(folio: string): Observable<string> {
    // responseType: 'text' es clave porque el backend devuelve String, no JSON
    return this.http.get(`${this.apiUrl}/ticket/${folio}`, { responseType: 'text' });
  }

  cancelarVenta(folio: string, autorizacion: { usuarioAdmin: string, passwordAdmin: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/${folio}/cancelar`, autorizacion);
  }
}
