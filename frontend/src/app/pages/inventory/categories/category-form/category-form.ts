import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Category, CategoriaRequest } from '../../../../core/services/category';
import { ToastService } from '../../../../core/services/toast';

@Component({
  selector: 'app-category-form',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './category-form.html',
  styleUrl: './category-form.css',
})
export class CategoryForm implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute); // Para leer la URL
  private categoryService = inject(Category);
  private toastService = inject(ToastService);

  form: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    descripcion: [''] // Opcional
  });

  isEditMode = false;
  currentId: string | null = null;

  ngOnInit(): void {
    // Verificamos si hay un ID en la URL (ej: /edit/5)
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.isEditMode = true;
      this.currentId = id;
      this.cargarDatos(this.currentId);
    }
  }

  cargarDatos(id: string) {
    // Pedimos al backend los datos de la categoría para rellenar el form
    this.categoryService.getById(id).subscribe({
      next: (cat) => {
        // patchValue rellena solo los campos que coincidan
        this.form.patchValue({
          nombre: cat.nombre,
          descripcion: cat.descripcion
        });
      },
      error: (err) => {
        console.error(err);
        this.toastService.error('Error al cargar la categoría', 'Error de Carga');
        this.router.navigate(['/inventory/categories']);
      }
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const data: CategoriaRequest = this.form.value;

    if (this.isEditMode && this.currentId) {
      // MODO EDICIÓN: PUT
      this.categoryService.update(this.currentId, data).subscribe({
        next: () => {
          this.toastService.success('Categoría actualizada', 'Éxito');
          this.router.navigate(['/inventory/categories']);
        },
        error: (err) => this.toastService.error(err.error?.message || 'Error desconocido', 'Error al Actualizar')
      });
    } else {
      // MODO CREACIÓN: POST
      this.categoryService.create(data).subscribe({
        next: () => {
          this.toastService.success('Categoría creada', 'Éxito');
          this.router.navigate(['/inventory/categories']);
        },
        error: (err) => this.toastService.error(err.error?.message || 'Error desconocido', 'Error al Crear')
      });
    }
  }
}
