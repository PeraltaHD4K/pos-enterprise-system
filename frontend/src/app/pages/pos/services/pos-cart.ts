import { Injectable, computed, inject, signal } from '@angular/core';
import { Sale, VentaRequest } from '../../../core/services/sale';
import { Producto } from '../../../core/services/product';
import { Cliente } from '../../../core/services/client';
import { Observable, tap } from 'rxjs';

export interface CartItem {
  producto: Producto;
  cantidad: number;
  subtotal: number;
}

@Injectable({
  providedIn: 'root',
})
export class PosCartService {
  private saleService = inject(Sale);

  // === 1. STATE (SIGNALS) ===
  // La fuente de la verdad. Si esto cambia, todo se actualiza solo.
  readonly items = signal<CartItem[]>([]);
  readonly client = signal<Cliente | null>(null);

  // === 2. COMPUTED (Cálculos Automáticos) ===
  // Se recalculan SOLO cuando 'items' cambia.
  readonly total = computed(() =>
    this.items().reduce((acc, item) => acc + item.subtotal, 0)
  );

  readonly totalItems = computed(() =>
    this.items().reduce((acc, item) => acc + item.cantidad, 0)
  );

  // === 3. ACTIONS (Métodos que modifican el estado) ===

  addItem(producto: Producto) {
    const currentItems = this.items();
    const existingIndex = currentItems.findIndex(i => i.producto.id === producto.id);

    if (existingIndex >= 0) {
      // Si ya existe, aumentamos cantidad
      this.updateQuantity(existingIndex, currentItems[existingIndex].cantidad + 1);
    } else {
      // Si es nuevo, lo agregamos (Validando stock inicial)
      if (producto.stockActual < 1) {
        alert('⚠️ Stock insuficiente'); // Idealmente usar Toast aquí después
        return;
      }

      // IMPORTANTE: En Signals siempre creamos un NUEVO array (inmutabilidad)
      this.items.update(items => [
        ...items,
        {
          producto,
          cantidad: 1,
          subtotal: producto.precioVenta
        }
      ]);
    }
  }

  updateQuantity(index: number, newQuantity: number) {
    this.items.update(items => {
      const item = items[index];

      // 1. Validaciones
      if (newQuantity <= 0) {
        // Si baja a 0, lo sacamos (filter)
        return items.filter((_, i) => i !== index);
      }

      if (newQuantity > item.producto.stockActual) {
        alert('⚠️ Stock insuficiente (Máx: ' + item.producto.stockActual + ')');
        return items; // Retornamos el mismo estado sin cambios
      }

      // 2. Actualización inmutable
      // Creamos copia del array
      const updatedItems = [...items];
      // Creamos copia del item modificado
      updatedItems[index] = {
        ...item,
        cantidad: newQuantity,
        subtotal: newQuantity * item.producto.precioVenta
      };

      return updatedItems;
    });
  }

  removeItem(index: number) {
    this.items.update(items => items.filter((_, i) => i !== index));
  }

  setClient(client: Cliente | null) {
    this.client.set(client);
  }

  clear() {
    this.items.set([]);
    this.client.set(null);
  }

  // === 4. CHECKOUT (Transacción) ===

  checkout(metodoPago: 'EFECTIVO' | 'TARJETA' | 'TRANSFERENCIA', montoPagado: number): Observable<any> {
    const payload: VentaRequest = {
      items: this.items().map(i => ({
        productoId: i.producto.id,
        cantidad: i.cantidad
      })),
      metodoPago,
      montoPagado,
      // Usamos el ID del cliente seleccionado o null (el backend manejará el default)
      clienteId: this.client()?.id
    };

    return this.saleService.registrarVenta(payload).pipe(
      // Si sale bien, limpiamos el carrito automáticamente
      tap(() => this.clear())
    );
  }
}
