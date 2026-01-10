import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-kpi-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './kpi-card.html',
  styleUrl: './kpi-card.css',
})
export class KpiCard {
  @Input() title: string = '';
  @Input() value: string | null = '';
  @Input() subtext: string = '';
  @Input() icon: string = '📊';
  @Input() iconBgColor: string = 'bg-gray-100 text-gray-600';

  get subtextColor() {
    return this.subtext.includes('-') ? 'text-red-500' : 'text-green-500';
  }

}
