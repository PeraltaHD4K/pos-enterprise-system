import { Component, inject, OnInit, ChangeDetectorRef, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { CashRegister, SesionCaja } from '../../core/services/cash-register';
import { Product, Producto } from '../../core/services/product';
import { Sale, VentaRequest } from '../../core/services/sale';
import { Router } from '@angular/router';
import { Auth } from '../../core/services/auth';

interface CartItem {
  producto: Producto;
  cantidad: number;
  subtotal: number;
}

@Component({
  selector: 'app-pos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './pos.html',
  styleUrl: './pos.css',
})
export class Pos implements OnInit {
  private cashRegisterService = inject(CashRegister);
  private productService = inject(Product);
  private saleService = inject(Sale);
  private fb = inject(FormBuilder);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);
  private authService = inject(Auth);

  @ViewChild('searchInput') searchInput!: ElementRef;

  // ESTADO DEL COMPONENTE
  isLoading = true;
  sesionActual: SesionCaja | null = null; // Si es null, mostramos pantalla de apertura

  username: string = '';

  // ESTADO DEL POS (VENTA)
  allProducts: Producto[] = [];
  filteredProducts: Producto[] = [];
  searchTerm: string = '';

  cart: CartItem[] = [];
  total: number = 0;

  // ESTADO DEL COBRO (MODAL)
  showCheckoutModal = false;
  montoPagado: number | null = null;
  cambio: number = 0;
  isProcessingSale = false;

  showTicketModal = false;
  ticketContent: string = '';

  showCloseModal = false;
  montoCierre: number | null = null;
  resumenCierre: SesionCaja | null = null;

  // FORMULARIO DE APERTURA (Solo pide Saldo Inicial)
  formApertura: FormGroup = this.fb.group({
    saldoInicial: [0, [Validators.required, Validators.min(0)]]
  });

  ngOnInit(): void {
    this.username = this.authService.getUsername() || 'Usuario';
    this.verificarEstadoCaja();
  }

  verificarEstadoCaja() {
    this.isLoading = true;
    this.cashRegisterService.getEstado().subscribe({
      next: (sesion) => {
        // Si el backend devuelve 204 No Content, 'sesion' podría ser null
        // Si es null (204 No Content), significa caja CERRADA
        this.sesionActual = sesion || null;
        if (this.sesionActual) {
          this.cargarProductos(); // Si hay caja, cargamos catálogo
        }
        this.isLoading = false;

        this.cdr.detectChanges();
      },
      error: (err) => {
        // Si hay error (ej. 403), asumimos cerrada o manejamos error
        console.error('Error verificando caja', err);
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
        this.cdr.detectChanges();
        alert('✅ Caja abierta correctamente. ¡Buen turno!');
      },
      error: (err) => {
        console.error(err);
        alert('Error al abrir caja: ' + (err.error?.message || 'Intente de nuevo'));
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
      // Validación segura: Si el campo es null, usamos cadena vacía ''
      const nombre = (p.nombre || '').toLowerCase();
      const codigo = (p.codigoBarras || '').toLowerCase();
      const sku = (p.sku || '').toLowerCase();

      return nombre.includes(lower) || codigo.includes(lower) || sku.includes(lower);
    });

    // Lógica de "Escaner Exacto" (Auto-agregar si es código de barras único)
    if (this.filteredProducts.length === 1) {
      const p = this.filteredProducts[0];
      // Si lo que escribí es EXACTAMENTE el código o SKU, lo agrego y limpio
      if (p.codigoBarras === term || p.sku === term) {
        this.addToCart(p);
        this.searchTerm = '';
        this.filteredProducts = this.allProducts;
        this.cdr.detectChanges(); // <-- Importante actualizar aquí también
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

    // Regresar foco al buscador para seguir escaneando rápido
    setTimeout(() => this.searchInput?.nativeElement.focus(), 100);
  }

  updateQuantity(index: number, delta: number) {
    const item = this.cart[index];
    const newQuantity = item.cantidad + delta;

    if (newQuantity <= 0) {
      this.removeFromCart(index);
      return;
    }

    // Validar Stock (Opcional visualmente, el backend valida final)
    if (newQuantity > item.producto.stockActual) {
      alert('⚠️ Stock insuficiente');
      return;
    }

    item.cantidad = newQuantity;
    item.subtotal = item.cantidad * item.producto.precioVenta;
    this.calculateTotal();
  }

  removeFromCart(index: number) {
    this.cart.splice(index, 1);
    this.calculateTotal();
  }

  calculateTotal() {
    this.total = this.cart.reduce((acc, item) => acc + item.subtotal, 0);
  }

  // --- 4. COBRO Y FINALIZACIÓN ---

  abrirModalCobro() {
    if (this.cart.length === 0) return;
    this.showCheckoutModal = true;
    this.montoPagado = null;
    this.cambio = 0;
    setTimeout(() => document.getElementById('inputPago')?.focus(), 100);
  }

  calcularCambio() {
    if (this.montoPagado !== null) {
      this.cambio = this.montoPagado - this.total;
    }
  }

  procesarVenta() {
    if (!this.montoPagado || this.montoPagado < this.total) {
      alert('El pago es insuficiente');
      return;
    }

    this.isProcessingSale = true;

    const payload: VentaRequest = {
      items: this.cart.map(i => ({
        productoId: i.producto.id,
        cantidad: i.cantidad
      })),
      metodoPago: 'EFECTIVO', // Por ahora fijo, luego puedes poner un select
      montoPagado: this.montoPagado
    };

    this.saleService.registrarVenta(payload).subscribe({
      next: (res) => {
        this.obtenerTicket(res.folio);

        // Limpiar todo
        this.cart = [];
        this.total = 0;
        this.montoPagado = null;
        this.cambio = 0;
        this.searchTerm = '';
        this.showCheckoutModal = false;
        this.isProcessingSale = false;
        this.filteredProducts = this.allProducts;

        // Recargar productos para actualizar stocks visuales
        this.cargarProductos();
      },
      error: (err) => {
        console.error(err);
        alert('❌ Error al procesar venta: ' + (err.error?.message || 'Error desconocido'));
        this.isProcessingSale = false;
      }
    });
  }

  cerrarModal() {
    this.showCheckoutModal = false;
  }

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

  cerrarTicket() {
    this.showTicketModal = false;
    this.ticketContent = '';
  }

  imprimirTicket() {
    // Truco simple para imprimir: Abre una ventana nueva, escribe el ticket e imprime
    const printWindow = window.open('', '', 'height=600,width=400');
    if (printWindow) {
      printWindow.document.write('<html><head><title>Ticket</title>');
      printWindow.document.write('<style>body{font-family: monospace; white-space: pre; font-size: 12px;}</style>');
      printWindow.document.write('</head><body>');
      printWindow.document.write(this.ticketContent);
      printWindow.document.write('</body></html>');
      printWindow.document.close();
      printWindow.print();
    }
  }

  iniciarCierreTurno() {
    this.showCloseModal = true;
    this.montoCierre = null;
  }

  confirmarCierre() {
    if (this.montoCierre === null || this.montoCierre < 0) {
      alert('Por favor ingresa el monto total de efectivo en caja.');
      return;
    }

    if (!confirm('¿Estás seguro de cerrar el turno? Ya no podrás vender.')) {
      return;
    }

    this.isLoading = true;

    // Llamamos al endpoint /caja/cerrar
    this.cashRegisterService.cerrar({ saldoFinalReal: this.montoCierre }).subscribe({
      next: (sesionCerrada) => {
        this.isLoading = false;
        this.sesionActual = null; // Esto ocultará el POS automáticamente
        this.resumenCierre = sesionCerrada; // Guardamos el resultado para mostrarlo
        this.showCloseModal = false; // Cerramos el modal de input

        // Forzamos la detección de cambios para mostrar la pantalla de resumen
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        alert('Error al cerrar caja: ' + err.error?.message);
        this.isLoading = false;
      }
    });
  }

  // Botón "Salir" o "Nuevo Turno" después del resumen
  finalizarDia() {
    this.resumenCierre = null;
    // Recargamos estado para volver a la pantalla de "Apertura" (Candado)
    this.verificarEstadoCaja();
  }

  cancelarCierre() {
    this.showCloseModal = false;
  }

  logout() {
    this.authService.logout();
  }
}
