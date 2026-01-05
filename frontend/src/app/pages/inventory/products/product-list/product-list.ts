import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Product, Producto } from '../../../../core/services/product';

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

  productos: Producto[] = [];
  isLoading = true;

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos() {
    this.isLoading = true;

    this.productService.getAll().subscribe({
      next: (data) => {
        this.productos = data;
        this.isLoading = false;
        this.cdr.detectChanges(); // Forzamos actualización visual
      },
      error: (err) => {
        console.error('Error al cargar productos', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  borrar(id: number) {
    if (confirm('¿Estás seguro de eliminar este producto?')) {
      this.productService.delete(id).subscribe({
        next: () => {
          // Optimistic update: Lo quitamos de la lista visualmente
          this.productos = this.productos.filter(p => p.id !== id);
          this.cdr.detectChanges();
        },
        error: (err) => alert('Error al eliminar producto')
      });
    }
  }
}
