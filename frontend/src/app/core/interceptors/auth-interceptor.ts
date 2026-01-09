import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('auth_token');

  // 2. VALIDAR SI ES LA RUTA DE LOGIN
  // Si la petición va hacia "/auth/login" (o como se llame tu endpoint), NO pegues el token.
  if (req.url.includes('/auth/login') || req.url.includes('/login')) {
    return next(req);
  }

  // 3. SI NO ES LOGIN, PEGAR EL TOKEN (Si existe)
  let authReq = req;
  if (token) {
    authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
  }

  return next(authReq);
};
