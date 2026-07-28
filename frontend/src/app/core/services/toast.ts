import { Injectable, signal } from '@angular/core';

export interface Toast {
  id: string;
  message: string;
  title?: string;
  type: 'success' | 'error' | 'warning' | 'info';
}

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  readonly toasts = signal<Toast[]>([]);
  private counter = 0;

  show(message: string, type: 'success' | 'error' | 'warning' | 'info' = 'info', title?: string) {
    const yaExiste = this.toasts().some(t => t.message === message && t.type === type);

    if (yaExiste) {
      return;
    }

    const id = (this.counter++).toString();
    const newToast: Toast = { id, message, type, title };

    this.toasts.update(current => [...current, newToast]);

    setTimeout(() => {
      this.remove(id);
    }, 3000);
  }

  // Atajos para escribir menos código
  success(msg: string, title: string = 'Éxito') { this.show(msg, 'success', title); }
  error(msg: string, title: string = 'Error') { this.show(msg, 'error', title); }
  warning(msg: string, title: string = 'Cuidado') { this.show(msg, 'warning', title); }
  info(msg: string, title: string = 'Info') { this.show(msg, 'info', title); }

  remove(id: string) {
    this.toasts.update(current => current.filter(t => t.id !== id));
  }
}
