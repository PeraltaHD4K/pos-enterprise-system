import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Purchase, Compra } from '../../../core/services/purchase';
import { ToastService } from '../../../core/services/toast';

@Component({
  selector: 'app-purchase-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './purchase-list.html',
  styleUrl: './purchase-list.css',
})
export class PurchaseList implements OnInit {
  private purchaseService = inject(Purchase);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);

  compras: Compra[] = [];
  isLoading = true;

  ngOnInit(): void {
    this.cargarCompras();
  }

  cargarCompras() {
    this.purchaseService.getAll().subscribe({
      next: (data) => {
        this.compras = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.toastService.error('Error al cargar compras', 'Error de Carga');
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  confirmar(id: number) {
    if (confirm('¿Confirmas que has recibido esta mercancía? Esto aumentará el stock.')) {
      this.purchaseService.confirmarRecepcion(id).subscribe({
        next: (compraActualizada) => {
          // Actualizamos la lista localmente para ver el cambio a VERDE
          const index = this.compras.findIndex(c => c.id === id);
          if (index !== -1) {
            this.compras[index] = compraActualizada; // Reemplazamos con la nueva info
            this.compras = [...this.compras]; // Forzar trigger de cambio si es necesario
            this.cdr.detectChanges();
          }
          this.toastService.success('Recepción confirmada. Inventario actualizado.', 'Éxito');
        },
        error: (err) => this.toastService.error(err.error?.message || 'Error al confirmar', 'Error')
      });
    }
  }
}
