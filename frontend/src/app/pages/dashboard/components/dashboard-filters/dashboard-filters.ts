import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface FilterData {
  start: string;
  end: string;
  isSingleDay: boolean; // Para saber si graficamos por horas o por días
}

@Component({
  selector: 'app-dashboard-filters',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard-filters.html',
  styleUrl: './dashboard-filters.css',
})
export class DashboardFilters {
  @Output() onFilterChange = new EventEmitter<FilterData>();

  mode: 'DIA' | 'RANGO' = 'DIA';

  today: string = (() => {
    const d = new Date();
    return `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;
  })();

  minDate: string = '2024-01-01';

  // Inputs
  fechaUnica: string = this.today;
  fechaInicio: string = this.today;
  fechaFin: string = this.today;

  errorMessage: string = '';
  rangoActivoLabel: string = 'Datos de Hoy';

  constructor() {
    // Emitir valor inicial al cargar
    setTimeout(() => this.emitirCambio(), 0);
  }

  setMode(m: 'DIA' | 'RANGO') {
    this.mode = m;
    this.errorMessage = '';
    // Reseteamos a valores seguros al cambiar modo
    if (m === 'DIA') {
      this.fechaUnica = this.today;
    } else {
      this.fechaInicio = this.today;
      this.fechaFin = this.today;
    }
    this.emitirCambio();
  }

  // BOTONES RÁPIDOS
  setQuickOption(option: 'HOY' | 'AYER' | 'SEMANA' | 'MES') {
    this.errorMessage = '';
    const hoy = new Date();

    if (this.mode === 'DIA') {
      if (option === 'HOY') {
        this.fechaUnica = this.getFechaLocal(hoy);
      } else if (option === 'AYER') {
        const ayer = new Date();
        ayer.setDate(hoy.getDate() - 1);
        this.fechaUnica = this.getFechaLocal(ayer);
      }
    } else {
      // MODO RANGO
      this.fechaFin = this.getFechaLocal(hoy);

      if (option === 'SEMANA') {
        const inicio = new Date();
        inicio.setDate(hoy.getDate() - 7);
        this.fechaInicio = this.getFechaLocal(inicio);
      } else if (option === 'MES') {
        const inicio = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
        this.fechaInicio = this.getFechaLocal(inicio);
      }
    }
    this.emitirCambio();
  }

  checkAndApply() {
    this.errorMessage = '';

    // 1. Validar Día Único
    if (this.mode === 'DIA') {
      if (!this.esFechaValida(this.fechaUnica)) {
        this.errorMessage = 'Fecha inválida o año incorrecto.';
        return;
      }
      if (this.fechaUnica > this.today) {
        this.errorMessage = 'No puedes seleccionar fechas futuras.';
        return;
      }
      if (this.fechaUnica < this.minDate) {
        this.errorMessage = 'La fecha es demasiado antigua.';
        return;
      }
    }
    // 2. Validar Rango
    else {
      if (!this.esFechaValida(this.fechaInicio) || !this.esFechaValida(this.fechaFin)) {
        this.errorMessage = 'Revisa las fechas ingresadas.';
        return;
      }
      if (this.fechaInicio > this.today || this.fechaFin > this.today) {
        this.errorMessage = 'El rango incluye fechas futuras.';
        return;
      }
      if (this.fechaInicio < this.minDate) {
        this.errorMessage = 'La fecha de inicio es demasiado antigua.';
        return;
      }
      if (this.fechaInicio > this.fechaFin) {
        this.errorMessage = 'La fecha inicio no puede ser mayor al fin.';
        return;
      }
    }

    // Si pasamos todas las validaciones, emitimos
    this.emitirCambio();
  }

  private emitirCambio() {
    let start, end, isSingle;

    if (this.mode === 'DIA') {
      start = end = this.fechaUnica;
      isSingle = true;
      this.rangoActivoLabel = `Reporte del día: ${this.fechaUnica}`;
    } else {
      start = this.fechaInicio;
      end = this.fechaFin;
      isSingle = false;
      this.rangoActivoLabel = `Reporte del: ${start} al ${end}`;
    }

    this.onFilterChange.emit({
      start,
      end,
      isSingleDay: isSingle
    });
  }

  private esFechaValida(fecha: string): boolean {
    if (!fecha) return false;
    const dateObj = new Date(fecha);
    const year = dateObj.getFullYear();
    // Validamos que sea un número y esté en un rango lógico (ej. 2000 - 2100)
    return !isNaN(dateObj.getTime()) && year > 2000 && year < 2100;
  }

  private getFechaLocal(fecha: Date): string {
    const year = fecha.getFullYear();
    const month = (fecha.getMonth() + 1).toString().padStart(2, '0');
    const day = fecha.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
