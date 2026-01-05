import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { User, UsuarioRegistro } from '../../../core/services/user';
import { RolService, Rol } from '../../../core/services/rol';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
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

  // Inyectamos los servicios con sus nombres de clase correctos
  private rolService = inject(RolService);
  private userService = inject(User);

  form!: FormGroup;
  roles$: Observable<Rol[]> | undefined; // Aquí usamos la Interfaz Rol

  ngOnInit(): void {
    // 1. Validaciones del formulario
    this.form = this.fb.group({
      nombreCompleto: ['', Validators.required],
      username: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rolId: ['', Validators.required]
    });

    // 2. Cargar roles al iniciar
    this.roles$ = this.rolService.getRoles();
  }

  onSubmit() {
    if (this.form.valid) {
      // Convertimos el ID del rol a número por si el HTML lo manda como texto
      const formValue = this.form.value;
      formValue.rolId = Number(formValue.rolId);

      const nuevoUsuario: UsuarioRegistro = formValue;

      this.userService.crearUsuario(nuevoUsuario).subscribe({
        next: () => {
          this.router.navigate(['/users']);
        },
        error: (err) => {
          console.error('Error creando usuario', err);
          // Muestra el mensaje del backend si existe, o uno genérico
          alert('Error: ' + (err.error?.message || 'No se pudo crear el usuario'));
        }
      });
    } else {
      // Si el usuario da click sin llenar, marcamos todo como "tocado" para mostrar errores rojos
      this.form.markAllAsTouched();
    }
  }
}
