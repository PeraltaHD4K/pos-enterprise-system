import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { Product, ProductoRequest } from '../../../../core/services/product';
import { Category, Categoria } from '../../../../core/services/category';
import { Observable } from 'rxjs';
import { ToastService } from '../../../../core/services/toast';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './product-form.html',
  styleUrl: './product-form.css',
})
export class ProductForm implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  private productService = inject(Product);
  private categoryService = inject(Category); // <--- Necesario para el dropdown
  private toastService = inject(ToastService);

  form: FormGroup = this.fb.group({
    sku: ['', Validators.required],
    codigoBarras: ['', Validators.required],
    nombre: ['', Validators.required],
    descripcion: [''],
    precioVenta: [0, [Validators.required, Validators.min(0)]],
    costoPromedio: [0, [Validators.required, Validators.min(0)]],
    stockMinimo: [5, [Validators.required, Validators.min(0)]],
    categoriaId: ['', Validators.required] // El select guardará el ID aquí
  });

  isEditMode = false;
  currentId: string | null = null;
  categorias$: Observable<Categoria[]> | undefined; // Observable para llenar el select

  ngOnInit(): void {
    // 1. Cargar la lista de categorías para el desplegable
    this.categorias$ = this.categoryService.getAll();

    // 2. Verificar si es edición
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.currentId = id;
      this.cargarDatos(this.currentId);
    }
  }

  cargarDatos(id: string) {
    this.productService.getById(id).subscribe({
      next: (prod) => {
        // Rellenamos el formulario
        this.form.patchValue({
          sku: prod.sku,
          codigoBarras: prod.codigoBarras,
          nombre: prod.nombre,
          descripcion: prod.descripcion,
          precioVenta: prod.precioVenta,
          costoPromedio: prod.costoPromedio,
          stockMinimo: prod.stockMinimo,
          categoriaId: prod.categoria?.id // Extraemos el ID del objeto categoría
        });
      },
      error: () => {
        this.toastService.error('Error al cargar producto', 'Error de Carga');
        this.router.navigate(['/inventory/products']);
      }
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const data: ProductoRequest = this.form.value;

    if (this.isEditMode && this.currentId) {
      this.productService.update(this.currentId, data).subscribe({
        next: () => {
          this.toastService.success('Producto actualizado', 'Éxito');
          this.router.navigate(['/inventory/products']);
        },
        error: (err) => this.toastService.error(err.error?.message || 'Error desconocido', 'Error al Actualizar')
      });
    } else {
      this.productService.create(data).subscribe({
        next: () => {
          this.toastService.success('Producto creado', 'Éxito');
          this.router.navigate(['/inventory/products']);
        },
        error: (err) => this.toastService.error(err.error?.message || 'Error desconocido', 'Error al Crear')
      });
    }
  }
}
