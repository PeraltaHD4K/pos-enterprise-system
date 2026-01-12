import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';
import { roleGuard } from './core/guards/role-guard';
import { MainLayout } from './shared/components/main-layout/main-layout';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
    },
    {
        path: 'login',
        loadComponent: () => import('./pages/auth/login/login').then(m => m.Login)
    },
    {
        path: 'pos',
        loadComponent: () => import('./pages/pos/pos').then(m => m.Pos),
        canActivate: [authGuard]
    },
    {
        path: '',
        component: MainLayout,
        canActivate: [authGuard],
        children: [
            {
                path: 'dashboard',
                loadComponent: () => import('./pages/dashboard/dashboard').then(m => m.Dashboard),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'users',
                loadComponent: () => import('./pages/users/users').then(m => m.Users),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN'] }
            },
            {
                path: 'users/create',
                loadComponent: () => import('./pages/users/create-user/create-user').then(m => m.CreateUser),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN'] }
            },
            {
                path: 'users/edit/:id',
                loadComponent: () => import('./pages/users/create-user/create-user').then(m => m.CreateUser),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN'] }
            },
            {
                path: 'inventory/categories',
                loadComponent: () => import('./pages/inventory/categories/category-list/category-list').then(m => m.CategoryList),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'inventory/categories/create',
                loadComponent: () => import('./pages/inventory/categories/category-form/category-form').then(m => m.CategoryForm),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'inventory/categories/edit/:id',
                loadComponent: () => import('./pages/inventory/categories/category-form/category-form').then(m => m.CategoryForm),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'inventory/products',
                loadComponent: () => import('./pages/inventory/products/product-list/product-list').then(m => m.ProductList),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'inventory/products/create',
                loadComponent: () => import('./pages/inventory/products/product-form/product-form').then(m => m.ProductForm),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'inventory/products/edit/:id',
                loadComponent: () => import('./pages/inventory/products/product-form/product-form').then(m => m.ProductForm),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'inventory/products/stock/:id', // Recibe el ID del producto
                loadComponent: () => import('./pages/inventory/products/stock-adjustment/stock-adjustment').then(m => m.StockAdjustment),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'purchases/suppliers',
                loadComponent: () => import('./pages/purchases/suppliers/supplier-list/supplier-list').then(m => m.SupplierList),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'purchases/suppliers/create',
                loadComponent: () => import('./pages/purchases/suppliers/supplier-form/supplier-form').then(m => m.SupplierForm),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'purchases/suppliers/edit/:id',
                loadComponent: () => import('./pages/purchases/suppliers/supplier-form/supplier-form').then(m => m.SupplierForm),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'purchases/list',
                loadComponent: () => import('./pages/purchases/purchase-list/purchase-list').then(m => m.PurchaseList),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'purchases/create',
                loadComponent: () => import('./pages/purchases/purchase-form/purchase-form').then(m => m.PurchaseForm),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'purchases/view/:id', // Solo lectura o confirmar recepción
                loadComponent: () => import('./pages/purchases/purchase-form/purchase-form').then(m => m.PurchaseForm),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'customers',
                loadComponent: () => import('./pages/customers/customer-list/customer-list').then(m => m.CustomerList),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE', 'CAJERO'] }
            },
            {
                path: 'customers/create',
                loadComponent: () => import('./pages/customers/customer-form/customer-form').then(m => m.CustomerForm),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE', 'CAJERO'] }
            },
            {
                path: 'customers/edit/:id',
                loadComponent: () => import('./pages/customers/customer-form/customer-form').then(m => m.CustomerForm),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN', 'GERENTE'] }
            },
            {
                path: 'settings',
                loadComponent: () => import('./pages/settings/settings').then(m => m.Settings),
                canActivate: [authGuard, roleGuard],
                data: { roles: ['ADMIN'] }
            },
        ]
    },
    {
        path: 'forbidden',
        loadComponent: () => import('./pages/forbidden/forbidden').then(m => m.Forbidden)
    },
    {
        path: '**',
        loadComponent: () => import('./pages/not-found/not-found').then(m => m.NotFound)
    }
];