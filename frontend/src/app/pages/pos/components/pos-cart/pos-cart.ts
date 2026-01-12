import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Producto } from '../../../../core/services/product';
import { CartItem } from '../../services/pos-cart';

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
  @Output() onEditQuantity = new EventEmitter<CartItem>();
  @Output() onRemoveItem = new EventEmitter<number>();
}
