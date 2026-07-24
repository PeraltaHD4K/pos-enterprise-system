import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Sale } from '../../../../core/services/sale';
import { ToastService } from '../../../../core/services/toast';

@Component({
  selector: 'app-pos-history-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pos-history-modal.html',
  styleUrl: './pos-history-modal.css',
})
export class PosHistoryModal {
  @Input() isOpen = false;
  @Input() ventas: any[] = [];
  @Input() isLoading = false;
  
  // Pagination
  @Input() currentPage = 0;
  @Input() totalPages = 0;
  @Input() totalElements = 0;
  @Input() pageSize = 10;

  @Output() onClose = new EventEmitter<void>();
  @Output() onPageChange = new EventEmitter<number>();
  @Output() onReprint = new EventEmitter<string>();
  @Output() onCancelSuccess = new EventEmitter<void>(); // Para recargar tabla

  private saleService = inject(Sale);
  private toastService = inject(ToastService);

  // Lógica interna de autorización
  showAuthModal = false;
  folioACancelar: string | null = null;
  supervisorUser = '';
  supervisorPass = '';
  isProcessing = false;

  solicitarCancelacion(folio: string) {
    this.folioACancelar = folio;
    this.supervisorUser = '';
    this.supervisorPass = '';
    this.showAuthModal = true;
  }

  cerrarAuthModal() {
    this.showAuthModal = false;
  }

  confirmarCancelacion() {
    if (!this.folioACancelar || !this.supervisorUser || !this.supervisorPass) {
      this.toastService.warning('Datos incompletos', 'Atención');
      return;
    }

    this.isProcessing = true;
    const creds = { usernameSupervisor: this.supervisorUser, passwordSupervisor: this.supervisorPass };

    this.saleService.cancelarVenta(this.folioACancelar, creds).subscribe({
      next: (res) => {
        this.toastService.success('Venta Cancelada', 'Éxito');
        this.isProcessing = false;
        this.cerrarAuthModal();
        this.onCancelSuccess.emit(); // Avisamos al padre para que recargue la lista
      },
      error: (err) => {
        this.isProcessing = false;
        this.toastService.error(err.error?.mensaje || 'Credenciales incorrectas', 'Error');
      }
    });
  }
}
