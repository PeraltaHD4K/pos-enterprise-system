import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-pos-close-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pos-close-modal.html',
  styleUrl: './pos-close-modal.css',
})
export class PosCloseModal {
  @Input() isOpen = false;
  @Input() isLoading = false;
  @Output() onClose = new EventEmitter<void>();
  @Output() onConfirm = new EventEmitter<number>();

  montoCierre: number | null = null;

  confirmar() {
    if (this.montoCierre === null || this.montoCierre < 0) {
      alert('Ingresa un monto válido');
      return;
    }
    // Enviamos el monto al padre
    this.onConfirm.emit(this.montoCierre);
    // Limpiamos
    this.montoCierre = null;
  }
}
