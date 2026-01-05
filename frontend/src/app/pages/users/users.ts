import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { User, Usuario } from '../../core/services/user';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users implements OnInit {
  private userService = inject(User);
  usuarios: Usuario[] = [];

  ngOnInit() {
    this.userService.getUsuarios().subscribe({
      next: (data) => {
        this.usuarios = data;
        console.log('Usuarios recibidos:', data);
      },
      error: (err) => {
        console.error('Error al obtener usuarios:', err);
      }
    });
  }

}
