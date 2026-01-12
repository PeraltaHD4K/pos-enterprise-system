import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const token = localStorage.getItem('auth_token');

  if (req.url.includes('/auth/login') || req.url.includes('/login')) {
    return next(req);
  }

  let authReq = req;
  if (token) {
    authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        console.warn('⚠️ Sesión expirada o inválida. Cerrando sesión...');
        localStorage.removeItem('auth_token');
        router.navigate(['/login']);
      }

      return throwError(() => error);
    })
  );
};
