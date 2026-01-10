import { Component, inject, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { CashRegister, SesionCaja } from '../../core/services/cash-register';
import { Product, Producto } from '../../core/services/product';
import { Sale, VentaRequest } from '../../core/services/sale';
import { Auth } from '../../core/services/auth';

import { PosHeader } from './components/pos-header/pos-header';
import { PosProductList } from './components/pos-product-list/pos-product-list';
import { PosCart, CartItem } from './components/pos-cart/pos-cart';
import { PosHistoryModal } from './components/pos-history-modal/pos-history-modal';
import { PosTicketModal } from './components/pos-ticket-modal/pos-ticket-modal';
import { PosCheckoutModal } from './components/pos-checkout-modal/pos-checkout-modal';
import { PosCloseModal } from './components/pos-close-modal/pos-close-modal';
import { PosSummaryModal } from './components/pos-summary-modal/pos-summary-modal';

@Component({
  selector: 'app-pos',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    PosHeader,
    PosProductList,
    PosCart,
    PosHistoryModal,
    PosTicketModal,
    PosCheckoutModal,
    PosCloseModal,
    PosSummaryModal,
  ],
  templateUrl: './pos.html',
  styleUrl: './pos.css',
})
export class Pos implements OnInit {
  private cashRegisterService = inject(CashRegister);
  private productService = inject(Product);
  private saleService = inject(Sale);
  private fb = inject(FormBuilder);
  private cdr = inject(ChangeDetectorRef);
  private authService = inject(Auth);

  // Estado general
  isLoading = true;
  sesionActual: SesionCaja | null = null;
  username: string = '';

  // Estado Productos
  allProducts: Producto[] = [];
  filteredProducts: Producto[] = [];
  searchTerm: string = '';

  // Estado Carrito
  cart: CartItem[] = [];
  total: number = 0;

  // Estado modales
  showHistoryModal = false;
  historialVentas: any[] = [];
  isLoadingHistory = false;

  showTicketModal = false;
  ticketContent: string = '';

  showCheckoutModal = false;
  isProcessingSale = false;

  showCloseModal = false;
  resumenCierre: SesionCaja | null = null;

  // Formulario de apertura
  formApertura: FormGroup = this.fb.group({
    saldoInicial: [0, [Validators.required, Validators.min(0)]]
  });

  ngOnInit(): void {
    this.username = this.authService.getUsername() || 'Usuario';
    this.verificarEstadoCaja();
  }

