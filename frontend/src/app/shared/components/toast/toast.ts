import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../core/services/toast';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="fixed bottom-5 right-5 z-[9999] flex flex-col gap-3 pointer-events-none">
      
      <div *ngFor="let toast of toastService.toasts()" 
           class="pointer-events-auto min-w-[300px] max-w-sm bg-white rounded-lg shadow-2xl border-l-4 p-4 transform transition-all duration-300 animate-slide-in relative overflow-hidden"
           [ngClass]="{
             'border-green-500': toast.type === 'success',
             'border-red-500': toast.type === 'error',
             'border-orange-500': toast.type === 'warning',
             'border-blue-500': toast.type === 'info'
           }">
        
        <div class="flex justify-between items-start">
          <div class="flex gap-3">
            <div class="text-2xl select-none">
              <span *ngIf="toast.type === 'success'">✅</span>
              <span *ngIf="toast.type === 'error'">🛑</span>
              <span *ngIf="toast.type === 'warning'">⚠️</span>
              <span *ngIf="toast.type === 'info'">ℹ️</span>
            </div>
            
            <div class="pt-0.5">
              <h4 class="font-bold text-gray-800 text-sm leading-tight" *ngIf="toast.title">{{ toast.title }}</h4>
              <p class="text-gray-600 text-sm mt-0.5 leading-snug">{{ toast.message }}</p>
            </div>
          </div>

          <button (click)="toastService.remove(toast.id)" 
                  class="text-gray-400 hover:text-gray-600 transition-colors ml-4 focus:outline-none">
            ✕
          </button>
        </div>
      </div>

    </div>
  `,
  styles: [`
    /* 🚀 Animación CSS Nativa (Sin Angular Animations) */
    @keyframes slideIn {
      from {
        opacity: 0;
        transform: translateX(100%);
      }
      to {
        opacity: 1;
        transform: translateX(0);
      }
    }
    .animate-slide-in {
      animation: slideIn 0.3s cubic-bezier(0.25, 1, 0.5, 1) forwards;
    }
  `]
})
export class ToastComponent {
  toastService = inject(ToastService);
}
