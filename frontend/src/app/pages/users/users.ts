import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { User, Usuario } from '../../core/services/user';
import { ToastService } from '../../core/services/toast';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users implements OnInit {
  private userService = inject(User);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);

  usuarios: Usuario[] = [];
  isLoading = true;

  ngOnInit() {
    this.cargarUsuarios();
  }

  cargarUsuarios() {
    this.isLoading = true;
    this.userService.getUsuarios().subscribe({
      next: (data) => {
        this.usuarios = data;
        this.isLoading = false;
        this.cdr.detectChanges(); // 👈 Forzamos que Angular se entere
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  alternarEstado(usuario: Usuario) {
    const accion = usuario.activo ? 'desactivar' : 'activar';
    if (!confirm(`¿Estás seguro de ${accion} a ${usuario.username}?`)) return;

    // 1. Optimismo UI: Cambiamos visualmente ANTES de que responda el servidor
    // Esto hace que se sienta instantáneo
    const estadoOriginal = usuario.activo;
    usuario.activo = !usuario.activo;

    this.userService.toggleEstado(usuario.id).subscribe({
      next: () => {
        // Todo salió bien, no hacemos nada porque ya lo actualizamos visualmente
        this.toastService.success('Estado actualizado correctamente', 'Éxito');
      },
      error: (err) => {
        // Si falló, revertimos el cambio visual y avisamos
        usuario.activo = estadoOriginal;
        this.toastService.error(err.error?.message || 'Error desconocido', 'Error al cambiar estado');
        this.cdr.detectChanges();
      }
    });
  }
}
