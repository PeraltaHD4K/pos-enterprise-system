import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Config } from '../../core/services/config';
import { ToastService } from '../../core/services/toast';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './settings.html',
  styleUrl: './settings.css',
})
export class Settings implements OnInit {
  private fb = inject(FormBuilder);
  private configService = inject(Config);
  private cdr = inject(ChangeDetectorRef);
  private toastService = inject(ToastService);

  form: FormGroup = this.fb.group({
    NOMBRE_TIENDA: ['', Validators.required],
    RFC: ['', Validators.required],
    DIRECCION: [''],
    TICKET_FOOTER: ['']
  });

  isLoading = false;

  ngOnInit() {
    this.isLoading = true;
    this.configService.getConfig().subscribe({
      next: (data) => {
        // patchValue llena el formulario con las claves que coincidan
        this.form.patchValue(data);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  save() {
    if (this.form.invalid) return;
    this.isLoading = true;

    this.configService.saveConfig(this.form.value).subscribe({
      next: () => {
        this.toastService.success('Configuración guardada', 'Éxito');
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.toastService.error('Error al guardar', 'Error');
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
