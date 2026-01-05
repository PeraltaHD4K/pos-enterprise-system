import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Para usar ngModel
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule], // Importamos los módulos necesarios
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {
  // Objeto para guardar los datos del formulario
  loginData = {
    username: '', // Ojo: Asegúrate si tu backend espera 'username' o 'email'
    password: ''
  };

  constructor(private http: HttpClient, private router: Router) { }

  onSubmit() {
    console.log('Intentando loguear con:', this.loginData);

    // URL de tu Backend (Ajusta la ruta exacta según tu Controller)
    const url = `${environment.apiUrl}/auth/login`;

    this.http.post(url, this.loginData).subscribe({
      next: (response: any) => {
        console.log('¡Login Exitoso!', response);
        // 1. Guardar el token en el navegador
        if (response.token) {
          localStorage.setItem('auth_token', response.token);
        }

        // 2. Redirigir al Dashboard
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        console.error('Error de Login:', error);
        alert('Credenciales incorrectas o error de servidor.');
      }
    });
  }
}