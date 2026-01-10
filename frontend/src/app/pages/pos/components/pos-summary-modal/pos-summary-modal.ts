import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SesionCaja } from '../../../../core/services/cash-register';

@Component({
  selector: 'app-pos-summary-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pos-summary-modal.html',
  styleUrl: './pos-summary-modal.css',
})
export class PosSummaryModal {
  @Input() resumen: SesionCaja | null = null;
  @Output() onFinish = new EventEmitter<void>();
}
