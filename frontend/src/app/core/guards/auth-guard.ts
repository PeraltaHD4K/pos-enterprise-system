import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  // 1. Inyectamos el Router para poder redirigir
  const router = inject(Router);

  // 2. Buscamos el token en el bolsillo del navegador
  const token = localStorage.getItem('auth_token');

  if (token) {
    // 3. Si hay token, ¡ADELANTE!
    return true;
  } else {
    // 4. Si no hay token, ¡FUERA! (Al login)
    router.navigate(['/login']);
    return false;
  }
};
