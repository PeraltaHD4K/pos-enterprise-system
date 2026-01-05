import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { Purchase, CompraRequest } from '../../../core/services/purchase';
import { Supplier, Proveedor } from '../../../core/services/supplier';
import { Product, Producto } from '../../../core/services/product';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-purchase-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './purchase-form.html',
  styles: ``
})
export class PurchaseForm implements OnInit {
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  private purchaseService = inject(Purchase);
  private supplierService = inject(Supplier);
  private productService = inject(Product);

  // 1. FORMULARIO PRINCIPAL (Cabecera)
  form: FormGroup = this.fb.group({
    proveedorId: ['', Validators.required],
    folioFactura: ['', Validators.required],
    fechaEstimadaEntrega: [''],
    observaciones: [''],
    estado: ['COMPLETADA', Validators.required]
  });

  // 2. FORMULARIO TEMPORAL (Para la caja gris de agregar items)
  itemForm: FormGroup = this.fb.group({
    productoId: ['', Validators.required],
    cantidadPedida: [1, [Validators.required, Validators.min(1)]],
    unidadesPorCaja: [1, [Validators.required, Validators.min(1)]],
    costoTotal: [0, [Validators.required, Validators.min(0)]]
  });

  // VARIABLES DE ESTADO
  items: any[] = []; // El carrito visual
  suppliers$: Observable<Proveedor[]> | undefined;
  products$: Observable<Producto[]> | undefined;

  productoSeleccionado: Producto | null = null; // Para recordar el costo histórico
  totalCalculado = 0;
  isSubmitting = false;
  isViewMode = false; // Controla si es modo lectura o escritura

  ngOnInit(): void {
    // Cargar catálogos
    this.suppliers$ = this.supplierService.getAll();
    this.products$ = this.productService.getAll();

    // Verificar si es MODO VER (Lectura)
    const id = this.route.snapshot.paramMap.get('id');
    const url = this.router.url;

    if (id) {
      if (url.includes('view')) {
        this.isViewMode = true;
        this.form.disable();     // Bloquear cabecera
        this.itemForm.disable(); // Bloquear formulario de items
      }
      this.cargarCompra(Number(id));
    }
  }

  // --- LÓGICA DE CÁLCULO DE PRECIOS (UX) ---

  // Se ejecuta cuando eliges un producto del dropdown
  onProductoChange() {
    const id = Number(this.itemForm.get('productoId')?.value);

    // Buscamos el producto completo para tener su 'ultimoCostoCompra'
    this.products$?.subscribe(lista => {
      const encontrado = lista.find(p => p.id === id);
      if (encontrado) {
        this.productoSeleccionado = encontrado;
        this.calcularCostoSugerido(); // Recalcular inmediatamente
      }
    });
  }

  // Se ejecuta al cambiar cantidad, unidades o producto
  calcularCostoSugerido() {
    // Si estamos en modo ver o no hay producto, no hacemos nada
    if (this.isViewMode || !this.productoSeleccionado) return;

    const cantidad = this.itemForm.get('cantidadPedida')?.value || 0;
    const cajas = this.itemForm.get('unidadesPorCaja')?.value || 1;

    // Obtenemos el último costo histórico (si existe)
    const ultimoCosto = (this.productoSeleccionado as any).ultimoCostoCompra || 0;

    if (ultimoCosto > 0) {
      const totalPiezas = cantidad * cajas;
      const estimado = totalPiezas * ultimoCosto;

      // Pre-llenamos el campo. El usuario puede borrarlo si el precio cambió.
      this.itemForm.patchValue({ costoTotal: estimado });
    }
  }

  // --- LÓGICA DEL CARRITO (Agregar/Quitar) ---

  agregarItem() {
    if (this.itemForm.invalid) {
      this.itemForm.markAllAsTouched();
      return;
    }

    const { productoId, cantidadPedida, unidadesPorCaja, costoTotal } = this.itemForm.value;

    // Volvemos a buscar el producto solo para obtener el nombre visualmente para la tabla
    this.products$?.subscribe(lista => {
      const prod = lista.find(p => p.id == productoId);

      if (prod) {
        const nuevoItem = {
          productoId: Number(productoId),
          nombreProducto: prod.nombre,
          sku: prod.sku,
          cantidadPedida: Number(cantidadPedida),
          unidadesPorCaja: Number(unidadesPorCaja),
          costoTotal: Number(costoTotal),
          // Dato meramente visual para la tabla
          costoUnitarioReal: Number(costoTotal) / (Number(cantidadPedida) * Number(unidadesPorCaja))
        };

        this.items.push(nuevoItem);
        this.calcularTotalGeneral();

        // Limpiamos el form temporal para el siguiente
        this.itemForm.reset({
          productoId: '',
          cantidadPedida: 1,
          unidadesPorCaja: 1,
          costoTotal: 0
        });
        this.productoSeleccionado = null;
      }
    });
  }

  eliminarItem(index: number) {
    this.items.splice(index, 1);
    this.calcularTotalGeneral();
  }

  calcularTotalGeneral() {
    this.totalCalculado = this.items.reduce((acc, item) => acc + item.costoTotal, 0);
  }

  // --- CARGA DE DATOS (MODO VER) ---

  cargarCompra(id: number) {
    this.purchaseService.getById(id).subscribe({
      next: (compra) => {
        // Llenar Cabecera
        this.form.patchValue({
          proveedorId: compra.proveedor.id,
          folioFactura: compra.folioFactura,
          estado: compra.estado,
          observaciones: compra.observaciones
        });

        // Llenar Tabla (Mapeando del Backend al Frontend)
        if (compra.detalles) {
          this.items = compra.detalles.map(d => ({
            productoId: d.producto.id,
            nombreProducto: d.producto.nombre, // Ojo: tu backend debe enviar el objeto producto anidado
            sku: d.producto.sku,
            cantidadPedida: d.cantidadPedida,
            unidadesPorCaja: d.unidadesPorCaja,
            costoTotal: d.costoTotalRenglon,
            costoUnitarioReal: d.costoUnitarioCalculado
          }));
          this.calcularTotalGeneral();
        }
      },
      error: () => {
        alert('Error al cargar la compra');
        this.router.navigate(['/purchases/list']);
      }
    });
  }

  // --- GUARDADO FINAL ---

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (this.items.length === 0) {
      alert('Debes agregar al menos un producto.');
      return;
    }

    this.isSubmitting = true;

    // Armamos el JSON final
    const payload: CompraRequest = {
      ...this.form.value,
      proveedorId: Number(this.form.value.proveedorId),
      items: this.items.map(i => ({
        productoId: i.productoId,
        cantidadPedida: i.cantidadPedida,
        unidadesPorCaja: i.unidadesPorCaja,
        costoTotal: i.costoTotal,
        // Si es completada, asumimos recepción total
        cantidadRecibida: this.form.value.estado === 'COMPLETADA' ? i.cantidadPedida : 0
      }))
    };

    this.purchaseService.create(payload).subscribe({
      next: (res) => {
        alert('Compra registrada. Folio interno: ' + res.id);
        this.router.navigate(['/purchases/list']);
      },
      error: (err) => {
        console.error(err);
        alert('Error: ' + (err.error?.message || 'Ocurrió un error desconocido'));
        this.isSubmitting = false;
      }
    });
  }
}