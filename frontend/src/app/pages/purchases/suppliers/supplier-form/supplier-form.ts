import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { Supplier, ProveedorRequest } from '../../../../core/services/supplier';

@Component({
  selector: 'app-supplier-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './supplier-form.html',
  styleUrl: './supplier-form.css',
})
export class SupplierForm implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private supplierService = inject(Supplier);

  form: FormGroup = this.fb.group({
    empresa: ['', [Validators.required, Validators.minLength(3)]],
    contacto: [''],
    telefono: ['', Validators.required],
    email: ['', Validators.email],
    diaVisita: ['']
  });

  isEditMode = false;
  currentId: number | null = null;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.currentId = Number(id);
      this.cargarDatos(this.currentId);
    }
  }

  cargarDatos(id: number) {
    this.supplierService.getById(id).subscribe({
      next: (prov) => {
        this.form.patchValue(prov); // patchValue mapea automático los nombres iguales
      },
      error: () => this.router.navigate(['/purchases/suppliers'])
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const data: ProveedorRequest = this.form.value;

    if (this.isEditMode && this.currentId) {
      this.supplierService.update(this.currentId, data).subscribe({
        next: () => this.router.navigate(['/purchases/suppliers']),
        error: (err) => alert('Error al actualizar')
      });
    } else {
      this.supplierService.create(data).subscribe({
        next: () => this.router.navigate(['/purchases/suppliers']),
        error: (err) => alert('Error al crear')
      });
    }
  }
}
