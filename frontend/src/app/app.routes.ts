import { Routes } from '@angular/router';
import { Login } from './pages/auth/login/login';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' }, // Redirigir raíz a login
    { path: 'login', component: Login }          // La ruta login carga el componente
];