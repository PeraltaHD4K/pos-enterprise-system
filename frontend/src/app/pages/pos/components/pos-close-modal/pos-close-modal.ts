import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../../../core/services/toast';

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

  private toastService = inject(ToastService);

  confirmar() {
    if (this.montoCierre === null || this.montoCierre < 0) {
      this.toastService.error('Ingresa un monto válido', 'Error');
      return;
    }
    // Enviamos el monto al padre
    this.onConfirm.emit(this.montoCierre);
    // Limpiamos
    this.montoCierre = null;
  }
}
