import { Injectable, inject, signal, computed } from '@angular/core';
import { CashRegister, SesionCaja } from '../../../core/services/cash-register';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PosSession {
  private cashRegisterService = inject(CashRegister);

  // === ESTADO (Signals) ===
  readonly session = signal<SesionCaja | null>(null);
  readonly isLoading = signal<boolean>(false);

  // Computed: Para saber fácil si hay turno activo
  readonly isOpen = computed(() => this.session() !== null);

  // === ACCIONES ===

  // 1. Cargar estado inicial
  loadSession(): Observable<SesionCaja | null> {
    this.isLoading.set(true);
    return this.cashRegisterService.getEstado().pipe(
      tap({
        next: (sesion) => {
          this.session.set(sesion || null);
          this.isLoading.set(false);
        },
        error: () => {
          this.session.set(null);
          this.isLoading.set(false);
        }
      })
    );
  }

  // 2. Abrir Caja
  openSession(saldoInicial: number): Observable<SesionCaja> {
    this.isLoading.set(true);
    return this.cashRegisterService.abrir({ saldoInicial }).pipe(
      tap({
        next: (nuevaSesion) => {
          this.session.set(nuevaSesion);
          this.isLoading.set(false);
        },
        error: () => this.isLoading.set(false)
      })
    );
  }

  // 3. Cerrar Caja
  closeSession(saldoFinalReal: number): Observable<SesionCaja> {
    this.isLoading.set(true);
    return this.cashRegisterService.cerrar({ saldoFinalReal }).pipe(
      tap({
        next: (sesionCerrada) => {
          this.session.set(null); // Ya no hay sesión activa
          this.isLoading.set(false);
        },
        error: () => this.isLoading.set(false)
      })
    );
  }

  // 4. Registrar Movimiento (Ingreso/Retiro)
  registerMovement(datos: { monto: number, tipo: 'INGRESO' | 'RETIRO', motivo: string }): Observable<void> {
    return this.cashRegisterService.registrarMovimiento(datos);
  }
}
