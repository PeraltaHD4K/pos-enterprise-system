import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Supplier, Proveedor } from '../../../../core/services/supplier';
import { ToastService } from '../../../../core/services/toast';

@Component({
  selector: 'app-supplier-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './supplier-list.html',
  styleUrl: './supplier-list.css',
})
export class SupplierList implements OnInit {
  private supplierService = inject(Supplier);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);

  proveedores: Proveedor[] = [];
  isLoading = true;

  ngOnInit(): void {
    this.cargarProveedores();
  }

  cargarProveedores() {
    this.supplierService.getAll().subscribe({
      next: (data) => {
        this.proveedores = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  borrar(id: string) {
    if (confirm('¿Estás seguro de eliminar este proveedor?')) {
      this.supplierService.delete(id).subscribe({
        next: () => {
          this.proveedores = this.proveedores.filter(p => p.id !== id);
          this.cdr.detectChanges();
        },
        error: () => this.toastService.error('Error al eliminar', 'Error')
      });
    }
  }
}
