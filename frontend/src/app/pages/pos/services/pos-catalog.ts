import { Injectable, inject, signal } from '@angular/core';
import { Product, Producto } from '../../../core/services/product';

@Injectable({
  providedIn: 'root',
})
export class PosCatalog {
  private productService = inject(Product);

  // === ESTADO (Signals) ===
  // 1. Lista cruda (Cache en memoria)
  private allProducts: Producto[] = [];

  // 2. Lista filtrada (Lo que ve el usuario)
  readonly products = signal<Producto[]>([]);

  // 3. Estado de carga
  readonly isLoading = signal<boolean>(false);

  // === ACCIONES ===

  loadProducts() {
    this.isLoading.set(true);
    // TODO: implement dynamic search or lazy loading
    this.productService.getAll(0, 1000).subscribe({
      next: (data) => {
        // Guardamos solo productos activos y con stock
        this.allProducts = data.content.filter((p: Producto) => p.activo);
        // Inicializamos la vista con todo
        this.products.set(this.allProducts);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error cargando catálogo', err);
        this.isLoading.set(false);
      }
    });
  }

  search(term: string) {
    if (!term) {
      this.products.set(this.allProducts);
      return;
    }

    const lower = term.toLowerCase();

    // Filtrado optimizado
    const filtered = this.allProducts.filter(p => {
      // Búsqueda por Nombre, Código o SKU
      const nombre = (p.nombre || '').toLowerCase();
      const codigo = (p.codigoBarras || '').toLowerCase();
      const sku = (p.sku || '').toLowerCase();
      return nombre.includes(lower) || codigo.includes(lower) || sku.includes(lower);
    });

    this.products.set(filtered);
  }

  // Helper para detectar "Scanner"
  // Retorna el producto si es un match exacto único (para auto-agregar)
  checkExactMatch(term: string): Producto | null {
    const current = this.products();
    if (current.length === 1) {
      const p = current[0];
      // Si el término es idéntico al código o SKU
      if (p.codigoBarras === term || p.sku === term) {
        return p;
      }
    }
    return null;
  }

  // Refrescar stock visualmente (útil después de una venta)
  refresh() {
    this.loadProducts();
  }
}
