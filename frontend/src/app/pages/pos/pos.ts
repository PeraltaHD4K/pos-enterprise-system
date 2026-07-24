import { Component, inject, OnInit, ChangeDetectorRef, ViewChild, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

import { SesionCaja } from '../../core/services/cash-register';
import { Producto } from '../../core/services/product';
import { Sale, VentaResponse } from '../../core/services/sale';
import { Auth } from '../../core/services/auth';
import { Cliente } from '../../core/services/client';
import { ToastService } from '../../core/services/toast';
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
  private toastService = inject(ToastService);

  public cartService = inject(PosCartService);
  public catalogService = inject(PosCatalog);
  public sessionService = inject(PosSession);

  // Estado general
  username: string = '';

  // Estado Productos
  searchTerm: string = '';

  // --- Estado Historial ---
  showHistoryModal = false;
  historialVentas: VentaResponse[] = [];
  isLoadingHistory = false;
  
  // Pagination estado historial
  historyCurrentPage = 0;
  historyPageSize = 10;
  historyTotalPages = 0;
  historyTotalElements = 0;

  showTicketModal = false;
  ticketContent: string = '';

  showCheckoutModal = false;
  isProcessingSale = false;

  showCloseModal = false;
  resumenCierre: SesionCaja | null = null;

  showMovementModal = false;
  isProcessingMovement = false;

  @ViewChild(PosCustomerSelector) customerSelector!: PosCustomerSelector;
  @ViewChild(PosProductList) productList!: PosProductList;

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
        this.toastService.success('Caja abierta correctamente. ¡Buen turno!', 'Turno Iniciado');
      },
      error: (err) => {
        console.error(err);
        this.toastService.error(err.error?.message || 'Error al abrir caja', 'Error');
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
        this.toastService.success(`${datos.tipo} registrado correctamente.`, 'Movimiento Exitoso');
        this.showMovementModal = false;
        this.isProcessingMovement = false;
        this.cdr.detectChanges(); // Aquí sí porque cerramos modal manual
      },
      error: (err) => {
        const msg = err.error?.message || 'Error al registrar movimiento';
        this.toastService.error(msg, 'Error');
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
        this.toastService.error(err.error?.message || 'Error al procesar venta', 'Error');
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
      error: (err) => {
        console.error('No se pudo cargar el ticket', err);
        this.toastService.error('No se pudo cargar la vista del ticket', 'Error de impresión');
      }
    });
  }

  // --- 6. HISTORIAL ---
  abrirHistorial(page: number = 0) {
    this.showHistoryModal = true;
    this.isLoadingHistory = true;
    this.historyCurrentPage = page;
    this.saleService.getMisVentasHoy(this.historyCurrentPage, this.historyPageSize).subscribe({
      next: (data) => {
        this.historialVentas = data.content;
        this.historyTotalPages = data.totalPages;
        this.historyTotalElements = data.totalElements;
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
      this.toastService.warning('Por favor ingresa el monto total de efectivo en caja.', 'Dato Requerido');
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
      error: (err) => this.toastService.error('Error al cerrar caja: ' + err.error?.message, 'Error')
    });
  }

  finalizarDia() {
    this.resumenCierre = null;
    this.verificarEstadoCaja();
  }

  logout() {
    this.authService.logout();
    this.cartService.clear();
  }

  @HostListener('window:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {

    // --- F2: BUSCADOR ---
    if (event.key === 'F2') {
      event.preventDefault();
      this.productList.focusInput();
    }

    // --- F12: COBRAR ---
    if (event.key === 'F12') {
      event.preventDefault();
      if (this.cartService.items().length > 0 && !this.showCheckoutModal) {
        this.abrirModalCobro();
      }
    }

    // --- ESC: CERRAR MODALES / LIMPIAR ---
    if (event.key === 'Escape') {
      // 1. Cerrar modal de cobro
      if (this.showCheckoutModal) {
        this.showCheckoutModal = false;
        return;
      }

      // 2. Cerrar dropdown de clientes (CORREGIDO PARA SIGNALS)
      // Verificamos si existe el selector Y si el signal showDropdown() es true
      if (this.customerSelector && this.customerSelector.showDropdown()) {
        this.customerSelector.showDropdown.set(false); // ✅ Usamos .set()
        return;
      }

      // 3. Limpiar búsqueda
      if (this.searchTerm) {
        this.onSearch('');
        return;
      }
    }
  }
}
