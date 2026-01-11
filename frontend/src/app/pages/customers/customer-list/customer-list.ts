import { Component, inject, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Client, Cliente } from '../../../core/services/client';
import { debounceTime, distinctUntilChanged, switchMap, startWith } from 'rxjs/operators';

@Component({
  selector: 'app-customer-list',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './customer-list.html',
  styleUrl: './customer-list.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CustomerList {
  private clientService = inject(Client);

  // 1. ESTADO CON SIGNALS (La nueva forma de Angular)
  clients = signal<Cliente[]>([]);
  isLoading = signal<boolean>(true);

  // Control para el input de búsqueda
  searchControl = new FormControl('');

  constructor() {
    // 2. REACTIVIDAD PURA
    // Escuchamos el input -> Esperamos 300ms -> Llamamos al Backend -> Actualizamos la Señal
    this.searchControl.valueChanges.pipe(
      startWith(''), // Cargar todos al inicio
      debounceTime(300), // Evitar saturar el servidor
      distinctUntilChanged(),
      switchMap(query => {
        this.isLoading.set(true); // Signal: Loading ON
        // Si está vacío traemos todos, si tiene texto buscamos
        const request$ = query?.trim()
          ? this.clientService.search(query)
          : this.clientService.getAll();

        return request$;
      })
    ).subscribe({
      next: (data) => {
        this.clients.set(data); // Signal: Actualizamos datos
        this.isLoading.set(false); // Signal: Loading OFF
      },
      error: () => this.isLoading.set(false)
    });
  }
}
