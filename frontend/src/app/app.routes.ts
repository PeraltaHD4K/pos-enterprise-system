import { Routes } from '@angular/router';
import { Login } from './pages/auth/login/login';
import { Dashboard } from './pages/dashboard/dashboard';
import { authGuard } from './core/guards/auth-guard';
import { roleGuard } from './core/guards/role-guard';
import { Users } from './pages/users/users';
import { CreateUser } from './pages/users/create-user/create-user';
import { CategoryList } from './pages/inventory/categories/category-list/category-list';
import { CategoryForm } from './pages/inventory/categories/category-form/category-form';
import { ProductList } from './pages/inventory/products/product-list/product-list';
import { ProductForm } from './pages/inventory/products/product-form/product-form';
import { StockAdjustment } from './pages/inventory/products/stock-adjustment/stock-adjustment';
import { SupplierList } from './pages/purchases/suppliers/supplier-list/supplier-list';
import { SupplierForm } from './pages/purchases/suppliers/supplier-form/supplier-form';
import { PurchaseList } from './pages/purchases/purchase-list/purchase-list';
import { PurchaseForm } from './pages/purchases/purchase-form/purchase-form';
import { Pos } from './pages/pos/pos';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' }, // Redirigir raíz a login
    { path: 'login', component: Login },          // La ruta login carga el componente
    {
        path: 'dashboard',
        component: Dashboard,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'GERENTE'] }
    },
    {
        path: 'users',
        component: Users,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN'] }
    },
    {
        path: 'users/create',
        component: CreateUser,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN'] }
    },
    {
        path: 'inventory/categories',
        component: CategoryList,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'GERENTE'] }
    },
    {
        path: 'inventory/categories/create',
        component: CategoryForm,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'GERENTE'] }
    },
    {
        path: 'inventory/categories/edit/:id',
        component: CategoryForm,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'GERENTE'] }
    },
    {
        path: 'inventory/products',
        component: ProductList,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'GERENTE'] }
    },
    {
        path: 'inventory/products/create',
        component: ProductForm,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'GERENTE'] }
    },
    {
        path: 'inventory/products/edit/:id',
        component: ProductForm,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'GERENTE'] }
    },
    {
        path: 'inventory/products/stock/:id', // Recibe el ID del producto
        component: StockAdjustment,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'GERENTE'] }
    },
    {
        path: 'purchases/suppliers',
        component: SupplierList,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'GERENTE'] }
    },
    {
        path: 'purchases/suppliers/create',
        component: SupplierForm,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'GERENTE'] }
    },
    {
        path: 'purchases/suppliers/edit/:id',
        component: SupplierForm,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'GERENTE'] }
    },
    {
        path: 'purchases/list',
        component: PurchaseList,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'GERENTE'] }
    },
    {
        path: 'purchases/create',
        component: PurchaseForm,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'GERENTE'] }
    },
    {
        path: 'purchases/view/:id', // Solo lectura o confirmar recepción
        component: PurchaseForm,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'GERENTE'] }
    },
    {
        path: 'pos',
        component: Pos,
        canActivate: [authGuard]
    }
];