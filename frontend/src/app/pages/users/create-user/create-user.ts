import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { User, UsuarioRegistro, UsuarioEdicion } from '../../../core/services/user';
import { RolService, Rol } from '../../../core/services/rol';
import { ToastService } from '../../../core/services/toast';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-create-user',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './create-user.html',
  styleUrl: './create-user.css',
})
export class CreateUser implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private rolService = inject(RolService);
  private userService = inject(User);
  private toastService = inject(ToastService);

  form!: FormGroup;
  roles$: Observable<Rol[]> | undefined;

  isEditMode = false;
  userId: string | null = null;

  ngOnInit(): void {
    // 1. Inicializar Formulario
    this.form = this.fb.group({
      nombreCompleto: ['', Validators.required],
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rolId: ['', Validators.required]
    });

    this.roles$ = this.rolService.getRoles();

    // 2. Detectar si estamos editando
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.userId = id;
        this.cargarDatosUsuario(this.userId);

        // En modo edición, la contraseña es opcional
        this.form.get('password')?.clearValidators();
        this.form.get('password')?.updateValueAndValidity();
      }
    });
  }

  cargarDatosUsuario(id: string) {
    this.userService.getUsuario(id).subscribe({
      next: (u) => {
        // Llenar el formulario
        this.form.patchValue({
          nombreCompleto: u.nombreCompleto,
          username: u.username,
          rolId: u.rol?.id,
          password: '' // Contraseña vacía por seguridad
        });
      },
      error: () => {
        this.toastService.error('Error cargando usuario', 'Error de Datos');
        this.router.navigate(['/users']);
      }
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const formValue = this.form.value;
    formValue.rolId = Number(formValue.rolId);

    if (this.isEditMode && this.userId) {
      // --- MODO EDICIÓN ---
      const datosEdicion: UsuarioEdicion = {
        nombreCompleto: formValue.nombreCompleto,
        username: formValue.username,
        rolId: formValue.rolId,
        password: formValue.password || undefined // Si está vacío, undefined para que el backend lo ignore
      };

      this.userService.actualizarUsuario(this.userId, datosEdicion).subscribe({
        next: () => {
          this.toastService.success('Usuario actualizado', 'Éxito');
          this.router.navigate(['/users']);
        },
        error: (err) => this.toastService.error(err.error?.message || '', 'Error al actualizar')
      });

    } else {
      // --- MODO CREACIÓN ---
      this.userService.crearUsuario(formValue).subscribe({
        next: () => {
          this.toastService.success('Usuario creado', 'Éxito');
          this.router.navigate(['/users']);
        },
        error: (err) => this.toastService.error(err.error?.message || '', 'Error al crear')
      });
    }
  }
}
