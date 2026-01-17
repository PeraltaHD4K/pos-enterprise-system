import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

export interface TicketMetrics {
  total_ventas: number;
  ticket_promedio: number;
  ticket_maximo: number;
  ticket_minimo: number;
  distribucion_precios: { [rango: string]: number };
}

@Injectable({
  providedIn: 'root',
})
export class Analytics {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/analytics`;

  obtenerMetricasTickets(start?: string, end?: string): Observable<TicketMetrics> {
    let params = new HttpParams();
    if (start) params = params.set('start', start);
    if (end) params = params.set('end', end);
    return this.http.get<TicketMetrics>(`${this.apiUrl}/dashboard/tickets`, { params });
  }
}
