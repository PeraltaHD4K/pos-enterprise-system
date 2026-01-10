import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pos-ticket-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pos-ticket-modal.html',
  styleUrl: './pos-ticket-modal.css',
})
export class PosTicketModal {
  @Input() isOpen = false;
  @Input() content = '';
  @Output() onClose = new EventEmitter<void>();

  imprimir() {
    // La lógica de window.open se mueve aquí
    const printWindow = window.open('', '', 'height=600,width=400');
    if (printWindow) {
      printWindow.document.write('<html><head><title>Ticket</title>');
      printWindow.document.write('<style>body{font-family: monospace; white-space: pre; font-size: 12px;}</style>');
      printWindow.document.write('</head><body>');
      printWindow.document.write(this.content);
      printWindow.document.write('</body></html>');
      printWindow.document.close();
      printWindow.print();
    }
  }
}
