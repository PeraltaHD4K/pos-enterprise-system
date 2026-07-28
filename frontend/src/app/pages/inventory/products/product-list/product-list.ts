import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Product, Producto, MovimientoInventario } from '../../../../core/services/product';
import { ToastService } from '../../../../core/services/toast';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './product-list.html',
  styleUrl: './product-list.css',
})
export class ProductList implements OnInit {
  private productService = inject(Product);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);

  productos: Producto[] = [];
  isLoading = true;

  // Pagination state
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  showKardexModal = false;
  selectedProduct: Producto | null = null;
  movimientos: MovimientoInventario[] = [];
  isLoadingKardex = false;

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos() {
    this.isLoading = true;

    this.productService.getAll(this.currentPage, this.pageSize).subscribe({
      next: (data) => {
        this.productos = data.content;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.isLoading = false;
        this.cdr.detectChanges(); // Forzamos actualización visual
      },
      error: (err) => {
        console.error('Error al cargar productos', err);
        this.toastService.error('Error al cargar productos', 'Error de Carga');
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  cambiarPagina(nuevaPagina: number) {
    if (nuevaPagina >= 0 && nuevaPagina < this.totalPages) {
      this.currentPage = nuevaPagina;
      this.cargarProductos();
    }
  }

  borrar(id: string) {
    if (confirm('¿Estás seguro de eliminar este producto?')) {
      this.productService.delete(id).subscribe({
        next: () => {
          // Optimistic update: Lo quitamos de la lista visualmente
          this.productos = this.productos.filter(p => p.id !== id);
          this.cdr.detectChanges();
        },
        error: (err) => this.toastService.error('Error al eliminar producto', 'Error')
      });
    }
  }

  abrirKardex(producto: Producto) {
    this.selectedProduct = producto;
    this.showKardexModal = true;
    this.movimientos = []; // Limpiar anterior
    this.isLoadingKardex = true;
    this.cdr.detectChanges(); // Actualizar para mostrar modal vacío

    this.productService.getKardex(producto.id).subscribe({
      next: (data) => {
        this.movimientos = data;
        this.isLoadingKardex = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error cargando kardex', err);
        this.toastService.error('No se pudo cargar el historial', 'Error');
        this.isLoadingKardex = false;
        this.cdr.detectChanges();
      }
    });
  }

  cerrarKardex() {
    this.showKardexModal = false;
    this.selectedProduct = null;
    this.cdr.detectChanges();
  }
}