  // --- 1. GESTION DE CAJA ---
  verificarEstadoCaja() {
    this.isLoading = true;
    this.cashRegisterService.getEstado().subscribe({
      next: (sesion) => {
        this.sesionActual = sesion || null;
        if (this.sesionActual) {
          this.cargarProductos();
        }

        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.sesionActual = null;
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  abrirCaja() {
    if (this.formApertura.invalid) return;
    const saldo = this.formApertura.value.saldoInicial;

    this.isLoading = true;
    this.cashRegisterService.abrir({ saldoInicial: saldo }).subscribe({
      next: (nuevaSesion) => {
        this.sesionActual = nuevaSesion;
        this.cargarProductos();
        alert('✅ Caja abierta correctamente. ¡Buen turno!');
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        alert('Error: ' + err.error?.message);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // --- 2. CATÁLOGO Y BÚSQUEDA ---
  cargarProductos() {
    this.productService.getAll().subscribe({
      next: (data) => {
        this.allProducts = data.filter(p => p.activo && p.stockActual > 0);
        this.searchTerm = '';
        this.filteredProducts = [...this.allProducts];
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error cargando productos', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onSearch(term: string) {
    this.searchTerm = term;
    if (!term) {
      this.filteredProducts = this.allProducts;
      return;
    }

    const lower = term.toLowerCase();
    this.filteredProducts = this.allProducts.filter(p => {
      const nombre = (p.nombre || '').toLowerCase();
      const codigo = (p.codigoBarras || '').toLowerCase();
      const sku = (p.sku || '').toLowerCase();

      return nombre.includes(lower) || codigo.includes(lower) || sku.includes(lower);
    });

    // Lógica de "Escaner Exacto" (Auto-agregar si es código de barras único)
    if (this.filteredProducts.length === 1) {
      const p = this.filteredProducts[0];
      if (p.codigoBarras === term || p.sku === term) {
        this.addToCart(p);
        this.searchTerm = '';
        this.filteredProducts = this.allProducts;
        this.cdr.detectChanges();
      }
    }
  }

  // --- 3. CARRITO DE COMPRAS ---
  addToCart(product: Producto) {
    const existing = this.cart.find(item => item.producto.id === product.id);
    if (existing) {
      existing.cantidad++;
      existing.subtotal = existing.cantidad * existing.producto.precioVenta;
    } else {
      this.cart.push({
        producto: product,
        cantidad: 1,
        subtotal: product.precioVenta
      });
    }
    this.calculateTotal();
  }

  updateQuantity(event: { index: number, delta: number }) {
    const item = this.cart[event.index];
    const newQuantity = item.cantidad + event.delta;

    if (newQuantity <= 0) {
      this.cart.splice(event.index, 1);
    } else {
      if (newQuantity > item.producto.stockActual) {
        alert('⚠️ Stock insuficiente');
        return;
      }
      item.cantidad = newQuantity;
      item.subtotal = item.cantidad * item.producto.precioVenta;
    }
    this.calculateTotal();
  }

  calculateTotal() {
    this.total = this.cart.reduce((acc, item) => acc + item.subtotal, 0);
  }

  // --- 4. COBRO Y FINALIZACIÓN ---
  abrirModalCobro() {
    this.showCheckoutModal = true;
  }

  onProcesarVenta(datosPago: { montoPagado: number, metodoPago: 'EFECTIVO' | 'TARJETA' | 'TRANSFERENCIA' }) {
    this.isProcessingSale = true;

    const payload: VentaRequest = {
      items: this.cart.map(i => ({
        productoId: i.producto.id,
        cantidad: i.cantidad
      })),
      metodoPago: datosPago.metodoPago,
      montoPagado: datosPago.montoPagado
    };

    this.saleService.registrarVenta(payload).subscribe({
      next: (res) => {
        this.obtenerTicket(res.folio);
        // Limpiar todo
        this.cart = [];
        this.total = 0;
        this.showCheckoutModal = false;
        this.isProcessingSale = false;
        this.cargarProductos();
        this.cdr.detectChanges();
      },
      error: (err) => {
        alert('Error: ' + err.error?.message);
        this.isProcessingSale = false;
        this.cdr.detectChanges();
      }
    });
  }

  // --- 5. TICKET ---
  obtenerTicket(folio: string) {
    this.saleService.obtenerTicket(folio).subscribe({
      next: (textoTicket) => {
        this.ticketContent = textoTicket;
        this.showTicketModal = true;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('No se pudo cargar el ticket', err)
    });
  }

  // --- 6. HISTORIAL ---
  abrirHistorial() {
    this.showHistoryModal = true;
    this.isLoadingHistory = true;
    this.saleService.getMisVentasHoy().subscribe({
      next: (data) => {
        this.historialVentas = data;
        this.isLoadingHistory = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.isLoadingHistory = false;
        this.cdr.detectChanges();
      }
    });
  }

  // --- 7. CIERRE ---
  iniciarCierreTurno() {
    this.showCloseModal = true;
  }

  cancelarCierre() {
    this.showCloseModal = false;
  }

  confirmarCierre(montoCierre: number) {
    if (montoCierre === null || montoCierre < 0) {
      alert('Por favor ingresa el monto total de efectivo en caja.');
      return;
    }

    if (!confirm('¿Estás seguro de cerrar el turno? Ya no podrás vender.')) {
      return;
    }

    this.isLoading = true;
    this.cashRegisterService.cerrar({ saldoFinalReal: montoCierre }).subscribe({
      next: (sesionCerrada) => {
        this.isLoading = false;
        this.sesionActual = null;
        this.resumenCierre = sesionCerrada;
        this.showCloseModal = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        alert('Error al cerrar caja: ' + err.error?.message);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  finalizarDia() {
    this.resumenCierre = null;
    this.verificarEstadoCaja();
  }

  logout() {
    this.authService.logout();
  }
}
