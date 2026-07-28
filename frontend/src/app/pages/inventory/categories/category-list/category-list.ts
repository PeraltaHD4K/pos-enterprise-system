import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Category, Categoria } from '../../../../core/services/category';
import { ToastService } from '../../../../core/services/toast';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './category-list.html',
  styleUrl: './category-list.css',
})
export class CategoryList implements OnInit {
  private categoryService = inject(Category);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);

  categorias: Categoria[] = [];
  isLoading = true;

  ngOnInit(): void {
    this.cargarCategorias();
  }

  cargarCategorias() {
    this.isLoading = true;
    // Nos suscribimos y guardamos los datos en el array local
    this.categoryService.getAll().subscribe({
      next: (data) => {
        this.categorias = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar categorías', err);
        this.toastService.error('Error al cargar categorías', 'Error de Carga');
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  borrar(id: string) {
    if (confirm('¿Estás seguro de eliminar esta categoría?')) {
      this.categoryService.delete(id).subscribe({
        next: () => {
          // Recargamos la lista "a la fuerza" o filtramos el observable (opción rápida: recargar)
          this.categorias = this.categorias.filter(cat => cat.id !== id);
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.toastService.error('Error al eliminar categoría', 'Error');
          console.error('Error al eliminar categoría', err);
        }
      });
    }
  }
}
