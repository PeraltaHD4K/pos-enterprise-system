import { Component, EventEmitter, Input, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SesionCaja, CashRegister } from '../../../../core/services/cash-register';
import { PosPrinter } from '../../services/pos-printer';
import { ToastService } from '../../../../core/services/toast';

@Component({
  selector: 'app-pos-summary-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pos-summary-modal.html',
  styleUrl: './pos-summary-modal.css',
})
export class PosSummaryModal {
  @Input() resumen: SesionCaja | null = null;
  @Output() onFinish = new EventEmitter<void>();

  private cashRegisterService = inject(CashRegister);
  private printerService = inject(PosPrinter); // 👈 Inyección
  private toastService = inject(ToastService);

  isPrinting = signal(false);

  imprimirCorte() {
    if (!this.resumen?.id) return;
    this.isPrinting.set(true);

    // 1. Pedir datos al Backend
    this.cashRegisterService.obtenerTicketCierre(this.resumen.id).subscribe({
      next: (ticketTexto) => {
        // 2. Imprimir con el servicio local
        this.printerService.printTicket(ticketTexto);

        setTimeout(() => {
          this.isPrinting.set(false);
        }, 2000);
      },
      error: (err) => {
        console.error(err);
        this.toastService.error('Error al generar el ticket', 'Error');
        this.isPrinting.set(false);
      }
    });
  }
}
