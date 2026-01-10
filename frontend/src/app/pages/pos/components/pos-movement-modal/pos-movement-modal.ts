import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-pos-movement-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pos-movement-modal.html',
  styleUrl: './pos-movement-modal.css',
})
export class PosMovementModal {
  @Input() isOpen = false;
  @Input() isProcessing = false;

  @Output() onClose = new EventEmitter<void>();
  @Output() onConfirm = new EventEmitter<{ monto: number, tipo: 'INGRESO' | 'RETIRO', motivo: string }>();

  // Formulario local
  monto: number | null = null;
  motivo: string = '';
  tipo: 'INGRESO' | 'RETIRO' = 'RETIRO'; // Por defecto Retiro (gasto)

  confirmar() {
    if (!this.monto || this.monto <= 0 || !this.motivo.trim()) {
      alert('Completa todos los campos correctamente');
      return;
    }

    this.onConfirm.emit({
      monto: this.monto,
      tipo: this.tipo,
      motivo: this.motivo
    });

    // Limpiar campos
    this.monto = null;
    this.motivo = '';
    this.tipo = 'RETIRO';
  }
}
