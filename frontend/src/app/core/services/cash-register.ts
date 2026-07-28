import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

export interface SesionCaja {
  id: string;
  usuario: any;
  fechaApertura: string;
  fechaCierre?: string;
  saldoInicial: number;
  saldoFinalCalculado?: number;
  saldoFinalReal?: number;
  diferencia?: number;
  estado: 'ABIERTA' | 'CERRADA';
}

export interface AperturaCajaRequest {
  saldoInicial: number;
}

export interface CierreCajaRequest {
  saldoFinalReal: number;
}

export interface CorteX {
  saldoInicial: number;
  ventasEfectivo: number;
  ventasOtrosMetodos: number;
  totalIngresos: number;
  totalRetiros: number;
  saldoEsperadoEnCaja: number;
}

export interface MovimientoCajaRequest {
  monto: number;
  tipo: 'INGRESO' | 'RETIRO';
  motivo: string;
}

@Injectable({
  providedIn: 'root',
})
export class CashRegister {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/caja`;

  // Verificar si tengo caja abierta (devuelve 204 No Content si está cerrada)
  getEstado(): Observable<SesionCaja> {
    return this.http.get<SesionCaja>(`${this.apiUrl}/estado`);
  }

  abrir(data: AperturaCajaRequest): Observable<SesionCaja> {
    return this.http.post<SesionCaja>(`${this.apiUrl}/abrir`, data);
  }

  cerrar(data: CierreCajaRequest): Observable<SesionCaja> {
    return this.http.post<SesionCaja>(`${this.apiUrl}/cerrar`, data);
  }

  obtenerCorteX(): Observable<CorteX> {
    return this.http.get<CorteX>(`${this.apiUrl}/corte-x`);
  }

  registrarMovimiento(data: MovimientoCajaRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/movimientos`, data);
  }

  listarMovimientos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/movimientos`);
  }

  obtenerTicketCierre(idSesion: string): Observable<string> {
    return this.http.get(`${this.apiUrl}/ticket-cierre/${idSesion}`, { responseType: 'text' });
  }
}
