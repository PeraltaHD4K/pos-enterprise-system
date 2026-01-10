import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SesionCaja } from '../../../../core/services/cash-register';

@Component({
  selector: 'app-pos-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pos-header.html',
  styleUrl: './pos-header.css',
})
export class PosHeader {
  @Input() sesionActual: SesionCaja | null = null;
  @Input() username: string = '';

  @Output() onLogout = new EventEmitter<void>();
  @Output() onOpenHistory = new EventEmitter<void>();
  @Output() onCloseShift = new EventEmitter<void>();
  @Output() onOpenMovements = new EventEmitter<void>();
}
