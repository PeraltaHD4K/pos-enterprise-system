import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { Page } from './page';

export interface PuntoGrafica {
  etiqueta: string;
  totalVentas: number;
  ganancia: number;
  cantidadVentas: number;
}

export interface ReporteGanancias {
  totalVentas: number;
  costoVentas: number;
  gananciaBruta: number;
  margenPorcentaje: number;
  totalTransacciones: number;
  ticketPromedio: number;
  grafica: PuntoGrafica[];
}

export interface ProductoTop {
  nombreProducto: string;
  cantidadVendida: number;
  totalDineroGenerado: number;
}

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

  cancelarVenta(folio: string, credencialesSupervisor: { usernameSupervisor: string, passwordSupervisor: string }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${folio}/cancelar`, credencialesSupervisor);
  }

  getMisVentasHoy(page: number = 0, size: number = 10): Observable<Page<VentaResponse>> {
    let params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<Page<VentaResponse>>(`${this.apiUrl}/mis-ventas-hoy`, { params });
  }

  obtenerReporteGanancias(inicio?: string, fin?: string): Observable<ReporteGanancias> {
    let params = new HttpParams();
    if (inicio) params = params.set('fechaInicio', inicio);
    if (fin) params = params.set('fechaFin', fin);

    return this.http.get<ReporteGanancias>(`${this.apiUrl}/reporte/ganancias`, { params });
  }

  obtenerTopProductos(inicio?: string, fin?: string): Observable<ProductoTop[]> {
    let params = new HttpParams();
    if (inicio) params = params.set('fechaInicio', inicio);
    if (fin) params = params.set('fechaFin', fin);

    return this.http.get<ProductoTop[]>(`${this.apiUrl}/reportes/top-productos`, { params });
  }
}
