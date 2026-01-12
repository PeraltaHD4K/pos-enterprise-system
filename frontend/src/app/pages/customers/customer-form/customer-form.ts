import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { Client } from '../../../core/services/client';
import { ToastService } from '../../../core/services/toast';

@Component({
  selector: 'app-customer-form',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './customer-form.html',
  styleUrl: './customer-form.css',
})
export class CustomerForm implements OnInit {
  private fb = inject(FormBuilder);
  private clientService = inject(Client);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private toastService = inject(ToastService);

  form: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    telefono: ['', [Validators.pattern('^[0-9]+$')]], // Solo números
    email: ['', [Validators.email]]
  });

  isEditMode = signal(false); // Signal para controlar UI
  clientId: number | null = null;
  isLoading = signal(false);

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode.set(true);
      this.clientId = Number(id);
      this.cargarDatos(this.clientId);
    }
  }

  cargarDatos(id: number) {
    this.isLoading.set(true);
    this.form.disable(); // Deshabilitar mientras carga

    this.clientService.getById(id).subscribe({
      next: (cliente) => {
        this.form.patchValue({
          nombre: cliente.nombre,
          telefono: cliente.telefono,
          email: cliente.email
        });
        this.form.enable();
        this.isLoading.set(false);
      },
      error: () => {
        this.toastService.error('Error cargando cliente', 'Error de Datos');
        this.router.navigate(['/customers']);
      }
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.form.disable(); // Evitar doble submit

    const dto = this.form.value;

    const request$ = this.isEditMode() && this.clientId
      ? this.clientService.update(this.clientId, dto)
      : this.clientService.create(dto);

    request$.subscribe({
      next: () => {
        this.toastService.success('Cliente guardado correctamente', 'Éxito');
        this.router.navigate(['/customers']);
      },
      error: (err) => {
        console.error(err);
        this.toastService.error(err.error?.message || 'No se pudo guardar', 'Error de Guardado');
        this.isLoading.set(false);
        this.form.enable();
      }
    });
  }
}
