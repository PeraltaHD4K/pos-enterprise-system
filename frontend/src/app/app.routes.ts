import { Routes } from '@angular/router';
import { Login } from './pages/auth/login/login';
import { Dashboard } from './pages/dashboard/dashboard';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' }, // Redirigir raíz a login
    { path: 'login', component: Login },          // La ruta login carga el componente
    {
        path: 'dashboard',
        component: Dashboard,
        canActivate: [authGuard]
    }
];