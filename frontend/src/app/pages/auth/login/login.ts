import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Para usar ngModel
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../../environments/environment';
import { Auth } from '../../../core/services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule], // Importamos los módulos necesarios
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {
  private authService = inject(Auth);
  private router = inject(Router);
  username = '';
  password = '';

  // Variable para mostrar spinner de carga
  isLoading = false;

  onLogin() {
    // Validación simple
    if (!this.username || !this.password) {
      alert('Por favor ingresa usuario y contraseña');
      return;
    }

    this.isLoading = true;

    // Preparamos el objeto que pide el servicio
    const credentials = {
      username: this.username,
      password: this.password
    };

    // Llamamos al AuthService
    this.authService.login(credentials).subscribe({
      next: () => {
        this.isLoading = false;

        // --- LÓGICA DE REDIRECCIÓN POR ROL ---
        const role = this.authService.getRole();

        // Si es CAJERO -> directo a vender
        if (role === 'CAJERO' || role === 'ROLE_CAJERO') {
          this.router.navigate(['/pos']);
        }
        // Si es ADMIN o GERENTE -> al dashboard
        else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
        alert('❌ Credenciales incorrectas o error de conexión');
      }
    });
  }
}