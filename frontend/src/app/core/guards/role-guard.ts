import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '../services/auth';

export const roleGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const authService = inject(Auth);

  // 1. Obtener roles esperados desde la ruta (data: { roles: [...] })
  const expectedRoles = route.data['roles'] as Array<string>;

  // 2. Verificar permisos
  if (authService.hasRole(expectedRoles)) {
    return true;
  }

  // 3. Si falla, redirigir a zona segura
  // alert('⛔ Acceso denegado: No tienes permisos suficientes.');

  if (authService.isAuthenticated()) {
    router.navigate(['/pos']); // Si ya entró, mándalo al POS
  } else {
    router.navigate(['/login']); // Si no, al login
  }

  return false;
};
