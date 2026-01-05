import { Routes } from '@angular/router';
import { Login } from './pages/auth/login/login';
import { Dashboard } from './pages/dashboard/dashboard';
import { authGuard } from './core/guards/auth-guard';
import { Users } from './pages/users/users';
import { CreateUser } from './pages/users/create-user/create-user';
import { CategoryList } from './pages/inventory/categories/category-list/category-list';
import { CategoryForm } from './pages/inventory/categories/category-form/category-form';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' }, // Redirigir raíz a login
    { path: 'login', component: Login },          // La ruta login carga el componente
    {
        path: 'dashboard',
        component: Dashboard,
        canActivate: [authGuard]
    },
    {
        path: 'users',
        component: Users,
        canActivate: [authGuard]
    },
    {
        path: 'users/create',
        component: CreateUser,
        canActivate: [authGuard]
    },
    {
        path: 'inventory/categories',
        component: CategoryList,
        canActivate: [authGuard]
    },
    {
        path: 'inventory/categories/create',
        component: CategoryForm,
        canActivate: [authGuard]
    },
    {
        path: 'inventory/categories/edit/:id',
        component: CategoryForm,
        canActivate: [authGuard]
    }
];