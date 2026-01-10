import { Component, EventEmitter, Input, Output, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Producto } from '../../../../core/services/product';

@Component({
  selector: 'app-pos-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pos-product-list.html',
  styleUrl: './pos-product-list.css',
})
export class PosProductList {
  @Input() products: Producto[] = [];
  @Input() searchTerm: string = '';

  @Output() onSearch = new EventEmitter<string>();
  @Output() onProductSelect = new EventEmitter<Producto>();

  @ViewChild('searchInput') searchInput!: ElementRef;

  onSearchChange(term: string) {
    this.onSearch.emit(term);
  }

  focusInput() {
    setTimeout(() => this.searchInput?.nativeElement.focus(), 100);
  }
}
