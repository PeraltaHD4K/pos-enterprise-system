import { Component, inject, signal, computed, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Sale, ReporteGanancias, ProductoTop } from '../../core/services/sale';
import { Analytics, TicketMetrics } from '../../core/services/analytics';
import { KpiCard } from './components/kpi-card/kpi-card';
import { DashboardFilters, FilterData } from './components/dashboard-filters/dashboard-filters';
import { ToastService } from '../../core/services/toast';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType, Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, KpiCard, BaseChartDirective, FormsModule, DashboardFilters],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  private saleService = inject(Sale);
  private analyticsService = inject(Analytics);
  private toastService = inject(ToastService);

  readonly isLoading = signal<boolean>(true);
  readonly isLoadingAnalytics = signal<boolean>(false);
  readonly verHistoricoGlobal = signal<boolean>(false);

  readonly reporte = signal<ReporteGanancias | null>(null);
  readonly topProductos = signal<ProductoTop[]>([]);
  readonly ticketMetrics = signal<TicketMetrics | null>(null);
  readonly currentFilter = signal<FilterData | null>(null);

  readonly currentChartType = signal<ChartType>('bar');

  readonly chartDataVentas = computed<ChartData<'bar' | 'line'>>(() => {
    const data = this.reporte();
    const filtro = this.currentFilter();
    const tipo = this.currentChartType();

    const baseData: ChartData<'bar' | 'line'> = {
      labels: [],
      datasets: [
        {
          data: [],
          label: 'Ventas ($)',
          backgroundColor: tipo === 'bar' ? 'rgba(79, 70, 229, 0.7)' : 'rgba(79, 70, 229, 0.2)', // Un poco más sólido para barras
          borderColor: 'rgba(79, 70, 229, 1)',
          hoverBackgroundColor: 'rgba(67, 56, 202, 1)',
          fill: tipo === 'line' ? 'origin' : false,
          tension: 0,
          pointBackgroundColor: '#fff',
          pointBorderColor: 'rgba(79, 70, 229, 1)',
          pointRadius: 4, // Agregado para que los puntos se vean bien
          pointHoverRadius: 6,
        }
      ]
    };

    if (!data || !data.grafica || !filtro) return baseData;

    let etiquetas: string[] = [];
    let valores: number[] = [];

    // Lógica de llenado de datos (Igual que antes)
    if (filtro.isSingleDay) {
      for (let i = 0; i < 24; i++) {
        const hora = i.toString().padStart(2, '0') + ':00';
        etiquetas.push(hora);
        const p = data.grafica.find(x => x.etiqueta === hora);
        valores.push(p ? p.totalVentas : 0);
      }
    } else {
      data.grafica.forEach(g => {
        etiquetas.push(g.etiqueta);
        valores.push(g.totalVentas);
      });
    }

    // Retornamos la estructura completa con tus estilos y los datos nuevos
    return {
      labels: etiquetas,
      datasets: [{ ...baseData.datasets[0], data: valores }]
    };
  });

  readonly histogramData = computed<ChartData<'bar'>>(() => {
    const metrics = this.ticketMetrics();

    // Configuración base
    const baseData: ChartData<'bar'> = {
      labels: [],
      datasets: [{
        data: [],
        label: 'Frecuencia',
        backgroundColor: '#10b981',
        borderRadius: 4,
        hoverBackgroundColor: '#059669'
      }]
    };

    // Si no hay datos, retornamos vacía
    if (!metrics || !metrics.distribucion_precios) return baseData;

    const rawLabels = Object.keys(metrics.distribucion_precios);
    const valores = Object.values(metrics.distribucion_precios);

    // ✅ TRUCO VISUAL: Formatear las etiquetas para que parezcan dinero
    const formattedLabels = rawLabels.map(label => {
      // Si viene como "0-50", le ponemos signos de pesos
      if (label.includes('-')) {
        const parts = label.split('-');
        // Intenta formatear si son números, si no, deja el original
        return `$${parts[0]} - $${parts[1]}`;
      }
      return label; // Fallback
    });

    return {
      labels: formattedLabels, // Usamos las etiquetas bonitas
      datasets: [{
        ...baseData.datasets[0],
        data: valores
      }]
    };
  });

  public chartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: {
        mode: 'index',
        intersect: false,
      }
    },
    scales: {
      x: { grid: { display: false } },
      y: {
        beginAtZero: true,
        ticks: { callback: (val) => '$' + val }
      }
    }
  };

  public histogramOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: {
        mode: 'index',
        intersect: false,
        callbacks: {
          // Personalizamos el tooltip para que diga "5 tickets" en lugar de solo "5"
          label: (context) => ` ${context.parsed.y} Tickets`
        }
      }
    },
    scales: {
      x: {
        grid: { display: false },
        // (Opcional) Si quisieras rotar las etiquetas si son muy largas
        ticks: { maxRotation: 0, autoSkip: true }
      },
      y: {
        beginAtZero: true,
        ticks: {
          // 1. SIN signo de pesos (es cantidad)
          // 2. stepSize: 1 para evitar decimales (no existen 1.5 tickets)
          stepSize: 1,
          precision: 0
        },
        title: {
          display: true,
          text: 'Cantidad de Tickets',
          font: { size: 10 }
        }
      }
    }
  };

  onFilterChange(filtro: FilterData) {
    this.currentFilter.set(filtro);
    this.cargarDatos();
  }

  cargarDatos() {
    const filtro = this.currentFilter();
    if (!filtro) return;

    this.isLoading.set(true);

    // 1. Carga Operativa (Java)
    this.saleService.obtenerReporteGanancias(filtro.start, filtro.end).subscribe({
      next: (data) => {
        this.reporte.set(data); // ¡Al setear esto, chartDataVentas se actualiza solo!
        this.isLoading.set(false);
      },
      error: () => {
        this.toastService.error('Error cargando finanzas', 'Error');
        this.isLoading.set(false);
      }
    });

    this.saleService.obtenerTopProductos(filtro.start, filtro.end).subscribe(
      data => this.topProductos.set(data)
    );

    // 2. Carga Analytics
    this.cargarAnalytics();
  }

  cargarAnalytics() {
    this.isLoadingAnalytics.set(true);
    const filtro = this.currentFilter();

    // Lógica del Switch Global
    const isGlobal = this.verHistoricoGlobal();
    const start = isGlobal ? undefined : filtro?.start;
    const end = isGlobal ? undefined : filtro?.end;

    this.analyticsService.obtenerMetricasTickets(start, end).subscribe({
      next: (metrics) => {
        this.ticketMetrics.set(metrics); // ¡Esto dispara histogramData automáticamente!
        this.isLoadingAnalytics.set(false);
      },
      error: () => this.isLoadingAnalytics.set(false)
    });
  }

  toggleHistorico() {
    this.verHistoricoGlobal.update(v => !v);
    this.cargarAnalytics();
  }

  toggleChartType(type: ChartType) {
    this.currentChartType.set(type);
  }
}
