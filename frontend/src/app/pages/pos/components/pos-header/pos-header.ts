import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { SesionCaja } from '../../../../core/services/cash-register';
import { Auth } from '../../../../core/services/auth';

@Component({
  selector: 'app-pos-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './pos-header.html',
  styleUrl: './pos-header.css',
})
export class PosHeader {
  private authService = inject(Auth);

  @Input() sesionActual: SesionCaja | null = null;
  @Input() username: string = '';

  @Output() onLogout = new EventEmitter<void>();
  @Output() onOpenHistory = new EventEmitter<void>();
  @Output() onCloseShift = new EventEmitter<void>();
  @Output() onOpenMovements = new EventEmitter<void>();

  public canExit = this.authService.hasRole(['ADMIN', 'GERENTE']);
}
