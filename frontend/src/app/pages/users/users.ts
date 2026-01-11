import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { User, Usuario } from '../../core/services/user';

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
        console.log('Estado actualizado correctamente');
      },
      error: (err) => {
        // Si falló, revertimos el cambio visual y avisamos
        usuario.activo = estadoOriginal;
        alert('Error al cambiar estado: ' + (err.error?.message || 'Error desconocido'));
        this.cdr.detectChanges();
      }
    });
  }
}
