import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export type MetodoPago = 'EFECTIVO' | 'TARJETA' | 'TRANSFERENCIA';

@Component({
  selector: 'app-pos-checkout-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pos-checkout-modal.html',
  styleUrl: './pos-checkout-modal.css',
})
export class PosCheckoutModal {
  @Input() isOpen = false;
  @Input() total = 0;
  @Input() isProcessing = false;

  @Output() onClose = new EventEmitter<void>();
  @Output() onConfirm = new EventEmitter<{ montoPagado: number, metodoPago: MetodoPago }>();

  // Lógica que antes estaba en el Padre
  montoPagado: number | null = null;
  cambio = 0;

  // Cuando cambia el input, calculamos localmente
  calcularCambio() {
    if (this.montoPagado !== null) {
      this.cambio = this.montoPagado - this.total;
    } else {
      this.cambio = 0;
    }
  }

  confirmar() {
    if (!this.montoPagado || this.montoPagado < this.total) return;

    // Enviamos al padre solo los datos finales
    this.onConfirm.emit({
      montoPagado: this.montoPagado,
      metodoPago: 'EFECTIVO'
    });

    // Reseteamos para la próxima
    this.montoPagado = null;
    this.cambio = 0;
  }
}
