import { Component, EventEmitter, Output, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Client, Cliente } from '../../../../core/services/client';
import { debounceTime, distinctUntilChanged, switchMap, tap, filter } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-pos-customer-selector',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './pos-customer-selector.html',
  styleUrl: './pos-customer-selector.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PosCustomerSelector {
  private clientService = inject(Client);

  // 👇 EVENTO DE SALIDA: Avisa al POS quién es el cliente
  @Output() customerSelected = new EventEmitter<Cliente | null>();

  searchControl = new FormControl('');

  // Estado local del selector
  results = signal<Cliente[]>([]);
  isLoading = signal(false);
  selectedClient = signal<Cliente | null>(null);
  showDropdown = signal(false);

  constructor() {
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      tap(() => {
        this.isLoading.set(true);
        this.showDropdown.set(true);
      }),
      switchMap(query => {
        // Si borran el texto o es muy corto, limpiamos resultados
        if (!query || query.length < 2) {
          return of([]);
        }
        return this.clientService.search(query);
      })
    ).subscribe({
      next: (data) => {
        this.results.set(data);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  select(client: Cliente) {
    this.selectedClient.set(client);
    this.searchControl.setValue(client.nombre, { emitEvent: false }); // Poner nombre en input sin disparar búsqueda
    this.showDropdown.set(false); // Ocultar lista

    // 🔥 Emitir al padre (POS)
    this.customerSelected.emit(client);
  }

  clear() {
    this.selectedClient.set(null);
    this.searchControl.setValue('');
    this.results.set([]);

    // 🔥 Avisar al padre que volvimos a Público General
    this.customerSelected.emit(null);
  }
}
