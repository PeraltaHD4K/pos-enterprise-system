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
    if (producto.stockActual < 1) {
      // Opcional: Mostrar alerta aquí o dejar que la UI maneje el visual (gris)
      return;
    }

    this.items.update(items => {
      const index = items.findIndex(i => i.producto.id === producto.id);

      if (index >= 0) {
        // Si ya existe, verificamos si podemos sumar 1 más
        const currentItem = items[index];
        if (currentItem.cantidad + 1 > producto.stockActual) {
          alert('⚠️ Stock insuficiente');
          return items; // Retornamos sin cambios
        }

        // Creamos copia segura para inmutabilidad
        const updated = [...items];
        updated[index] = {
          ...currentItem,
          cantidad: currentItem.cantidad + 1,
          subtotal: (currentItem.cantidad + 1) * producto.precioVenta
        };
        return updated;
      } else {
        // Si es nuevo
        return [...items, {
          producto,
          cantidad: 1,
          subtotal: producto.precioVenta
        }];
      }
    });
  }

  updateQuantity(index: number, newQuantity: number) {
    this.items.update(items => {
      const item = items[index];

      // 1. Si baja a 0 o menos, eliminar
      if (newQuantity <= 0) {
        return items.filter((_, i) => i !== index);
      }

      // 2. Validación de Stock con Ajuste (Clamping)
      // Si el usuario pide 50 y hay 10, le ponemos 10 y avisamos.
      if (newQuantity > item.producto.stockActual) {
        alert(`⚠️ Stock insuficiente. Máximo disponible: ${item.producto.stockActual}`);
        newQuantity = item.producto.stockActual; // Ajustamos al máximo posible
      }

      // 3. Actualización inmutable
      const updated = [...items];
      updated[index] = {
        ...item,
        cantidad: newQuantity,
        subtotal: newQuantity * item.producto.precioVenta
      };

      return updated;
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
