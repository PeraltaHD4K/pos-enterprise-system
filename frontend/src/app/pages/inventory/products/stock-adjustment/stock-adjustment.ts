import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Product, Producto, AjusteStockRequest } from '../../../../core/services/product';

@Component({
  selector: 'app-stock-adjustment',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './stock-adjustment.html',
  styleUrl: './stock-adjustment.css',
})
export class StockAdjustment implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private productService = inject(Product);
  private cdr = inject(ChangeDetectorRef);

  producto: Producto | undefined;
  isLoading = true;

  form: FormGroup = this.fb.group({
    tipo: ['ENTRADA', Validators.required], // ENTRADA o SALIDA
    cantidadAbsoluta: [1, [Validators.required, Validators.min(1)]], // Siempre positivo aquí
    motivo: ['', Validators.required]
  });

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.cargarProducto(Number(id));
    }
  }

  cargarProducto(id: number) {
    this.productService.getById(id).subscribe({
      next: (data) => {
        this.producto = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        alert('Producto no encontrado');
        this.isLoading = false;
        this.cdr.detectChanges();
        this.router.navigate(['/inventory/products']);
      }
    });
  }

  onSubmit() {
    if (this.form.invalid || !this.producto) return;

    const { tipo, cantidadAbsoluta, motivo } = this.form.value;

    // Calculamos la cantidad real (Positiva o Negativa)
    const cantidadFinal = tipo === 'ENTRADA' ? cantidadAbsoluta : -cantidadAbsoluta;

    const payload: AjusteStockRequest = {
      cantidad: cantidadFinal,
      motivo: motivo
    };

    this.productService.updateStock(this.producto.id, payload).subscribe({
      next: () => {
        alert('Stock actualizado correctamente');
        this.router.navigate(['/inventory/products']);
      },
      error: (err) => alert('Error al ajustar stock: ' + (err.error?.message || 'Error desconocido'))
    });
  }
}
