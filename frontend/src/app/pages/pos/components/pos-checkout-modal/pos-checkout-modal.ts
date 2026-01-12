import { Component, EventEmitter, Input, Output, OnInit, OnChanges, SimpleChanges } from '@angular/core';
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
  metodoSeleccionado: MetodoPago = 'EFECTIVO';

  ngOnChanges(changes: SimpleChanges) {
    if (changes['isOpen'] && this.isOpen) {
      this.resetModal();
    }
  }

  seleccionarMetodo(metodo: MetodoPago) {
    this.metodoSeleccionado = metodo;

    if (metodo === 'EFECTIVO') {
      this.montoPagado = null; // En efectivo obligamos a escribir
      this.cambio = -this.total;
    } else {
      // En Tarjeta/Transferencia asumimos pago exacto
      this.montoPagado = this.total;
      this.cambio = 0;
    }
  }

  // Cuando cambia el input, calculamos localmente
  calcularCambio() {
    if (this.metodoSeleccionado !== 'EFECTIVO') {
      this.cambio = 0;
      return;
    }

    if (this.montoPagado !== null) {
      this.cambio = this.montoPagado - this.total;
    } else {
      this.cambio = 0;
    }
  }

  confirmar() {
    if (!this.montoPagado) return;

    if (this.montoPagado < this.total && this.metodoSeleccionado === 'EFECTIVO') {
      return;
    }

    // Enviamos al padre solo los datos finales
    this.onConfirm.emit({
      montoPagado: this.montoPagado,
      metodoPago: this.metodoSeleccionado
    });
  }

  resetModal() {
    this.metodoSeleccionado = 'EFECTIVO';
    this.montoPagado = null;
    this.cambio = -this.total;
  }
}
