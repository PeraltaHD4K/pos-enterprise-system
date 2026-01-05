import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Category, CategoriaRequest } from '../../../../core/services/category';

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

  form: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    descripcion: [''] // Opcional
  });

  isEditMode = false;
  currentId: number | null = null;

  ngOnInit(): void {
    // Verificamos si hay un ID en la URL (ej: /edit/5)
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.isEditMode = true;
      this.currentId = Number(id);
      this.cargarDatos(this.currentId);
    }
  }

  cargarDatos(id: number) {
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
        alert('Error al cargar la categoría');
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
        next: () => this.router.navigate(['/inventory/categories']),
        error: (err) => alert('Error al actualizar: ' + (err.error?.message || 'Error desconocido'))
      });
    } else {
      // MODO CREACIÓN: POST
      this.categoryService.create(data).subscribe({
        next: () => this.router.navigate(['/inventory/categories']),
        error: (err) => alert('Error al crear: ' + (err.error?.message || 'Error desconocido'))
      });
    }
  }
}
