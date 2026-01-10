import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Producto } from '../../../../core/services/product';

export interface CartItem {
  producto: Producto;
  cantidad: number;
  subtotal: number;
}

@Component({
  selector: 'app-pos-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pos-cart.html',
  styleUrl: './pos-cart.css',
})
export class PosCart {
  @Input() cart: CartItem[] = [];
  @Input() total: number = 0;

  @Output() onUpdateQuantity = new EventEmitter<{ index: number, delta: number }>();
  @Output() onCheckout = new EventEmitter<void>();
}
