import { Component, inject, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

import { CashRegister, SesionCaja } from '../../core/services/cash-register';
import { Product, Producto } from '../../core/services/product';
import { Sale } from '../../core/services/sale';
import { Auth } from '../../core/services/auth';
import { Cliente } from '../../core/services/client';
import { PosCartService } from './services/pos-cart';
import { PosCatalog } from './services/pos-catalog';
import { PosSession } from './services/pos-session';

import { PosHeader } from './components/pos-header/pos-header';
import { PosProductList } from './components/pos-product-list/pos-product-list';
import { PosCart } from './components/pos-cart/pos-cart';
import { PosHistoryModal } from './components/pos-history-modal/pos-history-modal';
import { PosTicketModal } from './components/pos-ticket-modal/pos-ticket-modal';
import { PosCheckoutModal } from './components/pos-checkout-modal/pos-checkout-modal';
import { PosCloseModal } from './components/pos-close-modal/pos-close-modal';
import { PosSummaryModal } from './components/pos-summary-modal/pos-summary-modal';
import { PosMovementModal } from './components/pos-movement-modal/pos-movement-modal';
import { PosCustomerSelector } from './components/pos-customer-selector/pos-customer-selector';

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
    PosMovementModal,
    PosCustomerSelector,
  ],
  templateUrl: './pos.html',
  styleUrl: './pos.css',
})
export class Pos implements OnInit {
  private saleService = inject(Sale);
  private fb = inject(FormBuilder);
  private cdr = inject(ChangeDetectorRef);
  private authService = inject(Auth);

  public cartService = inject(PosCartService);
  public catalogService = inject(PosCatalog);
  public sessionService = inject(PosSession);

  // Estado general
  username: string = '';

  // Estado Productos
  searchTerm: string = '';

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

  showMovementModal = false;
  isProcessingMovement = false;

  @ViewChild(PosCustomerSelector) customerSelector!: PosCustomerSelector;

  // Formulario de apertura
  formApertura: FormGroup = this.fb.group({
    saldoInicial: [0, [Validators.required, Validators.min(0)]]
  });

  ngOnInit(): void {
    this.username = this.authService.getUsername() || 'Usuario';
    this.verificarEstadoCaja();
    this.cartService.clear();
  }

  // --- 1. GESTION DE CAJA ---
  verificarEstadoCaja() {
    this.sessionService.loadSession().subscribe((sesion) => {
      if (sesion) {
        this.catalogService.loadProducts();
      }
    });
  }

  abrirCaja() {
    if (this.formApertura.invalid) return;
    const saldo = this.formApertura.value.saldoInicial;

    this.sessionService.openSession(saldo).subscribe({
      next: () => {
        this.catalogService.loadProducts();
        alert('✅ Caja abierta correctamente. ¡Buen turno!');
      },
      error: (err) => {
        console.error(err);
        alert('Error: ' + err.error?.message);
      }
    });
  }

  abrirMovimientos() {
    this.showMovementModal = true;
  }

  onRegistrarMovimiento(datos: { monto: number, tipo: 'INGRESO' | 'RETIRO', motivo: string }) {
    this.isProcessingMovement = true;

    this.sessionService.registerMovement(datos).subscribe({
      next: () => {
        alert(`✅ ${datos.tipo} registrado correctamente.`);
        this.showMovementModal = false;
        this.isProcessingMovement = false;
        this.cdr.detectChanges(); // Aquí sí porque cerramos modal manual
      },
      error: (err) => {
        const msg = err.error?.message || 'Error al registrar movimiento';
        alert('❌ ' + msg);
        this.isProcessingMovement = false;
        this.cdr.detectChanges();
      }
    });
  }

  // --- 2. CATÁLOGO Y BÚSQUEDA ---

  onSearch(term: string) {
    this.searchTerm = term;

    this.catalogService.search(term);

    const match = this.catalogService.checkExactMatch(term);
    if (match) {
      this.cartService.addItem(match);
      this.searchTerm = '';
      this.catalogService.search('');
    }
  }

  // --- 3. CARRITO DE COMPRAS ---
  addToCart(product: Producto) {
    this.cartService.addItem(product);
  }

  updateQuantity(event: { index: number, delta: number }) {
    const currentItem = this.cartService.items()[event.index];
    if (!currentItem) return;

    // Calculamos la NUEVA CANTIDAD TOTAL
    const newQuantity = currentItem.cantidad + event.delta;

    // Llamamos al servicio con el total
    this.cartService.updateQuantity(event.index, newQuantity);
  }

  onEditQuantity(item: any) { // Puedes importar CartItem si quieres tipado estricto
    // Buscamos el índice real en el carrito actual
    const index = this.cartService.items().findIndex(i => i.producto.id === item.producto.id);
    if (index === -1) return;

    // Prompt simple (rápido y funcional)
    const input = prompt(`Ingresa la cantidad para: ${item.producto.nombre}`, item.cantidad.toString());

    if (input !== null) {
      const newQty = parseInt(input, 10);

      if (!isNaN(newQty)) {
        this.cartService.updateQuantity(index, newQty);
      }
    }
  }

  removeItem(index: number) {
    this.cartService.removeItem(index);
  }

  // --- 4. COBRO Y FINALIZACIÓN ---
  abrirModalCobro() {
    this.showCheckoutModal = true;
  }

  onClienteSeleccionado(cliente: Cliente | null) {
    this.cartService.setClient(cliente);
  }

  onProcesarVenta(datosPago: { montoPagado: number, metodoPago: 'EFECTIVO' | 'TARJETA' | 'TRANSFERENCIA' }) {
    this.isProcessingSale = true;

    this.cartService.checkout(datosPago.metodoPago, datosPago.montoPagado).subscribe({
      next: (res) => {
        this.obtenerTicket(res.folio);
        if (this.customerSelector) {
          this.customerSelector.clear();
        }

        this.showCheckoutModal = false;
        this.isProcessingSale = false;
        this.catalogService.refresh();
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

  onVentaCancelada() {
    this.abrirHistorial();
    this.catalogService.refresh();
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

    this.sessionService.closeSession(montoCierre).subscribe({
      next: (sesionCerrada) => {
        this.resumenCierre = sesionCerrada;
        this.showCloseModal = false;
        this.formApertura.reset({ saldoInicial: 0 });
        // La sesión ya se puso en null dentro del servicio
        this.cdr.detectChanges();
      },
      error: (err) => alert('Error al cerrar caja: ' + err.error?.message)
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
